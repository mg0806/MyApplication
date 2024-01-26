package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //if it is from notification then perform the certain task else the normal way.
        if ( FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            //from notification.
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionRefernce().document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    UserModel  model = task.getResult().toObject(UserModel.class);
                    Intent mainIntent = new Intent(this,MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(mainIntent);

                    Intent intent = new Intent(this, ChatActivity.class);
                    AndroidUtil.passUserModelAsIntent(intent,model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });


        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseUtil.isLoggedIn()){
                        startActivity(new Intent(Splash.this,MainActivity.class));
                    }
                    else{
                        startActivity(new Intent(Splash.this,Login.class));
                    }
                    finish();
                }
            },1000);
        }


    }
}

