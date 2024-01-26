package com.example.myapplication.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if (currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());

    }

    public  static CollectionReference allUserCollectionRefernce(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("Chatroom").document(chatroomId);
    }
    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");

    }

    public static String getchattoomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }
        else{
            return userId2+"_"+userId1;
        }
    }
    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("Chatroom");
    }
    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds ){
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionRefernce().document(userIds.get(1));
        }else{
            return allUserCollectionRefernce().document(userIds.get(0));

        }
    }


    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getcurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("Profile_Pic")
                .child(FirebaseUtil.currentUserId());
    }


}
