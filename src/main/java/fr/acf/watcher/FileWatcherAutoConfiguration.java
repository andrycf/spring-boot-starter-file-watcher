package fr.acf.watcher;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AutoConfiguration
@ConditionalOnBean(FileWatcherListener.class)
@EnableConfigurationProperties(FileWatcherProperties.class)
@Import(FileWatcherRunner.class)
@RequiredArgsConstructor
@Slf4j
class FileWatcherAutoConfiguration {

    private final FileWatcherProperties props;
    private final FileChangeListener fileChangeListener;

    @Bean
    FileSystemWatcher fileSystemWatcher(){
        var watcher = new FileSystemWatcher(props.isDaemon(), ofDuration(props.getPollInterval()), ofDuration(props.getQuietPeriod()));
        watcher.addSourceDirectory(ofSourceDirectory());
        watcher.addListener(fileChangeListener);
        watcher.start();
        return watcher;
    }

    @PreDestroy
    void onDestroy(){
        fileSystemWatcher().stop();
    }

    private Duration ofDuration(Long value){
        return Duration.ofMillis(value);
    }

    private File ofSourceDirectory(){
        File sourceDirectory = Path.of(props.getDirectory()).toFile();
        if(!sourceDirectory.exists()) sourceDirectory.mkdirs();
        log.info("Using directory '{}' for watching the files.",sourceDirectory.getAbsolutePath());
        return sourceDirectory;
    }
}
