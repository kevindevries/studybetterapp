package com.example.kevdevries.studybetter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateActivity extends AppCompatActivity {
    private RadioGroup radioType;
    private RadioButton radioButton;
    private EditText inputFirst, inputLast, inputEmail, inputCollege, inputDepartment, inputCourse, inputYear, inputSubject1, inputSubject2, inputSubject3, inputPassword;
    private Button btnupdate, btncancel;
    private DatabaseReference rootRef, userRef, childRef;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initialise();
    }

    private void initialise(){

        radioType = (RadioGroup) findViewById(R.id.radioType);
        inputFirst = (EditText) findViewById(R.id.namemessage);
        inputLast = (EditText) findViewById(R.id.surnamemessage);
        inputCollege = (EditText) findViewById(R.id.collegemessage);
        inputDepartment = (EditText) findViewById(R.id.departmentmessage);
        inputCourse = (EditText) findViewById(R.id.coursemessage);
        inputYear = (EditText) findViewById(R.id.yearmessage);
        inputSubject1 = (EditText) findViewById(R.id.subject1);
        inputSubject2 = (EditText) findViewById(R.id.subject2);
        inputSubject3 = (EditText) findViewById(R.id.subject3);
        btnupdate = (Button) findViewById(R.id.btn_update);
        btncancel = (Button) findViewById(R.id.btn_cancel);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAccount();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void updateAccount() {
        final int selectedId = radioType.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String type = radioButton.getText().toString();
        final String firstName = inputFirst.getText().toString();
        final String lastName = inputLast.getText().toString();
        final String college = inputCollege.getText().toString();
        final String department = inputDepartment.getText().toString();
        final String course = inputCourse.getText().toString();
        final String year = inputYear.getText().toString();
        final String subject1 = inputSubject1.getText().toString();
        final String subject2 = inputSubject2.getText().toString();
        final String subject3 = inputSubject3.getText().toString();

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(college) && TextUtils.isEmpty(department) && TextUtils.isEmpty(course)) {
            Toast.makeText(getApplicationContext(), "Please enter all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String userId = auth.getCurrentUser().getUid();

        final DatabaseReference currentUserDb = databaseReference.child(userId);

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUserDb.child("type").setValue(type);
                currentUserDb.child("firstName").setValue(firstName);
                currentUserDb.child("surname").setValue(lastName);
                currentUserDb.child("college").setValue(college);
                currentUserDb.child("department").setValue(department);
                currentUserDb.child("course").setValue(course);
                currentUserDb.child("year").setValue(year);
                currentUserDb.child("subject1").setValue(subject1);
                currentUserDb.child("subject2").setValue(subject2);
                currentUserDb.child("subject3").setValue(subject3);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }

        });

    }

    private void cancel(){

        final String userId = auth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef = rootRef.child(userId);
        childRef = userRef.child("type");

        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);

                if (type.equalsIgnoreCase("Student")){
                    Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(UpdateActivity.this, Home2Activity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }

        });
    }
}
