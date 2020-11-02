package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberActivity extends AppCompatActivity {
    private int flag=0;
    private ImageView profileImageVIew;
    private String profilePath;
    private RelativeLayout loaderLayout;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button bt;
        bt = (Button) findViewById(R.id.button_check);
        bt.setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderll);
        profileImageVIew = findViewById(R.id.profileImageView);
        profileImageVIew.setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_check:
                    //profileupdate();
                    storageUploader();

                    if(flag == 1)
                        finish();
                    break;
                case R.id.profileImageView:
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };


    private void storageUploader() {
        final String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.edit_phone)).getText().toString();

        if (name.length() > 0 && phoneNumber.length() > 9) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();

            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                User memberInfo = new User(name, phoneNumber);
                storeUploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();


                                String latitude="0";
                                String longitude="0";
                                //회원 텍스트정보-2
                                User memberInfo = new User(name, phoneNumber, downloadUri.toString(), latitude, longitude);
                                storeUploader(memberInfo);
                                //
                            } else {
                                toastmsg(MemberActivity.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            toastmsg(MemberActivity.this, "회원정보를 입력해주세요.");
        }
    }


    private void storeUploader(User memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        toastmsg(MemberActivity.this, "회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastmsg(MemberActivity.this, "회원정보 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w("error", "Error writing document", e);
                    }
                });
    }



    private void profileupdate() {
        String name = ((EditText)findViewById(R.id.edit_name)).getText().toString();
        String phone = ((EditText)findViewById(R.id.edit_phone)).getText().toString();

        if (name.length() >0 && phone.length() > 9 ) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();     //초기화

            //유저클래스 파이어베이스 등록
            User userinfo = new User(name,phone);
            if (user != null) {
                db.collection("users").document(user.getUid()).set(userinfo);
                flag=1;


            }
        } else {
            Toast.makeText(MemberActivity.this, "정확히 입력해주세요", Toast.LENGTH_SHORT).show();
            flag=0;
        }
    }

    private void startMain() {
        Intent intent = new Intent(this,  MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", "image");
        startActivityForResult(intent, 0);
    }

    public void toastmsg(MemberActivity memberActivity, String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



}
