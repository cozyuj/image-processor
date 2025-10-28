package com.example.demo.Service;

import com.example.demo.Domain.Image;
import com.example.demo.Domain.Project;
import com.example.demo.Domain.Status;
import com.example.demo.Exception.ApiException;
import com.example.demo.Exception.ErrorHandling;
import com.example.demo.Repository.ImageRepository;
import com.example.demo.Repository.ProjectRepository;
import com.example.demo.dto.ImageRes;
import com.example.demo.dto.ImageUploadReq;
import com.example.demo.dto.ImageUploadRes;
import com.example.demo.util.HashUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;
    private final MinioClient minioClient;
    private final ThumbnailService thumbnailService;

    private final String bucket = "images";

    public ImageService(ImageRepository imageRepository, ProjectRepository projectRepository, MinioClient minioClient, ThumbnailService thumbnailService) {
        this.imageRepository = imageRepository;
        this.projectRepository = projectRepository;
        this.minioClient = minioClient;
        this.thumbnailService = thumbnailService;
    }

    @Transactional
    public List<ImageUploadRes> uploadImages(Long projectId, ImageUploadReq req) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ImageUploadRes> resList = new ArrayList<>();

        for (MultipartFile file : req.getFiles()) {
            try (InputStream is = file.getInputStream()) {
                // 파일 해시(SHA-256) 기반 중복 체크
                String hash = HashUtil.sha256(is);
                //Optional<Image> existFile = imageRepository.findByHash(hash);
                //@Transactional 필수: 락이 트랜잭션 내에서만 유효
                //동시 요청 시, 먼저 락을 잡은 요청만 DB에 인서트 가능
                Optional<Image> existFile = imageRepository.findByHashForUpdate(hash);
                if (existFile.isPresent()) {
                    log.info("중복 업로드 차단: {}", file.getOriginalFilename());
                    throw new ApiException(ErrorHandling.DUPLICATE_REQUEST);
                }
                String filename = file.getOriginalFilename();

                byte[] fileBytes = file.getBytes();

                String objectName = System.currentTimeMillis() + "_" + filename;
                minioClient.putObject(
                        io.minio.PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
                String fileUrl = "http://localhost:9090/" + bucket + "/" + objectName;

                Image newImage = Image.builder()
                        .project(project)
                        .originFileName(filename)
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .binaryData(fileBytes)
                        .softDelete(false)
                        .memo(req.getMemo())
                        .hash(hash)
                        .tags(req.getTags())
                        .status(Status.PROCESSING)
                        .fileUrl(fileUrl)
                        .build();

                imageRepository.save(newImage);

                thumbnailService.generateThumbnailAsync(newImage, file);

                resList.add(ImageUploadRes.builder()
                        .id(newImage.getId())
                        .filename(filename)
                        .status(newImage.getStatus().name())
                        .fileUrl(fileUrl)
                        .build()
                );
            } catch (Exception e) {
                throw new ApiException(ErrorHandling.INTERNAL_SERVER_ERROR);
            }
        }
        return resList;
    }

    public ImageRes getImageOne(Long id) {
        Image img = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return ImageRes.entity(img);
    }

    public ImageRes updateImage(Long id, String memo, String tags, String status) {
        Image img = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        if (memo != null) img.setMemo(memo);
        if (tags != null) img.setTags(tags);
        if (status != null) img.setStatus(Status.valueOf(status));
        imageRepository.save(img);
        return ImageRes.entity(img);
    }

    public void softDeleteImage(Long id) {
        Image img = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        img.setSoftDelete(true);
        imageRepository.save(img);
    }
}