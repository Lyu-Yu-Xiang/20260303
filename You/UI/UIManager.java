import java.awt.event.*;
import javax.swing.*;

import status.DataManager;
import status.PlayerStats;

import java.awt.*;
import status.StoryManager;
import status.StoryNode;




public class UIManager extends JPanel implements Runnable {
  private java.util.List<JButton> currentStoryButtons = new java.util.ArrayList<>();
    private java.util.List<StoryNode.StoryOption> currentStoryOptions = new java.util.ArrayList<>();
    private Runnable refreshCell3UI;



    // 畫面寬度與高度
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    // 遊戲主迴圈執行緒
    private Thread gameThread;
    private boolean isRunning = false;

    // 遊戲目標 FPS (每秒幀數)
    private final int FPS = 60;

    // ⭐ 【加入這行】宣告玩家狀態數據
    private PlayerStats playerStats = new PlayerStats();


    public UIManager() {
          this.setLayout(new BorderLayout());
        // 設定這個畫布（Panel）的大小與背景顏色
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.WHITE); // 經典遊戲黑底
        this.setDoubleBuffered(true);    // 開啟雙緩衝，防止畫面閃爍

       
        //layout 宣告
         // 這是你的主面板，維持 1 列 5 格（因為截圖有五行：Instant, Loop, Upgrade, Next, Dungeon）
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 5, 10, 10)); // 改為 1 列 5 格

        // ===== 第 1 格 (Instant Action) =====
        JPanel cell1 = new JPanel();
        // 關鍵：改成垂直的 BoxLayout，按鈕才會乖乖由上往下排，且自動填滿寬度
        cell1.setLayout(new BoxLayout(cell1, BoxLayout.Y_AXIS)); 

        // 加一些測試按鈕
        Dimension buttonSize = new Dimension(150, 40);
        for(int i = 1; i <= 1; i++) {
            JButton btn = new JButton("Instant 行動 " + i);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // 讓按鈕寬度自動拉滿整列
            cell1.add(btn);
            cell1.add(Box.createVerticalStrut(5)); // 按鈕之間的間距
        }
        // 關鍵：將這一列塞進滾動面板，畫面上就會出現像截圖右邊那樣的滾動條
        JScrollPane scroll1 = new JScrollPane(cell1);
        centerPanel.add(scroll1);


        // ===== 第 2 格 (Loop Action) =====
       // 1. 初始化你的 cell2，用來放多個折疊面板
        JPanel cell2 = new JPanel();
        cell2.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // 寬度填滿
        gbc.weightx = 1;                          // 權重 1
        gbc.gridx = 0;
        gbc.gridy = 0;

  
        // --- 建立第一個折疊分類：Loop 行動 ---
        JPanel loopContent = new JPanel();
        loopContent.setLayout(new BoxLayout(loopContent, BoxLayout.Y_AXIS)); // 內部也用 Y_AXIS 直著排

      // 🧱 【按鈕 1：冥想】
        JButton btnMeditate = new JButton("休息");
        btnMeditate.setPreferredSize(buttonSize);
        btnMeditate.setMinimumSize(buttonSize);
        btnMeditate.setMaximumSize(buttonSize);
        btnMeditate.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 在這裡可以加按鈕的點擊事件
       btnMeditate.addActionListener(e -> {
            // 1. 先撈出目前全域的行動力數值
            double currentAction = DataManager.getInstance().getPlayerStats().getAction().getCurrent();
            
            // 2. 判斷行動力夠不夠扣（大於等於 1 才能執行）
            if (currentAction >= 1.0) {
                // 3. 夠扣的話，傳入 -1.0 扣除 1 點行動力
                DataManager.getInstance().getPlayerStats().getAction().add(1.0);
            } else {
                // 4. 不夠扣的話，彈出提示，不執行任何動作
            }
        });
        loopContent.add(btnMeditate);
        loopContent.add(Box.createVerticalStrut(5));

        // 用我們之前寫好的 CollapsiblePanel 把 loopContent 包起來
        CollapsiblePanel loopSection = new CollapsiblePanel("通用行動", loopContent);
        gbc.gridy = 0; // 第 0 行
        cell2.add(loopSection, gbc);


        // --- 你可以輕鬆複製這段，做第二個折疊分類：例如 Area 行動 ---
        JPanel areaContent = new JPanel();
        areaContent.setLayout(new BoxLayout(areaContent, BoxLayout.Y_AXIS));

        for(int i = 1; i <= 1; i++) {
            JButton btn = new JButton("Area 行動 " + i);
            btn.setPreferredSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            areaContent.add(btn);
            areaContent.add(Box.createVerticalStrut(1));
        }

        CollapsiblePanel areaSection = new CollapsiblePanel("Area Action", areaContent);
        gbc.gridy = 1; // 第 1 行
        gbc.insets = new Insets(10, 0, 0, 0); // 頂部留 10 像素空隙
        cell2.add(areaSection, gbc);

        // ⭐ 關鍵：加一個隱形的墊片在最下面，把上面的東西全部頂上去
        gbc.gridy = 2;
        gbc.weighty = 1; // 把剩餘的垂直空間全部給這個墊片
        cell2.add(Box.createGlue(), gbc);

        

        // 2. 保持你原本的滾動設定，完全不用變動
        JScrollPane scroll2 = new JScrollPane(cell2);
        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 強制顯示滾動條測試

        // 3. 丟進你的中央面板
        centerPanel.add(scroll2);

