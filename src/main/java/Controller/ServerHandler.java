package Controller;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import java.lang.*;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;
import ch.qos.logback.classic.LoggerContext;

@RestController
public class ServerHandler {

    // DataBase:
    ServerDbAndLogic DB = new ServerDbAndLogic();

    // Loggers, there are 2 loggers;
    // 'request-logger' - receive data about the server requests.
    // 'to-do-logger' - receive data about the database.
    Loggers loggers = new Loggers();

    // Json object:
    private JSONObject result;


    // Return "OK" when reaching this endpoint.
    @GetMapping("/todo/health")
    public ResponseEntity<String> healthReturn(HttpServletRequest request) {
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);
        timeOfEnd = System.currentTimeMillis();
        loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    // This endpoint receives json object with parameters, and create a to-do with these parameters.
    @PostMapping(value = "/todo", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createTodo(@RequestBody JsonNode json, HttpServletRequest request)
    {
        String reqTitle = json.get("title").asText();
        String reqContent = json.get("content").asText();
        long reqDueDate = json.get("dueDate").asLong();
        result = new JSONObject();

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        if(DB.isTitleExists(reqTitle)) // in case title is invalid
        {
            String message = "Error: TODO with the title '" + reqTitle + "' already exists in the system";
            result.put("errorMessage",message);
            loggers.generalTodoLoggerError(message);
            return new ResponseEntity<>(result.toString(), HttpStatus.CONFLICT);

        }
        else if (DB.isDueDateNotValid(reqDueDate)) // in case due date is in the past.
        {
            String message = "Error: Can't create new TODO that its due date is in the past";
            result.put("errorMessage",message);
            loggers.generalTodoLoggerError(message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.toString());
        }
        loggers.writeCreateTodoIntoLogger(reqTitle, DB.getTodosCounter());
        DB.createTodo(reqTitle,reqContent,reqDueDate);
        result.put("result", DB.getLastElement().getId());
        timeOfEnd = System.currentTimeMillis();
        loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
        return new ResponseEntity<>(result.toString(),HttpStatus.OK);
    }

    // This endpoint returns the number of to-do's with the given status in the database
    @GetMapping("/todo/size")
    public ResponseEntity<Object> returnTodosCount(@RequestParam(name = "status") String status,
                                                   @RequestParam(name = "persistenceMethod") String persistenceMethod,
                                                   HttpServletRequest request)
    {
        result = new JSONObject();
        int count;

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        if(!persistenceMethod.equals("POSTGRES") && !persistenceMethod.equals("MONGO")){
            String message = "Error: The given persistence method is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        count = DB.countTodos(status); // return -1 if the status is invalid.
        if(count == -1)
        {
            String message = "Error: The given status is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }
        else {
            result.put("result", count);
            loggers.writeTodosCountIntoLogger(status, count);
            timeOfEnd = System.currentTimeMillis();
            loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        }

    }

    // This endpoint return a list of to-do's data with the given status, and sorted by ID/Title/Due date.
    // If the endpoint doesn't receive a 'sortby' operator, the data will be sorted by ID.
    @GetMapping("/todo/content")
    public ResponseEntity<Object> returnTodosData(@RequestParam(name = "status") String status,
                                                  @RequestParam(name = "sortBy", required = false) String sortBy,
                                                  @RequestParam(name = "persistenceMethod") String persistenceMethod,
                                                  HttpServletRequest request)
    {
        result = new JSONObject();

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        if(!persistenceMethod.equals("POSTGRES") && !persistenceMethod.equals("MONGO")){
            String message = "Error: The given persistence method is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        if(DB.checkStatusAndSortBy(status,sortBy)) // check is the requested params are valid.
        {
            String message = "Error: The given status or sortBy are invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        JSONArray resultArr = DB.createJsonArray(sortBy,status); // creating json array.
        result.put("result", resultArr);
        loggers.writeTodosDataIntoLogger(status,sortBy,resultArr.length(), DB.getTodosSize());
        timeOfEnd = System.currentTimeMillis();
        loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
        return new ResponseEntity<>(result.toString(),HttpStatus.OK);

    }

    // This endpoint receives an id of to-do, and update its status to the given one.
    @PutMapping("/todo")
    private ResponseEntity<Object> updateTodoStatus(@RequestParam (name = "id") int id,
                                                    @RequestParam (name = "status") String status,
                                                    HttpServletRequest request)
    {
        JSONObject result = new JSONObject();

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        loggers.writeUpdateTodoStatusBeforeUptade(id,status);

        if(!DB.isGivenStatusValid(status)) // status is not valid
        {
            String message = "Error: The given status is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        String oldStatus = DB.isIdExistsAndUpdate(id, status); // return the old status, if to-do not exist, function will return null.
        if(oldStatus != null)
        {
            result.put("result", oldStatus);
            loggers.writeUpdateTodoStatusAfterUptade(id,status,oldStatus);
            timeOfEnd = System.currentTimeMillis();
            loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        }
        else // The to-do not found.
        {
            String message = "Error: no such TODO with id " + id;
            result.put("errorMessage",message);
           loggers.generalTodoLoggerError(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.toString());
        }
    }

    // This endpoint receives an id of to-do, and remove this to-do from the database.
    @DeleteMapping("/todo")
    private ResponseEntity<Object> deleteTodo(@RequestParam (name = "id") int id, HttpServletRequest request)
    {
        JSONObject result = new JSONObject();
        int index, newSize;

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        index = DB.isIdExists(id);
        if(index != -1) // check if to-do exist, index = -1 if to-do not exist.
        {
            loggers.writeDeleteTodoIntoLogger(id, DB.getTodosSize());
            newSize = DB.removeTodo(index);
            result.put("result", newSize);
            timeOfEnd = System.currentTimeMillis();
            loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        }
        else
        {
            String message = "Error: no such TODO with id " + id;
            result.put("errorMessage",message);
            loggers.generalTodoLoggerError(message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.toString());
        }
    }

    // This endpoint receives a logger name, and returns his current level.
    @GetMapping("/logs/level")
    private String getLoggerCurrentLevel(@RequestParam (name = "logger-name") String loggerName, HttpServletRequest request)
    {
        String level;

        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        if(!loggerName.equals("request-logger") && !loggerName.equals("todo-logger"))
        {
            return ("Failure: no such log file exists");
        }

        level = loggers.getLoggerLevel(loggerName);
        timeOfEnd = System.currentTimeMillis();
        loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
        return ("Success: " + level.toUpperCase());
    }

    // This endpoint receives a logger name and a logger level, and change his level to the given level.
    @PutMapping("/logs/level")
    private String updateLoggerLevel(@RequestParam (name = "logger-name") String loggerName, @RequestParam (name = "logger-level") String loggerLevel, HttpServletRequest request)
    {
        // logger:
        long timeOfEnd;
        long timeOfStart = System.currentTimeMillis();
        loggers.createRequestLogInfo(request);

        if(loggers.checkULL(loggerName,loggerLevel))
        {
            return ("Failure: no such log file exists or invalid level");
        }

        loggers.setLoggerLevel(loggerName,loggerLevel);
        timeOfEnd = System.currentTimeMillis();
        loggers.createRequestLogDebug(timeOfEnd - timeOfStart);
        return ("Success: " + loggerLevel.toUpperCase());
    }
}



