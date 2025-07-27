package com.content_management_system.lms.features.quiz.controller;

import com.content_management_system.lms.features.quiz.dto.CreateQuizRequest;
import com.content_management_system.lms.features.quiz.dto.DeleteQuizRequest;
import com.content_management_system.lms.features.quiz.dto.QuizResponse;
import com.content_management_system.lms.features.quiz.dto.UpdateQuizRequest;
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
}
