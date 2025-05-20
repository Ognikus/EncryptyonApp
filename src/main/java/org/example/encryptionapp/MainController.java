package org.example.encryptionapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.encryptionapp.encryption.DESCipher;

import java.util.HashMap;
import java.util.Map;

public class MainController {
    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private ToggleGroup actionGroup;
    @FXML
    private ComboBox<String> encryptionMethodComboBox;
    @FXML
    private StackPane paramsPane;
    @FXML
    private VBox caesarParams;
    @FXML
    private VBox vigenereParams;
    @FXML
    private VBox playfairParams;
    @FXML
    private VBox vernamParams;
    @FXML
    private VBox rsaParams;
    @FXML
    private VBox desParams;
    @FXML
    private TextField caesarStepField;
    @FXML
    private TextField vigenereKeyField;
    @FXML
    private TextField playfairKeyField;
    @FXML
    private TextField vernamKeyField;
    @FXML
    private TextField rsaPublicKeyField;
    @FXML
    private TextField rsaPrivateKeyField;
    @FXML
    private TextField desKeyField;

    private Map<String, VBox> paramsMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Инициализация карты параметров
        paramsMap.put("1. Шифр Цезаря", caesarParams);
        paramsMap.put("2. Шифр Виженера", vigenereParams);
        paramsMap.put("3. Шифр Атбаш", null);
        paramsMap.put("4. Шифр Плейфера", playfairParams);
        paramsMap.put("5. Шифр Вернам", vernamParams);
        paramsMap.put("6. Шифр RSA", rsaParams);
        paramsMap.put("7. Шифр DES", desParams);

