import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ApiServer {
    public static void main(String[] args) throws Exception {
        // UserService 관련 코드를 모두 삭제합니다.
        PriceService priceService = new PriceService();
        MarketSimulator marketSimulator = new MarketSimulator(priceService);
        TradingService tradingService = new TradingService();

        // 테스트용 사용자를 다시 직접 등록합니다.
        if (tradingService.getWallet("userA") == null) {
            tradingService.registerUser("userA", 100000000.0); // 1억원
            Wallet userAWallet = tradingService.getWallet("userA");
            if(userAWallet != null) {
                userAWallet.updateCoinBalance("BTC", 2.0);
                userAWallet.updateCoinBalance("ETH", 10.0);
            }
        }
        if (tradingService.getWallet("userB") == null) {
            tradingService.registerUser("userB", 50000000.0); // 5천만원
        }
        tradingService.saveDataToFile();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 핸들러 생성 시 UserService를 넘겨주지 않습니다.
        server.createContext("/trade", new TradeHandler(tradingService, marketSimulator));
        server.createContext("/wallet", new WalletHandler(tradingService, marketSimulator));

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
