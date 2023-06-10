package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.v;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;
    private Button mBtnRegister, mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this); // Initialize FirebaseApp first

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnLogin = findViewById(R.id.btn_login);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일과 비밀번호를 EditText에서 가져옵니다.
                String strEmail = mEtEmail.getText().toString().trim();
                String strPwd = mEtPwd.getText().toString().trim();

                // 이메일과 비밀번호가 비어 있는지 확인합니다.
/*                if (strEmail.isEmpty() || strPwd.isEmpty()) {
                      Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                      return;
                  } */
                // 이메일이 비어 있는지 확인합니다.
                if (strEmail.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 비밀번호가 비어 있는지 확인합니다.
                if (strPwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 로그인을 수행합니다.
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인한 사용자 식별자 가져오기
                            String userIdentifier = mFirebaseAuth.getCurrentUser().getEmail();

                            // 로그인 성공 시 MainActivity를 시작합니다.
                            // MainActivity 시작 및 사용자 ID 전달
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userIdentifier", userIdentifier);
                            startActivity(intent);
                            finish();
                        } else {
                            // 로그인 실패 시 메시지를 표시합니다.
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }
}
