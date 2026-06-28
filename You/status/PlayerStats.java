package status;



public class PlayerStats {
    private StatusProperty action;
    private double lastUpdateTime; // 記錄上一次更新的時間

    public PlayerStats() {
        // 初始化行動力：目前 5.58，上限 11 (跟你的截圖一樣)
        this.action = new StatusProperty(0, 6);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // 這個方法需要被定時呼叫，用來讓行動力隨時間增加
    public void updateRecovery() {
        long now = System.currentTimeMillis();
        // 計算距離上一次更新過去了幾秒（小數點）
        double deltaTime = (now - lastUpdateTime) / 1000.0; 
        lastUpdateTime = now;

        // 假設設定：每秒鐘自動回復 0.5 點行動力
        double regenAmount = 0.5 * deltaTime;
        action.add(regenAmount);
    }

    public StatusProperty getAction() {
        return action;
    }
}