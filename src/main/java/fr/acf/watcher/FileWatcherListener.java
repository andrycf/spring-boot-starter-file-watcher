package fr.acf.watcher;

import java.io.File;

@FunctionalInterface
public interface FileWatcherListener {
    void onChange(File file,FIleWatcherType type);
    default boolean filterByFilename(String filename){
        return true;
    }
}
