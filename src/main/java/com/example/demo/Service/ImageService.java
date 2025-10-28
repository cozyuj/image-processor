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
import com.sun.jdi.request.DuplicateRequestException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
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

    public List<ImageUploadRes> uploadImages(Long projectId, ImageUploadReq req) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ImageUploadRes> resList = new ArrayList<>();

        for (MultipartFile file : req.getFiles()) {
            String filename = file.getOriginalFilename();

            if (imageRepository.findByProjectIdAndOriginFileName(projectId, filename).isPresent()) {
                throw new RuntimeException("Duplicate file: " + filename);
            }

            try {
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
                        .memo(req.getMemo())
                        .tags(req.getTags())
                        .softDelete(false)
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
                throw new RuntimeException("Failed to upload file: " + filename, e);
            }
        }
        return resList;
    }

    public ImageRes getImageOne(Long id) {
        Image img = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return ImageRes.entity(img);
    }

    public ImageRes updateImage(Long id, String memo, String tags) {
        Image img = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        if (memo != null) img.setMemo(memo);
        if (tags != null) img.setTags(tags);
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
