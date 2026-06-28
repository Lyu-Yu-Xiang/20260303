import javax.swing.*;
import java.awt.*;

public class StatusSidebar extends JPanel {
    // 讓外層可以拿到的主要內容容器，方便以後從外部塞東西進去
    private JPanel contentContainer; 

    public StatusSidebar() {
        // 1. 設定側邊欄最外層的尺寸與配置
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(250, 600)); // 寬度固定 250，高度隨視窗

        // 2. 建立內部的大盒子：專門用來由上往下堆疊折疊面板
        contentContainer = new JPanel();
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setBackground(new Color(245, 245, 245)); // 背景淡灰色

        // 3. 建立滾動面板，把大盒子裝進去
        JScrollPane scrollPane = new JScrollPane(contentContainer);
        
        // 4. 滾動條基本設定（上下需要時出現、左右絕對不出現）
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // 5. 絲滑滾動優化（解決 Java 預設滾動極慢的問題）
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 6. 拿掉滾動面板的邊框，讓視覺乾淨
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 7. 將設定好的滾動面版填滿整個側邊欄
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 提供一個對外公開的方法：讓別的系統可以動態把做好的「折疊面板」塞進來
     */
    public void addSection(JPanel collapsiblePanel) {
        contentContainer.add(collapsiblePanel);
        contentContainer.add(Box.createVerticalStrut(10)); // 自動幫每個分類補上 10 像素的間距
        
        // 重新排版與刷新畫面
        contentContainer.revalidate();
        contentContainer.repaint();
    }
}