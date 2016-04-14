package com.github.ulisesbocchio.jar.resources;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author Ulises Bocchio
 */
public class JarResourceLoader implements ResourceLoader {

    private ResourceLoader delegate = new DefaultResourceLoader();
    private String extractPath;

    public JarResourceLoader(String extractPath) {
        this.extractPath = extractPath;
    }

    public JarResourceLoader() {
    }

    @Override
    public Resource getResource(String location) {
        Resource resource = delegate.getResource(location);
        return new JarResource(resource, extractPath);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    public void setDelegate(ResourceLoader delegate) {
        this.delegate = delegate;
    }
}
