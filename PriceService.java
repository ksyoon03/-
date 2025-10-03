import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class PriceService {

    private static final String UPBIT_TICKER_API_URL = "https://api.upbit.com/v1/ticker?markets=KRW-BTC,KRW-ETH,KRW-XRP,KRW-DOGE";
    private final HttpClient httpClient;
    private final Gson gson;

    public PriceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public Map<String, Double> getCoinPrices() {
        Map<String, Double> priceMap = new HashMap<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(UPBIT_TICKER_API_URL))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            UpbitTickerResponse[] tickerResponses = gson.fromJson(response.body(), UpbitTickerResponse[].class);

            if (tickerResponses != null) {
                for (UpbitTickerResponse ticker : tickerResponses) {
                    priceMap.put(ticker.getMarket(), ticker.getTrade_price());
                }
            }
        } catch (Exception e) {
            System.err.println("시세 조회 중 오류 발생: " + e.getMessage());
        }
        return priceMap;
    }

    private static class UpbitTickerResponse {
        private String market;
        private double trade_price;
        public String getMarket() { return market; }
        public double getTrade_price() { return trade_price; }
    }
}
