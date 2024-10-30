package src;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class RandomNumberSelector extends JFrame {
    private JPanel mainPanel;
    private Timer animationTimer;
    private Random random = new Random();
    private JButton fullScreenButton;

    // 在类的开头添加字体相关的常量
    private static final Font NUMBER_FONT = new Font("Helvetica", Font.BOLD, 48);
    private static final Color NUMBER_COLOR = new Color(0, 0, 0); // 纯黑色
    private static final Color SEPARATOR_COLOR = new Color(100, 100, 100); // 灰色分隔符

    // 在类的开头添加历史记录列表
    private List<NumberRecord> history = new ArrayList<>();

    // 在类的开头修改颜色常量
    private static final Color BACKGROUND_COLOR = new Color(240, 244, 248); // 统一使用这个背景色
    private static final Color BUTTON_COLOR = new Color(24, 144, 255);      // 更清新的蓝色
    private static final Color BUTTON_HOVER_COLOR = new Color(64, 169, 255); // 悬停时的浅蓝色
    private static final Color PANEL_BACKGROUND = Color.WHITE;              // 数字面板背景色

    // 修改setFlatButtonStyle方法
    private void setFlatButtonStyle(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setOpaque(true);  // 确保按钮背景色可见
        button.setContentAreaFilled(true);  // 确保内容区域填充
        
        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }

    public RandomNumberSelector() {
        setTitle("随机数字选择器");
        
        // 获取屏幕尺寸
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        
        // 检查是否为Mac系统
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        
        if (isMac) {
            // Mac系统使用最大化
            setSize(dm.getWidth(), dm.getHeight());
            setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            // Windows系统使用全屏
            setUndecorated(true);
            gd.setFullScreenWindow(this);
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建主面板，使用BorderLayout并添加更大的边距
        mainPanel = new JPanel(new BorderLayout(50, 50));  // 增加间距
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));  // 增加边距

        // 创建一个包装面板来实现垂直居中
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(30, 30));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // 创建上下两个子面板
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30)); // 增加垂直间距
        
        topPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBackground(BACKGROUND_COLOR);

        // 初始显示空面板
        displayEmptyPanels(topPanel, bottomPanel);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(bottomPanel, BorderLayout.CENTER);
        
        // 使用GridBagLayout来实现整体居中
        wrapperPanel.add(contentPanel);
        mainPanel.add(wrapperPanel, BorderLayout.CENTER);

        // 添加键盘监听器
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(e -> {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && 
                    e.getID() == KeyEvent.KEY_PRESSED) {
                    generateNumbers();
                    return true;  // 消费这个事件
                }
                return false;  // 不消费其他事件
            });

        // 添加按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // 生成数字按钮
        JButton generateButton = new JButton("生成数字");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));  // 增大按钮字体
        generateButton.addActionListener(e -> {
            generateNumbers();
        });

        // 全屏按钮
        fullScreenButton = new JButton("退出全屏");  // 使用成员变量
        fullScreenButton.setFont(new Font("Arial", Font.BOLD, 16));
        fullScreenButton.addActionListener(e -> {
            toggleFullScreen();
        });

        // 历史记录按钮
        JButton historyButton = new JButton("历史记录");
        historyButton.setFont(new Font("Arial", Font.BOLD, 16));
        historyButton.addActionListener(e -> showHistory());

        // 添加退出按钮
        JButton exitButton = new JButton("退出程序");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(e -> {
            SoundManager.playButtonClick();
            int result = JOptionPane.showConfirmDialog(
                this,
                "确定要退出程序吗？",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        buttonPanel.add(generateButton);
        buttonPanel.add(fullScreenButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(exitButton);  // 添加退出按钮

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 移除窗口大小改变的监听器，因为我们现在总是全屏显示
        
        // 初始化完成后立即更新面板大小
        SwingUtilities.invokeLater(this::updatePanelSizes);

        // 在构造函数中，对每个按钮应用新样式
        setFlatButtonStyle(generateButton);
        setFlatButtonStyle(fullScreenButton);
        setFlatButtonStyle(historyButton);
        setFlatButtonStyle(exitButton);  // 设置退出按钮样式

        // 添加窗口大小改变的监听器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePanelSizes();
            }
        });
    }

    private void updatePanelSizes() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // 获取当前窗口大小
            int windowWidth = getWidth();
            int windowHeight = getHeight();
            
            // 调整计算逻辑，确保有足够空间显示数字
            int panelWidth = Math.min((windowWidth - 300) / 3, (windowHeight - 250) / 2);
            int panelHeight = panelWidth * 3 / 4;  // 调整高宽比为 3:4，给数字留出更多空间
            
            // 调整最小尺寸
            panelWidth = Math.max(panelWidth, 160);  // 减小最小宽度
            panelHeight = Math.max(panelHeight, 120); // 相应调整最小高度
            
            // 调整字体大小计算逻辑
            int fontSize = Math.min(panelWidth / 4, panelHeight / 3);  // 调整字体大小比例
            fontSize = Math.max(fontSize, 20); // 调整最小字体大小
            
            // 获取正确的面板引用
            JPanel wrapperPanel = (JPanel) mainPanel.getComponent(0);
            JPanel contentPanel = (JPanel) wrapperPanel.getComponent(0);
            JPanel topPanel = (JPanel) contentPanel.getComponent(0);
            JPanel bottomPanel = (JPanel) contentPanel.getComponent(1);
            
            // 更新上面和下面的面板
            updatePanelsInContainer(topPanel, new Dimension(panelWidth, panelHeight), fontSize);
            updatePanelsInContainer(bottomPanel, new Dimension(panelWidth, panelHeight), fontSize);
            
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }

    private void updatePanelsInContainer(Container container, Dimension size, int fontSize) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setPreferredSize(size);
                
                // 更新面板中的标签
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if ("·".equals(label.getText())) {
                            // 点号使用相同大小的字体
                            label.setFont(new Font("Arial", Font.BOLD, fontSize));
                        } else {
                            // 数字使用粗体
                            label.setFont(new Font("Arial", Font.BOLD, fontSize));
                        }
                    }
                }
            }
        }
    }

    private void displayEmptyPanels(JPanel topPanel, JPanel bottomPanel) {
        topPanel.removeAll();
        bottomPanel.removeAll();

        // 添加上面三个面板
        for (int i = 0; i < 3; i++) {
            topPanel.add(createNumberPanel("", ""));
        }

        // 添加下面两个面板
        for (int i = 0; i < 2; i++) {
            bottomPanel.add(createNumberPanel("", ""));
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createNumberPanel(String num1, String num2) {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.setPreferredSize(new Dimension(400, 240));
        panel.setBackground(BACKGROUND_COLOR);
        
        // 减小边框和内边距
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 2, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)  // 减小内边距
        ));

        JLabel label1 = new JLabel(num1, SwingConstants.CENTER);
        JLabel separator = new JLabel("·", SwingConstants.CENTER);
        JLabel label2 = new JLabel(num2, SwingConstants.CENTER);
        
        // 设置标签背景为统一背景色
        label1.setBackground(BACKGROUND_COLOR);
        separator.setBackground(BACKGROUND_COLOR);
        label2.setBackground(BACKGROUND_COLOR);
        
        configureNumberLabel(label1);
        configureSeparatorLabel(separator);
        configureNumberLabel(label2);

        panel.add(label1);
        panel.add(separator);
        panel.add(label2);
        
        return panel;
    }

    // 修改configureNumberLabel方法
    private void configureNumberLabel(JLabel label) {
        label.setFont(NUMBER_FONT);
        label.setForeground(NUMBER_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        label.setBackground(BACKGROUND_COLOR);
        label.setOpaque(true);
    }

    // 修改configureSeparatorLabel方法
    private void configureSeparatorLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.PLAIN, 48));
        label.setForeground(SEPARATOR_COLOR);
        label.setBackground(BACKGROUND_COLOR);
        label.setOpaque(true);
    }

    private void startNumberAnimation() {
        final int[] frameCount = {0};
        final int totalFrames = 20;
        List<List<Integer>> finalNumbers = generateFinalNumbers();
        
        // 计算当前的面板和字体大小
        int panelWidth = (getWidth() - 180) / 3;
        int panelHeight = panelWidth * 2 / 3;
        int fontSize = Math.min(panelWidth / 4, panelHeight / 2);
        Dimension panelSize = new Dimension(panelWidth, panelHeight);
        
        animationTimer = new Timer(50, null);
        animationTimer.addActionListener(e -> {
            if (frameCount[0] >= totalFrames) {
                animationTimer.stop();
                displayFinalNumbers(finalNumbers, panelSize, fontSize);
                // 获取并启用生成按钮
                JPanel buttonPanel = (JPanel) getContentPane().getComponent(1);
                JButton generateButton = (JButton) buttonPanel.getComponent(0);
                generateButton.setEnabled(true);
                return;
            }
            
            // 获取正确的面板引用
            JPanel wrapperPanel = (JPanel) mainPanel.getComponent(0);
            JPanel contentPanel = (JPanel) wrapperPanel.getComponent(0);
            JPanel topPanel = (JPanel) contentPanel.getComponent(0);
            JPanel bottomPanel = (JPanel) contentPanel.getComponent(1);
            
            topPanel.removeAll();
            bottomPanel.removeAll();
            
            // 更新上面三个面板
            for (int i = 0; i < 3; i++) {
                int num1 = random.nextInt(20) + 1;
                int num2 = random.nextInt(20) + 1;
                JPanel numberPanel = createNumberPanel(
                    String.valueOf(num1),
                    String.valueOf(num2)
                );
                numberPanel.setPreferredSize(panelSize);
                updatePanelFontSize(numberPanel, fontSize);
                topPanel.add(numberPanel);
            }
            
            // 更新下面两个面板
            for (int i = 0; i < 2; i++) {
                int num1 = random.nextInt(20) + 1;
                int num2 = random.nextInt(20) + 1;
                JPanel numberPanel = createNumberPanel(
                    String.valueOf(num1),
                    String.valueOf(num2)
                );
                numberPanel.setPreferredSize(panelSize);
                updatePanelFontSize(numberPanel, fontSize);
                bottomPanel.add(numberPanel);
            }
            
            mainPanel.revalidate();
            mainPanel.repaint();
            
            frameCount[0]++;
        });
        animationTimer.start();
    }

    private List<List<Integer>> generateFinalNumbers() {
        List<List<Integer>> groups = new ArrayList<>();
        
        // 第一组 (1-4)
        List<Integer> group1 = new ArrayList<>();
        int num1 = random.nextInt(3) + 1;  // 1-3
        int num2;
        do {
            num2 = random.nextInt(4) + 1;  // 1-4
        } while (num2 <= num1);
        group1.add(num1);
        group1.add(num2);
        groups.add(group1);

        // 第二组 (5-8)
        List<Integer> group2 = new ArrayList<>();
        num1 = random.nextInt(3) + 5;  // 5-7
        do {
            num2 = random.nextInt(4) + 5;  // 5-8
        } while (num2 <= num1);
        group2.add(num1);
        group2.add(num2);
        groups.add(group2);

        // 第三组 (9-12)
        List<Integer> group3 = new ArrayList<>();
        num1 = random.nextInt(3) + 9;  // 9-11
        do {
            num2 = random.nextInt(4) + 9;  // 9-12
        } while (num2 <= num1);
        group3.add(num1);
        group3.add(num2);
        groups.add(group3);

        // 第四组 (13-16)
        List<Integer> group4 = new ArrayList<>();
        num1 = random.nextInt(3) + 13;  // 13-15
        do {
            num2 = random.nextInt(4) + 13;  // 13-16
        } while (num2 <= num1);
        group4.add(num1);
        group4.add(num2);
        groups.add(group4);

        // 第五组 (17-20)
        List<Integer> group5 = new ArrayList<>();
        num1 = random.nextInt(3) + 17;  // 17-19
        do {
            num2 = random.nextInt(4) + 17;  // 17-20
        } while (num2 <= num1);
        group5.add(num1);
        group5.add(num2);
        groups.add(group5);

        return groups;
    }

    private void displayFinalNumbers(List<List<Integer>> groups, Dimension panelSize, int fontSize) {
        // 播放数字生成完成音效
        SoundManager.playNumberGenerate();
        
        // 获取正确的面板引用
        JPanel wrapperPanel = (JPanel) mainPanel.getComponent(0);
        JPanel contentPanel = (JPanel) wrapperPanel.getComponent(0);
        JPanel topPanel = (JPanel) contentPanel.getComponent(0);
        JPanel bottomPanel = (JPanel) contentPanel.getComponent(1);
        
        topPanel.removeAll();
        bottomPanel.removeAll();

        // 显示上面三组数字
        for (int i = 0; i < 3; i++) {
            List<Integer> group = groups.get(i);
            JPanel numberPanel = createNumberPanel(
                String.valueOf(group.get(0)),
                String.valueOf(group.get(1))
            );
            numberPanel.setPreferredSize(panelSize);
            updatePanelFontSize(numberPanel, fontSize);
            topPanel.add(numberPanel);
        }

        // 显示下面两组数字
        for (int i = 3; i < 5; i++) {
            List<Integer> group = groups.get(i);
            JPanel numberPanel = createNumberPanel(
                String.valueOf(group.get(0)),
                String.valueOf(group.get(1))
            );
            numberPanel.setPreferredSize(panelSize);
            updatePanelFontSize(numberPanel, fontSize);
            bottomPanel.add(numberPanel);
        }

        mainPanel.revalidate();
        mainPanel.repaint();

        // 保存到历史记录
        int[][] numbers = new int[5][2];
        for (int i = 0; i < groups.size(); i++) {
            numbers[i][0] = groups.get(i).get(0);
            numbers[i][1] = groups.get(i).get(1);
        }
        history.add(new NumberRecord(numbers));
    }

    // 更新updatePanelFontSize方法
    private void updatePanelFontSize(JPanel panel, int fontSize) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if ("·".equals(label.getText())) {
                    // 分隔符使用较小的字体
                    label.setFont(new Font("Arial", Font.PLAIN, fontSize));
                    label.setForeground(SEPARATOR_COLOR);
                } else {
                    // 数字使用更清晰的字体
                    label.setFont(new Font("Helvetica", Font.BOLD, fontSize));
                    label.setForeground(NUMBER_COLOR);
                }
            }
        }
    }

    // 添加个新方法来处理生成数字的逻辑
    private void generateNumbers() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        // 播放按钮点击音效
        SoundManager.playButtonClick();
        
        // 获取底部面板中的生成数字按钮
        JPanel buttonPanel = (JPanel) getContentPane().getComponent(1);
        JButton generateButton = (JButton) buttonPanel.getComponent(0);
        generateButton.setEnabled(false);
        
        startNumberAnimation();
    }

    // 修改全屏切换方法，使用Mac友好的方式
    private void toggleFullScreen() {
        SoundManager.playButtonClick();
        // 检查是否为Mac系统
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        
        if (isMac) {
            // Mac系统使用窗口最大化
            if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
                // 当前是最大化状态，恢复到正常大小
                setExtendedState(Frame.NORMAL);
                fullScreenButton.setText("全屏显示");
            } else {
                // 最大化窗口
                setExtendedState(Frame.MAXIMIZED_BOTH);
                fullScreenButton.setText("退出全屏");
            }
        } else {
            // Windows系统使用原来的全屏方式
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            
            if (gd.getFullScreenWindow() == this) {
                // 退出全屏
                gd.setFullScreenWindow(null);
                dispose();
                setUndecorated(false);
                setVisible(true);
            } else {
                // 进入全屏
                dispose();
                setUndecorated(true);
                setVisible(true);
                gd.setFullScreenWindow(this);
            }
        }
        
        // 更新面板大小
        SwingUtilities.invokeLater(this::updatePanelSizes);
    }

    private void showHistory() {
        SoundManager.playButtonClick();
        HistoryDialog dialog = new HistoryDialog(this, history);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RandomNumberSelector selector = new RandomNumberSelector();
            selector.setVisible(true);
        });
    }
}