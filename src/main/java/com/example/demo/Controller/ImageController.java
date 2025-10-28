package com.example.demo.Controller;

import com.example.demo.Service.ImageService;
import com.example.demo.dto.ImageRes;
import com.example.demo.dto.ImageUploadReq;
import com.example.demo.dto.ImageUploadRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Tag(name = "Image API", description = "이미지 관련 기능을 제공하는 Controller")
@RestController
@RequestMapping("/api/v1")
public class ImageController {

    @Autowired
    ImageService imageService;

    @Operation(summary = "이미지 단건 조회", description = "메타데이터 + presigned URL 반환")
    @ApiResponse(responseCode = "200", description = "이미지 단건 조회 성공")
    @GetMapping("/images/{id}")
    public ResponseEntity<ImageRes> getImageOne(
            @Parameter(description = "이미지 ID", example = "1") @PathVariable Long id) {
        ImageRes image = imageService.getImageOne(id);
        return ResponseEntity.ok(image);
    }

    @Operation(summary = "이미지 업로드", description = "멀티파트 업로드(N장 가능), 중복 업로드 방지")
    @PostMapping(value="/projects/{projectId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ImageUploadRes>> uploadImages(
            @PathVariable Long projectId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "tags", required = false) String tags
    ) throws IOException {
        ImageUploadReq req = new ImageUploadReq(files, memo, tags);
        List<ImageUploadRes> uploaded = imageService.uploadImages(projectId, req);
        return ResponseEntity.ok(uploaded);
    }

    @Operation(summary = "이미지 수정", description = "태그, 메모, 상태 변경")
    @PatchMapping("/images/{id}")
    public ResponseEntity<ImageRes> updateImage(
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "status", required = false) String status,
            @PathVariable Long id
    ) {
        ImageRes updated = imageService.updateImage(id, memo, tags, status);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "이미지 삭제", description = "소프트 삭제 (softDelete=true) 처리")
    @DeleteMapping("/images/{id}")
    public ResponseEntity<String> deleteImage(
            @PathVariable Long id
    ) {
        imageService.softDeleteImage(id);
        return ResponseEntity.ok("이미지 " + id + " 삭제 완료");
    }
}
