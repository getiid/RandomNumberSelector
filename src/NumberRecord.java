package src;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NumberRecord {
    private int[][] numbers;
    private String timestamp;
    private LocalDateTime dateTime;
    private String[] names;
    private String[] cardIds;

    public NumberRecord(int[][] numbers) {
        this.numbers = numbers;
        this.dateTime = LocalDateTime.now();
        this.timestamp = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.names = new String[5];
        this.cardIds = new String[5];
    }

    public void addRegistration(int groupIndex, String name, String cardId) {
        if (groupIndex >= 0 && groupIndex < 5) {
            names[groupIndex] = name;
            cardIds[groupIndex] = cardId;
        }
    }

    public String getName(int groupIndex) {
        return names[groupIndex];
    }

    public String getCardId(int groupIndex) {
        return cardIds[groupIndex];
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
        for (int i = 0; i < numbers.length; i++) {
            sb.append(numbers[i][0]).append(" - ").append(numbers[i][1]);
            if (names[i] != null && !names[i].isEmpty()) {
                sb.append(" (").append(names[i]).append(", ").append(cardIds[i]).append(")");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
} 