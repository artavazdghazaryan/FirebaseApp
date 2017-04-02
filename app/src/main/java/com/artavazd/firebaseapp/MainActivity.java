package com.artavazd.firebaseapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
        //realtime-database UI
        database = FirebaseDatabase.getInstance();
        etMessage=(EditText)findViewById(R.id.et_message);

        tvBoard=(TextView)findViewById(R.id.tv_board);
        bindTVBoardToFirebase("message");
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

        myRef.setValue(message);
    }

    private void bindTVBoardToFirebase(String reference){
        // Read from the database
        myRef = database.getReference(reference);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                String s=tvBoard.getText().toString();
                tvBoard.setText("");
                tvBoard.append(value+"\n");
                tvBoard.append(s);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
