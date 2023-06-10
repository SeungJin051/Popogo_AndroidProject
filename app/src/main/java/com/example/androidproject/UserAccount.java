package com.example.androidproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// 사용자 정보 getter setter
// 객체 지향 프로그래밍에서 객체의 데이터는 객체 외부에서 직접적으로 접근하는 것을 막는데, 이를 해결하기 위해 메소드를 사용한다.
/* 게터(Getter)
       필드값 반환
       접근 제어 수식어 : public
       게터명 : get필드명 (필드명의 첫글자는 대문자)
       매개변수 없음 */
/* 세터(Setter)
       필드 초기화
       접근 제어 수식어 : (거의) public
       반환자료형 : void (반환값 X)
       세터명 : set필드명 (필드명의 첫글자는 대문자)
       매개변수 : 필드와 동일
       내용 : 생성자와 동일 (this.필드명 = 매개변수명;)
       내용 : return 필드; */

public class UserAccount {
    private String idToken;
    private String emailId;
    private String password;

    public UserAccount() {
        // Default constructor required for Firebase Realtime Database
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

