package me.neovitalism.neoapi.modloading.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoModLogger {
    private final String id;
    private final Logger logger;

    public NeoModLogger(String id) {
        this.id = "[" + id + "]: ";
        this.logger = LoggerFactory.getLogger(id);
    }

    public void info(String s) {
        this.logger.info("{}{}", this.id, s);
    }

    public void warn(String s) {
        this.logger.warn("{}{}", this.id, s);
    }

    public void error(String s) {
        this.logger.error("{}{}", this.id, s);
    }
}
