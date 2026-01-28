package com.gantenx.raffles.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileListing {

    public static List<String> getFlinkJars() {
        List<String> jars = new ArrayList<>();
        // 1. 加入主程序JAR
        File mainJar = new File("target/raffles-1.0-SNAPSHOT.jar");
        if (mainJar.exists())
            jars.add(mainJar.getAbsolutePath());
        // 2. 加入lib目录下所有JAR
        File libDir = new File("target/lib");
        if (libDir.exists() && libDir.isDirectory()) {
            for (File f : libDir.listFiles((dir, name) -> name.endsWith(".jar"))) {
                jars.add(f.getAbsolutePath());
            }
        }
        jars.forEach(j -> log.info("lunching jar: {}", j));
        return jars;
    }

    /**
     * 获取指定路径下的文件列表
     * 支持从文件系统和classpath中读取
     */
    public static List<String> getPaths(String path) {
        List<String> filePaths = new ArrayList<>();

        try {
            // 首先尝试从classpath中读取
            List<String> classpathPaths = getPathsFromClasspath(path);
            if (!classpathPaths.isEmpty()) {
                log.info("Found {} files in classpath: {}", classpathPaths.size(), path);
                return classpathPaths;
            }

            // 如果classpath中没有找到，则尝试从文件系统读取
            List<String> filesystemPaths = getPathsFromFilesystem(path);
            if (!filesystemPaths.isEmpty()) {
                log.info("Found {} files in filesystem: {}", filesystemPaths.size(), path);
                return filesystemPaths;
            }

            log.warn("No files found in path: {}", path);

        } catch (Exception e) {
            log.error("Error reading files from path: {}", path, e);
        }

        return filePaths;
    }

    /**
     * 从classpath中读取文件路径
     */
    private static List<String> getPathsFromClasspath(String path) {
        List<String> filePaths = new ArrayList<>();

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            // 尝试多种资源路径模式
            String[] patterns = {"classpath:" + path + "/*.jar", "classpath*:" + path + "/*.jar",
                    "classpath:/" + path + "/*.jar", "classpath*:/" + path + "/*.jar"};

            for (String pattern : patterns) {
                log.debug("Trying pattern: {}", pattern);
                try {
                    Resource[] resources = resolver.getResources(pattern);

                    if (resources.length > 0) {
                        log.info("Found {} resources with pattern: {}", resources.length, pattern);

                        for (Resource resource : resources) {
                            if (resource.exists()) {
                                try {
                                    // 对于JAR内的资源，我们需要特殊处理
                                    String resourcePath = resource.getURL().toString();

                                    // 如果是file:协议，转换为本地文件路径
                                    if (resourcePath.startsWith("file:")) {
                                        resourcePath = resource.getFile().getAbsolutePath();
                                    }

                                    filePaths.add(resourcePath);
                                    log.debug("Found classpath resource: {}", resourcePath);
                                } catch (Exception e) {
                                    log.debug("Could not get URL for resource: {}", resource.getDescription());
                                    // 尝试获取文件路径
                                    try {
                                        File file = resource.getFile();
                                        filePaths.add(file.getAbsolutePath());
                                    } catch (Exception ex) {
                                        log.debug("Could not get file path for resource: {}",
                                                resource.getDescription());
                                    }
                                }
                            }
                        }

                        if (!filePaths.isEmpty()) {
                            break; // 找到文件后就退出循环
                        }
                    }
                } catch (Exception e) {
                    log.debug("Pattern {} failed: {}", pattern, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.debug("Could not read from classpath: {}", path, e);
        }

        return filePaths;
    }

    /**
     * 从文件系统中读取文件路径
     */
    private static List<String> getPathsFromFilesystem(String path) {
        List<String> filePaths = new ArrayList<>();

        // 尝试多种路径解析方式
        String[] possiblePaths = {path, "src/main/resources/" + path, System.getProperty("user.dir") + "/" + path,
                System.getProperty("user.dir") + "/src/main/resources/" + path};

        for (String possiblePath : possiblePaths) {
            File directory = new File(possiblePath);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".jar"));
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        filePaths.add(file.getAbsolutePath());
                        log.debug("Found filesystem file: {}", file.getAbsolutePath());
                    }
                    break; // 找到文件后就退出循环
                }
            }
        }

        return filePaths;
    }
}
