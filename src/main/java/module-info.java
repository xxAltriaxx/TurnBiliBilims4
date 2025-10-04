module org.example.turnm4stomp4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.turnm4stomp4 to javafx.fxml;
    exports org.example.turnm4stomp4;
}