// ===== 第 3 格 (家/劇情) =====
        JPanel cell3 = new JPanel();
        cell3.setLayout(new GridBagLayout());
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.HORIZONTAL; gbc3.weightx = 1; gbc3.gridx = 0;

        // 建立折疊內部要裝東西的盒子
        JPanel homeContent = new JPanel();
        homeContent.setLayout(new BoxLayout(homeContent, BoxLayout.Y_AXIS));

        // 🧠 核心修正：把原本前面的 Runnable 拿掉！直接賦值給剛剛宣告的全域變數
        refreshCell3UI = new Runnable() {
            @Override
            public void run() {
                homeContent.removeAll(); 
                currentStoryButtons.clear();
                currentStoryOptions.clear();

                StoryNode currentNode = StoryManager.getInstance().getCurrentNode();

                if (currentNode != null) {
                    for (StoryNode.StoryOption option : currentNode.getOptions()) {
                        JButton btn = new JButton(option.text);
                        btn.setPreferredSize(buttonSize); btn.setMinimumSize(buttonSize); btn.setMaximumSize(buttonSize);
                        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

                        btn.addActionListener(e -> {
                            if (option.action != null) option.action.run(); 
                            StoryManager.getInstance().setCurrentNodeId(option.targetNodeId); 
                            run(); 
                        });

                        homeContent.add(btn);
                        homeContent.add(Box.createVerticalStrut(5));

                        currentStoryButtons.add(btn);
                        currentStoryOptions.add(option);
                    }
                }

                homeContent.revalidate();
                homeContent.repaint();
            }
        };

        // ⚡ 初始點火跑第一次
        refreshCell3UI.run();

        // =============================================================
        // 🟢 這裡就是漏掉的關鍵屁股！把東西真正裝進畫面上
        // =============================================================
        // 1. 用你原本的自訂類別，把裝有按鈕的盒子打包成名為「家」的折疊面板
        CollapsiblePanel homeSection = new CollapsiblePanel("家", homeContent);
        
        // 2. 把折疊面板塞進 cell3 的第一列 (gridy = 0)
        gbc3.gridy = 0; 
        cell3.add(homeSection, gbc3);

        // 3. 在下方塞一個彈性置頂墊片，把折疊面板往上推，維持排版美觀
        gbc3.gridy = 1; 
        gbc3.weighty = 1; 
        cell3.add(Box.createGlue(), gbc3);

        // 4. 把 cell3 丟進滾動面板裡，最後交給中央主要面板
        JScrollPane scroll3 = new JScrollPane(cell3);
        scroll3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // 💡 註：因為 update 裡已經用 this.revalidate() 全域刷新了，
        // 這裡只要確保 scroll3 有順利加到你的中央佈局（通常是裝這四格的容器）即可。
        // 如果你原本的寫法是 centerPanel.add(scroll3);，請保留它：
        centerPanel.add(scroll3);
       


        // ===== 第 4 格 (Next Action) =====
        JPanel cell4 = new JPanel();
        cell4.setLayout(new BoxLayout(cell4, BoxLayout.Y_AXIS));
        JScrollPane scroll4 = new JScrollPane(cell4);
        centerPanel.add(scroll4);


        // ===== 第 5 格 (Dungeon) =====
        JPanel cell5 = new JPanel();
        cell5.setLayout(new BoxLayout(cell5, BoxLayout.Y_AXIS));
        JScrollPane scroll5 = new JScrollPane(cell5);
        centerPanel.add(scroll5);


        // 讓整個區塊的 上、左、下、右 各留出 15 像素的空隙
        centerPanel.setBorder(BorderFactory.createEmptyBorder(126, 350, 100, 200));
        // 最後，把整個 5 大列的總面板，放到主畫面的正中央
        this.add(centerPanel, BorderLayout.CENTER);

          // =============================================================
        // 側邊欄狀態配置 (顯示 行動力 與 命運)
        // =============================================================
        // 1. 建立側邊欄框架與內容大盒子
        StatusSidebar sidebar = new StatusSidebar();
        sidebar.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        JPanel myEquipment = new JPanel();
        myEquipment.setLayout(new BoxLayout(myEquipment, BoxLayout.Y_AXIS));

        // -------------------------------------------------------------
        // ⚡ 【行動力進度條】
        // -------------------------------------------------------------
        int maxAction = (int) DataManager.getInstance().getPlayerStats().getAction().getMax();
        JProgressBar swordBar = new JProgressBar(0, maxAction) {
            @Override
            protected void paintComponent(Graphics g) {
                double currentVal = DataManager.getInstance().getPlayerStats().getAction().getCurrent();
                double maxVal = DataManager.getInstance().getPlayerStats().getAction().getMax();
                
                setValue((int) currentVal);
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                
                int width = getWidth(); int height = getHeight();
                FontMetrics fm = g2d.getFontMetrics();
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                
                g2d.drawString("行動", 5, textY);
                String valueText = String.format("%.2f/%.0f", currentVal, maxVal);
                int textX = width - fm.stringWidth(valueText) - 5;
                g2d.drawString(valueText, textX, textY);
            }
        };
        // 設定行動條外觀
        swordBar.setBackground(new Color(240, 240, 240));
        swordBar.setForeground(new Color(255, 182, 193));
        swordBar.setStringPainted(false);
        swordBar.setBorder(BorderFactory.createEmptyBorder());
        swordBar.setPreferredSize(new Dimension(180, 25));
        swordBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        myEquipment.add(swordBar);
        myEquipment.add(Box.createVerticalStrut(8)); // 兩條狀態列之間的間距

        // -------------------------------------------------------------
        // 🌟 新增：【命運進度條】
        // -------------------------------------------------------------
        int maxFate = (int) DataManager.getInstance().getPlayerStats().getFate().getMax();
        JProgressBar fateBar = new JProgressBar(0, maxFate) {
            @Override
            protected void paintComponent(Graphics g) {
                // 向 DataManager 撈取最新的命運數值
                double currentVal = DataManager.getInstance().getPlayerStats().getFate().getCurrent();
                double maxVal = DataManager.getInstance().getPlayerStats().getFate().getMax();
                
                // 動態同步進度條的最大值與目前值
                setMaximum((int) maxVal);
                setValue((int) currentVal);
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                
                int width = getWidth(); int height = getHeight();
                FontMetrics fm = g2d.getFontMetrics();
                int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
                
                // 繪製左邊字樣
                g2d.drawString("命運", 5, textY);
                // 繪製右邊帶小數點的精準數據
                String valueText = String.format("%.2f/%.0f", currentVal, maxVal);
                int textX = width - fm.stringWidth(valueText) - 5;
                g2d.drawString(valueText, textX, textY);
            }
        };
        // 設定命運條外觀（採用柔和的淺藍色）
        fateBar.setBackground(new Color(240, 240, 240)); 
        fateBar.setForeground(new Color(173, 216, 230)); 
        fateBar.setStringPainted(false); 
        fateBar.setBorder(BorderFactory.createEmptyBorder()); 
        fateBar.setPreferredSize(new Dimension(180, 25));
        fateBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); 
        
        myEquipment.add(fateBar);

        // 3. 打包裝進面板並加入側邊欄
        CollapsiblePanel equipSection = new CollapsiblePanel("能量", myEquipment);
        sidebar.addSection(equipSection);
        this.add(sidebar, BorderLayout.EAST);
    }

    

    // 啟動遊戲執行緒
    public void startGameLoop() {
        gameThread = new Thread(this);
        isRunning = true;
        gameThread.start();
    }

    @Override
   public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (isRunning) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
    

