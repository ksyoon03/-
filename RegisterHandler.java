import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterHandler implements HttpHandler {
    private final UserService userService;
    private final TradingService tradingService;

    public RegisterHandler(UserService userService, TradingService tradingService) {
        this.userService = userService;
        this.tradingService = tradingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, "{\"error\":\"POST method is required.\"}", 405);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseQuery(body);
        String username = params.get("username");
        String password = params.get("password");

        boolean success = userService.register(username, password);

        if (success) {
            tradingService.registerUser(username, 10000000); // 초기자금 1천만원
            tradingService.saveDataToFile();
            sendResponse(exchange, "{\"status\":\"success\", \"message\":\"Registration successful.\"}", 201);
        } else {
            sendResponse(exchange, "{\"status\":\"failed\", \"message\":\"Username already exists or invalid input.\"}", 400);
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