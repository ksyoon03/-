
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class ApiServer {
    public static void main(String[] args) throws Exception {
        // 서비스 객체들을 생성
        PriceService priceService = new PriceService();
        MarketSimulator marketSimulator = new MarketSimulator(priceService);
        TradingService tradingService = new TradingService();

        // 테스트용 사용자 정보 등록
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

        // 8081 포트로 서버를 생성합니다.
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // 각 경로에 맞는 핸들러를 연결
        server.createContext("/trade", new TradeHandler(tradingService, marketSimulator));
        server.createContext("/wallet", new WalletHandler(tradingService, marketSimulator));

        server.setExecutor(null);


        marketSimulator.start();
        server.start();

        System.out.println("백엔드 API 서버가 8081 포트에서 실행 중입니다...");
        System.out.println("서버를 중지하려면 Ctrl+C를 누르세요.");

        // 프로그램이 종료될 때 데이터가 자동 저장되도록 설정
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n서버를 종료합니다...");
            marketSimulator.stop();
            tradingService.saveDataToFile();
            System.out.println("모든 서비스가 안전하게 종료되었습니다.");
        }));
    }
}
