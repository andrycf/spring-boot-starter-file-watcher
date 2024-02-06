# File Watcher Spring Boot Starters

## Downloads

Gradle:

```gradle
dependencies {
  implementation 'fr.acf:spring-boot-starter-file-watcher:0.0.1-SNAPSHOT'
}
```

Maven:

```xml
<dependency>
    <groupId>fr.acf</groupId>
    <artifactId>spring-boot-starter-file-watcher</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Exemple for Java

Use interface FileWatcherListener:

```xml
@Component
public class ExempleFileWatcherListenerImpl implements FileWatcherListener {
    ...
    
    @Override
    public void onChange(File file, FIleWatcherType type) {
        ...
        if(type == FileWatcherType.ADD) {
            // TODO ...
        }
    }

    ...
}
```

