package com.example.kevdevries.studybetter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Grinds for Student view/leave
public class GrindsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference rootRef, grindRef;
    List<FireModel> list;
    RecyclerView recycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grinds);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();

        rootRef = database.getReference("Grinds");
        grindRef = rootRef.child("AJ6HCGZpDgbynNnjorpLE9T9jBf1");

        grindRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<FireModel>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    FireModel value = dataSnapshot1.getValue(FireModel.class);
                    FireModel fire = new FireModel();
                    //String title = dataSnapshot1.child("title").getValue().toString();
                    //String time = dataSnapshot1.child("time").getValue().toString();
                    //String date = dataSnapshot1.child("date").getValue().toString();
                    //String recurring = dataSnapshot1.child("recurring").getValue().toString();
                    String title = value.getTitle();
                    String time = value.getTime();
                    String date = value.getDate();
                    String recurring = value.getRecurring();
                    fire.setTitle(title);
                    fire.setTime(time);
                    fire.setDate(date);
                    fire.setRecurring(recurring);
                    list.add(fire);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }

        });

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list,GrindsActivity.this);
        RecyclerView.LayoutManager recyce = new LinearLayoutManager(GrindsActivity.this);
        recycle.setLayoutManager(recyce);
        //recycle.setItemAnimator( new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);
    }
}
