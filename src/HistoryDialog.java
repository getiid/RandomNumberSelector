package src;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.*;

public class HistoryDialog extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color(240, 244, 248);
    private static final Color BUTTON_COLOR = new Color(24, 144, 255);
    private static final Color BUTTON_HOVER_COLOR = new Color(64, 169, 255);
    private static final Color LIST_ITEM_COLOR = Color.WHITE;
    private static final Color LIST_ITEM_HOVER_COLOR = new Color(230, 247, 255);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);
    private static final Font CONTENT_FONT = new Font("微软雅黑", Font.PLAIN, 14);

    public HistoryDialog(JFrame parent, List<NumberRecord> history) {
        super(parent, "历史记录", true);
        setBackground(BACKGROUND_COLOR);
        
        // 设置窗口大小和位置
        setSize(800, 600);
        setLocationRelativeTo(parent);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("抽取历史记录");
        titleLabel.setFont(TITLE_FONT);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // 创建表格模型
        String[] columnNames = {"序号", "第一组", "第二组", "第三组", "第四组", "第五组", "抽取时间"};
        Object[][] data = new Object[history.size()][7];
        
        for (int i = history.size() - 1; i >= 0; i--) {
            NumberRecord record = history.get(i);
            int rowIndex = history.size() - 1 - i;
            data[rowIndex][0] = rowIndex + 1;
            
            int[][] numbers = record.getNumbers();
            for (int j = 0; j < 5; j++) {
                data[rowIndex][j + 1] = String.format("%d-%d", numbers[j][0], numbers[j][1]);
            }
            data[rowIndex][6] = record.getFormattedTime();
        }

        // 创建表格
        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 使表格不可编辑
            }
        };
        
        // 设置表格样式
        table.setFont(CONTENT_FONT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(233, 233, 233));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.setSelectionBackground(LIST_ITEM_HOVER_COLOR);
        
        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // 序号列
        for (int i = 1; i <= 5; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100);  // 数字组列
        }
        table.getColumnModel().getColumn(6).setPreferredWidth(150);  // 时间列

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // 导出按钮
        JButton exportButton = new JButton("导出记录");
        styleButton(exportButton);
        exportButton.addActionListener(e -> exportHistory(history));

        // 关闭按钮
        JButton closeButton = new JButton("关闭");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);

        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void exportHistory(List<NumberRecord> history) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择保存位置");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("抽取历史记录.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"))) {
                // 写入CSV头部
                writer.println("序号,第一组,第二组,第三组,第四组,第五组,抽取时间");
                
                // 写入数据
                for (int i = 0; i < history.size(); i++) {
                    NumberRecord record = history.get(i);
                    StringBuilder line = new StringBuilder();
                    line.append(i + 1).append(",");
                    
                    int[][] numbers = record.getNumbers();
                    for (int[] pair : numbers) {
                        line.append(pair[0]).append("-").append(pair[1]).append(",");
                    }
                    line.append(record.getFormattedTime());
                    
                    writer.println(line.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "导出成功！\n文件保存在：" + file.getAbsolutePath(),
                    "导出完成",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "导出失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton button) {
        button.setFont(CONTENT_FONT);
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
    }
} 