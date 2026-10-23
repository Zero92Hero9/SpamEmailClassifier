package com.rags.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rags.service.DeepLearningService;
import com.rags.service.DeepLearningService.DeepLearningResult;
import com.rags.service.SpamClassifierService;
import com.rags.service.SpamClassifierService.ClassificationResult;

@RestController
@RequestMapping ("/api/classify")
public class SpamEmailClassifierController {
    private SpamClassifierService service;
    private DeepLearningService deepLearningService;

    public SpamEmailClassifierController(SpamClassifierService service, DeepLearningService deepLearningService) {
        this.service = service;
        this.deepLearningService = deepLearningService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "Ok" + Instant.now();
    }

    @PostMapping("/email")
    public ResponseEntity<ClassificationResult> classifyEmail(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null) {
            return ResponseEntity.badRequest().build();
        }

        ClassificationResult result = service.classify(content);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/email/deep-learning")
    public ResponseEntity<DeepLearningResult> classifyEmailLearning(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        System.out.println(content);

        if (content == null) {
            return ResponseEntity.badRequest().build();
        }

        DeepLearningResult result = deepLearningService.classify(content);
        return ResponseEntity.ok(result);
    }
    



}
