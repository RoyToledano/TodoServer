package com.http_server.demo.model;

public interface Todo {
    public int getRawid();
    public void setRawid(int rawid);
    public String getTitle();
    public void setTitle(String title);
    public String getContent();
    public void setContent(String content);
    public long getDuedate();
    public void setDuedate(long duedate);
    public String getState();
    public void setState(String state);
}
