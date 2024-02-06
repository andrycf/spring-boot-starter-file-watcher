package fr.acf.watcher;

public enum FIleWatcherType {
    /**
     * A new file has been added.
     */
    ADD,

    /**
     * An existing file has been modified.
     */
    MODIFY,

    /**
     * An existing file has been deleted.
     */
    DELETE
}
