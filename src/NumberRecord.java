package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NumberRecord {
    private int[][] numbers;
    private String timestamp;
    private LocalDateTime dateTime;

    public NumberRecord(int[][] numbers) {
        this.numbers = numbers;
        this.dateTime = LocalDateTime.now();
        this.timestamp = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int[][] getNumbers() {
        return numbers;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getTime() {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append("\n");
        for (int[] pair : numbers) {
            sb.append(pair[0]).append(" - ").append(pair[1]).append("\n");
        }
        return sb.toString();
    }
} 