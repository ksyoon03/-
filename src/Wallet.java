import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Wallet {
    private transient String owner;
    private double cash;
    private Map<String, Double> coinBalances; // Map<코인 티커, 보유 수량>

    public Wallet(String ownerName, double initialCash) {
        this.owner = ownerName;
        this.cash = initialCash;
        this.coinBalances = new ConcurrentHashMap<>();
    }

    public void updateCoinBalance(String coinTicker, double quantity) {
        if (quantity > 0) {
            coinBalances.put(coinTicker, quantity);
        } else {
            coinBalances.remove(coinTicker);
        }
    }

    public double getCoinBalance(String coinTicker) {
        return coinBalances.getOrDefault(coinTicker, 0.0);
    }

    public double getTotalAssets(Map<String, Double> currentPrices) {
        double totalCoinValue = 0.0;
        for (Map.Entry<String, Double> entry : coinBalances.entrySet()) {
            String coinTicker = entry.getKey();
            double quantity = entry.getValue();
            // 마켓 코드는 "KRW-" + 티커 형식이라고 가정합니다.
            double price = currentPrices.getOrDefault("KRW-" + coinTicker, 0.0);
            totalCoinValue += quantity * price;
        }
        return this.cash + totalCoinValue;
    }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public double getCash() { return cash; }
    public void setCash(double cash) { this.cash = cash; }
    public Map<String, Double> getCoinBalances() { return coinBalances; }
}