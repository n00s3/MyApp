package com.example.sign;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuthl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //firebase
        firebaseAuthl = FirebaseAuth.getInstance();


        Button bt;
        bt = (Button) findViewById(R.id.button_login);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.button_signup);
        bt.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_login:
                String email;
                String password;

                EditText TextEmail;
                TextEmail = findViewById(R.id.txt_email);
                EditText TextPassword;
                TextPassword = findViewById(R.id.txt_password);
                email = TextEmail.getText().toString();
                password = TextPassword.getText().toString();

                if (email.length() <= 0 || password.length() <=0 ) {
                    Toast.makeText(login.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                    break;
                }

                loginUser(email, password);     //로그인
                TextEmail.setText("");
                TextPassword.setText("");
                break;
            case R.id.button_signup:
                startSign();
                break;
        }
    }

    private void loginUser(String email, String password)
    {
        final RelativeLayout loderLayout = findViewById(R.id.loaderll);
        loderLayout.setVisibility(View.VISIBLE);

        firebaseAuthl.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loderLayout.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuthl.getCurrentUser();
                            Toast.makeText(login.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                            startMain();
                        } else {
                            Toast.makeText(login.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void startMain() {
        Intent intent = new Intent(this,  MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void startSign() {
        Intent intent = new Intent(this,  Sign.class);
        startActivity(intent);
    }

}
