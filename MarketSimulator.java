import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MarketSimulator {

    private final PriceService priceService;
    // 'volatile'은 여러 스레드가 이 변수를 사용할 때 항상 최신 값을 보장해주는 키워드입니다.
    private volatile double currentBtcPrice;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MarketSimulator(PriceService priceService) {
        this.priceService = priceService;
        this.currentBtcPrice = -1.0; // 초기값
    }

    // 백그라운드에서 주기적으로 시세를 업데이트하는 작업을 시작합니다.
    public void start() {
        // Runnable은 실행할 작업을 정의합니다.
        final Runnable priceUpdater = () -> {
            double price = priceService.getCurrentBtcPrice();
            if (price > 0) {
                this.currentBtcPrice = price;
                // println을 printf로 변경하고 서식을 추가합니다.
                // %,.0f : 쉼표(,)를 추가하고 소수점 없이(0) 실수(f)를 출력
                System.out.printf("✅ 실시간 BTC 가격 업데이트: %,.0f원%n", this.currentBtcPrice);
            }
        };

        // 0초 후 시작해서, 매 5초마다 priceUpdater 작업을 실행합니다.
        scheduler.scheduleAtFixedRate(priceUpdater, 0, 5, TimeUnit.SECONDS);
    }

    // 저장된 최신 가격을 반환합니다.
    public double getCurrentBtcPrice() {
        return this.currentBtcPrice;
    }

    // 서버 종료 시 스케줄러를 안전하게 종료합니다.
    public void stop() {
        scheduler.shutdown();
    }
}