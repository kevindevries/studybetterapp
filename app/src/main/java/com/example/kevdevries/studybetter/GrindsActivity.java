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

//Grinds for Student view/join
public class GrindsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference userRef, childRef, userRootRef, grindRootRef, departRef, departRootRef, grindRef;
    private List<GrindModel> list;
    private RecyclerView recycle;
    private Button view;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grinds);
        view = (Button) findViewById(R.id.view);
        recycle = (RecyclerView) findViewById(R.id.recycle);
        database = FirebaseDatabase.getInstance();

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        final String userId = auth.getCurrentUser().getUid();

        userRootRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("department");

        database = FirebaseDatabase.getInstance();
        grindRootRef = database.getReference().child("Grinds");

        userRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userDataSnapshot) {
                final String userDepartment = userDataSnapshot.getValue(String.class);
                Log.i("The user department:", userDepartment);

                grindRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot grindDataSnapshot) {

                        for (DataSnapshot userSnapshot : grindDataSnapshot.getChildren()) {
                            final String grindDepartment = userSnapshot.getKey();
                            Log.i("The grind department:", grindDepartment);

                            if (grindDepartment.equalsIgnoreCase(userDepartment)) {
                                Log.i("Match found for:", grindDepartment);
                                grindRef = grindRootRef.child(userDepartment);
                                grindRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        //Loop 1 to go through all the child nodes of User ID
                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            //loop 2 to go through all the child nodes of grind ID
                                            list = new ArrayList<GrindModel>();
                                            for (DataSnapshot grindsSnapshot : userSnapshot.getChildren()) {
                                                //for (DataSnapshot dataSnapshot1 : grindsSnapshot.getChildren()) {

                                                GrindModel value = grindsSnapshot.getValue(GrindModel.class);
                                                GrindModel fire = new GrindModel();
                                                //String title = dataSnapshot1.child("title").getValue().toString();
                                                //String time = dataSnapshot1.child("time").getValue().toString();
                                                //String date = dataSnapshot1.child("date").getValue().toString();
                                                //String recurring = dataSnapshot1.child("recurring").getValue().toString();
                                                String date = value.getDate();
                                                String location = value.getLocation();
                                                String recurring = value.getRecurring();
                                                String time = value.getTime();
                                                String title = value.getTitle();
                                                fire.setDate(date);
                                                fire.setLocation(location);
                                                fire.setRecurring(recurring);
                                                fire.setTime(time);
                                                fire.setTitle(title);
                                                list.add(fire);
                                                //}
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w("Failed to read value.", error.toException());
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("Failed to read value.", error.toException());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });

        //Find current users department and tutors in same department
        //Show student available grinds in their department
        //Structure : Grinds > Department > tutorID > grindID > data
/*
        grindRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<FireModel>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    FireModel value = dataSnapshot.getValue(FireModel.class);
                    FireModel fire = new FireModel();
                    //String title = dataSnapshot1.child("title").getValue().toString();
                    //String time = dataSnapshot1.child("time").getValue().toString();
                    //String date = dataSnapshot1.child("date").getValue().toString();
                    //String recurring = dataSnapshot1.child("recurring").getValue().toString();
                    String date = value.getDate();
                    String recurring = value.getRecurring();
                    String time = value.getTime();
                    String title = value.getTitle();
                    fire.setRecurring(recurring);
                    fire.setDate(date);
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
*/

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list, GrindsActivity.this);
                RecyclerView.LayoutManager recyce = new LinearLayoutManager(GrindsActivity.this);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator(new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);

            }

        });
    }
}
