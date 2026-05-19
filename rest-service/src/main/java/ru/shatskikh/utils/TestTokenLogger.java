package ru.shatskikh.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.shatskikh.security.JwtService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestTokenLogger implements CommandLineRunner {

    private final JwtService jwtService;

    @Override
    public void run(String... args) throws Exception {

        String testToken = jwtService.generateToken(1021965324L,1L);

        log.info("==============================================");
        log.info("ТЕСТОВЫЙ JWT ТОКЕН ДЛЯ POSTMAN:");
        log.info("Bearer {}", testToken);
        log.info("==============================================");

    }
}
