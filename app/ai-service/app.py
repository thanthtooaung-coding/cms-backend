import os
import consul
import uvicorn
import logging
import time
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from typing import List
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.prompts import PromptTemplate
from langchain.output_parsers import PydanticOutputParser
from dotenv import load_dotenv

# --- Configuration & Logging ---
load_dotenv()
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def register_with_consul():
    """Registers the service with the Consul agent, with retries."""
    consul_host = os.getenv("CONSUL_HOST", "consul")
    consul_port = int(os.getenv("CONSUL_PORT", 8500))
    service_name = "ai-service"
    service_port = int(os.getenv("SERVICE_PORT", 8086))
    
    c = consul.Consul(host=consul_host, port=consul_port)
    
    service_id = f"{service_name}-{service_port}"
    health_check_url = f"http://ai-service:{service_port}/health"

    max_retries = 5
    retry_delay = 3

    for attempt in range(max_retries):
        try:
            c.agent.service.register(
                name=service_name,
                service_id=service_id,
                address="ai-service",
                port=service_port,
                check=consul.Check.http(
                    url=health_check_url,
                    interval="10s",
                    timeout="5s",
                    deregister="30s"
                )
            )
            logger.info(f"Successfully registered service '{service_name}' with Consul on attempt {attempt + 1}.")
            return
        except consul.ConsulException as e:
            logger.warning(f"Failed to register with Consul on attempt {attempt + 1}/{max_retries}: {e}")
            if attempt < max_retries - 1:
                logger.info(f"Retrying in {retry_delay} seconds...")
                time.sleep(retry_delay)
            else:
                logger.error("Could not register with Consul after all retries.")

class AnswerOption(BaseModel):
    answer: str = Field(description="The text of the answer option.")
    correct: bool = Field(description="True if this is the correct answer, false otherwise.")

class QuizQuestion(BaseModel):
    question: str = Field(description="The text of the quiz question.")
    answer_options: List[AnswerOption] = Field(description="A list of possible answers for the question.")

class QuizGenerationRequest(BaseModel):
    topic: str
    num_questions: int = 4
    difficulty: str = "beginner"

class RelatedTopic(BaseModel):
    id: int = Field(description="A unique identifier for the topic (1, 2, or 3).")
    name: str = Field(description="The name of the related topic.")

class RelatedTopicsResponse(BaseModel):
    topics: List[RelatedTopic] = Field(description="A list of exactly 3 related topics.")

class LessonContent(BaseModel):
    title: str = Field(description="The title of the lesson.")
    content: str = Field(description="The content of the lesson (could be video URL, slide content, link, or text).")
    material_type: str = Field(description="The type of material: Video, Slide, Link, PDF, or Article.")

class RelatedTopicsRequest(BaseModel):
    course_title: str = Field(description="The title of the course.")
    course_description: str = Field(description="The description of the course.")
    category: str = Field(description="The category of the course.")
    lessons: List[LessonContent] = Field(default=[], description="List of lessons in the course with their content.")

# Initialize Google Generative AI model following LangChain documentation
# See: https://docs.langchain.com/oss/python/integrations/chat/google_generative_ai
try:
    google_api_key = os.getenv("GOOGLE_API_KEY")
    if not google_api_key:
        logger.warning("GOOGLE_API_KEY not found in environment variables")
        llm = None
    else:
        # ChatGoogleGenerativeAI automatically reads GOOGLE_API_KEY from environment
        # Using gemini-2.5-flash as shown in LangChain documentation
        llm = ChatGoogleGenerativeAI(
            model="gemini-2.5-flash",
            temperature=0.7,
            max_retries=2,
        )
        logger.info("Successfully initialized Google Generative AI with gemini-2.5-flash")
except Exception as e:
    logger.error(f"Failed to initialize Google Generative AI. Ensure GOOGLE_API_KEY is set. Error: {e}")
    llm = None

output_parser = PydanticOutputParser(pydantic_object=QuizQuestion)

prompt_template = PromptTemplate(
    template="""
    You are an expert quiz creator for a Learning Management System.
    Your task is to generate a single, high-quality multiple-choice question based on the provided topic and difficulty.
    The question should have 4 answer options, and exactly one of them must be correct.

    {format_instructions}

    Topic: {topic}
    Difficulty: {difficulty}
    """,
    input_variables=["topic", "difficulty"],
    partial_variables={"format_instructions": output_parser.get_format_instructions()}
)

