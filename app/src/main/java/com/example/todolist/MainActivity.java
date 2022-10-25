package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.todolist.Database.DatabaseHelper;
import com.example.todolist.Model.TaskModel;
import com.example.todolist.Settings.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogListener {
    private DatabaseHelper databaseHelper;
    private List<TaskModel> tasksList = new ArrayList<>();
    private TaskAdapter adapter;

    String categories;
    Boolean status, sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton actionButton = findViewById(R.id.addButton);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        tasksList = new ArrayList<>();
        adapter = new TaskAdapter(databaseHelper, MainActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        categories = sharedPreferences.getString("key_categories", "All categories");
        status = sharedPreferences.getBoolean("key_status", false);
        sort = sharedPreferences.getBoolean("key_sort", false);

        tasksList = databaseHelper.getAllTasks(categories, status);
        if (sort){
            tasksList.sort(new Comparator<TaskModel>() {
                @Override                public int compare(TaskModel taskModel1, TaskModel taskModel2) {
                    try {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date1 = formatter.parse(taskModel1.getTaskDate() + " " + taskModel1.getTaskTime());
                    Date date2 = formatter.parse(taskModel2.getTaskDate() + " " + taskModel2.getTaskTime());
                        if (date1.after(date2)) {
                            return 1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return 0;
                }
            });
        } else {
            Collections.reverse(tasksList);
        }
        adapter.setTasks(tasksList);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        tasksList = databaseHelper.getAllTasks(categories, status);
        Collections.reverse(tasksList);
        adapter.setTasks(tasksList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int action = item.getItemId();
        if (action == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        categories = sharedPreferences.getString("key_categories", "All categories");
        status = sharedPreferences.getBoolean("key_status", false);
        sort = sharedPreferences.getBoolean("key_sort", false);

        tasksList = databaseHelper.getAllTasks(categories, status);
        if (sort){
            tasksList.sort(new Comparator<TaskModel>() {
                @Override
                public int compare(TaskModel taskModel1, TaskModel taskModel2) {

                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String date1 = taskModel1.getTaskDate();
                    String date2 = taskModel2.getTaskDate();

                    try {
                        if (simpleDateFormat.parse(date1).before(simpleDateFormat.parse(date2))) {
                            return 1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        } else {
            Collections.reverse(tasksList);
        }
        adapter.setTasks(tasksList);
        adapter.notifyDataSetChanged();
    }

    public void sortTasks(){

    }
}