package ting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The cors config for Ting app.
 */
@Configuration
public class CorsConfig {
    @Autowired
    private TingConfig tingConfig;

    /**
     * The concrete cors config.
     *
     * @return {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer}
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(tingConfig.getAllowedOrigin())
                        .allowedMethods("GET", "PUT", "POST", "DELETE", "PATCH", "HEAD")
                        .allowCredentials(true);
            }
        };
    }
}
