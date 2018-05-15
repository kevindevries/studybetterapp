package com.example.kevdevries.studybetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendStudyActivity extends AppCompatActivity {
    private TextView email;
    private Button btnExisting, btnSend;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private List<StudyModel> list;
    private DatabaseReference rootRef, userRootRef;
    private RecyclerView recycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_study);

        email = (TextView) findViewById(R.id.email);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnExisting = (Button) findViewById(R.id.existing);
        recycle = (RecyclerView) findViewById(R.id.recycle);

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d("Firebase", "token " + token);

        Intent intentExtras = getIntent();

        Bundle extrasBundle = intentExtras.getExtras();
        if (!extrasBundle.isEmpty()) {
            String userDetails = extrasBundle.getString("title");
            email.setText(userDetails);
        }

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
                    Log.i("Second dataSnapshot:", "Grind ID");

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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFCMPush();

            }
        });
/*
        // recipients
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(53779);
        userIds.add(960);

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setPushType(QBPushType.GCM);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("data.message", "Hello");
        data.put("data.type", "welcome message");
        event.setMessage(data);

        QBPushNotifications.createEvent(event).performAsync( new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });*/


        btnExisting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SendStudyRecyclerAdapter recyclerAdapter = new SendStudyRecyclerAdapter(list, SendStudyActivity.this);
                RecyclerView.LayoutManager recyce = new LinearLayoutManager(SendStudyActivity.this);
                recycle.setLayoutManager(recyce);
                recycle.setItemAnimator(new DefaultItemAnimator());
                recycle.setAdapter(recyclerAdapter);

            }

        });
    }

    private void sendFCMPush() {

        rootRef = FirebaseDatabase.getInstance().getReference("Users").child("NAd3ce61a2cEjOAUwdQiIHkHFXg1").child("token");

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String token = dataSnapshot.getValue(String.class);

                Intent intentExtras = getIntent();

                Bundle extrasBundle = intentExtras.getExtras();
                if (!extrasBundle.isEmpty()) {
                    String userDetails = extrasBundle.getString("title");
                    email.setText(userDetails);
                }

                //get firebase auth instance
                auth = FirebaseAuth.getInstance();
                //get current user
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                final String Legacy_SERVER_KEY = "AIzaSyBgXO9gKVzysokShiTwh4NeC_T_OHAQ_Bs";
                String msg = user.toString();
                String title = "The following user is looking to set up a Study Group with you. See below for details";
                String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";

                Log.d("Firebase", "token " + token);

                JSONObject obj = null;
                JSONObject objData = null;
                JSONObject dataobjData = null;

                try {
                    obj = new JSONObject();
                    objData = new JSONObject();

                    objData.put("body", msg);
                    objData.put("title", title);
                    objData.put("sound", "default");
                    objData.put("icon", "default"); //   icon_name image must be there in drawable
                    objData.put("tag", token);
                    objData.put("priority", "high");

                    dataobjData = new JSONObject();
                    dataobjData.put("text", msg);
                    dataobjData.put("title", title);

                    obj.put("to", token);
                    //obj.put("priority", "high");

                    obj.put("notification", objData);
                    obj.put("data", dataobjData);
                    Log.e("!_@rj@_@@_PASS:>", obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FCM_PUSH_URL, obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("!_@@_SUCESS", response + "");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("!_@@_Errors--", error + "");
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(SendStudyActivity.this);
                int socketTimeout = 1000 * 60;// 60 seconds
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                jsObjRequest.setRetryPolicy(policy);
                requestQueue.add(jsObjRequest);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }

        });
    }
}
