import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
This is the logger class that creates a logfile1.log to log all the messages.
It uses the filehandler class and the simpleFormatter for formatting the logs
 */
public class MyLogger {
    private static final Logger logger = Logger.getLogger(MyLogger.class.getName());

    static {
        try {
            // Create a FileHandler to write log messages to a file
            FileHandler fileHandler = new FileHandler(System.getProperty("user.dir")+"/logfile.log");
            // Create a SimpleFormatter for the FileHandler
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            // Add the FileHandler to the logger
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
