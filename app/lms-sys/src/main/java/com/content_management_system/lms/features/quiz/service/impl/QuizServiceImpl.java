package com.content_management_system.lms.features.quiz.service.impl;

import com.content_management_system.lms.features.quiz.dto.*;
import com.content_management_system.lms.features.quiz.mapper.QuizMapper;
import com.content_management_system.lms.shared.repository.AnswerRepository;
import com.content_management_system.lms.shared.repository.QuestionRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.shared.repository.StudentAnswerRepository;
import com.content_management_system.lms.shared.repository.StudentQuizRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import com.content_management_system.lms.features.quiz.service.QuizService;
import com.content_management_system.lms.shared.entity.Answer;
import com.content_management_system.lms.shared.entity.Module;
import com.content_management_system.lms.shared.entity.Question;
import com.content_management_system.lms.shared.entity.Quiz;
import com.content_management_system.lms.shared.entity.StudentAnswer;
import com.content_management_system.lms.shared.entity.StudentQuiz;
import com.content_management_system.lms.shared.entity.User;
import com.content_management_system.lms.shared.exception.ResourceNotFoundException;
import com.content_management_system.lms.shared.exception.UnauthorizedException;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import com.content_management_system.lms.shared.util.SecurityUtil;
import com.content_management_system.lms.features.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ModuleRepository moduleRepository;
    private final StudentQuizRepository studentQuizRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    private final CertificateService certificateService;

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

    @Override
    @Transactional
    public QuizResultResponse submitQuiz(SubmitQuizRequest request, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + request.getQuizId()));

        // Get latest attempt number
        List<StudentQuiz> previousAttempts = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(student.getId(), quiz.getId());
        int nextAttempt = previousAttempts.isEmpty() ? 1 : previousAttempts.get(0).getAttempt() + 1;

        // Create StudentQuiz
        StudentQuiz studentQuiz = new StudentQuiz();
        studentQuiz.setStudent(student);
        studentQuiz.setQuiz(quiz);
        studentQuiz.setAttempt(nextAttempt);

        // Calculate score
        int correctCount = 0;
        int totalQuestions = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;

        List<QuizResultResponse.QuestionResult> questionResults = new ArrayList<>();

        for (SubmitQuizRequest.AnswerSubmission answerSubmission : request.getAnswers()) {
            Question question = questionRepository.findById(answerSubmission.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerSubmission.getQuestionId()));

            Answer selectedAnswer = answerRepository.findById(answerSubmission.getAnswerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerSubmission.getAnswerId()));

            // Find correct answer
            Answer correctAnswer = question.getAnswers().stream()
                    .filter(Answer::getCorrect)
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = correctAnswer != null && correctAnswer.getId().equals(selectedAnswer.getId());
            if (isCorrect) {
                correctCount++;
            }

            // Create StudentAnswer
            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setStudentQuiz(studentQuiz);
            studentAnswer.setQuestion(question);
            studentAnswer.setAnswer(selectedAnswer);
            studentAnswerRepository.save(studentAnswer);

            // Build question result
            questionResults.add(QuizResultResponse.QuestionResult.builder()
                    .questionId(question.getId())
                    .questionText(question.getQuestionText())
                    .selectedAnswerId(selectedAnswer.getId())
                    .selectedAnswerText(selectedAnswer.getAnswerText())
                    .correctAnswerId(correctAnswer != null ? correctAnswer.getId() : null)
                    .correctAnswerText(correctAnswer != null ? correctAnswer.getAnswerText() : "No correct answer")
                    .isCorrect(isCorrect)
                    .build());
        }

        studentQuiz.setScore(correctCount);
        StudentQuiz savedStudentQuiz = studentQuizRepository.save(studentQuiz);

        // Check for certificate eligibility after quiz submission
        Long courseId = quiz.getModule().getCourse().getId();
        try {
            certificateService.generateCertificateIfEligible(student.getId(), courseId);
        } catch (Exception e) {
            // Log error but don't fail quiz submission
            log.error("Error generating certificate for student {} in course {}", student.getId(), courseId, e);
        }

        return QuizResultResponse.builder()
                .studentQuizId(savedStudentQuiz.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .score(correctCount)
                .totalQuestions(totalQuestions)
                .attempt(nextAttempt)
                .questionResults(questionResults)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultResponse getQuizResult(Long studentQuizId, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        
        StudentQuiz studentQuiz = studentQuizRepository.findById(studentQuizId)
                .orElseThrow(() -> new ResourceNotFoundException("Student quiz not found with id: " + studentQuizId));

        // Verify the student owns this quiz result
        if (!studentQuiz.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("You can only view your own quiz results");
        }

        Quiz quiz = studentQuiz.getQuiz();
        List<QuizResultResponse.QuestionResult> questionResults = new ArrayList<>();

        if (studentQuiz.getStudentAnswers() != null) {
            for (StudentAnswer studentAnswer : studentQuiz.getStudentAnswers()) {
                Question question = studentAnswer.getQuestion();
                Answer selectedAnswer = studentAnswer.getAnswer();
                
                // Find correct answer
                Answer correctAnswer = question.getAnswers().stream()
                        .filter(Answer::getCorrect)
                        .findFirst()
                        .orElse(null);

                boolean isCorrect = correctAnswer != null && correctAnswer.getId().equals(selectedAnswer.getId());

                questionResults.add(QuizResultResponse.QuestionResult.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .selectedAnswerId(selectedAnswer.getId())
                        .selectedAnswerText(selectedAnswer.getAnswerText())
                        .correctAnswerId(correctAnswer != null ? correctAnswer.getId() : null)
                        .correctAnswerText(correctAnswer != null ? correctAnswer.getAnswerText() : "No correct answer")
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        return QuizResultResponse.builder()
                .studentQuizId(studentQuiz.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .score(studentQuiz.getScore())
                .totalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .attempt(studentQuiz.getAttempt())
                .questionResults(questionResults)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizSubmissionHistoryResponse getSubmissionHistory(Long quizId, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        securityUtil.requireRole(student, com.content_management_system.lms.shared.constants.LmsRoleName.Student);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if student is enrolled in the course
        if (!quiz.getModule().getCourse().getEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getStudent().getId().equals(student.getId()))) {
            throw new UnauthorizedException("Student is not enrolled in the course for this quiz.");
        }

        // Get the latest submission (get first from ordered list)
        List<StudentQuiz> allSubmissions = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(student.getId(), quizId);
        if (allSubmissions.isEmpty()) {
            throw new ResourceNotFoundException("No submission found for this quiz");
        }
        StudentQuiz latestSubmission = allSubmissions.get(0);

        // Build response with questions but without correct answer indicators
        List<QuizSubmissionHistoryResponse.QuestionWithoutAnswer> questions = quiz.getQuestions().stream()
                .map(question -> {
                    List<QuizSubmissionHistoryResponse.AnswerOption> answerOptions = question.getAnswers().stream()
                            .map(answer -> QuizSubmissionHistoryResponse.AnswerOption.builder()
                                    .answerId(answer.getId())
                                    .answerText(answer.getAnswerText())
                                    .build())
                            .collect(Collectors.toList());
                    // Shuffle answers to prevent pattern recognition
                    Collections.shuffle(answerOptions);
                    
                    return QuizSubmissionHistoryResponse.QuestionWithoutAnswer.builder()
                            .questionId(question.getId())
                            .questionText(question.getQuestionText())
                            .answerOptions(answerOptions)
                            .build();
                })
                .collect(Collectors.toList());

        return QuizSubmissionHistoryResponse.builder()
                .studentQuizId(latestSubmission.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .attempt(latestSubmission.getAttempt())
                .score(latestSubmission.getScore())
                .totalQuestions(quiz.getQuestions().size())
                .submittedAt(latestSubmission.getCreatedAt())
                .questions(questions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizForRetake(Long quizId, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        securityUtil.requireRole(student, com.content_management_system.lms.shared.constants.LmsRoleName.Student);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if student is enrolled in the course
        if (!quiz.getModule().getCourse().getEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getStudent().getId().equals(student.getId()))) {
            throw new UnauthorizedException("Student is not enrolled in the course for this quiz.");
        }

        // Build quiz response with randomized answer order
        List<QuestionResponse> questions = quiz.getQuestions().stream()
                .map(question -> {
                    List<AnswerResponse> answers = question.getAnswers().stream()
                            .map(answer -> AnswerResponse.builder()
                                    .id(answer.getId())
                                    .answerText(answer.getAnswerText())
                                    .correct(answer.getCorrect())
                                    .build())
                            .collect(Collectors.toList());
                    // Randomize answer order for retake
                    Collections.shuffle(answers);
                    
                    return QuestionResponse.builder()
                            .id(question.getId())
                            .questionText(question.getQuestionText())
                            .answers(answers)
                            .build();
                })
                .collect(Collectors.toList());

        // Also randomize question order
        Collections.shuffle(questions);

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .moduleId(quiz.getModule().getId())
                .questions(questions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultResponse getLastSubmissionWithAnswers(Long quizId, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        securityUtil.requireRole(student, com.content_management_system.lms.shared.constants.LmsRoleName.Student);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if student is enrolled in the course
        if (!quiz.getModule().getCourse().getEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getStudent().getId().equals(student.getId()))) {
            throw new UnauthorizedException("Student is not enrolled in the course for this quiz.");
        }

        // Get the latest submission (get first from ordered list)
        List<StudentQuiz> allSubmissions = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(student.getId(), quizId);
        if (allSubmissions.isEmpty()) {
            throw new ResourceNotFoundException("No submission found for this quiz");
        }
        StudentQuiz latestSubmission = allSubmissions.get(0);

        // Build response with answers (for student's own review)
        List<QuizResultResponse.QuestionResult> questionResults = new ArrayList<>();
        Map<Long, Answer> correctAnswersMap = quiz.getQuestions().stream()
                .flatMap(q -> q.getAnswers().stream())
                .filter(Answer::getCorrect)
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        if (latestSubmission.getStudentAnswers() != null) {
            for (StudentAnswer studentAnswer : latestSubmission.getStudentAnswers()) {
                Question question = studentAnswer.getQuestion();
                Answer selectedAnswer = studentAnswer.getAnswer();
                
                // Find correct answer
                Answer correctAnswer = correctAnswersMap.get(question.getId());
                boolean isCorrect = correctAnswer != null && correctAnswer.getId().equals(selectedAnswer.getId());

                questionResults.add(QuizResultResponse.QuestionResult.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .selectedAnswerId(selectedAnswer.getId())
                        .selectedAnswerText(selectedAnswer.getAnswerText())
                        .correctAnswerId(correctAnswer != null ? correctAnswer.getId() : null)
                        .correctAnswerText(correctAnswer != null ? correctAnswer.getAnswerText() : "No correct answer")
                        .isCorrect(isCorrect)
                        .build());
            }
        }

        return QuizResultResponse.builder()
                .studentQuizId(latestSubmission.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .studentId(student.getId())
                .studentName(student.getName())
                .score(latestSubmission.getScore())
                .totalQuestions(quiz.getQuestions().size())
                .attempt(latestSubmission.getAttempt())
                .submittedAt(latestSubmission.getCreatedAt())
                .questionResults(questionResults)
                .build();
    }

    @Override
    @Transactional
    public void deleteAllSubmissionsExceptLatest(Long quizId, Long userId) {
        User student = securityUtil.getCurrentUser(userId);
        securityUtil.requireRole(student, com.content_management_system.lms.shared.constants.LmsRoleName.Student);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        // Check if student is enrolled in the course
        if (!quiz.getModule().getCourse().getEnrollments().stream()
                .anyMatch(enrollment -> enrollment.getStudent().getId().equals(student.getId()))) {
            throw new UnauthorizedException("Student is not enrolled in the course for this quiz.");
        }

        // Get all submissions ordered by attempt (latest first)
        List<StudentQuiz> allSubmissions = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(student.getId(), quizId);

        if (allSubmissions.isEmpty()) {
            throw new ResourceNotFoundException("No submissions found for this quiz");
        }

        // Keep the latest submission (first in the list)
        StudentQuiz latestSubmission = allSubmissions.get(0);

        // Delete all other submissions (skip the first one)
        List<StudentQuiz> submissionsToDelete = allSubmissions.subList(1, allSubmissions.size());

        // Delete all student answers for these submissions first
        for (StudentQuiz submission : submissionsToDelete) {
            if (submission.getStudentAnswers() != null) {
                studentAnswerRepository.deleteAll(submission.getStudentAnswers());
            }
        }

        // Then delete the submissions
        studentQuizRepository.deleteAll(submissionsToDelete);
        
        log.info("Deleted {} submissions for student {} in quiz {}, keeping latest attempt #{}", 
                submissionsToDelete.size(), student.getId(), quizId, latestSubmission.getAttempt());
    }
}