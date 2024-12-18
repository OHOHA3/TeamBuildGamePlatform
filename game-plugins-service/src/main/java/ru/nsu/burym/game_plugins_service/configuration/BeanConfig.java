package ru.nsu.burym.game_plugins_service.configuration;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
}
