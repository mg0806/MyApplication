package com.example.myapplication;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backButton;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get userModel

        otherUser = AndroidUtil.getUserModelFromInent(getIntent());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backButton.setOnClickListener((v -> {
            navigateToSearchUser();

            onBackPressed();
        }));
        otherUsername.setText(otherUser.getUsername());

    }

    private void navigateToSearchUser() {
        navigateToSearchUser();

        //yaha se dekh lena

    }

//    private void navigateToSearchUser() {
//        // Start the search_user activity
//        Intent intent = new Intent(ChatActivity.this, search_user.class);
//        startActivity(intent);
//
//        // Finish the current ChatActivity to remove it from the back stack
//        finish();
//    }



}