package ru.nsu.burym.game_plugins_service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class DockerContainerHandler {

    private final DockerClient dockerClient;

    private final AuthConfig authConfig;

    @Autowired
    public DockerContainerHandler(DockerClient dockerClient, AuthConfig authConfig) {
        this.dockerClient = dockerClient;
        this.authConfig = authConfig;
    }

    public String createContainer(String imageName, String roomId) {
        try {
            boolean isPresent = false;
            List<Image> images = dockerClient.listImagesCmd().exec();
            for (Image image : images) {
                String[] repoTags = image.getRepoTags();
                if (Arrays.asList(repoTags).contains(imageName)) {
                    isPresent = true;
                    break;
                }
            }
            if (!isPresent) {
                System.out.println("Pulling image: " + imageName);
                dockerClient.pullImageCmd(imageName)
                        .withAuthConfig(authConfig)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion(60, TimeUnit.SECONDS);
            }
            System.out.println("Creating and starting container...");
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    //.withHostConfig(HostConfig.newHostConfig())
                    // hostPort:containerPort
                    .withPortSpecs("5000:5000", "3000:3000") //todo переделать на случайный порт хоста
                    .withEnv("ROOM_ID", roomId)
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            System.out.println("Container started successfully! ID: " + container.getId());
            return container.getId();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopAndRemoveContainer(@PathVariable String containerId) {

        System.out.println("Stopping container: " + containerId);
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotModifiedException ignored) {}
        System.out.println("Container stopped successfully!");
        dockerClient.removeContainerCmd(containerId).exec();
    }

    @Deprecated
    @GetMapping("/docker/create/{imageName}")
    public void createContainerApi(@PathVariable String imageName) {
        try {
            System.out.println("Pulling image: " + imageName);
            dockerClient.pullImageCmd(imageName)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);

            System.out.println("Creating and starting container...");
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    //.withHostConfig(HostConfig.newHostConfig())
                    .withEnv("websocket.url", "ws://localhost:8080/websocket")
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            System.out.println("Container started successfully! ID: " + container.getId());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @GetMapping("/docker/stop/{containerId}")
    public void stopAndRemoveContainerApi(@PathVariable String containerId) {

        System.out.println("Stopping container: " + containerId);
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotModifiedException ignored) {}
        System.out.println("Container stopped successfully!");
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
