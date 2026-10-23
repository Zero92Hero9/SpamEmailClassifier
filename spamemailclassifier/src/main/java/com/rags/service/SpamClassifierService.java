package com.rags.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.rags.entity.EmailLog;
import com.rags.repository.EmailLogRepository;

import jakarta.annotation.PostConstruct;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

@Service
public class SpamClassifierService {
    private DoccatModel model;
    private DocumentCategorizerME categorizer;
    private final DoccatFactory factory;
    private EmailLogRepository repository;

    public SpamClassifierService(EmailLogRepository repository) {
        this.factory = new DoccatFactory();
        this.repository = repository;
    }

    @PostConstruct 
    public void trainModel() {
        try {
            InputStream dataIn = this.getClass().getResourceAsStream("/spam-training.txt");
            if (dataIn == null) {
                throw new RuntimeException("Training data file not found");
            }

            ObjectStream<String> lineStream = new PlainTextByLineStream(new InputStreamFactory() {
                @Override 
                public InputStream createInputStream() {
                    return dataIn;
                }
            }, StandardCharsets.UTF_8);

            ObjectStream<DocumentSample> sample = new DocumentSampleStream(lineStream);
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, 0);
            params.put(TrainingParameters.ITERATIONS_PARAM, 100);

            this.model = DocumentCategorizerME.train("en", sample, params, factory);
            this.categorizer = new DocumentCategorizerME(model);

            System.out.println(">> Spam classifier trained successfully");

        } catch (Exception e) {
           throw new RuntimeException("Failed to train model", e);     
        }
    }

    public ClassificationResult classify(String emailText) {
        if (emailText == null || emailText.isBlank()) {
            return new ClassificationResult("ham", 1.0);
        }

        String[] tokens = emailText.toLowerCase().split("\\s+");
        double[] outcomes = categorizer.categorize(tokens);
        String category = categorizer.getBestCategory(outcomes);
        double confidence = outcomes[categorizer.getIndex(category)];

        EmailLog logEntry = new EmailLog(emailText, category, confidence, Instant.now());
        repository.save(logEntry);

        return new ClassificationResult(category, confidence);
    }

    public record ClassificationResult(String label, double confidence) {
    }
}
