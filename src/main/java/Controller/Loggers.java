package Controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loggers {
    // The loggers:
    private static final Logger requestLogger = LoggerFactory.getLogger("request-logger");
    private static final Logger todoLogger = LoggerFactory.getLogger("todo-logger");

    // Server's request counter:
    private static int requestsCounter = 0;

    // Loggers Methods:

    public void createRequestLogInfo(HttpServletRequest request)
    {
        requestsCounter++;
        requestLogger.info("Incoming request | #{} | resource: {} | HTTP Verb {} | request #{}",requestsCounter , request.getRequestURI(), request.getMethod(), requestsCounter);
    }

    public void createRequestLogDebug(long duration)
    {
        requestLogger.debug("request #{} duration: {} ms | request #{}",requestsCounter, duration, requestsCounter);
    }

    public void writeCreateTodoIntoLogger(String title, int todosCounter)
    {
        todoLogger.info("Creating new TODO with Title [{}] | request #{}",title, requestsCounter);
        todoLogger.debug("Currently there are {} TODOs in the system. New TODO will be assigned with id {} | request #{}",todosCounter, todosCounter+1, requestsCounter);
    }

    public void writeTodosCountIntoLogger(String status, int count)
    {
        todoLogger.info("Total TODOs count for state {} is {} | request #{}",status, count, requestsCounter);
    }

    public void writeTodosDataIntoLogger(String status, String sortBy, int numOfTodos, int todosSize)
    {
        todoLogger.info("Extracting todos content. Filter: {} | Sorting by: {} | request #{}",status, sortBy, requestsCounter);
        todoLogger.debug("There are a total of {} todos in the system. The result holds {} todos | request #{}",todosSize, numOfTodos, requestsCounter);
    }

    public void writeUpdateTodoStatusBeforeUptade(int id, String status)
    {
        todoLogger.info("Update TODO id [{}] state to {} | request #{}",id, status, requestsCounter);
    }

    public void writeUpdateTodoStatusAfterUptade(int id, String status, String oldStatus)
    {
        todoLogger.debug("Todo id [{}] state change: {} --> {} | request #{}",id, oldStatus, status, requestsCounter);
    }

    public void writeDeleteTodoIntoLogger(int id, int todosSize)
    {
        todoLogger.info("Removing todo id {} | request #{}",id, requestsCounter);
        todoLogger.debug("After removing todo id [{}] there are {} TODOs in the system | request #{}",id, todosSize, requestsCounter);
    }

    public void generalTodoLoggerError(String message)
    {
        todoLogger.error("{} | request #{}",message, requestsCounter);
    }

    public String getLoggerLevel(String loggerName)
    {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(loggerName);
        return logger.getLevel().toString();
    }

    public void setLoggerLevel(String loggerName, String loggerLevel)
    {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger(loggerName);
        logger.setLevel(Level.toLevel(loggerLevel));
    }

    public boolean checkULL(String loggerName, String loggerLevel)
    {
        if(!loggerName.equals("request-logger") && !loggerName.equals("todo-logger"))
        {
            return true;
        }

        if(!loggerLevel.equals("INFO") && !loggerLevel.equals("DEBUG") && !loggerLevel.equals("ERROR"))
        {
            return true;
        }

        return false;
    }
}
