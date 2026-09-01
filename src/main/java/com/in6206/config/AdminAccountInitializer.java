package com.in6206.config;

import com.in6206.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final AdminProperties properties;
    private final UserService userService;

    public AdminAccountInitializer(AdminProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (properties.isEmpty()) {
            log.info("Administrator initialization is disabled; set ADMIN_USERNAME, ADMIN_EMAIL and ADMIN_PASSWORD to enable it");
            return;
        }

        properties.validate();
        userService.ensureAdministrator(
                properties.username(),
                properties.email(),
                properties.password()
        );
        log.info("Administrator account is ready: {}", properties.username());
    }
}
