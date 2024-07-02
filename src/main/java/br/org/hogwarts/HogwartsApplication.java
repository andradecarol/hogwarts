package br.org.hogwarts;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

@EnableMongock
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"br.org.hogwarts"})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class HogwartsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HogwartsApplication.class, args);
    }

}
