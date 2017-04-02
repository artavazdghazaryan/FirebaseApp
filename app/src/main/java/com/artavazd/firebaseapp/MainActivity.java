package com.artavazd.firebaseapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();

    public static String EXTRA_USER_NAME = "username";
    private TextView tv1, tv2;
    private TextView tvBoard;
    private EditText etMessage;
    private Button bSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();


    }

    private void setupUI() {
        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setText(getIntent().getExtras().getString(EXTRA_USER_NAME));

        tv2 = (TextView) findViewById(R.id.tv2);
        tv2.append(". Sign out?");
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        //RealTime Database UI
        database = FirebaseDatabase.getInstance();
        etMessage=(EditText)findViewById(R.id.et_message);

        tvBoard=(TextView)findViewById(R.id.tv_board);
        bindTVBoardToFirebaseChild("message");
        bSendMessage=(Button)findViewById(R.id.b_send_message);
        bSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateMessage()){
                    return;
                }
                sendMessage("message", etMessage.getText().toString());
            }
        });
        }

    private boolean validateMessage() {

            boolean result = true;
            if (TextUtils.isEmpty(etMessage.getText().toString())) {
                etMessage.setError("Required");
                result = false;
            } else {
                etMessage.setError(null);
            }

            if ((etMessage.getText().toString()).length()<3) {
                etMessage.setError("Too short");
                result = false;
            } else {
                etMessage.setError(null);
            }

            return result;
        }

   private FirebaseDatabase database;
   private DatabaseReference myRef;

    private void sendMessage(String reference,
                             String message) {
        // Write a message to the database
        myRef = database.getReference(reference);

        myRef.push().setValue(message);
    }

    private void bindTVBoardToFirebaseChild(String reference){
        // Read from the database
        myRef = database.getReference(reference);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                String message = dataSnapshot.getValue(String.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                tvBoard.append(message+"\n");
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

            }
        };
        myRef.addChildEventListener(childEventListener);
    }


}
