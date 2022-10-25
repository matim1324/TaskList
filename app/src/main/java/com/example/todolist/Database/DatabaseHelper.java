package com.example.todolist.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.todolist.Model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "TO_DO_LIST_DATABASE";
    private static final String TABLE_NAME = "TO_DO_LIST_TABLE";
    private static final String TASK_ID = "ID";
    private static final String TASK_TITLE = "TITLE";
    private static final String TASK_CATEGORY = "CATEGORY";
    private static final String TASK_DESCRIPTION = "DESCRIPTION";
    private static final String TASK_DATE = "DATE";
    private static final String TASK_TIME = "TIME";
    private static final String TASK_NOTIFICATION = "NOTIFICATION";
    private static final String TASK_STATUS = "STATUS";

    private static final String CREATE_TASKS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TASK_TITLE + " TEXT, "
                    + TASK_DESCRIPTION + " TEXT, "
                    + TASK_CATEGORY + " TEXT, "
                    + TASK_DATE + " TEXT, "
                    + TASK_TIME + " TEXT, "
                    + TASK_NOTIFICATION + " INTEGER, "
                    + TASK_STATUS + " INTEGER)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE" + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, TITLE TEXT, DESCRIPTION TEXT, CATEGORY TEXT, DATE TEXT, TIME TEXT, NOTIFICATION INTEGER, STATUS INTEGER)");
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertTask(TaskModel model){
        database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, model.getTaskTitle());
        values.put(TASK_DESCRIPTION, model.getTaskDescription());
        values.put(TASK_CATEGORY, model.getTaskCategory());
        values.put(TASK_DATE, model.getTaskDate());
        values.put(TASK_TIME, model.getTaskTime());
        values.put(TASK_NOTIFICATION, model.getTaskNotification());
        values.put(TASK_STATUS, 0);
        database.insert(TABLE_NAME, null , values);
    }

    public void updateTask(int id , String title, String description, String category, String date, String time, int notification){
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, title);
        values.put(TASK_DESCRIPTION, description);
        values.put(TASK_CATEGORY, category);
        values.put(TASK_DATE, date);
        values.put(TASK_TIME, time);
        //values.put(TASK_NOTIFICATION, notification);
        database.update(TABLE_NAME, values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void updateTaskStatus(int id , int status){
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TASK_STATUS, status);
        database.update(TABLE_NAME , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id ){
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME , "ID=?" , new String[]{String.valueOf(id)});
    }

    public int getDatabaseSize(){
        return (int) DatabaseUtils.queryNumEntries(database, TABLE_NAME);
    }

    @SuppressLint("Range")
    public List<TaskModel> getAllTasks(String category, Boolean status){
        database = this.getWritableDatabase();
        Cursor cursor = null;
        List<TaskModel> modelList = new ArrayList<>();

        database.beginTransaction();
        try {
            cursor = database.query(TABLE_NAME , null , null , null , null , null , null);
            if (cursor != null){
                if (cursor.moveToFirst()){
                    do {
                        TaskModel task = new TaskModel();
                        task.setTaskId(cursor.getInt(cursor.getColumnIndex(TASK_ID)));
                        task.setTaskTitle(cursor.getString(cursor.getColumnIndex(TASK_TITLE)));
                        task.setTaskDescription(cursor.getString(cursor.getColumnIndex(TASK_DESCRIPTION)));
                        task.setTaskCategory(cursor.getString(cursor.getColumnIndex(TASK_CATEGORY)));
                        task.setTaskDate(cursor.getString(cursor.getColumnIndex(TASK_DATE)));
                        task.setTaskTime(cursor.getString(cursor.getColumnIndex(TASK_TIME)));
                        task.setTaskNotification(cursor.getInt(cursor.getColumnIndex(TASK_NOTIFICATION)));
                        task.setTaskStatus(cursor.getInt(cursor.getColumnIndex(TASK_STATUS)));

                        // change category to display
                        switch (category) {
                            case "All categories":
                                if (status) {
                                    if (task.getTaskStatus() == 0)
                                        modelList.add(task);
                                } else {
                                    modelList.add(task);
                                }
                                break;
                            case "Learning":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("Learning")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("Learning")) modelList.add(task);
                                }
                                break;
                            case "Work":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("Work")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("Work")) modelList.add(task);
                                }
                                break;
                            case "House works":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("House works")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("House works")) modelList.add(task);
                                }
                                break;
                            case "Important things":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("Important things")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("Important things")) modelList.add(task);
                                }
                                break;
                            case "Myself activities":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("Myself activities")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("Myself activities")) modelList.add(task);
                                }
                                break;
                            case "Friends/Meetings":
                                if (status){
                                    if (task.getTaskStatus() == 0)
                                        if (task.getTaskCategory().equals("Friends/Meetings")) modelList.add(task);
                                } else {
                                    if (task.getTaskCategory().equals("Friends/Meetings")) modelList.add(task);
                                }
                                break;
                        }
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            database.endTransaction();
            cursor.close();
        }
        return modelList;
    }
}
