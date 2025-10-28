package com.example.demo.Service;

import com.example.demo.Domain.Image;
import com.example.demo.Domain.Status;
import com.example.demo.Repository.ImageRepository;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThumbnailService {

    private final MinioClient minioClient;
    private final ImageRepository imageRepository;
    private final String bucket = "images";

    @Async
    @Transactional
    public void generateThumbnailAsync(Image image, MultipartFile file) {
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            int width = 150;
            int height = (int)((double) img.getHeight()/img.getWidth() * width);
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();

            // MinIO에 썸네일 업로드
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "jpg", baos);
            byte[] bytes = baos.toByteArray();

            String thumbName = "thumb_" + System.currentTimeMillis() + "_" + image.getOriginFileName();
            minioClient.putObject(io.minio.PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(thumbName)
                    .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                    .contentType("image/jpeg")
                    .build());

            String thumbUrl = "http://localhost:9000/" + bucket + "/" + thumbName;

            image.setThumbnailUrl(thumbUrl);
            image.setStatus(Status.READY);
            imageRepository.save(image);

        } catch (Exception e) {
            image.setStatus(Status.FAILED);
            imageRepository.save(image);
            log.error("Thumbnail generation failed for image id={}", image.getId(), e);
        }
    }
}
