import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
            // 토큰 대신 다시 URL 쿼리에서 userId를 받습니다.
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String userId = params.get("userId");

            if (userId == null || userId.isEmpty()){
                sendResponse(exchange, "{\"error\":\"'userId' parameter is required.\"}", 400);
                return;
            }

            Wallet wallet = tradingService.getWallet(userId);
            Map<String, Double> currentPrices = marketSimulator.getAllPrices();

            if (wallet != null && !currentPrices.isEmpty()) {
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("userId", userId);
                responseMap.put("cash", wallet.getCash());
                responseMap.put("coinBalances", wallet.getCoinBalances());
                responseMap.put("totalAssets", wallet.getTotalAssets(currentPrices));
                responseMap.put("currentPrices", currentPrices);

                response = gson.toJson(responseMap);
            } else {
                statusCode = 404;
                response = "{\"error\":\"User not found or price not available.\"}";
            }
        } else {
            statusCode = 405;
            response = "{\"error\":\"GET method is required.\"}";
        }
        sendResponse(exchange, response, statusCode);
    }

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