package fr.acf.watcher;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

class FileWatcherRunnerTest {

    private String FOLDER_NAME = "test";

    private ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner();

    @Test
    void whenNotFoundClassFileSystemWatcher(){
        applicationContextRunner
        .withUserConfiguration(FileWatcherAutoConfiguration.class)
        .withClassLoader(new FilteredClassLoader(FileSystemWatcher.class))
        .run(context -> {
            assertFalse(context.containsBean("fileSystemWatcher"));
        });
    }

    @Test
    void whenShouldRunningAutoConfiguration(){
        applicationContextRunner
        .withUserConfiguration(MyApplicationTest.class,FileWatcherAutoConfiguration.class)
        .withPropertyValues("application.file.watch.directory="+FOLDER_NAME)
        .run(context -> {
            assertTrue(context.containsBean("fileSystemWatcher"));
            assertNotNull(context.getBean(FileChangeListener.class));
            var prop = context.getBean(FileWatcherProperties.class);
            assertEquals(true, prop.isDaemon());
            assertEquals(1000, prop.getPollInterval());
            assertEquals(400, prop.getQuietPeriod());
            assertEquals(FOLDER_NAME, prop.getDirectory());
            assertIterableEquals(List.of(Type.ADD,Type.DELETE,Type.MODIFY), prop.getTypes());
        });
    }

    @AutoConfiguration
    @PropertySource("classpath:/application.properties")
    private static class MyApplicationTest{
        @Bean
        FileWatcherListener listener(){
            return (file,type) -> {

            };
        }

        @Bean
        FileWatcherListener listenerOther(){
            return (file,type) -> {

            };
        }

        @Bean
        FileChangeListener change(){
            return (changeSet) -> {

            };
        }

        @Bean
        FileChangeListener changeOther(){
            return (changeSet) -> {

            };
        }
    }

}
