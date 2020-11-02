package com.example.sign;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class MessageActivity extends AppCompatActivity{
    private static final String TAG = "MessageActivity";
    private String img_url;
    private FirebaseFirestore db;
    private ArrayList<PostInfo> arrayList;
    private ArrayList<String> dataList;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금


        findViewById(R.id.send_layout).setVisibility(View.GONE);

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


            }
        });



        arrayList = new ArrayList<>();
        dataList = new ArrayList<>();
        arrayList.clear();
        msgload(user.getUid());

        //중복제거한 전체 메시지 로드 하기












        //findViewById(R.id.button_bak).setOnClickListener(onClickListener);
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

    private void msgload(final String uid) {
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //게시글 메인 출력
        //게시글 옵션 설정값에 따라 지역 분기
        db.collection("message")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if (document.getData().get("send").toString().equals(document.getData().get("recv").toString()))
                                    continue;
                                //카테고리 선택된 값만
                                if (document.getData().get("send").toString().equals(uid)) {
                                    dataList.add(document.getData().get("recv").toString());
                                } else if (document.getData().get("recv").toString().equals(uid)) {
                                    dataList.add(document.getData().get("send").toString());
                                }
                            }

                            HashSet<String> distinctData = new HashSet<String>(dataList);
                            List<String> newList = new ArrayList<String>(distinctData);

                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

                            RecyclerView.Adapter myAdapter = new MessageAdapter(MessageActivity.this, (ArrayList<String>) newList, "MyPage");
                            recyclerView.setAdapter(myAdapter);

                            /*//어댑터 적용
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

                            RecyclerView.Adapter myAdapter = new MainAdapter(MessageActivity.this, arrayList, "MyPage");
                            recyclerView.setAdapter(myAdapter);
                            */
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