        // Обработчик изменения выбранного метода шифрования
        encryptionMethodComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            paramsMap.values().forEach(box -> {
                if (box != null) box.setVisible(false);
            });
            if (newVal != null && paramsMap.containsKey(newVal) && paramsMap.get(newVal) != null) {
                paramsMap.get(newVal).setVisible(true);
            }
        });

        // Выбираем первый метод по умолчанию
        encryptionMethodComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleExecute() {
        String inputText = inputTextArea.getText();
        if (inputText.isEmpty()) {
            showAlert("Ошибка", "Введите текст для обработки");
            return;
        }

        boolean encrypt = ((RadioButton) actionGroup.getSelectedToggle()).getText().equals("Шифровать");
        String method = encryptionMethodComboBox.getValue();

        try {
            String result;
            System.out.println("Метод: " + method + ", Шифровать: " + encrypt);
            switch (method) {
                case "1. Шифр Цезаря":
                    int step = Integer.parseInt(caesarStepField.getText());
                    result = encrypt ? caesarEncrypt(inputText, step) : caesarDecrypt(inputText, step);
                    break;
                case "2. Шифр Виженера":
                    String vigenereKey = vigenereKeyField.getText();
                    if (vigenereKey.isEmpty()) throw new IllegalArgumentException("Введите ключ для шифра Виженера");
                    result = encrypt ? vigenereEncrypt(inputText, vigenereKey) : vigenereDecrypt(inputText, vigenereKey);
                    break;
                case "3. Шифр Атбаш":
                    result = atbashCipher(inputText);
                    break;
                case "4. Шифр Плейфера":
                    String playfairKey = playfairKeyField.getText();
                    if (playfairKey.isEmpty()) throw new IllegalArgumentException("Введите ключ для шифра Плейфера");
                    PlayfairCipher playfairCipher = new PlayfairCipher(playfairKey);
                    result = encrypt ? playfairCipher.encrypt(inputText) : playfairCipher.decrypt(inputText);
                    break;
                case "5. Шифр Вернам":
                    String vernamKey = vernamKeyField.getText();
                    if (vernamKey.isEmpty()) throw new IllegalArgumentException("Введите ключ для шифра Вернама");
                    result = vernamCipher(inputText, vernamKey, encrypt);
                    break;
                case "6. Шифр RSA":
                    String rsaPublicKey = rsaPublicKeyField.getText();
                    String rsaPrivateKey = rsaPrivateKeyField.getText();
                    RSACipher rsaCipher = new RSACipher(rsaPublicKey, rsaPrivateKey);
                    result = encrypt ? rsaCipher.encrypt(inputText) : rsaCipher.decrypt(inputText);
                    break;
                case "7. Шифр DES":
                    String desKey = desKeyField.getText();
                    if (desKey.length() != 8) {
                        throw new IllegalArgumentException("Ключ DES должен быть 8 символов");
                    }
                    try {
                        DESCipher desCipher = new DESCipher(desKey);
                        result = encrypt ? DESCipher.encrypt(inputText)
                                : DESCipher.decrypt(inputText);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка DES шифрования: " + e.getMessage());
                    }
                    break;
                default:
                    result = "Неизвестный метод шифрования";
            }
            System.out.println("Результат: " + result);
            outputTextArea.setText(result);
        } catch (Exception e) {
            showAlert("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleCopyResult() {
        if (!outputTextArea.getText().isEmpty()) {
            ClipboardContent content = new ClipboardContent();
            content.putString(outputTextArea.getText());
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    // Реализации методов шифрования:

    // Реализация шифра Цезаря
    private String caesarEncrypt(String text, int step) {
        return text.chars()
                .map(c -> {
                    if (Character.isLetter(c)) {
                        if (isRussianLetter(c)) {
                            char base = Character.isUpperCase(c) ? 'А' : 'а';
                            return (char) ((c - base + step) % 32 + base);
                        } else if (isEnglishLetter(c)) {
                            char base = Character.isUpperCase(c) ? 'A' : 'a';
                            return (char) ((c - base + step) % 26 + base);
                        }
                    }
                    return c;
                })
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String caesarDecrypt(String text, int step) {
        return text.chars()
                .map(c -> {
                    if (Character.isLetter(c)) {
                        if (isRussianLetter(c)) {
                            char base = Character.isUpperCase(c) ? 'А' : 'а';
                            return (char) ((c - base - step + 32) % 32 + base);
                        } else if (isEnglishLetter(c)) {
                            char base = Character.isUpperCase(c) ? 'A' : 'a';
                            return (char) ((c - base - step + 26) % 26 + base);
                        }
                    }
                    return c;
                })
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private boolean isRussianLetter(int c) {
        return (c >= 'А' && c <= 'Я') || (c >= 'а' && c <= 'я') || c == 'Ё' || c == 'ё';
    }

    private boolean isEnglishLetter(int c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    // Реализация шифра Виженера
    private String vigenereEncrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = key.toLowerCase();
        int russianAlphabetSize = 33;
        int englishAlphabetSize = 26;

        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                int shift;
                if (c >= 'а' && c <= 'я') {
                    shift = key.charAt(j % key.length()) - 'а';
                    result.append((char) ((c - 'а' + shift + russianAlphabetSize) % russianAlphabetSize + 'а'));
                    j++;
                } else if (c >= 'a' && c <= 'z') {
                    shift = key.charAt(j % key.length()) - 'a';
                    result.append((char) ((c - 'a' + shift + englishAlphabetSize) % englishAlphabetSize + 'a'));
                    j++;
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String vigenereDecrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        key = key.toLowerCase();
        int russianAlphabetSize = 33;
        int englishAlphabetSize = 26;

        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetter(c)) {
                int shift;
                if (c >= 'а' && c <= 'я') {
                    shift = key.charAt(j % key.length()) - 'а';
                    result.append((char) ((c - 'а' - shift + russianAlphabetSize) % russianAlphabetSize + 'а'));
                    j++;
                } else if (c >= 'a' && c <= 'z') {
                    shift = key.charAt(j % key.length()) - 'a';
                    result.append((char) ((c - 'a' - shift + englishAlphabetSize) % englishAlphabetSize + 'a'));
                    j++;
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    // Реализация шифра Атбаш
    private String atbashCipher(String text) {
        return text.chars()
                .map(c -> {
                    if (Character.isLetter(c)) {
                        if (c >= 'A' && c <= 'Z') {
                            return 'Z' - (c - 'A');
                        } else if (c >= 'a' && c <= 'z') {
                            return 'z' - (c - 'a');
                        } else if (c >= 'А' && c <= 'Я') {
                            return 'Я' - (c - 'А');
                        } else if (c >= 'а' && c <= 'я') {
                            return 'я' - (c - 'а');
                        }
                    }
                    return c;
                })
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    // Реализация шифра Вернам
    private String vernamCipher(String text, String key, boolean encrypt) {
        if (key.length() < text.length()) {
            throw new IllegalArgumentException("Ключ должен быть не короче текста");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            char k = key.charAt(i % key.length());


            char resultChar = (char) (c ^ k);
            result.append(resultChar);
        }
        return result.toString();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}