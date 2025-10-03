public class Wallet {
    // transient 키워드는 이 필드를 JSON으로 저장할 때 제외하라는 의미입니다.
    // owner 정보는 Map의 키 값으로 관리되므로 중복 저장을 피하기 위함입니다.
    private transient String owner;
// 기존 Wallet.java 코드에 아래 메소드 2개를 추가하세요.

    // 보유 코인의 현재 가치를 계산
    public double getHoldingCoinValue(double currentPrice) {
        return this.coinCount * currentPrice;
    }

    // 총 자산(현금 + 코인 평가액)을 계산
    public double getTotalAssets(double currentPrice) {
        return this.cash + getHoldingCoinValue(currentPrice);
    }

    private double cash;
    private double coinCount;

    public Wallet(String ownerName, double initialCash, double initialCoin) {
        this.owner = ownerName;
        this.cash = initialCash;
        this.coinCount = initialCoin;
    }

    // --- Getter & Setter 메소드들 ---

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public double getCash() { return cash; }
    public void setCash(double cash) { this.cash = cash; }
    public double getCoinCount() { return coinCount; }
    public void setCoinCount(double coinCount) { this.coinCount = coinCount; }

    public void printStatus() {
        System.out.println("--- " + this.owner + "님의 지갑 ---");
        System.out.println("현금 보유액: " + this.cash + "원");
        System.out.println("코인 보유량: " + this.coinCount + "개");
        System.out.println("--------------------------");
    }
}