// 【步驟 1】更新遊戲邏輯
    private void update() {
        playerStats.updateRecovery();
        DataManager.getInstance().getPlayerStats().updateRecovery();

        // 🌟 強制喚醒：不使用 centerPanel，改用 Swing 最頂層的 this (也就是 UIManager 本身) 來重新排版
        if (currentStoryButtons.isEmpty() && refreshCell3UI != null) {
            refreshCell3UI.run();
            
            // 💡 修正：直接叫 UIManager 本身重新布局與重繪，保證全畫面重新整理
            this.revalidate();
            this.repaint();
        }

        // 劇情控制大腦：每幀即時檢查按鈕狀態與動態更新 ToolTip
        StoryNode currentNode = StoryManager.getInstance().getCurrentNode();
        if (currentNode != null) {
            
            for (int i = 0; i < currentStoryButtons.size(); i++) {
                if (i >= currentStoryOptions.size()) break;

                JButton btn = currentStoryButtons.get(i);
                StoryNode.StoryOption option = currentStoryOptions.get(i);

                // 🧠 檢查條件是否通過
                boolean isAvailable = (option.condition == null) || option.condition.get();

                // 1. 控管按鈕能不能點
                if (btn.isEnabled() != isAvailable) {
                    btn.setEnabled(isAvailable);
                }

                // 2. 控管滑鼠指上去的 ToolTip
                double currentFate = DataManager.getInstance().getPlayerStats().getFate().getCurrent();
                double maxFate = DataManager.getInstance().getPlayerStats().getFate().getMax();

                btn.setToolTipText(
                    "<html>" +
                    "<div style='background-color: #2D2D2D; color: #E0E0E0; padding: 12px 15px; font-family: \"Microsoft YaHei\", sans-serif; font-size: 14px; border: 1px solid #1A1A1A; line-height: 1.4;'>" +
                        "<b style='font-size: 18px; color: #FFFFFF;'>" + option.text + "</b><br>" +
                        "<i style='color: #B5B5B5; display: inline-block; margin-top: 4px; margin-bottom: 4px;'>" + currentNode.getDescription() + "</i><br>" +
                        "<span style='color: #8A8A8A;'>&lt;&lt; 花費 &gt;&gt;</span><br>" +
                        (isAvailable ? 
                        "<span style='color: #5AFF5A;'>命運 : -4 (" + String.format("%.2f", currentFate) + " / " + (int)maxFate + ") 【可執行】</span><br>" : 
                        "<span style='color: #FF5A5A;'>命運 : -4 (" + String.format("%.2f", currentFate) + " / " + (int)maxFate + ") 【無法執行】</span><br>") +
                        "<span style='color: #8A8A8A;'>[完成效果]</span><br>" +
                        "<span style='color: #E0E0E0;'>命運 上限 : +5</span>" +
                    "</div>" +
                    "</html>"
                );
            }
        }
    }

    // 【步驟 2】繪製遊戲畫面
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 這裡就是你的畫布！
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        

        
    
    }

    /**
 * 快速建立一個空白的折疊面板
 * @param title 摺疊選單的標題（例如 "冒險行動"）
 * @return 裡面還沒有裝東西的空白 CollapsiblePanel
 */
    private CollapsiblePanel createCollapsibleSection(String title, JPanel contentPanel) {
        // 設定垂直排列，這樣之後你從外面加按鈕進去時，按鈕會乖乖直著排
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // 直接用折疊面板包裹這個內容面板並回傳
        return new CollapsiblePanel(title, contentPanel);
    }

    // 主程式入口
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 按 X 就關閉程式
        window.setResizable(false);                            // 固定視窗大小
        window.setTitle("You");

        UIManager gamePanel = new UIManager();
        window.add(gamePanel);
        window.pack(); // 讓視窗大小剛好符合畫布大小



        window.setLocationRelativeTo(null); // 讓視窗顯示在螢幕正中央
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        window.setVisible(true);            // 讓視窗看得見
        
        // 開始執行遊戲迴圈
        gamePanel.startGameLoop();
    }

}
