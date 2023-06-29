package Controller;
import model.Todo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ServerDbAndLogic {

    private Vector<Todo> todos;
    private static int todosCounter = 0;

    public ServerDbAndLogic()
    {
        todos = new Vector<Todo>();
    }

    public int getTodosCounter()
    {
        return todosCounter;
    }

    public int getTodosSize()
    {
        return todos.size();
    }

    public void createTodo(String title, String content, long dueDate)
    {
        Todo newTodo = new Todo();
        todosCounter++;
        newTodo.setId(todosCounter);
        newTodo.setTitle(title);
        newTodo.setContent(content);
        newTodo.setDueDate(dueDate);
        newTodo.setStatus("PENDING");
        todos.add(newTodo);

    }

    public Todo getLastElement()
    {
        return todos.lastElement();
    }

    public int countTodos(String status)
    {
        boolean flag = true;
        int res = 0;

        if(status.equals("ALL")) // return the vector size for all
        {
            return todos.size();
        }

        if(isGivenStatusValid(status)) // checking that the given staus is valid.
        {
            flag = false;
            for(Todo todo : todos)
            {
                if(todo.getStatus().equals(status))
                {
                    res++;
                }
            }
        }

        if(flag) // if the given status is invalid, return -1.
        {
            return -1;
        }

        return res;
    }

    public JSONArray createJsonArray(String sortBy, String status)
    {
        ArrayList<Todo> res;

        if(sortBy == null) // if sort by is not given, sorting by ID.
        {
            res = createAndSortTodoArr(status,"ID");
            sortBy = "ID";
        }
        else // in case sort by is given.
        {
            res = createAndSortTodoArr(status,sortBy);
        }

        return toJsonArray(res);
    }

    public boolean isGivenStatusValid(String status)
    {
        if(!status.equals("PENDING")  && !status.equals("LATE") && !status.equals("DONE"))
        {
            return false;
        }
        return true;
    }

    public String isIdExistsAndUpdate(int id, String status)
    {
        String oldStatus = null;
        for(Todo todo: todos)
        {
            if(todo.getId() == id)
            {
                oldStatus = todo.getStatus();
                todo.setStatus(status);
                return oldStatus;
            }
        }
        return oldStatus;
    }

    public int removeTodo(int index)
    {
        todos.remove(index);
        return todos.size();
    }

    // returns the index of the to-do in the vector, -1 return value for not found.
    public int isIdExists(int id)
    {
        int index =0;
        for(Todo todo: todos)
        {
            if(todo.getId() == id)
            {
                return index;
            }

            index++;
        }
        return -1;
    }

    public boolean checkStatusAndSortBy(String status, String sortBy)
    {
        if(!status.equals("ALL")  && !status.equals("PENDING") && !status.equals("LATE") && !status.equals("DONE"))
        {

            return true;
        }
        if(sortBy != null && !sortBy.equals("ID") && !sortBy.equals("DUE_DATE") && !sortBy.equals("TITLE"))
        {
            return true;
        }
        return false;
    }

    public boolean isTitleExists(String title)
    {
        for(Todo todo: todos)
        {
            if(todo.getTitle().equals(title))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isDueDateNotValid(long dueDate)
    {
        long currentTimeMillis = System.currentTimeMillis();
        if(dueDate>currentTimeMillis)
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    private ArrayList<Todo> createAndSortTodoArr(String status, String sortBy)
    {
        ArrayList<Todo> res = new ArrayList<Todo>();
        for(Todo todo : todos)
        {
            if(todo.getStatus().equals(status) || status.equals("ALL"))
            {
                res.add(todo);
            }
        }

        switch (sortBy)
        {
            case "ID":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return Integer.compare(o1.getId(), o2.getId());
                    }
                });
                break;

            case "DUE_DATE":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return Long.compare(o1.getDueDate(), o2.getDueDate());
                    }
                });
                break;

            case "TITLE":
                Collections.sort(res, new Comparator<Todo>() {
                    @Override
                    public int compare(Todo o1, Todo o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                break;
        }

        return res;
    }

    private JSONArray toJsonArray(ArrayList<Todo> todosList) {
        JSONArray jsonArray = new JSONArray();
        for (Todo todo : todosList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", todo.getId());
            map.put("title", todo.getTitle());
            map.put("content", todo.getContent());
            map.put("status", todo.getStatus());
            map.put("dueDate", todo.getDueDate());

            // create the JSONObject using the Map object
            JSONObject jsonObject = new JSONObject(map);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
