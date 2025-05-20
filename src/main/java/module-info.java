module org.example.encryptionapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.encryptionapp to javafx.fxml;
    exports org.example.encryptionapp;
}