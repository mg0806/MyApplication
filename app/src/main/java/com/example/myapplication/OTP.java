package com.example.myapplication;



import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.myapplication.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class OTP extends AppCompatActivity {
    String phoneNumber;
    Long timeOutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText  otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOtpTextview;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        //intialized variables from xml file
        otpInput = findViewById(R.id.login_OTP);
        nextBtn = findViewById(R.id.login_next);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextview = findViewById(R.id.resend_otp);


        //printing the phone number that we have passed from login page
        phoneNumber = getIntent().getExtras().getString("phone");

        sendOtp( phoneNumber, false);

        nextBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString();
           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
           signIn(credential);


        });

        resendOtpTextview.setOnClickListener((v) -> sendOtp(phoneNumber,true));
    }
    //created sendOtp method
        void sendOtp(String phoneNumber,boolean isResend){
        startResendTimer();
            setInProgress(true);
            PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
                    .setTimeout(timeOutSeconds, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signIn(phoneAuthCredential);
                            setInProgress(false);


                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                            setInProgress(false);
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            verificationCode = s;
                            resendingToken = forceResendingToken;
                            AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
                            setInProgress(false);


                        }
                    });
            if (isResend){
                PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
            }
            else{
                PhoneAuthProvider.verifyPhoneNumber(builder.build());

            }
        }
        void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
        }

        void signIn(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
            setInProgress(true);
            mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    setInProgress(false);
                 if(task.isSuccessful()){
                     //if task is successful move to next page
                     Intent intent = new Intent(OTP.this,Login_username.class);
                     //i forgot to write this line and my app kept getting crashed and i was wondering why ?? lol
                     intent.putExtra("phone",phoneNumber);
                     startActivity(intent);
                 }
                 else{
                     AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");

                 }
                }
            });

        }


void startResendTimer(){
        resendOtpTextview.setEnabled(false);
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            timeOutSeconds--;
            resendOtpTextview.setText("Resend OTP in "+timeOutSeconds+"seconds");
            if (timeOutSeconds<=0){
                timeOutSeconds = 60L;
                timer.cancel();
                runOnUiThread(()-> resendOtpTextview.setEnabled(true));
            }
        }
    },0,1000);
}

}