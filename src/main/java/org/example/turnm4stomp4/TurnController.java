package org.example.turnm4stomp4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class TurnController {
    @FXML
    private Label resultText;

    @FXML
    private Label targetText;

    @FXML
    private Label sourceText;

    @FXML
    private TextField textField;

    private File source;
    private File target;
    private Stage stage;

    public void setStage(Stage stage){
        this.stage=stage;
    }


    @FXML
    protected void onSourceButtonClick() {
        DirectoryChooser directoryChooser =new DirectoryChooser();
        directoryChooser.setTitle("选择源文件夹");
        source= directoryChooser.showDialog(stage);
        sourceText.setText(source.getAbsolutePath());
    }

    @FXML
    protected void onTargetButtonClick() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("选择要生成的目标文件夹");
        target=fileChooser.showOpenDialog(stage);
        targetText.setText(target.getAbsolutePath());
    }

    @FXML
    protected void onTargetFileNameChanged() {

    }

    @FXML
    protected void onStartToTurn() {

    }
}