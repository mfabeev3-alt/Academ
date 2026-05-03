package ru.shatskikh.node.service.commands.service;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class RootService {

    private final String rootToken = UUID.randomUUID().toString();

    @PostConstruct
    public void init(){

        log.info("\n\n===============================================\n" +
                "SECURITY ALERT: ROOT REGISTRATION TOKEN\n" +
                "COMMAND: /iamroot:" + rootToken + "\n" +
                "===============================================\n");
    }

    public boolean verifyToken(String inputToken) {
        return rootToken.equals(inputToken);
    }

}
