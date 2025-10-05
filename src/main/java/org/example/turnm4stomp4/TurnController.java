package org.example.turnm4stomp4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;

public class TurnController {
    @FXML
    private TextField fileType;

    @FXML
    private Label targetText;

    @FXML
    private Label sourceText;

    private File source;
    private File target;
    private Stage stage;

    public void setProperty(Stage stage){
        this.stage=stage;
        this.source=new File(System.getProperty("user.dir"));
        this.target=new File(System.getProperty("user.dir"));
        sourceText.setText(source.getAbsolutePath());
        targetText.setText(target.getAbsolutePath());
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
        DirectoryChooser directoryChooser =new DirectoryChooser();
        directoryChooser.setTitle("选择要生成的目标文件夹");
        target=directoryChooser.showDialog(stage);
        targetText.setText(target.getAbsolutePath());
    }

    @FXML
    protected void onStartToTurn() {
        //获取目标文件夹中所有以ms4结尾的文件
        File[] ms4s=  Arrays.stream(source.listFiles()).filter(a->a.getName().matches(".*\\.m4s")).toArray(File[]::new);
        StringBuilder command=new StringBuilder();
        command.append(".\\ffmpeg.exe");
        for (File file : ms4s) {
            decrypt(file);
            command.append(" -i ");
            command.append(file.getAbsolutePath());
            System.out.println(file.getAbsolutePath());
        }
        command.append(" -codec copy ");
        command.append(target.getAbsolutePath());
        command.append("\\");
        command.append(fileType.getText());

        System.out.println(command);

        try {
            // 使用 ProcessBuilder 可以更好地控制进程
            ProcessBuilder builder = new ProcessBuilder(command.toString().split(" "));
            // 关键设置：将错误流合并到标准输出流，只需读取一个流即可
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 单个线程处理合并后的所有输出
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[FFmpeg]: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputThread.start();

            int exitCode = process.waitFor();
            System.out.println("FFmpeg 进程执行完毕，退出码: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (File ms4 : ms4s) encrypt(ms4);
        }
    }

    private void decrypt(File file){
        String filePath = file.getAbsolutePath(); // 替换为你的文件路径

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            // 检查文件长度是否足够
            if (raf.length() <= 9) {
                System.out.println("文件长度不足9字节，无法删除前9位。");
                return;
            }

            // 1. 定位到第10个字节（索引9）
            raf.seek(9);

            // 2. 读取从第10个字节开始的所有数据
            byte[] remainingData = new byte[(int)raf.length() - 9];
            raf.read(remainingData);

            // 3. 将文件截断至新长度，并回到文件开头
            raf.setLength(raf.length() - 9);
            raf.seek(0);

            // 4. 将剩余数据写回文件
            raf.write(remainingData);

            System.out.println("成功删除文件前9个字节。");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encrypt(File file){
        String filePath = file.getAbsolutePath();

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            // 1. 读取文件的全部原始数据
            byte[] originalData = new byte[(int) raf.length()];
            raf.seek(0);
            raf.read(originalData);

            // 2. 在文件开头腾出9个字节的空间
            // 先将文件长度扩展9个字节
            raf.setLength(raf.length() + 9);
            // 将文件指针移动到新开辟空间的末尾（即第9个字节之后）
            raf.seek(9);
            // 写入原始数据，这样原始数据就从第10个字节开始了
            raf.write(originalData);

            // 3. 回到文件开头，写入9个ASCII 48的字节（即字符'0'）
            raf.seek(0);
            for (int i = 0; i < 9; i++) {
                raf.write(48); // 48是'0'的ASCII码
            }

            System.out.println("成功在文件开头补回9个'0'字符。");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}