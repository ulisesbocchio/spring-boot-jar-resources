package com.github.ulisesbocchio;

import com.github.ulisesbocchio.jar.resources.JarResourceLoader;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

@SpringBootApplication
@Slf4j
public class SpringBootJarResourcesDemoApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext appContext;

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(SpringBootJarResourcesDemoApplication.class)
            .resourceLoader(new JarResourceLoader())
            .build()
            .run();
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("RUNNING DEMO APPLICATION");
        Resource resource = appContext.getResource("classpath:/application.properties");
        log.info("Loaded resource: {}", resource.toString());
        log.info("Resource exists: {}", resource.exists());
        log.info("Resource File: {}", resource.getFile().getAbsolutePath());
        log.info("STOPPING DEMO APPLICATION");
    }
}
