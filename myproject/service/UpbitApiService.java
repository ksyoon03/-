package com.myproject.service;

import com.google.gson.Gson;
import com.myproject.model.Coin;
import com.myproject.model.Market;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpbitApiService {

    /**
     * ✅ 다른 팀원들이 호출할 최종 메소드
     */
    public List<Coin> getTickerForAllKrwMarkets() {
        List<String> allKrwMarketCodes = getAllKrwMarketCodes();
        return getCoinData(allKrwMarketCodes);
    }

    /**
     * [내부 기능 1] 모든 원화 마켓 코드를 가져옵니다.
     */
    private List<String> getAllKrwMarketCodes() {
        List<String> krwMarketCodes = new ArrayList<>();
        try {
            URL url = new URL("https://api.upbit.com/v1/market/all");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            Gson gson = new Gson();
            Market[] markets = gson.fromJson(response.toString(), Market[].class);

            for (Market market : markets) {
                if (market.getMarket().startsWith("KRW-")) {
                    krwMarketCodes.add(market.getMarket());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return krwMarketCodes;
    }

    /**
     * [내부 기능 2] 특정 코인 목록의 현재가를 조회합니다.
     */
    private List<Coin> getCoinData(List<String> markets) {
        if (markets == null || markets.isEmpty()) return new ArrayList<>();
        try {
            String marketParams = String.join(",", markets);
            URL url = new URL("https://api.upbit.com/v1/ticker?markets=" + marketParams);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");


            // 서버로부터 응답을 읽기 위한 BufferedReader를 생성합니다.
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            // 응답이 끝날 때까지 한 줄씩 읽어서 합칩니다.
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            // 자원을 해제합니다.
            br.close();
            conn.disconnect();


            // 이제 읽어온 응답(response)을 파싱합니다.
            Gson gson = new Gson();
            Coin[] coinArray = gson.fromJson(response.toString(), Coin[].class);
            return Arrays.asList(coinArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
