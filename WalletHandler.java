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
    private final UserService userService;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public WalletHandler(TradingService tradingService, MarketSimulator marketSimulator, UserService userService) {
        this.tradingService = tradingService;
        this.marketSimulator = marketSimulator;
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String token = exchange.getRequestHeaders().getFirst("X-Auth-Token");
        String userId = userService.getUsernameFromToken(token);

        if (userId == null) {
            sendResponse(exchange, "{\"error\":\"Unauthorized\"}", 401);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, "{\"error\":\"GET method is required.\"}", 405);
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

            String response = gson.toJson(responseMap);
            sendResponse(exchange, response, 200);
        } else {
            sendResponse(exchange, "{\"error\":\"User wallet not found or price not available.\"}", 404);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    // 이 핸들러는 URL 쿼리를 사용하지 않지만, 향후 확장을 위해 남겨둘 수 있습니다.
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
