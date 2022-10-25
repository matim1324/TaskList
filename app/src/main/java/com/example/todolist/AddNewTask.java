package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.Database.DatabaseHelper;
import com.example.todolist.Model.TaskModel;
import com.example.todolist.Notification.ReminderBroadcast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "NewTasks";

    private EditText addTaskTitle;
    private EditText addTaskDescription;
    private Spinner addTaskCategory;
    private EditText addTaskDate;
    private EditText addTaskTime;
    private EditText addTaskAttachment;
    private CheckBox addTaskNotification;
    private DatabaseHelper myDatabase;

    private int year, month, day, hour, minute;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_task_layout, container, false);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addTaskTitle = view.findViewById(R.id.addTaskTitle);
        Button addTaskButton = view.findViewById(R.id.saveTask);
        addTaskDescription = view.findViewById(R.id.addTaskDescription);
        addTaskCategory = view.findViewById(R.id.categorySpinner);
        addTaskDate = view.findViewById(R.id.addTaskDate);
        addTaskTime = view.findViewById(R.id.addTaskTime);
        addTaskNotification = view.findViewById(R.id.taskNotificationCheckBox);
        myDatabase = new DatabaseHelper(getActivity());

        boolean taskChanged = false;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addTaskCategory.setAdapter(adapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            taskChanged = true;
            String title = bundle.getString("title");
            String description = bundle.getString("description");
            String category = bundle.getString("category");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            //int notification = bundle.getInt("notification");

            addTaskTitle.setText(title);
            addTaskDescription.setText(description);
            addTaskCategory.setSelection(adapter.getPosition(category));
            addTaskDate.setText(date);
            addTaskTime.setText(time);
            //addTaskNotification.setChecked(notification == 1);
        }

        addTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        (view1, setYear, setMonth, setDay) -> {
                            Calendar temp = Calendar.getInstance();
                            temp.set(setYear, setMonth, setDay);
                            addTaskDate.setText(DateFormat.format("dd/MM/yyyy", temp));
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        addTaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar temp = Calendar.getInstance();
                int tempHour = temp.get(Calendar.HOUR_OF_DAY);
                int tempMinute = temp.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (view1, setHourOfDay, setMinute) -> {
                            hour = setHourOfDay;
                            minute = setMinute;
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            addTaskTime.setText(DateFormat.format("HH:mm", calendar));
                        }, tempHour, tempMinute, true);
                timePickerDialog.show();
            }
        });

        boolean finalTaskChanged = taskChanged;
        addTaskButton.setOnClickListener(v -> {
            String title = addTaskTitle.getText().toString();
            String description = addTaskDescription.getText().toString();
            String category = addTaskCategory.getSelectedItem().toString();
            String date = addTaskDate.getText().toString();
            String time = addTaskTime.getText().toString();
            int notification = addTaskNotification.isChecked() ? 1 : 0;

            if (validateTaskInput()){
                if (finalTaskChanged){
                    myDatabase.updateTask(bundle.getInt("id") , title, description, category, date, time, notification);
                } else {
                    System.out.println("ADD NEW TASK");

                    TaskModel item = new TaskModel();
                    item.setTaskTitle(title);
                    item.setTaskDescription(description);
                    item.setTaskCategory(category);
                    item.setTaskDate(date);
                    item.setTaskTime(time);
                    item.setTaskNotification(notification);
                    item.setTaskStatus(0);
                    myDatabase.insertTask(item);
                    if (notification == 1){
                        createNotification();
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogListener){
            ((OnDialogListener)activity).onDialogClose(dialog);
        }
    }

    public boolean validateTaskInput() {
        if (addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this.getActivity(), "Enter a valid title", Toast.LENGTH_SHORT).show();
            return false;
        } else if (addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this.getActivity(), "Enter a valid description", Toast.LENGTH_SHORT).show();
            return false;
        } else if (addTaskCategory.getSelectedItem().toString().equalsIgnoreCase("")) {
            Toast.makeText(this.getActivity(), "Enter category", Toast.LENGTH_SHORT).show();
            return false;
        } else if (addTaskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this.getActivity(), "Please enter date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (addTaskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this.getActivity(), "Please enter time", Toast.LENGTH_SHORT).show();
            return false;
        } else if (checkIsDateActual(addTaskDate.getText().toString(), addTaskTime.getText().toString()) == 1) {
            Toast.makeText(this.getActivity(), "Entered past datetime", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public int checkIsDateActual(String date, String time) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateTime = formatter.parse(date + " " + time);

            if (dateTime.before(new Date())){
                return 1;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public void createNotification(){
        String[] time = addTaskTime.getText().toString().split(":");
        String hour = time[0];
        String minute = time[1];

        String[] date = addTaskDate.getText().toString().split("/");
        String day = date[0];
        String month = date[1];
        String year = date[2];

        int id = myDatabase.getDatabaseSize();
//        //System.out.println("Notification for hour: " + addTaskTime.getText().toString() + ":" + addTaskDate.getText().toString() + " ,  day: " + "\n--------");
//        System.out.println("CREATE NOTIFICATION id: " + id + "\n");
//        System.out.println("ON TIME: " + hour + ":" + minute + "on date: " + day + "/" + month + "/" + year + "\n----------------");

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar1 = new GregorianCalendar();
        calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar1.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.DATE, Integer.parseInt(month));
        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

        Intent intent = new Intent(getContext(), ReminderBroadcast.class);
        intent.putExtra("ID", id);
        intent.putExtra("TITLE", addTaskTitle.getText().toString());
        intent.putExtra("CATEGORY", addTaskCategory.getSelectedItem().toString());
        intent.putExtra("TIME", addTaskTime.getText().toString());
        intent.putExtra("DATE", addTaskDate.getText().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);

        NotificationChannel channel = new NotificationChannel("notificationChannel", "notification", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

    }
}
