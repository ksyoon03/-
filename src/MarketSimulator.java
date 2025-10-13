import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketSimulator {

    private final PriceService priceService;
    private volatile Map<String, Double> currentPrices;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MarketSimulator(PriceService priceService) {
        this.priceService = priceService;
        this.currentPrices = new ConcurrentHashMap<>();
    }

    public void start() {
        final Runnable priceUpdater = () -> {
            Map<String, Double> prices = priceService.getCoinPrices();
            if (!prices.isEmpty()) {
                this.currentPrices = prices;

                // --- 이 부분을 수정합니다 ---
                System.out.println("✅ 실시간 시세 업데이트:");
                for (Map.Entry<String, Double> entry : this.currentPrices.entrySet()) {
                    // printf를 사용해 깔끔한 형식으로 출력합니다.
                    // %-10s : 10자리 문자열 공간을 확보하고 왼쪽 정렬
                    // %,.0f : 쉼표를 포함하고 소수점 없이 출력
                    System.out.printf("\t- %-10s : %,.0f원%n", entry.getKey(), entry.getValue());
                }
                System.out.println("------------------------------------");
                // --- 여기까지 수정 ---
            }
        };
        scheduler.scheduleAtFixedRate(priceUpdater, 0, 5, TimeUnit.SECONDS);
    }

    public double getPrice(String market) {
        return this.currentPrices.getOrDefault(market, -1.0);
    }

    public Map<String, Double> getAllPrices() {
        return this.currentPrices;
    }

    public void stop() {
        scheduler.shutdown();
    }
}