package status; // 🌟 宣告屬於 status 包

import java.util.HashMap;
import java.util.Map;

public class StoryManager {
    private static StoryManager instance;
    private Map<String, StoryNode> storyMap = new HashMap<>();
    private String currentNodeId = "father"; // 初始劇情起點 ID

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
       // 建立主場景節點，描述填入父親的話
StoryNode fatherScene = new StoryNode("father", "兒子啊，你必須要變強。");
     
// 添加「與父親交談」選項
fatherScene.addOption(
    "與父親交談", 
    "father_talk_success", // 成功後導向的下一個劇情節點
    () -> {
        // 🟢 【點擊事件】扣除 4 點命運，並增加命運上限 5 點
        // 備註：這裡假設你的 PlayerStats 已經擴充了 getFate() 方法
        var fate = DataManager.getInstance().getPlayerStats().getFate();
        fate.consume(4.0); 
        
        // 如果你的 StatusProperty 未來有寫變更上限的方法，可以在此處呼叫，例如：
        // fate.setMax(fate.getMax() + 5);
        System.out.println("【劇情】你聆聽了父親的教誨，命運上限提升了！");
    }, 
    () -> {
        // 🧠 【動態判定】去 DataManager 檢查目前的命運值是否大於等於 4
        double currentFate = DataManager.getInstance().getPlayerStats().getFate().getCurrent();
        return currentFate >= 4.0; 
    },
    // ❌ 【條件不足變灰時】滑鼠移上去顯示的 ToolTip（100% 還原圖片中的排版與顏色）
    "<html>" +
    "<div style='background-color: #2D2D2D; color: #E0E0E0; padding: 12px 15px; font-family: \"Microsoft YaHei\", sans-serif; font-size: 14px; border: 1px solid #1A1A1A; line-height: 1.5;'>" +
        "<b style='font-size: 18px; color: #FFFFFF;'>與父親交談</b><br>" +
        "<i style='color: #B5B5B5; display: inline-block; margin-top: 2px; margin-bottom: 4px;'>兒子啊，你必須要變強。</i><br>" +
        "<span style='color: #8A8A8A;'>&lt;&lt; 花費 &gt;&gt;</span><br>" +
        // 這裡動態去撈目前玩家的 命運值/上限 (例如圖片中的 0 / 10)
        "<span style='color: #FF5A5A;'>命運 : -4 (" + 
            (int)DataManager.getInstance().getPlayerStats().getFate().getCurrent() + " / " + 
            (int)DataManager.getInstance().getPlayerStats().getFate().getMax() + ")</span><br>" +
        "<span style='color: #8A8A8A;'>[完成效果]</span><br>" +
        "<span style='color: #E0E0E0;'>命運 上限 : +5</span>" +
    "</div>" +
    "</html>"
);
storyMap.put(fatherScene.getId(), fatherScene);

// ---- 後續分支節點：交談成功後的發展 (第二階段) ----
StoryNode talkSuccess = new StoryNode(
    "father_talk_success", 
    "幾年前，你的母親被魔王抓走了。我希望你去救她。"
);

// 修正：這裡的 targetNodeId 從 "story_end" 改成指向第三階段的 "father_talk_stage3"
talkSuccess.addOption(
    "與父親交談", 
    "father_talk_stage3", 
    () -> {
        status.StatusProperty fate = DataManager.getInstance().getPlayerStats().getFate();
        fate.consume(8.0);
        fate.setMax(fate.getMax() + 5.0);
        System.out.println("【系統】你答應了父親。命運 -8，上限變為: " + fate.getMax());
    }, 
    () -> {
        return DataManager.getInstance().getPlayerStats().getFate().getCurrent() >= 8.0;
    },
    ""
);
storyMap.put(talkSuccess.getId(), talkSuccess);


// ---- 與父親交談 (第三階段修改) ----
StoryNode talkStage3 = new StoryNode(
    "father_talk_stage3", 
    "我曾奮力抵抗，但還是沒能保護她……還失去了右臂。"
);

// 修正：將這裡的 targetNodeId 從 "story_end" 改成指向第四階段的 "father_talk_stage4"
talkStage3.addOption(
    "與父親交談", 
    "father_talk_stage4", 
    () -> {
        status.StatusProperty fate = DataManager.getInstance().getPlayerStats().getFate();
        fate.consume(12.0);
        fate.setMax(fate.getMax() + 5.0);
        System.out.println("【系統】你聆聽了父親的過去。命運 -12，上限變為: " + fate.getMax());
    }, 
    () -> {
        return DataManager.getInstance().getPlayerStats().getFate().getCurrent() >= 12.0;
    },
    ""
);
storyMap.put(talkStage3.getId(), talkStage3);


// 🌟 新增：與父親交談 (第四階段)
StoryNode talkStage4 = new StoryNode(
    "father_talk_stage4", 
    "你應該去學校，在那裡能學到許多我教不到的知識。"
);

talkStage4.addOption(
    "與父親交談", 
    "story_end", // 如果後面還有第五階段，再往下接新 ID 即可！
    () -> {
        status.StatusProperty fate = DataManager.getInstance().getPlayerStats().getFate();
        
        // 真正執行扣除命運 16 點
        fate.consume(16.0);
        
        // 真正執行命運上限再 +5 (從 25 變成 30)
        fate.setMax(fate.getMax() + 5.0);
        
        System.out.println("【系統】你接受了父親的建議。命運 -16，上限變為: " + fate.getMax());
    }, 
    () -> {
        // 檢查當前命運是否大於等於 16
        return DataManager.getInstance().getPlayerStats().getFate().getCurrent() >= 16.0;
    },
    ""
);
storyMap.put(talkStage4.getId(), talkStage4);

     
        
    }

    public StoryNode getCurrentNode() { return storyMap.get(currentNodeId); }
    public void setCurrentNodeId(String id) { this.currentNodeId = id; }
}
