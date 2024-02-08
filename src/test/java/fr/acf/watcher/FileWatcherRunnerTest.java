package fr.acf.watcher;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.FileCopyUtils;

class FileWatcherRunnerTest {

    private final File DIRECTORY = new File("test");
    private ApplicationContextRunner applicationContextRunner = new ApplicationContextRunner();
    private static int fileWatcherListenerCount;

    @AfterEach
    void clean(){
        removeAllDirectory();
    }

    @BeforeEach
    void init(){
        removeAllDirectory();
        fileWatcherListenerCount = 0;
    }

    @Test
    void whenShouldRunningAutoConfiguration(){
        applicationContextRunner
        .withUserConfiguration(MyApplicationTest.class,FileWatcherAutoConfiguration.class)
        .withPropertyValues("application.file.watch.directories="+DIRECTORY.getAbsolutePath())
        .run(context -> {
            assertTrue(context.containsBean("fileSystemWatcher"));
            assertNotNull(context.getBean(FileChangeListener.class));
            var prop = context.getBean(FileWatcherProperties.class);
            assertEquals(true, prop.isDaemon());
            assertEquals(1000, prop.getPollInterval());
            assertEquals(400, prop.getQuietPeriod());
            assertEquals(List.of(DIRECTORY.getAbsolutePath()), prop.getDirectories());
            assertIterableEquals(List.of(Type.ADD,Type.DELETE,Type.MODIFY), prop.getTypes());
            assertTrue(DIRECTORY.exists());
            var file = createNewFile("file.txt");
            Thread.sleep(5000);
            touch(file,"Hello");
            Thread.sleep(5000);
            assertEquals(fileWatcherListenerCount, 2);
        });
    }

    @Test
    void whenNotFoundClassFileSystemWatcher(){
        applicationContextRunner
        .withUserConfiguration(MyApplicationTest.class,FileWatcherAutoConfiguration.class)
        .withClassLoader(new FilteredClassLoader(FileSystemWatcher.class))
        .run(context -> {
            assertFalse(context.containsBean("fileSystemWatcher"));
        });
    }

    @Test
    void whenNotFoundBeanForFileWatcherListener(){
        applicationContextRunner
        .withUserConfiguration(FileWatcherAutoConfiguration.class)
        .run(context -> {
            assertFalse(context.containsBean("fileSystemWatcher"));
        });
    }

    private File createNewFile(String filename) throws IOException{
        var file = new File(DIRECTORY,"file.txt");
        file.createNewFile();
        return file;
    }

    private void touch(File file,String body) throws IOException{
        FileCopyUtils.copy(body.getBytes(), file);
    }

    private void removeAllDirectory(){
        if(DIRECTORY.listFiles() != null) 
            Arrays.asList(DIRECTORY.listFiles()).forEach(file -> file.deleteOnExit());
        DIRECTORY.deleteOnExit();
    }


    @AutoConfiguration
    @PropertySource("classpath:/application.properties")
    private static class MyApplicationTest{
        @Bean
        FileWatcherListener listener(){
            return (file,type) -> {
                fileWatcherListenerCount++;
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
