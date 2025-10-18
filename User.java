import java.util.HashMap;
import java.util.Map;

/**
 * 사용자의 자산 정보를 관리하는 클래스입니다.
 * - balance: 사용자가 보유한 현금
 * - portfolio: 사용자가 보유한 코인과 수량을 저장하는 맵(Map)
 */
public class User {
    private double balance; // 현금 잔고
    private Map<String, Integer> portfolio; // 보유 코인 포트폴리오 <코인 이름, 수량>

    /**
     * 사용자를 생성할 때 초기 자본금을 설정합니다.
     * @param initialBalance 초기 자본금
     */
    public User(double initialBalance) {
        this.balance = initialBalance;
        this.portfolio = new HashMap<>(); // 비어있는 포트폴리오로 시작
    }

    // 현금 잔고를 확인하는 메소드
    public double getBalance() {
        return balance;
    }

    // 포트폴리오(보유 코인 목록)를 확인하는 메소드
    public Map<String, Integer> getPortfolio() {
        return portfolio;
    }

    /**
     * 현금 잔고를 변경하는 메소드입니다.
     * @param amount 변경할 금액 (양수: 입금, 음수: 출금)
     */
    public void updateBalance(double amount) {
        this.balance += amount;
    }

    /**
     * 포트폴리오에 코인을 추가하거나 수량을 변경하는 메소드입니다.
     * @param coinName 코인 이름
     * @param quantity 변경할 수량
     */
    public void updatePortfolio(String coinName, int quantity) {
        // 기존에 보유한 수량에 새로운 수량을 더합니다. getOrDefault는 해당 코인이 없으면 0을 반환합니다.
        int newQuantity = portfolio.getOrDefault(coinName, 0) + quantity;

        if (newQuantity > 0) {
            portfolio.put(coinName, newQuantity); // 수량이 0보다 크면 포트폴리오에 추가/수정
        } else {
            portfolio.remove(coinName); // 수량이 0이 되면 포트폴리오에서 제거
        }
    }

    // 현재 사용자 정보를 출력하는 메소드
    public void printStatus() {
        System.out.println("--------------------");
        System.out.printf("현금 잔고: %,.0f원\n", balance);
        System.out.println("보유 코인: " + (portfolio.isEmpty() ? "없음" : portfolio));
        System.out.println("--------------------");
    }
}