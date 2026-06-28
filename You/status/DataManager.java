package status;

public class DataManager {
    // 1. 建立一個自己類別的「靜態成員變數」，用來存那唯一的一個實體
    private static DataManager instance;

    // 2. 把你的玩家數據放進來
    private PlayerStats playerStats;

    // 3. 【關鍵】把建構子宣告成 private (私有)！
    // 這樣可以防止別人在其他地方手癢寫出 `new DataManager()` 導致數據重置
    private DataManager() {
        playerStats = new PlayerStats(); // 在這裡初始化你的玩家數據
    }

    // 4. 提供一個公開的靜態方法，讓全世界的程式碼都可以來拿這唯一的一份資料
    public static DataManager getInstance() {
        // 如果還沒有建立過，就建立它（這輩子只會執行這一次）
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // 提供拿取 PlayerStats 的方法
    public PlayerStats getPlayerStats() {
        return playerStats;
    }
}