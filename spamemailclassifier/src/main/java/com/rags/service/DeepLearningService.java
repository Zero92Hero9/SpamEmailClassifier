package com.rags.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.LongBuffer;
import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rags.entity.EmailLog;
import com.rags.repository.EmailLogRepository;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service 
public class DeepLearningService {
    private OrtEnvironment environment;
    private OrtSession session;
    private EmailLogRepository repository;

    public DeepLearningService(EmailLogRepository repository) {
        this.repository = repository;
    }

    @PostConstruct 
    public void initModel() {
        environment = OrtEnvironment.getEnvironment();
        InputStream modelStream = this.getClass().getResourceAsStream("model.onnx");

        if (modelStream == null) {
            System.err.println("Model not found");
            return;
        }

        try {
            session = environment.createSession(modelStream.readAllBytes());
            System.out.println("Pre-trained model loaded successfully");
        } catch (OrtException e) {
            throw new RuntimeException("Exception loading the model " + e);
        } catch (IOException e) {
            throw new RuntimeException("Exception reading the stream " + e);
        }
    }

    public DeepLearningResult classify(String text) {
        if (session == null) {
            return null;
        }
         try {
            // Basic Tokenizer step: Convert string text to numeric tokens expected by BERT
            // Real models use a vocabulary map; for a simplified resume-ready showcase:
            long[] tokenIds = tokenizeText(text); 
            long[] inputShape = new long[]{1, tokenIds.length};

            // Wrap tokens into ONNX tensors
            OnnxTensor inputTensor = OnnxTensor.createTensor(environment, LongBuffer.wrap(tokenIds), inputShape);
            Map<String, OnnxTensor> inputs = Map.of("input_ids", inputTensor);

            // Execute the model forward-pass (Inference)
            try (OrtSession.Result results = session.run(inputs)) {
                float[][] outputLogits = (float[][]) results.get(0).getValue();
                float[] probabilities = softmax(outputLogits[0]);

                // Index 0 = Ham, Index 1 = Spam
                String label = probabilities[1] > probabilities[0] ? "spam" : "ham";
                double confidence = Math.max(probabilities[0], probabilities[1]);

                EmailLog log = new EmailLog(text, label, confidence, Instant.now());
                repository.save(log);

                return new DeepLearningResult(label, confidence);
            }
        } catch (Exception e) {
            throw new RuntimeException("Inference error" + e);
        }
    }

    private long[] tokenizeText(String text) {
        String[] words = text.split("\\s+");
        long[] ids = new long[words.length];

        for (int i = 0; i < words.length; i++) {
            ids[i] = Math.abs(words[i].hashCode() % 30522);
        }

        return ids;
    }

    private float[] softmax(float[] logits) {
        float max = Float.NEGATIVE_INFINITY;
        for (float v : logits) if (v > max) max = v;
        float sum = 0.0f;
        float[] result = new float[logits.length];
        for (int i = 0; i < logits.length; i++) {
            result[i] = (float) Math.exp(logits[i] - max);
            sum += result[i];
        }
        for (int i = 0; i < result.length; i++) result[i] /= sum;
        return result;
    }

    @PreDestroy
    public void close() throws Exception {
        if (session != null) session.close();
        if (environment != null) environment.close();
    }

    public record DeepLearningResult(String label, double confidence){}

}
