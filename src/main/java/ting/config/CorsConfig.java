package ting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The cors config for Ting app.
 */
@Configuration
public class CorsConfig {
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
                // TODO: it's better to keep allowed origins in application.yml
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5000")
                        .allowedMethods("GET", "PUT", "POST", "DELETE", "PATCH")
                        .allowCredentials(true);
            }
        };
    }
}
