package com.example.kevdevries.studybetter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Register a user profile
public class RegisterActivity extends AppCompatActivity {
    private RadioGroup radioType;
    private RadioButton radioButton;
    private EditText inputFirst, inputLast, inputEmail, inputCollege, inputDepartment, inputCourse, inputYear, inputSubject1, inputSubject2, inputSubject3, inputPassword;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, locationReference;
    private Button btnregister, btncancel;
    private DatabaseReference rootRef, userRef, childRef;
    private ProgressDialog progressBar;
    private FirebaseAuth auth;

    final String TAG = "createAccount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialise();
    }

    private void initialise() {

        radioType = (RadioGroup) findViewById(R.id.radioType);
        inputFirst = (EditText) findViewById(R.id.namemessage);
        inputLast = (EditText) findViewById(R.id.surnamemessage);
        inputEmail = (EditText) findViewById(R.id.emailmessage);
        inputCollege = (EditText) findViewById(R.id.collegemessage);
        inputDepartment = (EditText) findViewById(R.id.departmentmessage);
        inputCourse = (EditText) findViewById(R.id.coursemessage);
        inputYear = (EditText) findViewById(R.id.yearmessage);
        inputSubject1 = (EditText) findViewById(R.id.subject1);
        inputSubject2 = (EditText) findViewById(R.id.subject2);
        inputSubject3 = (EditText) findViewById(R.id.subject3);
        inputPassword = (EditText) findViewById(R.id.passwordmessage);
        btnregister = (Button) findViewById(R.id.btn_register);
        btncancel = (Button) findViewById(R.id.btn_cancel);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }

    private void createAccount() {
        final int selectedId = radioType.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String type = radioButton.getText().toString();
        final String firstName = inputFirst.getText().toString();
        final String lastName = inputLast.getText().toString();
        final String email = inputEmail.getText().toString();
        final String college = inputCollege.getText().toString();
        final String department = inputDepartment.getText().toString();
        final String course = inputCourse.getText().toString();
        final String year = inputYear.getText().toString();
        final String subject1 = inputSubject1.getText().toString();
        final String subject2 = inputSubject2.getText().toString();
        final String subject3 = inputSubject3.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(college) && TextUtils.isEmpty(department) && TextUtils.isEmpty(course) && TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //Debug if user creation successful
                            Log.d(TAG, "createUserWithEmail:success");

                            final String userId = auth.getCurrentUser().getUid();

                            //Verify email address
                            verifyEmail();

                            final DatabaseReference currentUserDb = databaseReference.child(userId);
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

                            if (type.equalsIgnoreCase("Student")) {
                                locationReference = database.getReference().child("Users").child("Locations").child(userId);
                                locationReference.child("latitude").setValue(0);
                                locationReference.child("longitude").setValue(0);
                                locationReference.child("firstName").setValue(firstName);
                                locationReference.child("subject1").setValue(subject1);
                                locationReference.child("subject2").setValue(subject2);
                                locationReference.child("subject3").setValue(subject3);
                                locationReference.child("year").setValue(year);
                            }
                            updateUserInfoAndUI();

                        } else {

                            //Debug if user creation unsuccessful
                            Log.w(TAG, "createUserWithEmail:failed");
                            Toast.makeText(getApplicationContext(), "User Creation Failed!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }


                });

    }

    private void updateUserInfoAndUI() {

        final String userId = auth.getCurrentUser().getUid();

        rootRef = FirebaseDatabase.getInstance().getReference("Users");
        userRef = rootRef.child(userId);
        childRef = userRef.child("type");

        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot.getValue(String.class);

                if (type.equalsIgnoreCase("Student")) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RegisterActivity.this, Home2Activity.class);
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

    private void verifyEmail() {
        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Verification email sent to: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }
}
