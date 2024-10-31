package src;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryDialog extends JDialog {
    private JTable table;
    private List<NumberRecord> historyData;

    public HistoryDialog(Frame owner, List<NumberRecord> history) {
        super(owner, "历史记录", true);
        setUndecorated(true);
        this.historyData = history;
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // 创建标题栏
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(240, 244, 248));
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 创建标题面板（左侧）
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(new Color(240, 244, 248));
        
        // 返回按钮
        JButton backButton = new JButton("←");
        styleButton(backButton, new Color(240, 244, 248));
        backButton.addActionListener(e -> dispose());
        
        JLabel titleLabel = new JLabel("历史记录");
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        titlePanel.add(backButton);
        titlePanel.add(titleLabel);
        
        // 创建按钮面板（右侧）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(240, 244, 248));
        
        // 导出按
        JButton exportButton = new JButton("导出");
        styleButton(exportButton, new Color(24, 144, 255));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportHistory(history));
        
        // 关闭按钮
        JButton closeButton = new JButton("×");
        styleButton(closeButton, new Color(240, 244, 248));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        titleBar.add(titlePanel, BorderLayout.WEST);
        titleBar.add(buttonPanel, BorderLayout.EAST);
        
        // 创建表格模型
        String[] columnNames = {"日期", "时间", 
            "第一组", "姓名", "卡号",
            "第二组", "姓名", "卡号",
            "第三组", "姓名", "卡号",
            "第四组", "姓名", "卡号",
            "第五组", "姓名", "卡号"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // 添加数据到表格
        for (NumberRecord record : history) {
            Object[] row = new Object[17]; // 2 + (3 * 5) = 17列
            row[0] = record.getDate();
            row[1] = record.getTime();
            int[][] numbers = record.getNumbers();
            for (int i = 0; i < 5; i++) {
                int baseIndex = 2 + (i * 3);
                row[baseIndex] = numbers[i][0] + " - " + numbers[i][1];
                String name = record.getName(i);
                String cardId = record.getCardId(i);
                if ((name != null && !name.isEmpty()) || (cardId != null && !cardId.isEmpty())) {
                    row[baseIndex + 1] = name != null ? name : "";
                    row[baseIndex + 2] = cardId != null ? cardId : "";
                } else {
                    row[baseIndex + 1] = "点击补登";
                    row[baseIndex + 2] = "";
                }
            }
            model.addRow(row);
        }
        
        // 创建表格
        table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(230, 244, 255));
        table.setSelectionForeground(Color.BLACK);
        
        // 设置单元格渲染器
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // 检查是否是姓名列（4, 7, 10, 13, 16）
                if (column == 3 || column == 6 || column == 9 || column == 12 || column == 15) {
                    if ("点击补登".equals(value)) {
                        setForeground(new Color(24, 144, 255));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        // 添加表格点击事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                // 检查是否点击了姓名列
                if (row >= 0 && (col == 3 || col == 6 || col == 9 || col == 12 || col == 15)) {
                    int groupIndex = (col - 3) / 3;  // 计算组索引
                    String currentName = (String)table.getValueAt(row, col);
                    if ("点击补登".equals(currentName)) {
                        showRegisterDialog(row, groupIndex);
                    }
                }
            }
        });
        
        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(240, 244, 248));
        header.setForeground(new Color(51, 51, 51));
        header.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        
        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // 日期列
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // 时间列
        for (int i = 2; i < 7; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(70); // 数字列
        }
        
        // 居中对齐所有列
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // 组装对话框
        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // 设置对���框大小和位置
        setSize(1200, 700);
        setLocationRelativeTo(owner);
        
        // 添加ESC键关闭功能
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }
    
    private void exportHistory(List<NumberRecord> history) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择保存位置");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("随机数历史记录_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8")) {
                // 写入 BOM
                writer.write('\ufeff');
                
                // 写入表头
                writer.write("日期,时间," +
                    "第一组,姓名,卡号," +
                    "第二组,姓名,卡号," +
                    "第三组,姓名,卡号," +
                    "第四组,姓名,卡号," +
                    "第五组,姓名,卡号\n");
                
                // 写入数据
                for (NumberRecord record : history) {
                    StringBuilder line = new StringBuilder();
                    line.append(record.getDate()).append(",");
                    line.append(record.getTime()).append(",");
                    int[][] numbers = record.getNumbers();
                    for (int i = 0; i < 5; i++) {
                        line.append(numbers[i][0]).append("-").append(numbers[i][1]).append(",");
                        line.append(record.getName(i) != null ? record.getName(i) : "").append(",");
                        line.append(record.getCardId(i) != null ? record.getCardId(i) : "");
                        if (i < 4) line.append(",");
                    }
                    line.append("\n");
                    writer.write(line.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "导出成功！\n文件保存在：" + file.getAbsolutePath(),
                    "导出完成",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "导出失败：" + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRegisterDialog(int row, int groupIndex) {
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
            // 更新数据
            NumberRecord record = historyData.get(row);
            record.addRegistration(groupIndex, nameField.getText(), cardIdField.getText());
            DataManager.updateRecord(record);
            
            // 更新表格显示
            int baseIndex = 2 + (groupIndex * 3);
            table.setValueAt(nameField.getText(), row, baseIndex + 1);
            table.setValueAt(cardIdField.getText(), row, baseIndex + 2);
            
            // 刷新表格
            table.repaint();
            
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
} 