app = FastAPI(
    title="LMS AI Service",
    description="A service for generating educational content using AI.",
    version="1.0.0"
)

@app.on_event("startup")
def on_startup():
    register_with_consul()

@app.get("/health")
def health_check():
    return {"status": "ok"}

@app.post("/generate-quiz", response_model=List[QuizQuestion])
async def generate_quiz(request: QuizGenerationRequest):
    if not llm:
        raise HTTPException(status_code=500, detail="AI model is not initialized. Check server logs.")

    chain = prompt_template | llm | output_parser
    
    generated_questions = []
    try:
        for _ in range(request.num_questions):
            response = await chain.ainvoke({
                "topic": request.topic,
                "difficulty": request.difficulty
            })
            generated_questions.append(response)
        
        return generated_questions
    except Exception as e:
        logger.error(f"Error during quiz generation: {e}")
        raise HTTPException(status_code=500, detail=f"An error occurred while generating the quiz. Details: {str(e)}")

# Related Topics Generation
related_topics_output_parser = PydanticOutputParser(pydantic_object=RelatedTopicsResponse)

related_topics_prompt_template = PromptTemplate(
    template="""
    You are an expert educational content curator for a Learning Management System.
    Your task is to analyze the actual course content (lessons) and generate exactly 3 related learning topics that students might be interested in after completing this course.
    
    IMPORTANT: You must analyze the actual lesson content provided below. Look at:
    - Lesson titles to understand what topics are covered
    - Lesson content (video descriptions, slide text, article content, links)
    - Material types (Video, Slide, Link, PDF, Article) to understand the format
    
    Based on the REAL course content, generate 3 specific, relevant topics that:
    1. Are directly related to what students learned in the course
    2. Build upon the concepts covered in the lessons
    3. Are valuable next steps for students to continue learning
    4. Are specific and actionable (not generic topics)

    {format_instructions}

    Course Title: {course_title}
    Course Description: {course_description}
    Course Category: {category}

    Course Lessons (analyze these carefully):
    {lessons_content}

    Generate exactly 3 related topics based on the ACTUAL course content analyzed above.
    """,
    input_variables=["course_title", "course_description", "category", "lessons_content"],
    partial_variables={"format_instructions": related_topics_output_parser.get_format_instructions()}
)

@app.post("/generate-related-topics", response_model=RelatedTopicsResponse)
async def generate_related_topics(request: RelatedTopicsRequest):
    if not llm:
        raise HTTPException(status_code=500, detail="AI model is not initialized. Check server logs.")

    # Format lessons content for the prompt
    lessons_content_parts = []
    if request.lessons and len(request.lessons) > 0:
        for idx, lesson in enumerate(request.lessons, 1):
            lesson_text = f"Lesson {idx}: {lesson.title}\n"
            lesson_text += f"  Type: {lesson.material_type}\n"
            if lesson.content:
                # Truncate very long content to avoid token limits
                content_preview = lesson.content[:500] if len(lesson.content) > 500 else lesson.content
                lesson_text += f"  Content: {content_preview}"
                if len(lesson.content) > 500:
                    lesson_text += "... (truncated)"
            else:
                lesson_text += "  Content: (No content available)"
            lessons_content_parts.append(lesson_text)
        lessons_content = "\n\n".join(lessons_content_parts)
    else:
        lessons_content = "No lessons available in this course yet."

    chain = related_topics_prompt_template | llm | related_topics_output_parser
    
    try:
        response = await chain.ainvoke({
            "course_title": request.course_title,
            "course_description": request.course_description,
            "category": request.category,
            "lessons_content": lessons_content
        })
        return response
    except Exception as e:
        logger.error(f"Error during related topics generation: {e}")
        raise HTTPException(status_code=500, detail=f"An error occurred while generating related topics. Details: {str(e)}")

if __name__ == "__main__":
    service_port = int(os.getenv("SERVICE_PORT", 8086))
    uvicorn.run(app, host="0.0.0.0", port=service_port)
