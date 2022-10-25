package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.Database.DatabaseHelper;
import com.example.todolist.Model.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskModel> tasksList;
    private final MainActivity mainActivity;
    private final DatabaseHelper databaseHelper;

    public TaskAdapter(DatabaseHelper database, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.databaseHelper = database;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskModel item = tasksList.get(position);
        holder.taskTitle.setText(item.getTaskTitle());
        holder.taskDescription.setText(item.getTaskDescription());
        holder.taskCategory.setText(item.getTaskCategory());
        holder.taskDate.setText(item.getTaskDate());
        holder.taskTime.setText(item.getTaskTime());

        holder.taskDelete.setOnClickListener(view -> showPopUpWindow(view, position, 1));
        holder.taskEdit.setOnClickListener(view -> showPopUpWindow(view, position, 2));

        holder.taskNotification.setText(item.getTaskNotification() == 1 ? "Notification enabled" : "Notification disabled");
        holder.taskNotification.setTextColor(item.getTaskNotification() == 1 ? Color.parseColor("#FF008FBA") : Color.parseColor("#B63D3D"));

        holder.taskCardView.setCardBackgroundColor(item.getTaskStatus() == 1 ? Color.GRAY : Color.WHITE);

        holder.taskCheckBox.setChecked(toBoolean(item.getTaskStatus()));
        holder.taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    databaseHelper.updateTaskStatus(item.getTaskId() , 1);
                    holder.taskCardView.setCardBackgroundColor(Color.GRAY);
                } else{
                    databaseHelper.updateTaskStatus(item.getTaskId() , 0);
                    holder.taskCardView.setCardBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    public void showPopUpWindow(View view, int position, int type) {
        if (type == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete this task");
            builder.setMessage("Are you sure you want to delete this task?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteTask(position);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notifyItemChanged(position);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            editTask(position);
        }
    }

    public boolean toBoolean(int num){
        return num!=0;
    }

    public Context getContext(){
        return mainActivity;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TaskModel> mList){
        this.tasksList = mList;
        notifyDataSetChanged();
    }

    public void deleteTask(int position){
        try {
            TaskModel item = tasksList.get(position);
            databaseHelper.deleteTask(item.getTaskId());
            tasksList.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e){
            Toast.makeText(mainActivity, "Delete error. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void editTask(int position){
        try {
            TaskModel item = tasksList.get(position);

            Bundle bundle = new Bundle();
            bundle.putInt("id", item.getTaskId());
            bundle.putString("title", item.getTaskTitle());
            bundle.putString("description", item.getTaskDescription());
            bundle.putString("category", item.getTaskCategory());
            bundle.putString("date", item.getTaskDate());
            bundle.putString("time", item.getTaskTime());
            //bundle.putInt("notification", item.getTaskNotification());

            AddNewTask task = new AddNewTask();
            task.setArguments(bundle);
            task.show(mainActivity.getSupportFragmentManager() , task.getTag());
        } catch (Exception e){
            Toast.makeText(mainActivity, "Something go wrong with task edition.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDescription;
        TextView taskCategory;
        TextView taskTime;
        TextView taskDate;
        CheckBox taskCheckBox;
        ImageView taskDelete;
        ImageView taskEdit;
        TextView taskNotification;
        TextView taskStatusInfo;
        CardView taskCardView;

        public TaskViewHolder(@NonNull View view) {
            super(view);
            taskTitle = view.findViewById(R.id.title);
            taskDescription = view.findViewById(R.id.description);
            taskCategory = view.findViewById(R.id.category);
            taskDate = view.findViewById(R.id.taskDate);
            taskTime = view.findViewById(R.id.taskTime);
            taskCheckBox = view.findViewById(R.id.taskCheckbox);
            taskNotification = view.findViewById(R.id.taskNotificationInfo);

            taskDelete = view.findViewById(R.id.deleteTask);
            taskEdit = view.findViewById(R.id.editTask);

            taskCardView = view.findViewById(R.id.cardViewLayout);
        }
    }
}
