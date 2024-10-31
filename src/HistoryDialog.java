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
    public HistoryDialog(Frame owner, List<NumberRecord> history) {
        super(owner, "历史记录", true);
        setUndecorated(true);
        
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
        
        // 导出按��
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
        String[] columnNames = {"日期", "时间", "第一组", "第二组", "第三组", "第四组", "第五组"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // 添加数据到表格
        for (NumberRecord record : history) {
            Object[] row = new Object[7];
            row[0] = record.getDate();
            row[1] = record.getTime();
            int[][] numbers = record.getNumbers();
            for (int i = 0; i < 5; i++) {
                row[i + 2] = numbers[i][0] + " - " + numbers[i][1];
            }
            model.addRow(row);
        }
        
        // 创建表格
        JTable table = new JTable(model);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(230, 244, 255));
        table.setSelectionForeground(Color.BLACK);
        
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
        
        // 设置对话框大小和位置
        setSize(800, 600);
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择保存位置");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("随机数历史记录_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // 写入表头
                writer.println("日期,时间,第一组,第二组,第三组,第四组,第五组");
                
                // 写入数据
                for (NumberRecord record : history) {
                    StringBuilder line = new StringBuilder();
                    line.append(record.getDate()).append(",");
                    line.append(record.getTime()).append(",");
                    int[][] numbers = record.getNumbers();
                    for (int i = 0; i < 5; i++) {
                        line.append(numbers[i][0]).append("-").append(numbers[i][1]);
                        if (i < 4) line.append(",");
                    }
                    writer.println(line);
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
} 