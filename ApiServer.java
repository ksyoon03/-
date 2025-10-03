import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ApiServer {
    public static void main(String[] args) throws Exception {
        PriceService priceService = new PriceService();
        MarketSimulator marketSimulator = new MarketSimulator(priceService);
        TradingService tradingService = new TradingService();

        if (tradingService.getWallet("userA") == null) {
            tradingService.registerUser("userA", 100000000.0, 2.0);
        }
        if (tradingService.getWallet("userB") == null) {
            tradingService.registerUser("userB", 50000000.0, 5.0);
        }
        tradingService.saveDataToFile();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/trade", new TradeHandler(tradingService, marketSimulator));
        server.createContext("/wallet", new WalletHandler(tradingService, marketSimulator));

        server.setExecutor(null);

        marketSimulator.start();
        server.start();

        System.out.println("백엔드 API 서버가 8080 포트에서 실행 중입니다...");
        System.out.println("서버를 중지하려면 Ctrl+C를 누르세요.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n서버를 종료합니다...");
            marketSimulator.stop();
            tradingService.saveDataToFile();
            System.out.println("모든 서비스가 안전하게 종료되었습니다.");
        }));
    }
}