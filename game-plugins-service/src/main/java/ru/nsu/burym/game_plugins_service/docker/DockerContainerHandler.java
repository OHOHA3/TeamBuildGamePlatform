package ru.nsu.burym.game_plugins_service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotModifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@Controller
public class DockerContainerHandler {

    private final DockerClient dockerClient;

    @Autowired
    public DockerContainerHandler(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String createContainer(String imageName, String webSocketUrl) {
        try {
            System.out.println("Pulling image: " + imageName);
            dockerClient.pullImageCmd(imageName)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);

            System.out.println("Creating and starting container...");
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    //.withHostConfig(HostConfig.newHostConfig())
                    .withEnv("WEBSOCKET_URL", webSocketUrl)
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
