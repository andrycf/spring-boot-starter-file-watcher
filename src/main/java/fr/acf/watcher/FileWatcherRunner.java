package fr.acf.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class FileWatcherRunner  implements FileChangeListener{

    final FileWatcherProperties fileWatcherProperties;
    final List<FileWatcherListener> fileWatcherListeners;

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        for(ChangedFiles changedFiles : changeSet){
            for(ChangedFile changedFile : changedFiles){
                if( 
                    isType(changedFile.getType()) && 
                    !isLocked(changedFile.getFile().toPath())
                ){
                    File file = changedFile.getFile();
                    fileWatcherListeners
                    .stream().filter(fileWatcherListener -> fileWatcherListener.filterByFilename(file.getName()))
                    .forEach(fileWatcherListener -> fileWatcherListener.onChange(file,FIleWatcherType.valueOf(changedFile.getType().name())));
                }
            }
        }
    }

    private boolean isLocked(Path path) {
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.WRITE); FileLock lock = ch.tryLock()) {
            return lock == null;
        } catch (IOException e) {
            return true;
        }
    }
    
    private boolean isType(Type type) {
        return fileWatcherProperties.getTypes().contains(type);
    }
}
