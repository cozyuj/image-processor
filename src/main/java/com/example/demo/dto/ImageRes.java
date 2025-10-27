package com.example.demo.dto;

import com.example.demo.Domain.Image;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageRes {
    private Long id;
    private String fileUrl;
    private String thumbnailUrl;
    private String originFileName;
    private String memo;
    private String tags;
    private String status;


    public static ImageRes entity(Image img) {
        return ImageRes.builder()
                .id(img.getId())
                .fileUrl(img.getFileUrl())
                .thumbnailUrl(img.getThumbnailUrl())
                .originFileName(img.getOriginFileName())
                .memo(img.getMemo())
                .tags(img.getTags())
                .status(img.getStatus().name())
                .build();
    }
}
