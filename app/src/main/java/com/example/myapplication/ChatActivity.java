package com.example.myapplication;

import static androidx.constraintlayout.widget.Constraints.TAG;

import static com.google.common.net.MediaType.*;

import android.content.Intent;
import android.content.SharedPreferences;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.adapter.search_user_recycler_adapter;
import com.example.myapplication.model.ChatMessages;
import com.example.myapplication.model.ChatRoom;
import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.common.net.MediaType;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.okhttp.internal.OkHostnameVerifier;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatRoom chatRoom;
    ChatRecyclerAdapter adapter;

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
        chatroomId = FirebaseUtil.getchattoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backButton = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backButton.setOnClickListener((v -> {
//            navigateToSearchUser();

            onBackPressed();
        }));

        sendMessageBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        otherUsername.setText(otherUser.getUsername());
        getOrCreateChatroom();
        setupChatRecyclerView();
    }

     void setupChatRecyclerView() {
         Query query = FirebaseUtil.getChatroomMessageReference(chatroomId).orderBy("timestamp",Query.Direction.DESCENDING);

         FirestoreRecyclerOptions<ChatMessages> options = new FirestoreRecyclerOptions.Builder<ChatMessages>()
                 .setQuery(query, ChatMessages.class).build();

         adapter = new ChatRecyclerAdapter(options,getApplicationContext());
         LinearLayoutManager manager = new LinearLayoutManager(this);
         manager.setReverseLayout(true);
         recyclerView.setLayoutManager(manager);
         recyclerView.setAdapter(adapter);
         adapter.startListening();
         adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
             @Override
             public void onItemRangeInserted(int positionStart, int itemCount) {
                 super.onItemRangeInserted(positionStart, itemCount);
                 recyclerView.smoothScrollToPosition(0);
             }
         });
    }

    void sendMessageToUser(String message) {

            chatRoom.setLastMessageTimestamp(Timestamp.now());
            chatRoom.setLastMessageSenderId(FirebaseUtil.currentUserId());
            chatRoom.setLastMessage(message);
         FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);


         ChatMessages chatMessages = new ChatMessages(message,FirebaseUtil.currentUserId(),Timestamp.now());
         FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessages).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
             @Override
             public void onComplete(@NonNull Task<DocumentReference> task) {
                 if (task.isSuccessful()){
                     messageInput.setText("");
                     sendNotification(message);
                 }
             }
         });

    }

     void sendNotification(String message) {

        //current Username,userid,userToken;
         FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
             if (task.isSuccessful()){
                 UserModel currentUser = task.getResult().toObject(UserModel.class);
                 try {
                     JSONObject jsonObject = new JSONObject();

                     JSONObject notificationObject = new JSONObject();
                        notificationObject.put("title",currentUser.getUsername());
                        notificationObject.put("body",message);

                     JSONObject dataObject = new JSONObject();
                     dataObject.put("userId",currentUser.getUserId());

                     jsonObject.put("notification",notificationObject);
                     jsonObject.put("data",dataObject);
                     jsonObject.put("to",otherUser.getFcmToken());

                     callApi(jsonObject);

                 }catch (Exception e){

                 }
              }

         });




    }

    void callApi(JSONObject jsonObject){

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAA3Z08dkE:APA91bEqic9nvXzRe2yfqEhwFZEBY717-o1V6unnUxaFYzkusOXXVlt4F9CuEQBx2Oxhh5pXK5h1GTM-e8Pww3DoY3MMQqqgRwgFXZYAgRTmXo2zAmVCVqaZy5Vbev_bMfDp3bzUg7lq")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });



    }


    void getOrCreateChatroom() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoom = task.getResult().toObject(ChatRoom.class);
                if (chatRoom == null){
                    chatRoom = new ChatRoom(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatRoom);
                }
            }
        });
    }
//no need of this code now as we have fixed that app crashing problem by changing on resume factor
// to notify data set changed from start listening.
    //if the app crashes again uncomment the below code for smooth running and going back to search user fragment.


//    private void navigateToSearchUser() {
//        // Get the current search term from the EditText
//        String currentSearchTerm = ""; // Replace this with the actual way to get the current search term
//        // For example, if you have an EditText in the current layout:
//        // String currentSearchTerm = editTextSearch.getText().toString().trim();
//
//        // Save the current search term to SharedPreferences
//        saveSearchTermToPreferences(currentSearchTerm);
//
//        // Start the search_user activity
//        Intent intent = new Intent(ChatActivity.this, search_user.class);
//        startActivity(intent);
////
////        // Finish the current ChatActivity to remove it from the back stack
//        finish();
//    }
//
//    private void saveSearchTermToPreferences(String searchTerm) {
//        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("searchTerm", searchTerm);
//        editor.apply();
//    }



//    private void navigateToSearchUser() {
//        // Start the search_user activity
//        Intent intent = new Intent(ChatActivity.this, search_user.class);
//        startActivity(intent);
//
//        // Finish the current ChatActivity to remove it from the back stack
//        finish();
//    }


}