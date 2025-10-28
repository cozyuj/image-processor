package com.example.demo.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thumbnail")
public class ThumbnailConfig {
    private int width;
    private int height;
}
