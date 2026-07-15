package io.novel2video.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.nio.file.Paths;

@Configuration
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${storage.local.dir:./data/storage}")
    private String storageDir;

    @Value("${storage.local.url-prefix:/files/}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(storageDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler(urlPrefix + "**")
                .addResourceLocations(absolutePath);
    }
}
