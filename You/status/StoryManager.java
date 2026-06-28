package status; // 🌟 宣告屬於 status 包

import java.util.HashMap;
import java.util.Map;

public class StoryManager {
    private static StoryManager instance;
    private Map<String, StoryNode> storyMap = new HashMap<>();
    private String currentNodeId = "dinner_choice"; // 初始劇情起點 ID

    private StoryManager() {
        initStory(); 
    }

    public static synchronized StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    private void initStory() {
        // -------------------------------------------------------------
        // 🏠 劇本幕次：決定晚餐
        // -------------------------------------------------------------
        StoryNode dinnerScene = new StoryNode("dinner_choice", "今天晚餐要吃什麼呢？");

        // 選項一：在家吃飯（無條件，永遠可點）
        dinnerScene.addOption("在家吃飯", "eat_at_home", () -> {
            System.out.println("【劇情】你決定在家做飯。");
        }, () -> true, "");

        // 選項二：出門吃飯（動態連動：必須命運值足夠，且天氣不能下雨）
        dinnerScene.addOption(
            "出門吃飯", 
            "eat_outside", 
            () -> {
                System.out.println("【劇情】你跟父親出門去隔壁街吃飯。");
                // 這裡可以寫點擊後的扣除邏輯，例如：
                // DataManager.getInstance().getPlayerStats().getFate().consume(4);
            }, 
            () -> {
                // 🧠 ⭐【動態狀態判定】直接與你的 DataManager 和 PlayerStats 對接！
                // 假設你未來在 PlayerStats 裡有命運屬性，或是先用現有的數值判定：
                // double currentFate = DataManager.getInstance().getPlayerStats().getFate().getCurrent();
                
                boolean isRaining = false; // 這裡可以對接你的天氣系統，例如 DataManager.getInstance().isRaining()
                boolean hasEnoughFate = true; // 例如判定 currentFate >= 4;

                return hasEnoughFate && !isRaining; // 判定公式
            },
            // ❌ 判定失敗變灰時，滑鼠指上去顯示的黑色提示字
            "<html><div style='background-color: #2D2D2D; color: #E0E0E0; padding: 10px; font-family: \"Microsoft YaHei\"; border: 1px solid #1A1A1A;'>" +
            "<b style='color: #FF5A5A;'>【無法出門】</b><br>" +
            "<span style='color: #8A8A8A;'>外面正在下大雨，或你的命運值不足，無法出門吃飯。</span>" +
            "</div></html>"
        );
        storyMap.put(dinnerScene.getId(), dinnerScene);

        // ---- 後續分支節點 ----
        StoryNode atHome = new StoryNode("eat_at_home", "在家吃完了溫馨的家常菜。");
        atHome.addOption("去洗碗", "story_end", () -> System.out.println("你乖乖去洗碗了。"), () -> true, "");
        storyMap.put(atHome.getId(), atHome);

        StoryNode outside = new StoryNode("eat_outside", "在餐館吃到了美味的大餐。");
        outside.addOption("結帳回家", "story_end", () -> System.out.println("花費了金幣。"), () -> true, "");
        storyMap.put(outside.getId(), outside);
    }

    public StoryNode getCurrentNode() { return storyMap.get(currentNodeId); }
    public void setCurrentNodeId(String id) { this.currentNodeId = id; }
}