package com.github.ulisesbocchio.jar.resources;

import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Optional;

/**
 * @author Ulises Bocchio
 */
public class JarResourceLoader implements ResourceLoader {

    private ResourceLoader delegate = new DefaultResourceLoader();
    private String extractPath;
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
        String resolvedExtractPath = resolveExtractPath();
        return new JarResource(resource, resolvedExtractPath);
    }

    private String resolveExtractPath() {
        return Optional.ofNullable(environment)
                .map(env -> environment.getProperty(propertyName))
                .orElse(extractPath);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public void setDelegate(ResourceLoader delegate) {
        this.delegate = delegate;
    }
}
