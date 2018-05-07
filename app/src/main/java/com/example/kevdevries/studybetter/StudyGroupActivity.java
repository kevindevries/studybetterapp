package com.example.kevdevries.studybetter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Grinds for Student view/leave
public class StudyGroupActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference userRootRef;
    private List<StudyModel> list;
    private RecyclerView recycle;
    private Button view;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group);
        view = (Button) findViewById(R.id.view);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        final String userId = auth.getCurrentUser().getUid();

        //userRootRef = FirebaseDatabase.getInstance().getReference("Study Groups").child(userId);

        database = FirebaseDatabase.getInstance();
        userRootRef = database.getReference().child("Study Groups").child(userId);

        userRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userDataSnapshot) {
                Log.i("Entered Listener", "333");
                    list = new ArrayList<StudyModel>();
                    for (DataSnapshot dataSnapshot : userDataSnapshot.getChildren()) {
                        Log.i("Second datashot:", "Grind ID");

                        StudyModel value = dataSnapshot.getValue(StudyModel.class);
                        StudyModel fire = new StudyModel();
                        Log.i("At model:", "111");
                        String date = value.getDate();
                        String location = value.getLocation();
                        String members = value.getMembers();
                        String recurring = value.getRecurring();
                        String time = value.getTime();
                        String title = value.getTitle();

                        fire.setDate(date);
                        fire.setLocation(location);
                        fire.setMembers(members);
                        fire.setRecurring(recurring);
                        fire.setTime(time);
                        fire.setTitle(title);
                        list.add(fire);
                    }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyRecyclerAdapter recyclerAdapter = new StudyRecyclerAdapter(list, StudyGroupActivity.this);
                RecyclerView.LayoutManager recyce = new LinearLayoutManager(StudyGroupActivity.this);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator(new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);

            }

        });
    }
}
