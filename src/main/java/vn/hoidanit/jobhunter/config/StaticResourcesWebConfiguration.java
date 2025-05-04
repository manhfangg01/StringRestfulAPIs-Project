package vn.hoidanit.jobhunter.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.annotation.PostConstruct;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    @Value("${hoidanit.upload-file.base-path}")
    private String basePath;

    @PostConstruct
    public void init() {
        System.out.println("Base path configured: " + basePath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Thêm tiền tố '/' nếu cần thiết
        String formattedPath = basePath;
        if (!formattedPath.endsWith("/")) {
            formattedPath += "/";
        }

        registry.addResourceHandler("/storage/**")
                .addResourceLocations(formattedPath)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        return resource.exists() && resource.isReadable() ? resource : null;
                    }
                });
    }
}