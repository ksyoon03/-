import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public boolean register(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty() || users.containsKey(username)) {
            return false;
        }
        String passwordHash = hashPassword(password);
        users.put(username, new User(username, passwordHash));
        System.out.println("[회원가입 성공] 사용자: " + username);
        return true;
    }

    public String login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, username);
            System.out.println("[로그인 성공] 사용자: " + username);
            return token;
        }
        System.out.println("[로그인 실패] 사용자: " + username);
        return null;
    }

    public String getUsernameFromToken(String token) {
        return activeTokens.get(token);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}