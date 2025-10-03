import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PriceService {

    // 실시간 가격 조회를 위한 업비트 API 주소 (비트코인 원화 마켓)
    private static final String UPBIT_TICKER_API_URL = "https://api.upbit.com/v1/ticker?markets=KRW-BTC";
    private final HttpClient httpClient;
    private final Gson gson;

    public PriceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // 비트코인의 현재가를 가져오는 메소드
    public double getCurrentBtcPrice() {
        try {
            // 1. 업비트 서버에 보낼 요청(Request) 만들기
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(UPBIT_TICKER_API_URL))
                    .build();

            // 2. 요청을 보내고 응답(Response) 받기
            // 서버의 응답이 올 때까지 기다렸다가, 응답 본문을 문자열로 받음
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. 받은 JSON 응답을 객체로 변환하기
            // 업비트는 배열 형태로 응답을 주므로 배열로 받습니다.
            UpbitTickerResponse[] tickerResponses = gson.fromJson(response.body(), UpbitTickerResponse[].class);

            if (tickerResponses != null && tickerResponses.length > 0) {
                // 배열의 첫 번째 요소에서 현재가(trade_price)를 반환
                return tickerResponses[0].getTrade_price();
            }

        } catch (Exception e) {
            System.out.println("시세 조회 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 거래가 실패하도록 -1 또는 예외를 반환할 수 있습니다.
            return -1.0;
        }
        return -1.0;
    }

    // 업비트 응답 JSON을 담기 위한 작은 클래스 (DTO)
    private static class UpbitTickerResponse {
        private String market;
        private double trade_price; // GSON이 JSON의 "trade_price"를 이 필드에 자동으로 매핑해줍니다.

        public double getTrade_price() {
            return trade_price;
        }
    }
}