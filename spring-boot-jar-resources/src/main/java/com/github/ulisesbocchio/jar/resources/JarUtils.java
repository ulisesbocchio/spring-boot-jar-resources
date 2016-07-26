package com.github.ulisesbocchio.jar.resources;

import com.google.common.io.Files;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Ulises Bocchio
 */
@Slf4j
public class JarUtils {

    public static File getFile(Resource resource) {
        return getFile(resource, null);
    }

    @SneakyThrows
    public static File getFile(Resource resource, String extractPath) {
        return ResourceUtils.isJarURL(resource.getURL()) ? getFromJar(resource, extractPath) : resource.getFile();
    }

    @SneakyThrows
    private static File getFromJar(Resource resource) {
        return getFromJar(resource, null);
    }

    @SneakyThrows
    private static File getFromJar(Resource resource, String extractPath) {
        FileObject file = VFS.getManager().resolveFile(maybeFixUri(resource));
        File extractDir;
        if (extractPath != null) {
            extractDir = new File(extractPath);
            FileUtils.forceMkdir(extractDir);
        } else {
            extractDir = Files.createTempDir();
        }
        log.info("EXTRACT DIR CREATED {}", extractDir.getAbsolutePath());
        return copyToDir(file, extractDir);
    }

    private static String maybeFixUri(Resource resource) throws IOException {
        String uri = resource.getURI().toString();
        int numOfJarsInResource = numbOfJars(uri);
        String jarPrefix = jarPrefix(numOfJarsInResource);
        uri = jarPrefix + uri.substring(4);
        return uri;
    }

    private static String jarPrefix(int n) {
        return IntStream.range(0, n)
            .mapToObj(num -> "jar:")
            .reduce((r, l) -> r + l)
            .orElse("jar:");
    }

    private static int numbOfJars(String uri) {
        Matcher matcher = Pattern.compile("\\.jar!").matcher(uri);
        int matches = 0;
        while (matcher.find()) {
            matches++;
        }
        return matches;
    }

    @SneakyThrows
    private static File copyToDir(FileObject jarredFile, File destination) {
        switch (jarredFile.getType()) {
            case FILE:
                return copyFileToDir(jarredFile, destination);
            case FOLDER:
                return copyDirToDir(jarredFile, destination);
            default:
                throw new IllegalStateException("File Type not supported: " + jarredFile.getType());
        }
    }

    @SneakyThrows
    private static File copyDirToDir(FileObject jarredDir, File destination) {
        File tempDir = new File(destination, jarredDir.getName().getBaseName());
        createDir(tempDir);
        Arrays.stream(jarredDir.getChildren())
            .forEach(fileObject -> copyToDir(fileObject, tempDir));
        return tempDir;
    }

    @SneakyThrows
    private static File copyFileToDir(FileObject jarredFile, File destination) {
        File tempFile = new File(destination, jarredFile.getName().getBaseName());
        createFile(tempFile);
        log.info("TEMP FILE CREATED {}", tempFile.getAbsolutePath());
        FileUtils.copyInputStreamToFile(jarredFile.getContent().getInputStream(), tempFile);
        return tempFile;
    }

    private static void createDir(File file) {
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalStateException(String.format("Could not create temp directory: %s", file.getAbsolutePath()));
        }
    }

    @SneakyThrows
    private static void createFile(File file) {
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        if (!file.createNewFile()) {
            throw new IllegalStateException(String.format("Could not create temp jarredFile: %s", file.getAbsolutePath()));
        }
    }
}
