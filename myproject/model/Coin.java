package com.myproject.model;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

public class Coin {
    private String market;

    @SerializedName("trade_price")
    private double tradePrice;

    /**
     * 이 객체를 화면에 출력할 때의 형식을 지정하는 메소드입니다.
          */
    @Override
    public String toString() {
        // 2. 어떤 모양으로 찍을지 '도장 양식'을 만듭니다.
        //    "#,##0"은 3자리마다 쉼표(,)를 찍으라는 의미입니다.
        DecimalFormat formatter = new DecimalFormat("#,##0");

        // 3. tradePrice라는 숫자에 도장을 찍어서(format), "원"이라는 글자를 뒤에 붙입니다.
        String formattedPrice = formatter.format(tradePrice) + "원";

        // 4. 최종적으로 완성된 문자열을 반환합니다.
        return "마켓: " + market + ", 현재가: " + formattedPrice;
    }

}
