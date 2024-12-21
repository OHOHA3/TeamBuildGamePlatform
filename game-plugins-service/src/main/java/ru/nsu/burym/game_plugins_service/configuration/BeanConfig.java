package ru.nsu.burym.game_plugins_service.configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.api.model.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.net.URI;
import java.util.Objects;

@Configuration
public class BeanConfig {
    private final Environment environment;

    @Autowired
    public BeanConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DockerClient dockerClient() {
        DefaultDockerClientConfig.Builder config
                = DefaultDockerClientConfig.createDefaultConfigBuilder();
        String dockerHost = Objects.requireNonNull(environment.getProperty("dockerHostUrl"));

        OkDockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(URI.create(dockerHost)) // или tcp://<host>:<port> для TCP
                .connectTimeout(5000)
                .readTimeout(5000)
                .build();

        // Создание Docker клиента
        return DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    @Bean
    public AuthConfig authConfig() {
        return new AuthConfig()
                .withUsername(environment.getProperty("dockerUsername"))
                .withUsername(environment.getProperty("dockerPassword"));
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        return loggingFilter;
    }
}
