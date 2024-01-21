package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class Login extends AppCompatActivity {
//intializing variable for every element in xml file;
    CountryCodePicker  countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//storing or say fetching id of the element from the xml file
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp);
        progressBar = findViewById(R.id.login_progress_bar);
        //disabling the progressbar visibility
        progressBar.setVisibility(View.GONE);
        //registering the country code picker
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
            //if code or number is not valid return phone number is not valid
            if (!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone Number is not Valid");
                return;
            }
            //moving to next page
            Intent intent= new Intent(Login.this,OTP.class);
            //passing the phone number to the next page
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });

    }
}