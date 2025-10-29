# image-processor

## Project Overview
This project is a **legacy structure-based CRUD API** for managing image data.

- Stores original images/thumbnails directly in **DB BLOB** columns
- Provides **Offset pagination** only
- Upload request:
  - Generates thumbnails synchronously
  - Process status transition: `PROCESSING` → `READY` or `FAILED`
- Prevents duplicate uploads:
  - SHA-256
  - Pessimistic Lock
  - Transaction management

## 🛠️ Technology Stack
- Java 21, Spring Boot 3
- MySQL (image metadata + BLOB storage)
- MinIO (image storage, DB binary storage)
- Lombok, JPA/Hibernate
- Swagger/OpenAPI 3
- k6 (load testing)

## 🚀 API Specification

### Project
| Feature | Method | Endpoint | Description |
|---------|--------|----------|-------------|
| Create Project | POST | `/projects/new` | Create a project with an optional name |
| Get Project | GET | `/projects/{id}` | Fetch project information only |
| Update Project | PATCH | `/projects/{id}` | Update project name |
| Delete Project | DELETE | `/projects/{id}` | Delete a project |

### Image
| Feature | Method | Endpoint | Description |
|---------|--------|----------|-------------|
| Upload Image | POST | `/projects/{projectId}/images` | Multipart upload, duplicate prevention, synchronous thumbnail generation |
| Get Image List | GET | `/projects/{projectId}/images` | Offset pagination, filter by status/tags |
| Get Single Image | GET | `/images/{id}` | Returns metadata + presigned URL |
| Update Image | PATCH | `/images/{id}` | Update tags, memo, or status |
| Delete Image | DELETE | `/images/{id}` | Soft delete (`softDelete=true`) |


<img width="1035" height="747" alt="Imace Processor ApIU C" src="https://github.com/user-attachments/assets/d1f5755e-0fd2-47b7-836c-0904e11200a7" />
Swagger Documentation: [Swagger Docs](http://localhost:8080/swagger-ui.html)

## Load Testing Tool

### Installation
**macOS**
```bash
brew install k6
```

**Windows**
```bash
choco install k6
```

**Linux (Ubuntu)**
```bash
sudo apt install k6
```

**Run Script**
```bash
# Navigate to the k6 directory and run the scripts below.
k6 run upload.js
k6 run upload-multi.js
k6 run offset.js
```

**Load Testing Objectives**
 - The terminal will display request count, failure rate, and response time distribution (p50, p95, p99).

 - p50 is the median response time, p95 is the 95th percentile, and p99 is the top 1% response time.
