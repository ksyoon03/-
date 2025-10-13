import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TradingService {

    private Map<String, Wallet> userWallets;
    private static final String DATA_FILE = "wallets.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TradingService() {
        this.userWallets = new ConcurrentHashMap<>();
        loadDataFromFile();
    }

    public void registerUser(String userId, double initialCash) {
        if (userWallets.containsKey(userId)) {
            return;
        }
        Wallet newWallet = new Wallet(userId, initialCash);
        userWallets.put(userId, newWallet);
        System.out.println(userId + "님, 환영합니다. 지갑이 생성되었습니다.");
    }

    public Wallet getWallet(String userId) {
        Wallet wallet = userWallets.get(userId);
        if (wallet != null) {
            wallet.setOwner(userId);
        }
        return wallet;
    }

    public boolean buyCoin(String userId, String market, double coinPrice, double quantity) {
        Wallet userWallet = getWallet(userId);
        if (userWallet == null || quantity <= 0 || coinPrice <= 0) return false;

        double requiredCash = coinPrice * quantity;
        if (userWallet.getCash() >= requiredCash) {
            userWallet.setCash(userWallet.getCash() - requiredCash);

            String coinTicker = market.split("-")[1];
            double currentBalance = userWallet.getCoinBalance(coinTicker);
            userWallet.updateCoinBalance(coinTicker, currentBalance + quantity);

            System.out.printf("[거래 성공] %s님이 %s를 %.4f개 매수했습니다.%n", userId, market, quantity);
            return true;
        } else {
            System.out.println("[거래 실패] " + userId + " 현금 부족.");
            return false;
        }
    }

    public boolean sellCoin(String userId, String market, double coinPrice, double quantity) {
        Wallet userWallet = getWallet(userId);
        if (userWallet == null || quantity <= 0 || coinPrice <= 0) return false;

        String coinTicker = market.split("-")[1];
        double currentBalance = userWallet.getCoinBalance(coinTicker);

        if (currentBalance >= quantity) {
            double income = coinPrice * quantity;
            userWallet.setCash(userWallet.getCash() + income);
            userWallet.updateCoinBalance(coinTicker, currentBalance - quantity);

            System.out.printf("[거래 성공] %s님이 %s를 %.4f개 매도했습니다.%n", userId, market, quantity);
            return true;
        } else {
            System.out.println("[거래 실패] " + userId + " " + coinTicker + " 코인 부족.");
            return false;
        }
    }

    public synchronized void saveDataToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(this.userWallets, writer);
        } catch (IOException e) {
            System.err.println("데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private synchronized void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<ConcurrentHashMap<String, Wallet>>(){}.getType();
            this.userWallets = gson.fromJson(reader, type);
            if (this.userWallets == null) {
                this.userWallets = new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            System.err.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
        }
    }
}