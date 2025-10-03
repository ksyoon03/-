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

    public void registerUser(String userId, double initialCash, double initialCoin) {
        if (userWallets.containsKey(userId)) {
            System.out.println("오류: 이미 존재하는 사용자 ID입니다.");
            return;
        }
        Wallet newWallet = new Wallet(userId, initialCash, initialCoin);
        userWallets.put(userId, newWallet);
        System.out.println(userId + "님, 환영합니다. 지갑이 생성되었습니다.");
    }

    public Wallet getWallet(String userId) {
        Wallet wallet = userWallets.get(userId);
        if (wallet != null) {
            wallet.setOwner(userId); // 불러올 때 owner 이름 설정
        }
        return wallet;
    }

    public boolean buyCoin(String userId, double coinPrice, double quantity) {
        Wallet userWallet = getWallet(userId);
        if (userWallet == null) return false;

        double requiredCash = coinPrice * quantity;
        if (userWallet.getCash() >= requiredCash) {
            userWallet.setCash(userWallet.getCash() - requiredCash);
            userWallet.setCoinCount(userWallet.getCoinCount() + quantity);
            System.out.println("[거래 성공] " + userId + " 매수 완료. 수량: " + quantity);
            return true;
        } else {
            System.out.println("[거래 실패] " + userId + " 현금 부족.");
            return false;
        }
    }

    public boolean sellCoin(String userId, double coinPrice, double quantity) {
        Wallet userWallet = getWallet(userId);
        if (userWallet == null) return false;

        if (userWallet.getCoinCount() >= quantity) {
            double income = coinPrice * quantity;
            userWallet.setCoinCount(userWallet.getCoinCount() - quantity);
            userWallet.setCash(userWallet.getCash() + income);
            System.out.println("[거래 성공] " + userId + " 매도 완료. 수량: " + quantity);
            return true;
        } else {
            System.out.println("[거래 실패] " + userId + " 코인 부족.");
            return false;
        }
    }

    public synchronized void saveDataToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(this.userWallets, writer);
        } catch (IOException e) {
            System.out.println("데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private synchronized void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("저장된 데이터 파일이 없습니다. 새 파일을 생성합니다.");
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<ConcurrentHashMap<String, Wallet>>(){}.getType();
            this.userWallets = gson.fromJson(reader, type);
            if (this.userWallets == null) {
                this.userWallets = new ConcurrentHashMap<>();
            }
            System.out.println(DATA_FILE + "에서 데이터를 성공적으로 불러왔습니다.");
        } catch (IOException e) {
            System.out.println("데이터 불러오기 중 오류 발생: " + e.getMessage());
        }
    }
}