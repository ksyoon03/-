package com.myproject.service;

import com.myproject.model.Coin;

import java.util.List;

public class ApiTest {

    public static void main(String[] args) {
        UpbitApiService apiService = new UpbitApiService();

        System.out.println("모든 원화 마켓 시세 조회 테스트 시작...");

        // 1. 새로 만든 최종 메소드를 호출합니다. (더 이상 코인 목록을 직접 만들 필요 없음)
        List<Coin> allCoins = apiService.getTickerForAllKrwMarkets();

        // 2. 결과 확인
        if (allCoins != null && !allCoins.isEmpty()) {
            System.out.println("✅ 호출 성공! 총 " + allCoins.size() + "개의 코인 시세를 가져왔습니다.");
            // 너무 많으니 앞 5개만 출력해보기
            for (int i = 0; i < 5 && i < allCoins.size(); i++) {
                System.out.println(allCoins.get(i).toString());
            }
        } else {
            System.out.println("❌ 호출 실패!");
        }
    }
}