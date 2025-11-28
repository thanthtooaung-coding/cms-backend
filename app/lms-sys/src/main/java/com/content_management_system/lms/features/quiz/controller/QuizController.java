package com.content_management_system.lms.features.quiz.controller;

import com.content_management_system.lms.features.quiz.dto.*;
import com.content_management_system.lms.features.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<Void> createQuiz(@RequestBody CreateQuizRequest request) {
        quizService.create(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<List<QuizResponse>> getAllQuizzesByModuleId(@PathVariable Long moduleId) {
        return ResponseEntity.ok(quizService.getAllByModuleId(moduleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long id, @RequestBody UpdateQuizRequest request) {
        return ResponseEntity.ok(quizService.update(id, request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteQuiz(@RequestBody DeleteQuizRequest request) {
        quizService.delete(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long id) {
        QuizResponse quizResponse = quizService.getById(id);
        return ResponseEntity.ok(quizResponse);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @RequestBody SubmitQuizRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuizResultResponse response = quizService.submitQuiz(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/result/{studentQuizId}")
    public ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable Long studentQuizId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuizResultResponse response = quizService.getQuizResult(studentQuizId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/submission-history")
    public ResponseEntity<QuizSubmissionHistoryResponse> getSubmissionHistory(
            @PathVariable Long quizId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuizSubmissionHistoryResponse response = quizService.getSubmissionHistory(quizId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/retake")
    public ResponseEntity<QuizResponse> getQuizForRetake(
            @PathVariable Long quizId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuizResponse response = quizService.getQuizForRetake(quizId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/last-submission")
    public ResponseEntity<QuizResultResponse> getLastSubmissionWithAnswers(
            @PathVariable Long quizId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        QuizResultResponse response = quizService.getLastSubmissionWithAnswers(quizId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{quizId}/submissions/keep-latest")
    public ResponseEntity<Void> deleteAllSubmissionsExceptLatest(
            @PathVariable Long quizId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        quizService.deleteAllSubmissionsExceptLatest(quizId, userId);
        return ResponseEntity.noContent().build();
    }
}
