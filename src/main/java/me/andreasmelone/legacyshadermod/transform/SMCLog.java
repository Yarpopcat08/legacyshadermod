package me.andreasmelone.legacyshadermod.transform;

import java.util.Calendar;
import java.util.logging.*;

public abstract class SMCLog {
    public static final String LOG_NAME = "SMC";
    public static final Logger logger = new SMCLogger(LOG_NAME);
    public static final Level SMCINFO = new SMCLevel("INF", 850);
    public static final Level SMCCONFIG = new SMCLevel("CFG", 840);
    public static final Level SMCFINE = new SMCLevel("FNE", 830);
    public static final Level SMCFINER = new SMCLevel("FNR", 820);
    public static final Level SMCFINEST = new SMCLevel("FNT", 810);

    public static void log(Level level, String format, Object... args) {
        if (logger.isLoggable(level)) {
            logger.log(level, String.format(format, args));
        }
    }

    public static void severe(String format, Object... args) {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.log(Level.SEVERE, String.format(format, args));
        }
    }

    public static void warning(String format, Object... args) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, String.format(format, args));
        }
    }

    public static void info(String format, Object... args) {
        if (logger.isLoggable(SMCINFO)) {
            logger.log(SMCINFO, String.format(format, args));
        }
    }

    public static void config(String format, Object... args) {
        if (logger.isLoggable(SMCCONFIG)) {
            logger.log(SMCCONFIG, String.format(format, args));
        }
    }

    public static void fine(String format, Object... args) {
        if (logger.isLoggable(SMCFINE)) {
            logger.log(SMCFINE, String.format(format, args));
        }
    }

    public static void finer(String format, Object... args) {
        if (logger.isLoggable(SMCFINER)) {
            logger.log(SMCFINER, String.format(format, args));
        }
    }

    public static void finest(String format, Object... args) {
        if (logger.isLoggable(SMCFINEST)) {
            logger.log(SMCFINEST, String.format(format, args));
        }
    }

    private static class SMCFormatter extends Formatter {
        int tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();

        SMCFormatter() {
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            int time = (int) ((record.getMillis() + this.tzOffset) % 86400000L);
            sb.append("[")
                    .append(time / 36000000)
                    .append(time / 3600000 % 10)
                    .append(time / 600000 % 6)
                    .append(time / 60000 % 10)
                    .append(time / 10000 % 6)
                    .append(time / 10000 % 10)
                    .append(".")
                    .append(time / 100 % 10)
                    .append(time / 10 % 10)
                    .append(time % 10)
                    .append(" ")
                    .append(record.getLoggerName())
                    .append(" ")
                    .append(record.getLevel())
                    .append("]")
                    .append(record.getMessage())
                    .append("\n");
            return sb.toString();
        }
    }

    private static class SMCLevel extends Level {
        private SMCLevel(String name, int value) {
            super(name, value);
        }
    }

    private static class SMCLogger extends Logger {
        SMCLogger(String name) {
            super(name, null);
            this.setUseParentHandlers(false);
            Formatter formatter = new SMCFormatter();
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(formatter);
            this.addHandler(handler);
            this.setLevel(Level.ALL);
        }
    }
}
