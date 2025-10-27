package com.example.demo.dto;

import com.example.demo.Domain.Image;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageUploadRes {
    private Long id;
    private String filename;
    private String fileUrl;
    private String status;

    public static ImageUploadRes entity(Image image) {
        return ImageUploadRes.builder()
                .id(image.getId())
                .filename(image.getOriginFileName())
                .fileUrl(image.getFileUrl())
                .status(image.getStatus().name())
                .build();
    }
}
