package com.example.c022ass2mad;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.c022ass2mad.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private List<Task> taskList;
    private DatabaseReference dbRef;

    public TaskAdapter(Context context, List<Task> taskList, DatabaseReference dbRef) {
        this.context = context;
        this.taskList = taskList;
        this.dbRef = dbRef;
    }

    public void updateList(List<Task> list) {
        taskList = list;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position) {
        return taskList.get(position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.name.setText(task.getName());
        holder.description.setText("(" + task.getDescription() + ")");
        holder.priority.setText(task.getPriority());

        holder.itemView.setOnClickListener(v -> {
            // Update task (simple example with dialog)
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
            EditText editDesc = dialogView.findViewById(R.id.editTask);
            Spinner editPriority = dialogView.findViewById(R.id.editPriority);

            editDesc.setText(task.getDescription());
            if (task.getPriority().equals("High")) editPriority.setSelection(0);
            else if (task.getPriority().equals("Medium")) editPriority.setSelection(1);
            else editPriority.setSelection(2);

            builder.setView(dialogView);
            builder.setPositiveButton("Update", (dialog, which) -> {
                String newDesc = editDesc.getText().toString();
                String newPriority = editPriority.getSelectedItem().toString();

                // ✅ Keep task name intact while updating
                Task updated = new Task(task.getId(), task.getName(), newDesc, newPriority);
                dbRef.child(task.getId()).setValue(updated);
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, priority;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.taskName);
            description = itemView.findViewById(R.id.taskDescription);
            priority = itemView.findViewById(R.id.taskPriority);
        }
    }

}
