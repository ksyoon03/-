import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TradeHandler implements HttpHandler {
    private final TradingService tradingService;
    private final MarketSimulator marketSimulator;

    public TradeHandler(TradingService tradingService, MarketSimulator marketSimulator) {
        this.tradingService = tradingService;
        this.marketSimulator = marketSimulator;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response = "";
        int statusCode = 200;

        if ("POST".equalsIgnoreCase(method)) {
            double currentPrice = marketSimulator.getCurrentBtcPrice();
            if (currentPrice < 0) {
                sendResponse(exchange, "{\"status\":\"failed\", \"message\":\"Market price is not available.\"}", 503);
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseQuery(body);

            String action = params.get("action");
            String userId = params.get("userId");
            double quantity = Double.parseDouble(params.getOrDefault("quantity", "0"));

            boolean result = false;
            if ("buy".equals(action)) {
                result = tradingService.buyCoin(userId, currentPrice, quantity);
            } else if ("sell".equals(action)) {
                result = tradingService.sellCoin(userId, currentPrice, quantity);
            }

            if (result) {
                response = "{\"status\":\"success\", \"userId\":\"" + userId + "\", \"price\":" + currentPrice + ", \"quantity\":" + quantity + "}";
                tradingService.saveDataToFile();
            } else {
                statusCode = 400;
                response = "{\"status\":\"failed\", \"message\":\"Trade failed. Check balance or quantity.\"}";
            }
        } else {
            statusCode = 405;
            response = "{\"error\":\"POST method is required.\"}";
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