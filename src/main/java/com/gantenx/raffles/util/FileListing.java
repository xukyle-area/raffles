package com.gantenx.raffles.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileListing {
    public static List<String> getPaths(String path) {
        List<String> filePaths = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        for (File file : files) {
            filePaths.add(file.getAbsolutePath());
        }
        return filePaths;
    }
}
