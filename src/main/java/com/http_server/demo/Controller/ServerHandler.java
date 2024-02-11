package com.http_server.demo.Controller;

import com.fasterxml.jackson.databind.JsonNode;

import java.lang.*;

import com.http_server.demo.service.TodoMongoService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.http_server.demo.service.TodoPostgresService;


@RestController
public class ServerHandler {

    // Server logic:
    private final ServerLogic logic = new ServerLogic();
    // DataBases:
    @Autowired
    private TodoPostgresService todoPostgreService;
    @Autowired
    private TodoMongoService todoMongoService;
    private boolean isLogicInitWithServices = false;
    // Json object:
    private JSONObject result;

    // Return "OK" when reaching this endpoint.
    @GetMapping("/todo/health")
    public ResponseEntity<String> healthReturn(HttpServletRequest request) {
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
        setServices();

        if(logic.isTitleExists(reqTitle)) // in case title is invalid
        {
            String message = "Error: TODO with the title '" + reqTitle + "' already exists in the system";
            result.put("errorMessage",message);
            return new ResponseEntity<>(result.toString(), HttpStatus.CONFLICT);
        }
        else if (logic.isDueDateNotValid(reqDueDate)) // in case due date is in the past.
        {
            String message = "Error: Can't create new TODO that its due date is in the past";
            result.put("errorMessage",message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result.toString());
        }
        logic.createTodo(reqTitle,reqContent,reqDueDate);
        result.put("result", logic.getLastElement().getRawid());
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
        setServices();

        if(!persistenceMethod.equals("POSTGRES") && !persistenceMethod.equals("MONGO")){
            String message = "Error: The given persistence method is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        count = logic.countTodos(status, persistenceMethod); // return -1 if the status is invalid.
        if(count == -1)
        {
            String message = "Error: The given status is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }
        else {
            result.put("result", count);
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
        setServices();

        if(!persistenceMethod.equals("POSTGRES") && !persistenceMethod.equals("MONGO")){
            String message = "Error: The given persistence method is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        if(logic.checkStatusAndSortBy(status,sortBy)) // check is the requested params are valid.
        {
            String message = "Error: The given status or sortBy are invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        JSONArray resultArr = logic.createJsonArray(sortBy,status, persistenceMethod); // creating json array.
        result.put("result", resultArr);
        return new ResponseEntity<>(result.toString(),HttpStatus.OK);

    }

    // This endpoint receives an id of to-do, and update its status to the given one.
    @PutMapping("/todo")
    private ResponseEntity<Object> updateTodoStatus(@RequestParam (name = "id") int id,
                                                    @RequestParam (name = "status") String status,
                                                    HttpServletRequest request)
    {
        JSONObject result = new JSONObject();
        setServices();

        if(!logic.isGivenStatusValid(status)) // status is not valid
        {
            String message = "Error: The given status is invalid.";
            result.put("errorMessage",message);
            return ResponseEntity.badRequest().body(result.toString());
        }

        String oldStatus = logic.updateStatus(id, status); // return the old status, if to-do not exist, function will return null.
        if(oldStatus != null)
        {
            result.put("result", oldStatus);
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        }
        else // The to-do not found.
        {
            String message = "Error: no such TODO with id " + id;
            result.put("errorMessage",message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.toString());
        }
    }

    // This endpoint receives an id of to-do, and remove this to-do from the database.
    @DeleteMapping("/todo")
    private ResponseEntity<Object> deleteTodo(@RequestParam (name = "id") int id, HttpServletRequest request)
    {
        JSONObject result = new JSONObject();
        int index, newSize;
        setServices();

        index = logic.isIdExists(id);
        if(index != -1) // check if to-do exist, index = -1 if to-do not exist.
        {
            newSize = logic.removeTodo(index);
            result.put("result", newSize);
            return new ResponseEntity<>(result.toString(),HttpStatus.OK);
        }
        else
        {
            String message = "Error: no such TODO with id " + id;
            result.put("errorMessage",message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.toString());
        }
    }

    private void setServices(){
        if (!isLogicInitWithServices){
            logic.setTodosCounter(todoPostgreService.getTodosCount());
            logic.setPostgreService(todoPostgreService);
            logic.setMongoService(todoMongoService);
            isLogicInitWithServices = true;
        }
    }
}



