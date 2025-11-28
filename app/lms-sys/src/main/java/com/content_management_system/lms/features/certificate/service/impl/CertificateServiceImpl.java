package com.content_management_system.lms.features.certificate.service.impl;

import com.content_management_system.lms.features.certificate.dto.CertificateResponse;
import com.content_management_system.lms.features.certificate.service.CertificateService;
import com.content_management_system.lms.shared.entity.Certificate;
import com.content_management_system.lms.shared.entity.Course;
import com.content_management_system.lms.shared.entity.Quiz;
import com.content_management_system.lms.shared.entity.StudentQuiz;
import com.content_management_system.lms.shared.repository.CertificateRepository;
import com.content_management_system.lms.shared.repository.CourseRepository;
import com.content_management_system.lms.shared.repository.StudentQuizRepository;
import com.content_management_system.lms.shared.repository.ModuleRepository;
import com.content_management_system.lms.shared.repository.QuizRepository;
import com.content_management_system.lms.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentQuizRepository studentQuizRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private static final BigDecimal PASSING_THRESHOLD = new BigDecimal("75.00");

    @Override
    @Transactional
    public CertificateResponse generateCertificateIfEligible(Long studentId, Long courseId) {
        // Check if certificate already exists
        Certificate existingCertificate = certificateRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElse(null);
        
        if (existingCertificate != null) {
            return toResponse(existingCertificate);
        }

        // Check eligibility
        if (!isEligibleForCertificate(studentId, courseId)) {
            return null;
        }

        // Calculate average score
        BigDecimal averageScore = calculateAverageScore(studentId, courseId);

        // Generate certificate
        Certificate certificate = new Certificate();
        certificate.setStudent(userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found")));
        certificate.setCourse(courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found")));
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setIssuedDate(OffsetDateTime.now());
        certificate.setScorePercentage(averageScore);

        Certificate savedCertificate = certificateRepository.save(certificate);
        log.info("Certificate generated for student {} in course {} with score {}", studentId, courseId, averageScore);
        
        return toResponse(savedCertificate);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getCertificate(Long studentId, Long courseId) {
        Certificate certificate = certificateRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElse(null);
        return certificate != null ? toResponse(certificate) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateResponse> getAllCertificatesByStudent(Long studentId) {
        return certificateRepository.findAllByStudentId(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEligibleForCertificate(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Get all modules for the course
        List<com.content_management_system.lms.shared.entity.Module> modules = moduleRepository.findAllByCourseId(courseId);
        
        // Get all quizzes from all modules
        List<Quiz> courseQuizzes = modules.stream()
                .flatMap(module -> quizRepository.findAllByModuleIdWithSoftDelete(module.getId()).stream())
                .collect(Collectors.toList());

        if (courseQuizzes.isEmpty()) {
            return false; // No quizzes, no certificate
        }

        // Get all student quiz submissions for this course (only latest for each quiz)
        List<StudentQuiz> allSubmissions = courseQuizzes.stream()
                .flatMap(quiz -> {
                    List<StudentQuiz> quizSubmissions = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(studentId, quiz.getId());
                    // Get only the latest submission for each quiz
                    return quizSubmissions.isEmpty() ? java.util.stream.Stream.empty() : java.util.stream.Stream.of(quizSubmissions.get(0));
                })
                .collect(Collectors.toList());

        if (allSubmissions.isEmpty()) {
            return false; // No submissions
        }

        // Calculate average score across all quizzes
        BigDecimal averageScore = calculateAverageScore(studentId, courseId);

        return averageScore.compareTo(PASSING_THRESHOLD) >= 0;
    }

    private BigDecimal calculateAverageScore(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Get all modules for the course
        List<com.content_management_system.lms.shared.entity.Module> modules = moduleRepository.findAllByCourseId(courseId);
        
        // Get all quizzes from all modules
        List<Quiz> courseQuizzes = modules.stream()
                .flatMap(module -> quizRepository.findAllByModuleIdWithSoftDelete(module.getId()).stream())
                .collect(Collectors.toList());

        if (courseQuizzes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        int totalQuestions = 0;

        for (Quiz quiz : courseQuizzes) {
            List<StudentQuiz> submissions = studentQuizRepository.findAllByStudentIdAndQuizIdOrdered(studentId, quiz.getId());
            if (!submissions.isEmpty()) {
                // Get the best attempt (latest is first in ordered list, but we want the best score)
                StudentQuiz bestAttempt = submissions.stream()
                        .max((s1, s2) -> {
                            int totalQ1 = quiz.getQuestions().size();
                            int totalQ2 = quiz.getQuestions().size();
                            double score1 = totalQ1 > 0 ? (double) s1.getScore() / totalQ1 * 100 : 0;
                            double score2 = totalQ2 > 0 ? (double) s2.getScore() / totalQ2 * 100 : 0;
                            return Double.compare(score1, score2);
                        })
                        .orElse(null);

                if (bestAttempt != null) {
                    int quizQuestions = quiz.getQuestions().size();
                    totalScore = totalScore.add(new BigDecimal(bestAttempt.getScore())
                            .divide(new BigDecimal(quizQuestions), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100")));
                    totalQuestions += quizQuestions;
                }
            }
        }

        if (totalQuestions == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate weighted average
        return totalScore.divide(new BigDecimal(courseQuizzes.size()), 2, RoundingMode.HALF_UP);
    }

    private String generateCertificateNumber() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + System.currentTimeMillis();
    }

    private CertificateResponse toResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .studentId(certificate.getStudent().getId())
                .studentName(certificate.getStudent().getName())
                .courseId(certificate.getCourse().getId())
                .courseTitle(certificate.getCourse().getTitle())
                .certificateNumber(certificate.getCertificateNumber())
                .issuedDate(certificate.getIssuedDate())
                .scorePercentage(certificate.getScorePercentage())
                .build();
    }
}

