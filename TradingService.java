import java.util.Map;

/**
 * 코인 매수/매도 기능을 담당하는 서비스 클래스입니다.
 */
public class TradingService {

    /**
     * 코인 매수 기능을 처리하는 메소드입니다.
     * @param user        거래를 요청한 사용자
     * @param coinName    매수할 코인 이름
     * @param quantity    매수할 수량
     * @param marketPrice 현재 코인 시세 정보
     */
    public void buyCoin(User user, String coinName, int quantity, Map<String, Double> marketPrice) {
        System.out.printf("\n[매수 시도] 코인: %s, 수량: %d개\n", coinName, quantity);

        // 1. 시장에 해당 코인이 존재하는지 확인
        if (!marketPrice.containsKey(coinName)) {
            System.out.println("[매수 실패] 존재하지 않는 코인입니다.");
            return;
        }

        // 2. 총 거래 비용 계산
        double price = marketPrice.get(coinName);
        double totalCost = price * quantity;

        // 3. 사용자의 현금 잔고가 충분한지 확인
        if (user.getBalance() < totalCost) {
            System.out.printf("[매수 실패] 잔고가 부족합니다. (필요 금액: %,.0f원)\n", totalCost);
            return;
        }

        // 4. 거래 진행: 사용자 자산 업데이트
        user.updateBalance(-totalCost); // 현금 차감
        user.updatePortfolio(coinName, quantity); // 포트폴리오에 코인 추가

        System.out.printf("[매수 성공] %s %d개를 성공적으로 매수했습니다.\n", coinName, quantity);
    }

    /**
     * 코인 매도 기능을 처리하는 메소드입니다.
     * @param user        거래를 요청한 사용자
     * @param coinName    매도할 코인 이름
     * @param quantity    매도할 수량
     * @param marketPrice 현재 코인 시세 정보
     */
    public void sellCoin(User user, String coinName, int quantity, Map<String, Double> marketPrice) {
        System.out.printf("\n[매도 시도] 코인: %s, 수량: %d개\n", coinName, quantity);

        // 1. 사용자가 해당 코인을 보유하고 있는지 확인
        Map<String, Integer> portfolio = user.getPortfolio();
        if (!portfolio.containsKey(coinName)) {
            System.out.println("[매도 실패] 해당 코인을 보유하고 있지 않습니다.");
            return;
        }

        // 2. 보유한 수량이 팔려는 수량보다 많은지 확인
        int ownedQuantity = portfolio.get(coinName);
        if (ownedQuantity < quantity) {
            System.out.printf("[매도 실패] 보유 수량이 부족합니다. (보유량: %d개)\n", ownedQuantity);
            return;
        }

        // 3. 시장에 해당 코인이 존재하는지 확인 (가격을 알아보기 위함)
        if (!marketPrice.containsKey(coinName)) {
            System.out.println("[매도 실패] 현재 거래되고 있지 않은 코인입니다."); // 이론상 발생하기 힘든 경우
            return;
        }

        // 4. 거래 진행: 사용자 자산 업데이트
        double price = marketPrice.get(coinName);
        double totalRevenue = price * quantity; // 총 판매 금액

        user.updateBalance(totalRevenue); // 현금 증가
        user.updatePortfolio(coinName, -quantity); // 포트폴리오에서 코인 차감

        System.out.printf("[매도 성공] %s %d개를 성공적으로 매도했습니다.\n", coinName, quantity);
    }
}