package com.staysmart.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Serves uploaded property/review images from the local {@code uploads/} directory as static
 * resources under {@code app.uploads.base-url}. In production this local-disk strategy can be
 * swapped for S3/Cloud Storage by replacing {@link com.staysmart.property.service.ImageStorageService}
 * without touching callers.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = appProperties.getUploads().getBaseUrl();
        Path uploadsDir = Path.of(appProperties.getUploads().getDir()).toAbsolutePath().normalize();
        registry.addResourceHandler(baseUrl + "/**")
                .addResourceLocations("file:" + uploadsDir + "/");
    }
}
