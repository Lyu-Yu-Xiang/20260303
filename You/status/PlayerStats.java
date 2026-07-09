package status;

public class PlayerStats {
    private StatusProperty action;
    private StatusProperty fate; // 🌟 1. 宣告命運屬性
    private double lastUpdateTime; // 記錄上一次更新的時間

    public PlayerStats() {
        // 初始化行動力：目前 0，上限 6 
        this.action = new StatusProperty(0, 6);
        
        // 🌟 2. 初始化命運：目前 0，上限 10 (完全比照你的截圖)
        this.fate = new StatusProperty(0, 10);
        
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // 這個方法需要被定時呼叫，用來讓行動力隨時間增加
    public void updateRecovery() {
        long now = System.currentTimeMillis();
        double deltaTime = (now - lastUpdateTime) / 1000.0; 
        lastUpdateTime = now;

        // 假設設定：每秒鐘自動回復 0.5 點行動力
        double regenAmount = 0.5 * deltaTime;
        action.add(regenAmount);
        
        // 💡 如果你希望「命運」未來也會隨時間自動恢復，可以在這邊加上：
        // fate.add(0.1 * deltaTime); 
        double fateRegenAmount = 5 * deltaTime;
         fate.add(fateRegenAmount);
    }

    public StatusProperty getAction() {
        return action;
    }

    // 🌟 3. 提供拿取命運（Fate）的方法，這樣 StoryManager 才能對接！
    public StatusProperty getFate() {
        return fate;
    }
}
