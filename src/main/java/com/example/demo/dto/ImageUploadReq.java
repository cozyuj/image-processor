package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class ImageUploadReq {
    private List<MultipartFile> files;

    private String memo;

    private String tags;

    @Builder
    public ImageUploadReq(List<MultipartFile> files, String memo, String tags) {
        this.files = files;
        this.memo = memo;
        this.tags = tags;
    }
}
