package com.example.todolist.Model;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TaskModel{
    private int id;
    private String taskTitle;
    private String taskDescription;
    private String taskCategory;
    private String taskDate;
    private String taskTime;
    private int taskNotification;
    private int status;

    public int getTaskId() {
        return id;
    }

    public void setTaskId(int id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public int getTaskStatus() {
        return status;
    }

    public void setTaskStatus(int status) {
        this.status = status;
    }

    public int getTaskNotification(){
        return taskNotification;
    }

    public void setTaskNotification(int taskNotification) {
        this.taskNotification = taskNotification;
    }
}
