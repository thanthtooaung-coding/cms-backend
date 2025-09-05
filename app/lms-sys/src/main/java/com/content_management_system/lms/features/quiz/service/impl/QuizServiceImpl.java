package com.content_management_system.lms.features.quiz.service.impl;

import com.content_management_system.lms.features.quiz.dto.*;
import com.content_management_system.lms.features.quiz.mapper.QuizMapper;
import com.content_management_system.lms.shared.repository.AnswerRepository;
import com.content_management_system.lms.shared.repository.QuestionRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.features.quiz.service.QuizService;
import com.content_management_system.lms.shared.entity.Answer;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.entity.Question;
import com.content_management_system.lms.shared.entity.Quiz;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public void create(CreateQuizRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module with id " + request.getModuleId() + " not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setModule(module);

        List<Question> questions = new ArrayList<>();
        for (QuestionRequest questionRequest : request.getQuestions()) {
            Question question = new Question();
            question.setQuestionText(questionRequest.getQuestion());
            question.setQuiz(quiz);

            List<Answer> answers = questionRequest.getAnswerOptions().stream()
                    .map(answerOptionRequest -> {
                        Answer answer = new Answer();
                        answer.setAnswerText(answerOptionRequest.getAnswer());
                        answer.setCorrect(answerOptionRequest.isCorrect());
                        answer.setQuestion(question);
                        return answer;
                    }).collect(Collectors.toList());
            question.setAnswers(answers);
            questions.add(question);
        }
        quiz.setQuestions(questions);

        quizRepository.save(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllByModuleId(Long moduleId) {
        List<Quiz> quizzes = quizRepository.findAllByModuleId(moduleId);
        return QuizMapper.toQuizResponseList(quizzes);
    }

    @Override
    @Transactional
    public QuizResponse update(Long quizId, UpdateQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + quizId + " not found"));

        quiz.setTitle(request.getTitle());

        quiz.getQuestions().clear();

        for (QuestionRequest questionRequest : request.getQuestions()) {
            Question question = new Question();
            question.setQuestionText(questionRequest.getQuestion());
            question.setQuiz(quiz);

            List<Answer> answers = questionRequest.getAnswerOptions().stream()
                    .map(answerOptionRequest -> {
                        Answer answer = new Answer();
                        answer.setAnswerText(answerOptionRequest.getAnswer());
                        answer.setCorrect(answerOptionRequest.isCorrect());
                        answer.setQuestion(question);
                        return answer;
                    }).collect(Collectors.toList());

            question.setAnswers(answers);
            quiz.getQuestions().add(question);
        }

        quizRepository.save(quiz);

        return QuizMapper.toQuizResponse(quiz);
    }

    @Override
    @Transactional
    public void delete(DeleteQuizRequest request) {
        if (request.isForceDelete()) {
            quizRepository.deleteAllById(request.getIds());
        } else {
            request.getIds().forEach(id -> {
                quizRepository.findById(id).ifPresent(quiz -> {
                    quizRepository.deleteById(quiz.getId());
                });
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz with id " + id + " not found"));
        return QuizMapper.toQuizResponse(quiz);
    }
}