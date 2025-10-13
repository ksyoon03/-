import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ApiServer {
    public static void main(String[] args) throws Exception {
        // 모든 서비스 객체 생성
        UserService userService = new UserService();
        PriceService priceService = new PriceService();
        MarketSimulator marketSimulator = new MarketSimulator(priceService);
        TradingService tradingService = new TradingService();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 새로운 핸들러들을 각 경로에 등록
        server.createContext("/register", new RegisterHandler(userService, tradingService));
        server.createContext("/login", new LoginHandler(userService));

        // 기존 핸들러 생성자에 userService 추가
        server.createContext("/trade", new TradeHandler(tradingService, marketSimulator, userService));
        server.createContext("/wallet", new WalletHandler(tradingService, marketSimulator, userService));

        server.setExecutor(null);

        marketSimulator.start();
        server.start();

        System.out.println("백엔드 API 서버가 8080 포트에서 실행 중입니다...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n서버를 종료합니다...");
            marketSimulator.stop();
            tradingService.saveDataToFile();
            System.out.println("모든 서비스가 안전하게 종료되었습니다.");
        }));
    }
}
