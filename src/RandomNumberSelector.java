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

    // 在类的开头添加字体相关的常量
    private static final Font NUMBER_FONT = new Font("Helvetica", Font.BOLD, 150);
    private static final Color NUMBER_COLOR = new Color(0, 0, 0); // 纯黑色
    private static final Color SEPARATOR_COLOR = new Color(100, 100, 100); // 灰色分隔符

    // 在类的开头修改颜色常量
    private static final Color BACKGROUND_COLOR = new Color(240, 244, 248); // 统一使用这个背景色
    private static final Color BUTTON_COLOR = new Color(24, 144, 255);      // 更清新的蓝色
    private static final Color BUTTON_HOVER_COLOR = new Color(64, 169, 255); // 悬蓝色
    private static final Color PANEL_BACKGROUND = Color.WHITE;              // 数字面板背景色

    // 修改setFlatButtonStyle方法
    private void setFlatButtonStyle(JButton button) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setOpaque(true);  // 保按钮背景色可见
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
        
        // 获取屏幕设备
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        // 设置为无边框
        setUndecorated(true);
        
        // 检查是否支持全屏模式
        if (gd.isFullScreenSupported()) {
            // 设置为全屏
            gd.setFullScreenWindow(this);
        } else {
            // 如果不支持全屏，则使用最大化窗口
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            // 获取屏幕尺寸
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width, screenSize.height);
        }
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 删除 setLocationRelativeTo(null); 因为我们已经用 setBounds 设置了位置

        // 修改标题栏布局
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        titleBar.setBackground(BACKGROUND_COLOR);
        
        // 创建最小化按钮
        JButton minimizeButton = new JButton("─");
        minimizeButton.setFocusPainted(false);
        minimizeButton.setBorderPainted(false);
        minimizeButton.setBackground(BACKGROUND_COLOR);
        minimizeButton.setPreferredSize(new Dimension(45, 30));
        minimizeButton.addActionListener(e -> {
            setState(Frame.ICONIFIED);
        });

        // 创建闭按钮
        JButton closeButton = new JButton("×");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(BACKGROUND_COLOR);
        closeButton.setPreferredSize(new Dimension(45, 30));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager.playButtonClick();
                showCustomConfirmDialog();
            }
        });

        // 设置按钮样式
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        minimizeButton.setFont(buttonFont);
        closeButton.setFont(buttonFont);
        
        // 添加鼠标悬停效果
        Color hoverColor = new Color(220, 220, 220);
        MouseAdapter buttonHover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                ((JButton)e.getSource()).setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                ((JButton)e.getSource()).setBackground(BACKGROUND_COLOR);
            }
        };
        
        minimizeButton.addMouseListener(buttonHover);
        closeButton.addMouseListener(buttonHover);

        titleBar.add(minimizeButton);
        titleBar.add(closeButton);

        // 将标题栏添加到窗口顶部
        add(titleBar, BorderLayout.NORTH);

        // 创建主面板使用BorderLayout并添加更大的边距
        mainPanel = new JPanel(new BorderLayout(50, 50));  // 加间距
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));  // 增加边距

        // 创建一个包装面板来实现垂直居中
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BACKGROUND_COLOR);
        
        // 创建内容面
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

        // 修改底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // 生成数字按钮
        JButton generateButton = new JButton("生成数字");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNumbers();
            }
        });

        // 历史记录按钮
        JButton historyButton = new JButton("历史记录");
        historyButton.setFont(new Font("Arial", Font.BOLD, 16));
        historyButton.addActionListener(e -> {
            SoundManager.playButtonClick();
            HistoryDialog dialog = new HistoryDialog(this, DataManager.getHistory());
            dialog.setVisible(true);
        });

        buttonPanel.add(generateButton);
        buttonPanel.add(historyButton);

        // 设置按钮样式
        setFlatButtonStyle(generateButton);
        setFlatButtonStyle(historyButton);

        // 添加面板到窗口
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 初始化完成后立即更新面板大小
        SwingUtilities.invokeLater(this::updatePanelSizes);

        // 添加窗口大小改变的监听
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
            int panelHeight = panelWidth * 3 / 4;  // 调整高宽比
            
            // 调整最小尺寸
            panelWidth = Math.max(panelWidth, 200);  // 增加最小宽度
            panelHeight = Math.max(panelHeight, 150); // 增加最小高度
            
            // 调整字体大小计算逻辑
            int fontSize = (int)Math.min(panelWidth / 1.5, panelHeight / 1.5);
            fontSize = Math.max(fontSize, 64);
            
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
                
                // 更新面板中的标
                for (Component c : panel.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if ("·".equals(label.getText())) {
                            // 点号使用相同大小的字体
                            label.setFont(new Font("Arial", Font.BOLD, fontSize));
                        } else {
                            // 字使用粗体
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
            topPanel.add(createNumberPanel("", "", 0));
        }

        // 添加下面两个面板
        for (int i = 0; i < 2; i++) {
            bottomPanel.add(createNumberPanel("", "", 0));
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createNumberPanel(String num1, String num2, int groupIndex) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(400, 240));
        panel.setBackground(BACKGROUND_COLOR);
        
        // 增加内边距，特别是底部边距
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 2, true),
            BorderFactory.createEmptyBorder(15, 15, 25, 15)  // 将底部内边距从15改为25
        ));

        // 创建一个固定宽度的面板来包含数字和分隔符
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);

        // 创建三个固定宽度的面板
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel middlePanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());
        
        leftPanel.setBackground(BACKGROUND_COLOR);
        middlePanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setBackground(BACKGROUND_COLOR);

        // 增加面板高度
        leftPanel.setPreferredSize(new Dimension(150, 220));    // 从200改为220
        middlePanel.setPreferredSize(new Dimension(100, 220));  // 从200改为220
        rightPanel.setPreferredSize(new Dimension(150, 220));   // 从200改为220

        // 创建标签
        JLabel label1 = new JLabel(num1, SwingConstants.RIGHT);
        JLabel separator = new JLabel("·", SwingConstants.CENTER);
        JLabel label2 = new JLabel(num2, SwingConstants.LEFT);
        
        // 配置标签
        configureNumberLabel(label1);
        configureSeparatorLabel(separator);
        configureNumberLabel(label2);

        // 为标签添加一些底部边距
        label1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        separator.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        label2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 添加标签到各自的面板
        leftPanel.add(label1);
        middlePanel.add(separator);
        rightPanel.add(label2);

        // 使用 GridBagLayout 布局
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // 添加三个面板到中心面板
        centerPanel.add(leftPanel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        centerPanel.add(middlePanel, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        centerPanel.add(rightPanel, gbc);

        // 将中心面板添加到主面板
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void configureNumberLabel(JLabel label) {
        label.setFont(NUMBER_FONT);
        label.setForeground(NUMBER_COLOR);
        label.setBackground(BACKGROUND_COLOR);
        label.setOpaque(true);
        label.setBorder(null);
    }

    private void configureSeparatorLabel(JLabel label) {
        label.setFont(NUMBER_FONT);
        label.setForeground(SEPARATOR_COLOR);
        label.setBackground(BACKGROUND_COLOR);
        label.setOpaque(true);
        label.setBorder(null);
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
        
        animationTimer = new Timer(50, e -> {
            if (frameCount[0] >= totalFrames) {
                animationTimer.stop();
                displayFinalNumbers(finalNumbers, panelSize, fontSize);
                // ��取并启用生成按钮
                JPanel buttonPanel = (JPanel) getContentPane().getComponent(2);
                JButton generateButton = (JButton) buttonPanel.getComponent(0);
                generateButton.setEnabled(true);
                return;
            }
            
            // 获取正确的面板引用
            JPanel wrapperPanel = (JPanel) mainPanel.getComponent(0);
            JPanel contentPanel = (JPanel) wrapperPanel.getComponent(0);
            JPanel topPanel = (JPanel) contentPanel.getComponent(0);
            JPanel bottomPanel = (JPanel) contentPanel.getComponent(1);
            
            // 动画逻辑
            displayRandomNumbers(topPanel, bottomPanel, panelSize, fontSize);
            
            frameCount[0]++;
        });
        
        animationTimer.start();
    }

    private void displayRandomNumbers(JPanel topPanel, JPanel bottomPanel, Dimension panelSize, int fontSize) {
        topPanel.removeAll();
        bottomPanel.removeAll();
        
        // 更新上面三面板
        for (int i = 0; i < 3; i++) {
            int num1 = random.nextInt(20) + 1;
            int num2 = random.nextInt(20) + 1;
            JPanel numberPanel = createNumberPanel(
                String.valueOf(num1),
                String.valueOf(num2),
                0
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
                String.valueOf(num2),
                0
            );
            numberPanel.setPreferredSize(panelSize);
            updatePanelFontSize(numberPanel, fontSize);
            bottomPanel.add(numberPanel);
        }
        
        mainPanel.revalidate();
        mainPanel.repaint();
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
        
        // 获取正确的板引用
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
                String.valueOf(group.get(1)),
                i  // 传入正确的组索引
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
                String.valueOf(group.get(1)),
                i  // 传入正确的组索引
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
        DataManager.addRecord(new NumberRecord(numbers));
    }

    // 更新updatePanelFontSize方法
    private void updatePanelFontSize(JPanel panel, int fontSize) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel centerPanel = (JPanel) c;
                for (Component centerComp : centerPanel.getComponents()) {
                    if (centerComp instanceof JPanel) {
                        JPanel subPanel = (JPanel) centerComp;
                        for (Component label : subPanel.getComponents()) {
                            if (label instanceof JLabel) {
                                JLabel jLabel = (JLabel) label;
                                if ("·".equals(jLabel.getText())) {
                                    // 分隔符使用固定大小的字体
                                    jLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
                                    jLabel.setForeground(SEPARATOR_COLOR);
                                } else {
                                    // 数字使用更大的字体
                                    jLabel.setFont(new Font("Helvetica", Font.BOLD, fontSize));
                                    jLabel.setForeground(NUMBER_COLOR);
                                }
                            }
                        }
                    }
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
        JPanel buttonPanel = (JPanel) getContentPane().getComponent(2);
        JButton generateButton = (JButton) buttonPanel.getComponent(0);
        generateButton.setEnabled(false);
        
        startNumberAnimation();
    }

    private void showHistory() {
        SoundManager.playButtonClick();
        HistoryDialog dialog = new HistoryDialog(this, DataManager.getHistory());
        dialog.setVisible(true);
    }

    // 添加自定义确认对话框方法
    private void showCustomConfirmDialog() {
        // 创建自定义对话框
        JDialog dialog = new JDialog(this, "确认退出", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // 创建标题栏
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BACKGROUND_COLOR);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel titleLabel = new JLabel("确认退出");
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel messageLabel = new JLabel("确定要退出程序吗？");
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 15));
        
        // 创建取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.setPreferredSize(new Dimension(80, 30));
        styleDialogButton(cancelButton, Color.WHITE, new Color(108, 117, 125));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // 创建确认按钮
        JButton confirmButton = new JButton("确认");
        confirmButton.setPreferredSize(new Dimension(80, 30));
        styleDialogButton(confirmButton, new Color(220, 53, 69), Color.WHITE);
        confirmButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        // 组装对话框
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        // 添加按下ESC键关闭对话框的功能
        dialog.getRootPane().registerKeyboardAction(
            e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // 显示对话框
        dialog.setVisible(true);
    }

    // 添加对话框按钮样式法
    private void styleDialogButton(JButton button, Color background, Color foreground) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorder(BorderFactory.createLineBorder(foreground, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (background.equals(Color.WHITE)) {
                    button.setBackground(new Color(248, 249, 250));
                } else {
                    button.setBackground(background.darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
    }

    // 添加补登按钮样式方法
    private void styleRegisterButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setBackground(new Color(24, 144, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(64, 169, 255));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(24, 144, 255));
            }
        });
    }

    // 添加补登对话框方法
    private void showRegisterDialog(int groupIndex) {
        JDialog dialog = new JDialog(this, "补登信息", true);
        dialog.setUndecorated(true);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // 创建标题栏
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(240, 244, 248));
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("补登信息 - 第" + (groupIndex + 1) + "组");
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        JButton closeButton = new JButton("×");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(new Color(240, 244, 248));
        closeButton.addActionListener(e -> dialog.dispose());
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);
        
        // 创建内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 添加姓名输入框
        JTextField nameField = new JTextField(20);
        JTextField cardIdField = new JTextField(20);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("姓名："), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("卡号："), gbc);
        
        gbc.gridx = 1;
        contentPanel.add(cardIdField, gbc);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton confirmButton = new JButton("确认");
        styleDialogButton(confirmButton, new Color(24, 144, 255), Color.WHITE);
        confirmButton.addActionListener(e -> {
            // 保存补登信息
            NumberRecord currentRecord = DataManager.getHistory().get(DataManager.getHistory().size() - 1);
            currentRecord.addRegistration(groupIndex, nameField.getText(), cardIdField.getText());
            DataManager.updateRecord(currentRecord);
            dialog.dispose();
        });
        
        buttonPanel.add(confirmButton);
        
        // 组装对话框
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        // 添加卡号输入框的键盘监听
        cardIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmButton.doClick();
                }
            }
        });
        
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RandomNumberSelector selector = new RandomNumberSelector();
            selector.setVisible(true);
        });
    }
}