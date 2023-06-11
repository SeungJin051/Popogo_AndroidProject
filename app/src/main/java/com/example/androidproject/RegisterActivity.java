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

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
        private FirebaseAuth mFirebaseAuth;
        private DatabaseReference mDatabaseRef;
        private EditText mEtEmail, mEtPwd;
        private Button mBtnRegister, mBtnCancel;

    @Override
        protected void onCreate (Bundle savedInstanceState){

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            mFirebaseAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증
            mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 리얼타임 데이터베이스

            mEtEmail = findViewById(R.id.et_email);
            mEtPwd = findViewById(R.id.et_pwd);
            mBtnRegister = findViewById(R.id.btn_register);
            mBtnCancel = findViewById(R.id.btn_cancel);

            mBtnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 회원가입 처리
                    String strEmail = mEtEmail.getText().toString();
                    String strPwd = mEtPwd.getText().toString();

                    // 이메일과 비밀번호가 비어있는지 확인
                    if (strEmail.isEmpty() || strPwd.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 이메일 형식이 올바른지 확인
                    if (!strEmail.contains("@")) {
                        Toast.makeText(RegisterActivity.this, "이메일에 @를 포함하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // 비밀번호 길이가 6자 이상인지 확인
                    if (strPwd.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "6자 이상의 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Firebase Auth 처리
                    // createUserWithEmailAndPassword = strEmail, strPwd를 스트링으로 현재 RegisterActivity
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 파이어베이스 유저 만들기
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                // account.set
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPwd);

                                // account 넣어주기
                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                // 가입이 이루어져을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                finish();
                                Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });


            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            });
        }
    }
