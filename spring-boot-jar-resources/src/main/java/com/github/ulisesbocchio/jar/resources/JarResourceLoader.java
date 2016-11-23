package com.github.ulisesbocchio.jar.resources;

import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Optional;

/**
 * @author Ulises Bocchio
 */
@Slf4j
public class JarResourceLoader implements ResourceLoader {

    private ResourceLoader delegate = new DefaultResourceLoader();
    private String extractPath;
    private String resolvedExtractPath;
    private Environment environment;
    private String propertyName;

    public JarResourceLoader(String extractPath) {
        this.extractPath = extractPath;
    }

    public JarResourceLoader() {
    }

    public JarResourceLoader(Environment environment, String propertyName) {
        this.environment = environment;
        this.propertyName = propertyName;
    }

    @Override
    public Resource getResource(String location) {
        Resource resource = delegate.getResource(location);
        String resolvedExtractPath = getResolvedExtractPath();
        return new JarResource(resource, resolvedExtractPath);
    }

    private String getResolvedExtractPath() {
        if(resolvedExtractPath == null) {
            resolvedExtractPath = Optional.ofNullable(environment)
                .map(env -> environment.getProperty(propertyName))
                .orElse(extractPath);
            if (extractPath == null) {
                extractPath = Files.createTempDir().getAbsolutePath();
                log.debug("TEMP EXTRACT DIR CREATED {}", extractPath);
            }
            log.debug("Resolved JarResource extract path to: {}", resolvedExtractPath);
        }
        return resolvedExtractPath;
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public void setDelegate(ResourceLoader delegate) {
        this.delegate = delegate;
    }
}
