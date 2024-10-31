package src;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataManager {
    private static final String DATA_FILE = "data/history.csv";
    private static List<NumberRecord> history = new ArrayList<>();
    
    static {
        // 确保数据目录存在
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // 加载历史数据
        loadHistory();
    }
    
    public static void loadHistory() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // 跳过表头
                }
                String[] parts = line.split(",");
                if (parts.length >= 17) { // 2 + (3 * 5) = 17列
                    int[][] numbers = new int[5][2];
                    for (int i = 0; i < 5; i++) {
                        String[] numParts = parts[i * 3 + 2].split("-");
                        numbers[i][0] = Integer.parseInt(numParts[0].trim());
                        numbers[i][1] = Integer.parseInt(numParts[1].trim());
                    }
                    NumberRecord record = new NumberRecord(numbers);
                    for (int i = 0; i < 5; i++) {
                        record.addRegistration(i, parts[i * 3 + 3], parts[i * 3 + 4]);
                    }
                    history.add(record);
                }
            }
        } catch (IOException e) {
            System.out.println("加载历史记录失败: " + e.getMessage());
        }
    }
    
    public static void saveHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            // 写入表头
            writer.println("日期,时间," +
                "第一组,姓名,卡号," +
                "第二组,姓名,卡号," +
                "第三组,姓名,卡号," +
                "第四组,姓名,卡号," +
                "第五组,姓名,卡号");
            
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
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("保存历史记录失败: " + e.getMessage());
        }
    }
    
    public static List<NumberRecord> getHistory() {
        return history;
    }
    
    public static void addRecord(NumberRecord record) {
        history.add(record);
        saveHistory(); // 每次添加记录后自动保存
    }
    
    public static void updateRecord(NumberRecord record) {
        saveHistory(); // 每次更新记录后自动保存
    }
} 