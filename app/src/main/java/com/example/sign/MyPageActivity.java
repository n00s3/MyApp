package com.example.sign;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class MyPageActivity extends AppCompatActivity{
    private static final String TAG = "MyPageActivity";
    private String img_url;
    private FirebaseFirestore db;
    private ArrayList<PostInfo> arrayList;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());

//            docRef.update("latitude","위도1",
//                    "longitude", "경도2");


            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                startAct(MemberActivity.class);
                            }
                        }
                  ;      Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User usr = documentSnapshot.toObject(User.class);

                TextView txt1 = findViewById(R.id.TEXT1);
                txt1.setText("이름 : "+usr.getName());
                TextView txt2 = findViewById(R.id.TEXT2);
                txt2.setText("전화번호 : "+usr.getPhone());
                ImageView profile = findViewById(R.id.profileImageView);
                String temp = usr.getPhotoUrl();
                temp = temp.trim();
                Glide.with(MyPageActivity.this).load(temp).centerCrop().override(1000, 1000).into(profile);
            }
        });



        arrayList = new ArrayList<>();
        arrayList.clear();
        postload(user.getUid());






        findViewById(R.id.button_bak).setOnClickListener(onClickListener);
        //findViewById(R.id.button_logout).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                /*
                case R.id.button_logout:
                    toastmsg("로그아웃");
                    FirebaseAuth.getInstance().signOut();
                    startAct(login.class);
                    break;
                */
                case R.id.button_bak:
                    finish();
                    break;
            }
        }
    };

    private void startAct(Class c) {
        Intent intent = new Intent(this,  c);
        startActivity(intent);
    }

    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void postload(final String uid) {
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //게시글 메인 출력
        //게시글 옵션 설정값에 따라 지역 분기
        db.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //카테고리 선택된 값만
                                if (document.getData().get("publisher").toString().equals(uid)) {
                                    arrayList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            document.getData().get("laltitude").toString(),
                                            document.getData().get("longitude").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getData().get("address_lat").toString(),
                                            document.getData().get("pet_name").toString(),
                                            document.getData().get("pet_age").toString(),
                                            document.getData().get("pet_sex").toString(),
                                            document.getData().get("category").toString()+"MyPage",
                                            document.getId()));
                                }
                            }
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MyPageActivity.this));

                            RecyclerView.Adapter myAdapter = new MainAdapter(MyPageActivity.this, arrayList, "MyPage");
                            recyclerView.setAdapter(myAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startMain();
    }

    private void startMain() {
        Intent intent = new Intent(this,  MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}


