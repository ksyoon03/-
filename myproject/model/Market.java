package com.myproject.model;

import com.google.gson.annotations.SerializedName;

// '마켓 코드 조회' API의 응답을 담을 그릇
public class Market {
    // "KRW-BTC" 같은 마켓 코드
    private String market;

    // "비트코인" 같은 한글 이름
    @SerializedName("korean_name")
    private String koreanName;

    // "Bitcoin" 같은 영어 이름
    @SerializedName("english_name")
    private String englishName;

    // market 변수만 외부에서 사용할 수 있도록 getter 추가
    public String getMarket() {
        return market;
    }
}