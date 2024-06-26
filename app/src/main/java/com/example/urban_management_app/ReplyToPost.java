package com.example.urban_management_app;

// necessary imports
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;

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

        // get postId from intent
        String postId = getIntent().getStringExtra("post_id");

        // firebase reference for replies
        repliesRef = FirebaseDatabase.getInstance().getReference("replies").child(postId);

        buttonSendReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendReply();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendReply() throws IOException {
        String message = editTextReply.getText().toString().trim();

        // error checking
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a reply message", Toast.LENGTH_SHORT).show();
            return;
        }

        // bad words check
        BadWordsFilter badWordsFilter = new BadWordsFilter(getResources().getAssets().open("bad-words.txt"));

        if (badWordsFilter.containsSwearWord(message)) {
            Toast.makeText(this, "Do not use inappropriate language, try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // current user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // fetch username from user profile database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if user exists, fetch
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String username = user.getUsername();

                        // new reply object
                        Reply reply = new Reply(userId, username, message);

                        // push reply to firebase
                        repliesRef.push().setValue(reply);

                        Toast.makeText(ReplyToPost.this, "Reply sent successfully", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                } else {
                    // if user doesn't exist
                    Toast.makeText(ReplyToPost.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // todo: handle database error
                Toast.makeText(ReplyToPost.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

