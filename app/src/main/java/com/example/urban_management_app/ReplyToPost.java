package com.example.urban_management_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReplyToPost extends AppCompatActivity {

    private EditText editTextReply;
    private Button buttonSendReply;

    private DatabaseReference repliesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_to_post);

        editTextReply = findViewById(R.id.editTextReply);
        buttonSendReply = findViewById(R.id.buttonSendReply);

        // Get postId from intent
        String postId = getIntent().getStringExtra("post_id");

        // Firebase database reference for replies
        repliesRef = FirebaseDatabase.getInstance().getReference("replies").child(postId);

        buttonSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReply();
            }
        });
    }

    private void sendReply() {
        String replyMessage = editTextReply.getText().toString().trim();

        // Check if reply message is not empty
        if (replyMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a reply message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a new reply object
        Reply reply = new Reply(userId, replyMessage);

        // Push the reply to Firebase database
        repliesRef.push().setValue(reply);

        Toast.makeText(this, "Reply sent successfully", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }
}

