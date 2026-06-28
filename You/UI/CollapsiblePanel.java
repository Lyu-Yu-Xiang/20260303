import javax.swing.*;
import java.awt.*;

public class CollapsiblePanel extends JPanel {
    private JButton toggleButton; // 標題按鈕
    private JPanel contentPanel;  // 內容面板
    private boolean isExpanded = true; // 目前是否展開

    public CollapsiblePanel(String title, JPanel content) {
        this.contentPanel = content;
        this.setLayout(new BorderLayout());

        // 1. 建立標題按鈕
        toggleButton = new JButton("▼ " + title);
        toggleButton.addActionListener(e -> toggle()); // 點擊時切換狀態

        // 2. 組裝
        this.add(toggleButton, BorderLayout.NORTH); // 標題在上方
        this.add(contentPanel, BorderLayout.CENTER); // 內容在中央
    }

    // 核心邏輯：切換展開/收合
    private void toggle() {
        isExpanded = !isExpanded; // 反轉狀態
        
        // 設定內容面板是否看得到
        contentPanel.setVisible(isExpanded); 
        
        // 改變按鈕的箭頭圖示
        if (isExpanded) {
            toggleButton.setText("▼ " + toggleButton.getText().substring(2));
        } else {
            toggleButton.setText("► " + toggleButton.getText().substring(2));
        }

        // 關鍵：叫 Java 重新計算排版並重繪畫面
        this.revalidate();
        this.repaint();
    }
    
}