package com.docker.demo;

//import com.spotify.docker.client.DefaultDockerClient;
//import com.spotify.docker.client.DockerClient;
//import com.spotify.docker.client.exceptions.DockerCertificateException;
//import com.spotify.docker.client.exceptions.DockerException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@RestController
class HelloDockerRestController {

    @RequestMapping("/hello")
    public void helloDocker() {

        // Connect to docker deamon
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        System.out.println("connection --> " + dockerClient.pingCmd());

        // Pull image
        // check for duplicate container
        List<Container> containers = dockerClient.listContainersCmd().exec();
        for (Container c : containers) {
            System.out.println("container image name--> " + c.getImage());
            System.out.println("container name --> " + c.getNames().toString());
        }
        try {
            // create two containers and link each other
            // mount the container with volume
//        Volume volume1 = new Volume("/opt/app");

//            CreateContainerResponse mongo = dockerClient.createContainerCmd("mongo")
//                    .withCmd("--bind_ip_all")
//                    .withName("mongo")
//                    .withPortBindings(PortBinding.parse("9999:27017"))
//                    //                    .withBinds(Bind.parse("/Users/baeldung/mongo/data/db:/data/db"))
//                    .withPublishAllPorts(true)
//                    .exec();
//            dockerClient.startContainerCmd(mongo.getId()).exec();

            // exposed port
            ExposedPort tcp9080 = ExposedPort.tcp(9080);
            Ports portBindings = new Ports();
            portBindings.bind(tcp9080, Ports.Binding.bindPort(8081));
            CreateContainerResponse springboot = dockerClient.createContainerCmd("mahima14/docker-trial-v1:0.0.0-SNAPSHOT")
                    .withCmd("sleep", "9999")
                    .withName("springboot-port-mapping-v2")
                    .withExposedPorts(tcp9080)
                    .withPortBindings(portBindings)
//                    .withLinks(new Link("mongo", "mongoLink"))
                    .withPublishAllPorts(true)
                    .exec();
            dockerClient.startContainerCmd(springboot.getId()).exec();
        } catch (ConflictException ce) {
            System.out.println("conatainer with name sprinboot already exist");
        }

    }
}
