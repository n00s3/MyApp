package com.example.sign;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance();


        //회원가입 완료 버튼
        Button bt;
        bt = (Button) findViewById(R.id.button_sign);
        bt.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }




    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_sign:  //회원가입 완료 버튼
                String email;
                String password;

                EditText editTextEmail;
                editTextEmail = findViewById(R.id.txt_email);
                EditText editTextPassword;
                editTextPassword = findViewById(R.id.txt_password);

                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if(email.length() <=0 || password.length() <= 0) {
                    Toast.makeText(Sign.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                    break;
                }


                createUser(email, password);

                editTextEmail.setText("");
                editTextPassword.setText("");
                finish();
                break;
        }
    }

    private void createUser(String email, String password) {
        final RelativeLayout loderLayout = findViewById(R.id.loaderll);
        loderLayout.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loderLayout.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(Sign.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Sign.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
