package com.example.kevdevries.studybetter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class MainActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnRegister, btnLogin, btnForgotPassword;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btn_registernow);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnForgotPassword = (Button) findViewById(R.id.btn_forgot_password);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(MainActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    final String userId = auth.getCurrentUser().getUid();

                                    rootRef = FirebaseDatabase.getInstance().getReference("Users");
                                    userRef = rootRef.child(userId);
                                    childRef = userRef.child("type");

                                    childRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String type = dataSnapshot.getValue(String.class);

                                            if (type.equalsIgnoreCase("Student")) {
                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(MainActivity.this, Home2Activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                }
                            }
                        });
            }
        });
    }

    private void resetPassword() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_reset_password, null);
        dialogBuilder.setView(dialogView);

        final EditText editEmail = (EditText) dialogView.findViewById(R.id.email);
        final Button btnReset = (Button) dialogView.findViewById(R.id.btn_reset_password);

        final AlertDialog dialog = dialogBuilder.create();

        btnReset.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String email = editEmail.getText().toString().trim();

                            if (TextUtils.isEmpty(email)) {
                                Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                            }

                                            dialog.dismiss();
                                        }
                                    });

                        }
                    });
                    dialog.show();
                }

}
