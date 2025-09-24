package com.example.c022ass2mad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.c022ass2mad.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText taskInput, taskDescInput;
    private Spinner prioritySpinner;
    private Button addTaskBtn;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskInput = findViewById(R.id.taskInput);
        taskDescInput = findViewById(R.id.taskDescInput);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbRef = FirebaseDatabase.getInstance().getReference("tasks");

        adapter = new TaskAdapter(this, new ArrayList<>(), dbRef);
        recyclerView.setAdapter(adapter);

        // Add new task
        addTaskBtn.setOnClickListener(v -> {
            String name = taskInput.getText().toString().trim();
            String description = taskDescInput.getText().toString().trim();
            String priority = (prioritySpinner.getSelectedItem() != null) ?
                    prioritySpinner.getSelectedItem().toString() : "";

            if (!name.isEmpty() && !priority.isEmpty()) {
                String id = dbRef.push().getKey();
                if (id != null) {
                    Task task = new Task(id, name, description, priority);
                    dbRef.child(id).setValue(task);

                    taskInput.setText("");
                    taskDescInput.setText("");
                }
            } else {
                Toast.makeText(MainActivity.this, "Enter task name and select priority", Toast.LENGTH_SHORT).show();
            }
        });


        // Listen for changes in Firebase
        dbRef.orderByChild("priority").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Task task = child.getValue(Task.class);
                    taskList.add(task);
                }

                // Custom sort: High > Medium > Low
                Collections.sort(taskList, (t1, t2) -> {
                    return getPriorityValue(t1.getPriority()) - getPriorityValue(t2.getPriority());
                });
                adapter.updateList(taskList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
            }
        });

        // Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Task task = adapter.getTaskAt(viewHolder.getAdapterPosition());
                dbRef.child(task.getId()).removeValue();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "High": return 1;
            case "Medium": return 2;
            case "Low": return 3;
            default: return 4;
        }
    }
}
