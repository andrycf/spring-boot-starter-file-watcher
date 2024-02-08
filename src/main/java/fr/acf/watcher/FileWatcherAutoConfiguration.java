package fr.acf.watcher;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AutoConfiguration
@ConditionalOnBean({FileWatcherListener.class})
@EnableConfigurationProperties(FileWatcherProperties.class)
@Import(FileWatcherRunner.class)
@RequiredArgsConstructor
@Slf4j
class FileWatcherAutoConfiguration {

    private final FileWatcherProperties props;
    private final FileChangeListener fileChangeListener;
    private FileSystemWatcher watcher;

    @Bean
    @ConditionalOnClass(FileSystemWatcher.class)
    FileSystemWatcher fileSystemWatcher(){
        this.watcher = new FileSystemWatcher(props.isDaemon(), ofDuration(props.getPollInterval()), ofDuration(props.getQuietPeriod()));
        this.watcher.addSourceDirectories(ofSourceDirectory());
        this.watcher.addListener(fileChangeListener);
        this.watcher.start();
        return this.watcher;
    }

    @PreDestroy
    void onDestroy(){
        if(this.watcher != null) this.watcher.stop();
        else {
            props.getDirectories().forEach(folder -> {
                File directory = Path.of(folder).toFile();
                log.error("For watching the directory '{}' is not started.",directory.getAbsolutePath());
            });
        }
    }

    private Duration ofDuration(Long value){
        return Duration.ofMillis(value);
    }

    private List<File> ofSourceDirectory(){
        List<File> sourceDirectory = new ArrayList<>();
        props.getDirectories().forEach(folder -> {
            File directory = Path.of(folder).toFile();
            if(!directory.exists()) directory.mkdirs();
            sourceDirectory.add(directory);
            log.info("Using directory '{}' for watching the files.",directory.getAbsolutePath());
        });
        return sourceDirectory;
    }
}
