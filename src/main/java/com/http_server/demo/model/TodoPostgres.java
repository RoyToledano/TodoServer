package com.http_server.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "todos")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TodoPostgres implements Serializable, Todo {
    @Id
    @Column(name = "rawid", nullable = false)
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer rawid;
    @Column(name = "title", nullable = false)
    private String title = null;
    @Column(name = "content", nullable = false)
    private String content = null;
    @Column(name = "duedate", nullable = false)
    private Long duedate;
    @Column(name = "state", nullable = false)
    private String state = null;


    public TodoPostgres(Integer rawid, String title, String content, Long duedate, String state) {
        this.rawid = rawid;
        this.title = title;
        this.content = content;
        this.duedate = duedate;
        this.state = state;
    }

    public TodoPostgres() {
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
