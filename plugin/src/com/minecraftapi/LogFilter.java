package com.minecraftapi;

/**
 * A custom log filter for prepending plugin identifier on all log messages.
 * 
 * @author frelling
 */

import java.util.logging.Filter;
import java.util.logging.LogRecord;

class LogFilter implements Filter
{

    private String prefix;

    /**
     * Constructs a log filter that prepends the given prefix on all log
     * messages.
     * 
     * @param prefix
     */
    public LogFilter(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Always returns true, but adds prefix to log message.
     */
    public boolean isLoggable(LogRecord record)
    {
        record.setMessage(prefix + record.getMessage());
        return true;
    }
}