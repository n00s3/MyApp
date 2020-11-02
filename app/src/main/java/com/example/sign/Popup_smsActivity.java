package com.example.sign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Popup_smsActivity extends Activity {

    TextView txtText;
    String name, number, title;
    private FirebaseUser user;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //화면 회전잠금
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sms_popup);

        //UI 객체생성
        //txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        title = intent.getStringExtra("title");
















    }

    //확인 버튼 클릭
    public void mOnClose(View v){

            //데이터 전달하기
            Intent intent = new Intent();
            intent.putExtra("result", 1);
            setResult(RESULT_OK, intent);

            //액티비티(팝업) 닫기
            finish();
    }


    public void mOnClick(View v) {
        //메시지 보내기
        /*
        String sms_text = name+"님 ["+title+"]"+"글 보고 연락남깁니다.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, sms_text, null, null);

            toastmsg("메시지를 보냈습니다.");
        } catch (Exception e) {
            toastmsg("메시지 전송 실패");
        }
        */

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();

        //보낼 메시지 생성
        String send = user.getUid();
        String recv = intent.getStringExtra("publisher");
        EditText txt_msg = (EditText)findViewById(R.id.text_send);
        String msg = txt_msg.getText().toString();
        Message chat_msg = new Message(send, recv, msg, new Date());

        //파이어베이스 데이터 쓰기
        msg_sender(chat_msg);



        finish();

    }
    private void msg_sender(Message chat_msg) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("message").document().set(chat_msg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //toastmsg(Popup_smsActivity.this, "회원정보 등록을 성공하였습니다.");
                        //loaderLayout.setVisibility(View.GONE);
                        finish();
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
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            finish();
        }
        return true;
    }

    /*
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
    */
    public void toastmsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}