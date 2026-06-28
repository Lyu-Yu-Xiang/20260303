package status; // 🌟 宣告屬於 status 包

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StoryNode {
    private String id;              
    private String description;     
    private List<StoryOption> options = new ArrayList<>(); 

    public StoryNode(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public void addOption(String text, String targetNodeId, Runnable action, Supplier<Boolean> condition, String failDescription) {
        options.add(new StoryOption(text, targetNodeId, action, condition, failDescription));
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public List<StoryOption> getOptions() { return options; }

    public static class StoryOption {
        public String text;               
        public String targetNodeId;      
        public Runnable action;          
        public Supplier<Boolean> condition; 
        public String failDescription;   

        public StoryOption(String text, String targetNodeId, Runnable action, Supplier<Boolean> condition, String failDescription) {
            this.text = text;
            this.targetNodeId = targetNodeId;
            this.action = action;
            this.condition = condition;
            this.failDescription = failDescription;
        }
    }
}