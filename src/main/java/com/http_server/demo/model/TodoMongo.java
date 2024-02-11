package com.http_server.demo.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
@Document(collection = "todos")
public class TodoMongo implements Serializable, Todo {
    @Id
    private String strid;
    private Integer rawid;
    private String title = null;
    private String content = null;
    private Long duedate;
    private String state = null;

    public TodoMongo(Integer rawid, String title, String content, Long duedate, String state) {
        this.rawid = rawid;
        this.title = title;
        this.content = content;
        this.duedate = duedate;
        this.state = state;
    }

    public TodoMongo() {
    }

    public String getStrid() {
        return strid;
    }
    public void setStrid(String strid) {
        this.strid = strid;
    }

    public int getRawid() {
        return rawid;
    }

    public void setRawid(int rawid) {
        this.rawid = rawid;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getDuedate() {
        return duedate;
    }
    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
