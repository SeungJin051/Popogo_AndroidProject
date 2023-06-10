package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button mBtn_logout;
    private TextView mUserId;

    EditText whatText;  // 번역할 텍스트를 입력하는 EditText
    TextView resultText;   // 번역 결과를 표시하는 TextView
    Button resultBtn;          // 번역 실행 버튼
    String 번역결과 = "";   // 번역 결과를 저장하는 변수

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        mUserId = findViewById(R.id.Title);
        TextView toLanguageTV = findViewById(R.id.toLanguageTV);
        TextView fromLanguageTV = findViewById(R.id.fromLanguageTV);

        mBtn_logout = findViewById(R.id.btn_logout);
        ImageButton languageChangeIB = findViewById(R.id.languageChangeIB);

        resultBtn = findViewById(R.id.button2);
        whatText = findViewById(R.id.whatTranslateET);
        resultText = findViewById(R.id.changedTextTV);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Intent에서 사용자 이메일 가져오기
        String userEmail = getIntent().getStringExtra("userIdentifier");

        resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Translate translate = new Translate() {
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        // 값과 현재 날짜 가져오기
                        String whatTranslate = whatText.getText().toString();
                        String toLanguage = toLanguageTV.getText().toString();
                        String changedText = 번역결과;
                        String fromLanguage = fromLanguageTV.getText().toString();
                        Date currentDate = new Date();

                        // 문서 참조 생성
                        DocumentReference docRef = db.collection("AndroidProject").document();

                        // 데이터 객체 생성
                        Map<String, Object> data = new HashMap<>();
                        data.put("whatTranslate", whatTranslate);
                        data.put("toLanguage", toLanguage);
                        data.put("changedText", changedText);
                        data.put("fromLanguage", fromLanguage);
                        data.put("created_at", currentDate);
                        data.put("userEmail", userEmail);
                        // Firestore에 데이터 저장
                        docRef.set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 데이터 저장 성공
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // 데이터 저장 실패
                                    }
                                });
                    }
                };
                translate.execute();
            }
        });


        // 이메일에서 사용자 이름 추출
        String userName = extractUsernameFromEmail(userEmail);
        // TextView 초기화 및 사용자 이름 표시
        mUserId.setText(userName);
        // 로그아웃 버튼 클릭 이벤트 처리
        mBtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        // ImageButton 클릭 이벤트 처리
        languageChangeIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 텍스트 가져오기
                String fromLanguage = fromLanguageTV.getText().toString();
                String toLanguage = toLanguageTV.getText().toString();

                // 언어 교환
                String temp = fromLanguage;
                fromLanguage = toLanguage;
                toLanguage = temp;

                // 언어 TextView 업데이트
                fromLanguageTV.setText(fromLanguage);
                toLanguageTV.setText(toLanguage);
            }

        });

        // 결과 이동
        mUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("userIdentifier", userEmail); // Pass the userID to ResultActivity
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageTV.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // 이메일에서 사용자 이름 추출하는 메서드
    private String extractUsernameFromEmail(String email) {
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            if (parts.length == 2) {
                return parts[0];
            }
        }
        return email;
    }

    class Translate extends AsyncTask<String, Void, String> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String clientId = "C0u6iySNChciDq83oX3Q";             // 네이버 API 클라이언트 아이디
            String clientSecret = "cNw8vMle4P";                   // 네이버 API 클라이언트 시크릿
            try {
                TextView fromLanguageTV = findViewById(R.id.fromLanguageTV);
                TextView toLanguageTV = findViewById(R.id.toLanguageTV);

                String fromLanguage = fromLanguageTV.getText().toString();
                String toLanguage = toLanguageTV.getText().toString();

                String text = URLEncoder.encode(whatText.getText().toString(), "UTF-8");  // 번역할 문장
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                // 네이버 클라이언트 아이디 설정
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                // 네이버 클라이언트 시크릿 설정
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // 번역 소스 언어는 한국어(ko), 목표 언어는 영어(en), 번역할 텍스트는 변수 text에 저장된 값
                String postParams;
                if (toLanguage.equals("한국어") && fromLanguage.equals("영어")) {
                    postParams = "source=ko&target=en&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("일본어")) {
                    postParams = "source=ko&target=ja&text=" + text;
                }else if (toLanguage.equals("한국어") && fromLanguage.equals("중국어")) {
                    postParams = "source=ko&target=zh-CN&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("베트남어")) {
                    postParams = "source=ko&target=vi&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("인도네시아어")) {
                    postParams = "source=ko&target=id&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("태국어")) {
                    postParams = "source=ko&target=th&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("독일어")) {
                    postParams = "source=ko&target=de&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("러시아어")) {
                    postParams = "source=ko&target=ru&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("스페인어")) {
                    postParams = "source=ko&target=es&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("이탈리아어")) {
                    postParams = "source=ko&target=it&text=" + text;
                } else if (toLanguage.equals("한국어") && fromLanguage.equals("프랑스어")) {
                    postParams = "source=ko&target=fr&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("한국어")) {
                    postParams = "source=en&target=ko&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("일본어")) {
                    postParams = "source=en&target=ja&text=" + text;
                }else if (toLanguage.equals("영어") && fromLanguage.equals("중국어")) {
                    postParams = "source=en&target=zh-CN&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("베트남어")) {
                    postParams = "source=en&target=vi&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("인도네시아어")) {
                    postParams = "source=en&target=id&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("태국어")) {
                    postParams = "source=en&target=th&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("독일어")) {
                    postParams = "source=en&target=de&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("러시아어")) {
                    postParams = "source=en&target=ru&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("스페인어")) {
                    postParams = "source=en&target=es&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("이탈리아어")) {
                    postParams = "source=en&target=it&text=" + text;
                } else if (toLanguage.equals("영어") && fromLanguage.equals("프랑스어")) {
                    postParams = "source=en&target=fr&text=" + text;
                } else {
                    // Default translation parameters if no specific conditions are met
                    postParams = "source=en&target=en&text=" + text;
                }

                // Check if source and target languages are different
                if (postParams.contains("source=" + toLanguage.toLowerCase())) {
                    // Show an error message or handle the condition accordingly
                    System.out.println("Error: Source and target languages must be different.");
                }

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());

                // HTTP 요청이 성공
                if (responseCode == 200) {
                    // 번역 결과를 문자열 형태로 저장
                    String result = response.toString();
                    // response에서 "translatedText" 다음에 오는 값만 추출하여 번역된 텍스트를 얻음
                    // 추출한 텍스트는 translationResult 변수에 저장
                    번역결과 = result.split("\"translatedText\":\"")[1].split("\"")[0];
                } else {
                    번역결과 = "번역 실패";
                }

            } catch (Exception e) {
                System.out.println(e);
                번역결과 = "번역 실패";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            resultText.setText(번역결과);
        }
    }
}
