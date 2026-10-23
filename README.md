# Spam Email Classifier

## Services

The system is split into two distinct decoupled layers to showcase both statistical and deep learning approaches to text classification:

    • SpamClassifierService: A foundational text classification layer powered by Apache OpenNLP. It leverages maximum entropy models to perform fast, deterministic tokenization and probabilistic classification on inbound payloads.

    • DeepLearningService: An advanced, low-latency inference layer utilizing ONNX Runtime (Open Neural Network Exchange). This service loads a tiny BERT-based model locally, managing tensor shapes, numerical vector spaces, and raw mathematical matrix evaluations directly inside the JVM without external runtime dependencies.


## Storage & Data Persistence

The persistence layer handles data logging for downstream analytical reporting and audit trails:
    • Embedded H2 Database: Configured as an in-memory datastore for rapid, zero-overhead prototyping and seamless integration testing.

    • EmailLog Entity: Structured to capture inbound payload metadata, processing timestamps, tokenization signatures, and the comparative classification outputs from both execution pipelines.

## API Endpoints

The REST implementation provides 3 core endpoints:

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/ping` | Liveness probe to verify system health and availability. |
| `POST` | `/email` | Classifies inbound email payloads via the **Apache OpenNLP** pipeline. |
| `POST` | `/email/deep-learning` | Classifies inbound email payloads via the local **ONNX / BERT** pipeline. |

## Usage Examples

You can interact with the API endpoints locally using `curl`. Ensure the application is running on port `8080` before executing these commands.

### 1. System Health Check
Verify the liveness and readiness of the application server:
```bash
curl -sS http://localhost:8080/api/classify/ping
```

### 2. Statistical Classification (Apache OpenNLP)
Submit an email payload to evaluate through the maximum entropy pipeline:
```bash
curl -sS -X POST http://localhost:8080/api/classify/email \
  -H "Content-Type: application/json" \
  -d '{"content": "project status update"}'
```

### 3. Deep Learning Inference (ONNX / BERT)
Submit an email payload to evaluate through the local neural network inference engine:
```bash
curl -sS -X POST http://localhost:8080/api/classify/email/deep-learning \
  -H "Content-Type: application/json" \
  -d '{"content": "URGENT: Win a free gift card now!"}'
```






