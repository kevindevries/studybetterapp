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
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.widget.Toast;
import java.util.Date;
import java.sql.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class CreateGrindsActivity extends AppCompatActivity {
    private RadioGroup radioType;
    private RadioButton radioButton;
    private EditText inputTitle;
    private TimePicker timePicker;
    private DatePicker datePicker;
    private SimpleDateFormat timeFormatter, dateFormatter;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button btnregister, btncancel;
    private FirebaseAuth auth;
    private String enteredDate;
    private Date date;
    private String enteredTime;
    private Time time;

    final String TAG = "createGrind";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_grinds);

        initialise();
    }

    private void initialise(){

        inputTitle = (EditText) findViewById(R.id.titlemessage);
        timePicker = (TimePicker) findViewById(R.id.timepickermessage);
        datePicker = (DatePicker) findViewById(R.id.datepickermessage);
        radioType = (RadioGroup) findViewById(R.id.radioType);
        btnregister = (Button) findViewById(R.id.btn_register);
        btncancel = (Button) findViewById(R.id.btn_cancel);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Grinds");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGrind();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGrindsActivity.this, Home2Activity.class));
            }
        });
    }

    private void createGrind() {
        final int selectedId = radioType.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        final String type = radioButton.getText().toString();
        final String title = inputTitle.getText().toString();
        final int day = datePicker.getDayOfMonth();
        final int month = datePicker.getMonth();
        final int year = datePicker.getYear();
        final int hour = timePicker.getCurrentHour();
        final int minute = timePicker.getCurrentMinute();

        //date = new Date(day, month, year);
        //dateFormatter = new SimpleDateFormat("E, dd-MM-yy");
        //enteredDate = dateFormatter.format(date);

        //timePicker.setIs24HourView(true);

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getApplicationContext(), "Please enter all fields!", Toast.LENGTH_SHORT).show();
            return;

        } else {

            final String userId = auth.getCurrentUser().getUid();

            final DatabaseReference currentUserDb = databaseReference.child(userId);
            currentUserDb.child("title").setValue(title);
            currentUserDb.child("time").setValue(hour + ":" +  minute);
            currentUserDb.child("date").setValue(day + "/" + (month+1) + "/" + year);
            currentUserDb.child("recurring").setValue(type);

            updateGrindInfoAndUI();
        }

    }

    private void updateGrindInfoAndUI(){
        startActivity(new Intent(CreateGrindsActivity.this, Home2Activity.class));
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
