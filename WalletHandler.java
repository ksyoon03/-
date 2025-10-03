

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class WalletHandler implements HttpHandler {
    private final TradingService tradingService;
    private final MarketSimulator marketSimulator;

    public WalletHandler(TradingService tradingService, MarketSimulator marketSimulator) {
        this.tradingService = tradingService;
        this.marketSimulator = marketSimulator;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        int statusCode = 200;

        if ("GET".equalsIgnoreCase(method)) {
            // URL에서 userId 파라미터를 추출 (예: /wallet?userId=userA)
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String userId = params.get("userId");

            Wallet wallet = tradingService.getWallet(userId);
            double currentPrice = marketSimulator.getCurrentBtcPrice();

            if (wallet != null && currentPrice > 0) {
                // JSON 형태로 상세한 지갑 정보를 만들어 응답
                response = String.format(
                        "{\"userId\":\"%s\", \"cash\":%.0f, \"coinCount\":%.4f, \"currentCoinPrice\":%.2f, \"holdingCoinValue\":%.2f, \"totalAssets\":%.2f}",
                        userId,
                        wallet.getCash(),
                        wallet.getCoinCount(),
                        currentPrice,
                        wallet.getHoldingCoinValue(currentPrice),
                        wallet.getTotalAssets(currentPrice)
                );
            } else {
                statusCode = 404; // Not Found
                response = "{\"error\":\"User not found or price not available.\"}";
            }
        } else {
            statusCode = 405; // Method Not Allowed
            response = "{\"error\":\"GET method is required.\"}";
        }
        sendResponse(exchange, response, statusCode);
    }

    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 오류가 발생했던 두 메소드를 여기에 추가합니다 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            } else {
                result.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }
}