package org.example.encryptionapp;

public class PlayfairCipher {

    private char[][] matrix;

    public PlayfairCipher(String key) {
        this.matrix = createPlayfairMatrix(key);
    }

    private char[][] createPlayfairMatrix(String key) {
        StringBuilder uniqueChars = new StringBuilder();
        boolean[] used = new boolean[26]; // Для отслеживания использованных букв

        // Добавляем уникальные буквы из ключа
        for (char c : key.toCharArray()) {
            if (Character.isLetter(c)) {
                c = Character.toLowerCase(c);
                if (!used[c - 'a']) {
                    used[c - 'a'] = true;
                    uniqueChars.append(c);
                }
            }
        }

        // Добавляем оставшиеся буквы
        for (char c = 'a'; c <= 'z'; c++) {
            if (!used[c - 'a'] && c != 'j') { // 'j' обычно объединяется с 'i'
                uniqueChars.append(c);
            }
        }

        // Создаем квадрат 5x5
        char[][] matrix = new char[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = uniqueChars.charAt(i * 5 + j);
            }
        }
        return matrix;
    }

    private String prepareText(String text) {
        StringBuilder preparedText = new StringBuilder();
        text = text.replaceAll("[^a-zA-Z]", "").toLowerCase(); // Удаляем все, кроме букв

        for (int i = 0; i < text.length(); i++) {
            preparedText.append(text.charAt(i));
            if (i + 1 < text.length() && text.charAt(i) == text.charAt(i + 1)) {
                preparedText.append('x'); // Добавляем 'x' между одинаковыми буквами
            }
        }

        // Если длина текста нечетная, добавляем 'x' в конец
        if (preparedText.length() % 2 != 0) {
            preparedText.append('x');
        }

        return preparedText.toString();
    }

    private int[] findPosition(char c) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == c) {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("Character not found in matrix: " + c);
    }

    public String encrypt(String text) {
        StringBuilder result = new StringBuilder();
        text = prepareText(text);

        for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = (i + 1 < text.length()) ? text.charAt(i + 1) : 'X'; // Если нечетное количество, добавляем 'X'

            if (first == second) {
                second = 'X'; // Если одинаковые, заменяем вторую на 'X'
                i--; // Возвращаемся на один шаг назад
            }

            int[] pos1 = findPosition(first);
            int[] pos2 = findPosition(second);

            if (pos1[0] == pos2[0]) { // Одна строка
                result.append(matrix[pos1[0]][(pos1[1] + 1) % 5]);
                result.append(matrix[pos2[0]][(pos2[1] + 1) % 5]);
            } else if (pos1[1] == pos2[1]) { // Один столбец
                result.append(matrix[(pos1[0] + 1) % 5][pos1[1]]);
                result.append(matrix[(pos2[0] + 1) % 5][pos2[1]]);
            } else { // Прямоугольник
                result.append(matrix[pos1[0]][pos2[1]]);
                result.append(matrix[pos2[0]][pos1[1]]);
            }
        }
        return result.toString();
    }

    public String decrypt(String text) {
        StringBuilder result = new StringBuilder();
        text = prepareText(text);

        for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = (i + 1 < text.length()) ? text.charAt(i + 1) : 'X'; // Если нечетное количество, добавляем 'X'

            int[] pos1 = findPosition(first);
            int[] pos2 = findPosition(second);

            if (pos1[0] == pos2[0]) { // Одна строка
                result.append(matrix[pos1[0]][(pos1[1] + 4) % 5]); // Сдвиг влево
                result.append(matrix[pos2[0]][(pos2[1] + 4) % 5]);
            } else if (pos1[1] == pos2[1]) { // Один столбец
                result.append(matrix[(pos1[0] + 4) % 5][pos1[1]]); // Сдвиг вверх
                result.append(matrix[(pos2[0] + 4) % 5][pos2[1]]);
            } else { // Прямоугольник
                result.append(matrix[pos1[0]][pos2[1]]);
                result.append(matrix[pos2[0]][pos1[1]]);
            }
        }
        return result.toString();
    }
}

