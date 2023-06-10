package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.androidproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private LinearLayout documentContainer;

    private Button btn_logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btn_logout = findViewById(R.id.btn_logout);

        // 로그아웃 버튼 클릭 이벤트 처리
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.putExtra("userIdentifier", getIntent().getStringExtra("userIdentifier")); // Pass the userID back to MainActivity
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        // 결과를 표시할 컨테이너 뷰를 가져옴
        documentContainer = findViewById(R.id.documentContainer);

        // Firestore 데이터 가져오기 및 타임스탬프 기준으로 내림차순 정렬
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("AndroidProject")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Firestore 컬렉션에서 문서들을 가져옴
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();

                            // 문서들을 순회하며 뷰를 생성하여 채움
                            for (DocumentSnapshot document : documents) {
                                // 문서로부터 필드 데이터를 가져옵니다.
                                String whatTranslate = document.getString("whatTranslate");
                                String toLanguage = document.getString("toLanguage");
                                String changedText = document.getString("changedText");
                                String fromLanguage = document.getString("fromLanguage");
                                Date createdAt = document.getDate("created_at");
                                String userEmail = document.getString("userEmail");

                                // 원하는 날짜 형식과 한국어 Locale을 사용하여 SimpleDateFormat를 생성합니다
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일 HH시 mm분 ss초", new Locale("ko"));

                                // createdAt 날짜를 한국어로 포맷팅합니다
                                String formattedDate = sdf.format(createdAt);

                                // 각 문서마다 새로운 LinearLayout을 생성합니다.
                                LinearLayout parentLayout = new LinearLayout(ResultActivity.this);
                                LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                parentLayoutParams.setMargins(16, 16, 16, 16); // 여백 설정 (왼쪽, 위, 오른쪽, 아래)
                                parentLayout.setLayoutParams(parentLayoutParams);

                                // 각 문서마다 새로운 TextView를 생성합니다.
                                TextView textView = new TextView(ResultActivity.this);
                                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                textView.setTextAppearance(R.style.CustomTextViewStyle); // 커스텀 스타일 적용
                                textView.setText(String.format("사용자 아이디 : %s\n생성일 : %s\n원본 언어 : %s\n원본 내용 : %s\n번역 언어 : %s\n번역된 언어 : %s",
                                        userEmail, formattedDate, toLanguage, whatTranslate, fromLanguage, changedText));


                                // TextView를 부모 레이아웃에 추가합니다.
                                parentLayout.addView(textView);

                                // 부모 레이아웃을 컨테이너 뷰에 추가합니다.
                                documentContainer.addView(parentLayout);

                                // 구분선을 추가합니다.
                                View divider = new View(ResultActivity.this);
                                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                divider.setLayoutParams(dividerParams);
                                divider.setBackgroundColor(ContextCompat.getColor(ResultActivity.this, R.color.dividerColor));
                                documentContainer.addView(divider);
                            }
                        }
                    }
                });
    }
}
