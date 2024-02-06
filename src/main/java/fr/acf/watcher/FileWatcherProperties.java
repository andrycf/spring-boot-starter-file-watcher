package fr.acf.watcher;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
@Validated
@ConfigurationProperties(prefix = "application.file.watch")
class FileWatcherProperties{
    /** 
     * if a deamon thread monitors the changes. 
     * Set it to true if you want the thread (the monitoring) to be killed when the jvm is stopped
     */
    private boolean daemon;
    /**
     * time to wait between checking again for changes
     */
    private Long pollInterval;
    /**
     * time to wait after a change is detected to ensure. 
     * If you transfer large files to the directory, this will have to be taken into account to avoid file corruption
     */
    private Long quietPeriod;
    /**
     * The application will scan the directory for modifications every pollInterval ms. 
     * And the change will be trigger/propagated after quietPeriod ms.
     */
    private String directory;
    /**
     * The single file is of type ChangedFile which provides access to the File and Type of the change/event.
     */
    private List<Type> types;
    
    public FileWatcherProperties(
        @DefaultValue("true") boolean daemon, 
        @DefaultValue("1000") @Min(1) @Positive Long pollInterval, 
        @DefaultValue("400") @Positive Long quietPeriod,
        @NotBlank String directory, 
        @DefaultValue("add,delete,modify") @NotBlank List<Type> types) {

        this.daemon = daemon;
        this.pollInterval = pollInterval;
        this.quietPeriod = quietPeriod;
        this.directory = directory;
        this.types = types;

    }

    
}
