package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.model.UserModel;
import com.example.myapplication.utils.AndroidUtil;
import com.example.myapplication.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;


public class profle_Fragment extends Fragment {

    private final int Gallery_req_code = 1000;

    ImageView profilePic;
    EditText usernameInput;
    EditText PhoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;

    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    ActivityResultLauncher<String> photoPickerLauncher; // New launcher for photo picker
    Uri selectedImageUri;
    public profle_Fragment() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
            if (result.getResultCode()==Activity.RESULT_OK){
                Intent data = result.getData();
                if (data!=null && data.getData()!=null){
                    selectedImageUri = data.getData();
                    AndroidUtil.setProfilePic(getContext(),selectedImageUri,profilePic);
                }
            }
                });

        photoPickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        selectedImageUri = result;
                        AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profle_, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_user_name);
        PhoneInput = view.findViewById(R.id.profile_phone_number);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        logoutBtn = view.findViewById(R.id.profile_log_out_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);

        getUserData();

        updateProfileBtn.setOnClickListener(v->{
            updateBtnClick();
        });

        logoutBtn.setOnClickListener(v->{

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FirebaseUtil.logout();
                    Intent intent = new Intent(getContext(),Splash.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            });


        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the photoPickerLauncher to pick an image from the gallery
                photoPickerLauncher.launch("image/*");
//                startActivityForResult();
            }
        });


//            @Override
//            public void onClick(View view) {
//                ImagePicker.with(profle_Fragment.this)
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
//            }

//        });



        return view;
    }

    void updateBtnClick() {
        String newUserName = usernameInput.getText().toString();
        if (newUserName.isEmpty() || newUserName.length() < 3) {
            usernameInput.setError("Username length should be at least 3 Chars");
            return;
        }
        currentUserModel.setUsername(newUserName);
        setInProgress(true);
        if (selectedImageUri != null) {
            FirebaseUtil.getcurrentProfilePicStorageRef().putFile(selectedImageUri).addOnCompleteListener(task -> {
                updateToFireStore();
            });
        } else {
            updateToFireStore();
        }
    }
    void updateToFireStore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                AndroidUtil.showToast(getContext(),"Update Successful");
            }else{
                AndroidUtil.showToast(getContext(),"Update failed");

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        profilePic.setImageURI(uri);
    }

    void getUserData() {
        setInProgress(true);
         FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
             setInProgress(false);
             currentUserModel = task.getResult().toObject(UserModel.class);
             usernameInput.setText(currentUserModel.getUsername());
             PhoneInput.setText(currentUserModel.getPhone());
         });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}