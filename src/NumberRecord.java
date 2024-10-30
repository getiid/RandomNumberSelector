package src;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NumberRecord {
    private LocalDateTime timestamp;
    private int[][] numbers;  // 5组数字，每组2个
    
    public NumberRecord(int[][] numbers) {
        this.timestamp = LocalDateTime.now();
        this.numbers = numbers;
    }
    
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    public int[][] getNumbers() {
        return numbers;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFormattedTime()).append("\n");
        for (int i = 0; i < numbers.length; i++) {
            sb.append(String.format("第%d组: %d·%d\n", i + 1, numbers[i][0], numbers[i][1]));
        }
        return sb.toString();
    }
} 