package it.schoolboard.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class AppApplication {

    protected static final Logger parentLogger = LogManager.getLogger();

    // entrypoint
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // prints the authentication server in the logs
    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            parentLogger.info("AUTH SERVER IS " + environment.getProperty("keycloak.auth-server-url"));
        };
    }

}
