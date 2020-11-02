package com.example.sign;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashSet;
import java.util.List;


public class MessageChatActivity extends AppCompatActivity{
    private static final String TAG = "MessageActivity";
    private String img_url;
    private FirebaseFirestore db;
    private ArrayList<Message> arrayList;
    private ArrayList<String> dataList;
    private FirebaseUser user;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금

        Intent intent = getIntent();
        key = intent.getStringExtra("key");


        user = FirebaseAuth.getInstance().getCurrentUser();






        arrayList = new ArrayList<>();
        arrayList.clear();
        msgload(user.getUid(), key);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef_user = db.collection("users").document(key);
        docRef_user.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User usr = documentSnapshot.toObject(User.class);

                TextView who = findViewById(R.id.who_txt);
                who.setText(usr.getName());
            }
        });








        //실시간 이벤트 대기
        final DocumentReference docRef = db.collection("message").document();
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    msgload(user.getUid(), key);

                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                    msgload(user.getUid(), key);
                }
            }
        });









        findViewById(R.id.send_button).setOnClickListener(onClickListener);
        //findViewById(R.id.button_logout).setOnClickListener(onClickListener);

    }

    private void msg_sender() {
        //보낼 메시지 생성
        String send = user.getUid();
        String recv = key;
        EditText txt_msg = (EditText)findViewById(R.id.edit_txt);
        String msg = txt_msg.getText().toString();

        Message chat_msg = new Message(send, recv, msg, new Date());

        //파이어베이스 데이터 쓰기



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("message").document().set(chat_msg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //toastmsg(Popup_smsActivity.this, "회원정보 등록을 성공하였습니다.");
                        //loaderLayout.setVisibility(View.GONE);
                        //finish();
                        msgload(user.getUid(), key);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //toastmsg(Popup_smsActivity.this, "회원정보 등록에 실패하였습니다.");
                        //loaderLayout.setVisibility(View.GONE);
                        Log.w("error", "Error writing document", e);
                    }
                });
        txt_msg.setText("");
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.send_button:
                    msg_sender();
                    toastmsg("메시지를 보냈습니다.");
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

    private void msgload(final String uid, final String key1) {
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //게시글 메인 출력
        //게시글 옵션 설정값에 따라 분기
        db = FirebaseFirestore.getInstance();
        db.collection("message")
                .orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //카테고리 선택된 값만
                                if (document.getData().get("send").toString().equals(uid) && document.getData().get("recv").toString().equals(key1)) {
                                    arrayList.add(new Message(
                                            document.getData().get("send").toString(),
                                            document.getData().get("recv").toString(),
                                            document.getData().get("msg").toString(),
                                            new Date(document.getDate("time").getTime())));
                                } else if (document.getData().get("send").toString().equals(key1) && document.getData().get("recv").toString().equals(uid)) {
                                    arrayList.add(new Message(
                                            document.getData().get("send").toString(),
                                            document.getData().get("recv").toString(),
                                            document.getData().get("msg").toString(),
                                            new Date(document.getDate("time").getTime())));
                                }
                            }
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MessageChatActivity.this));

                            RecyclerView.Adapter myAdapter = new MessageChatAdapter(MessageChatActivity.this, arrayList, key1);
                            recyclerView.setAdapter(myAdapter);
                            //스크롤 자동으로 내리기
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

//    @Override
//    public void onBackPressed() {
//        startMain();
//    }

    private void startMain() {
        Intent intent = new Intent(this,  MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}


