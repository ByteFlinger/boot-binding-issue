package org.example.bindingIssue;

import lombok.extern.log4j.Log4j2;
import org.example.bindingIssue.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConfigurationProperties
@ConditionalOnProperty(name = "operation1")
public class Operation1 implements ApplicationRunner {

    @Autowired
    @Qualifier("proxyFactoryBean")
    OtherService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Run operation1");
        log.info("ServiceMethod: '{}'", service.someMethod());
    }
}
