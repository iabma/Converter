import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {
    private static Main instance;
    private List<DataPoint> data;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // INITIALIZATION

        instance = this;
        data = readData();

        // ACTION
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setTitle("Converter");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

        ShutdownTask shutDownTask = new ShutdownTask();
        Runtime.getRuntime().addShutdownHook(shutDownTask);
    }

    public static Main getInstance() {
        return instance;
    }

    public void addDataPoint(DataPoint point) {
        data.add(point);
        Collections.sort(data);
    }

    public DataPoint getDataPoint(LocalDate date) {
        DataPoint toReturn = null;
        for (DataPoint point : data) {
            if (point.getDate().equals(date))
                toReturn = point;
        }
        return toReturn;
    }

    public List<DataPoint> getData() {
        return data;
    }

    private List<DataPoint> readData() throws FileNotFoundException, URISyntaxException {
        File dataFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI().getPath().substring(0, Main.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI().getPath().length() - 42) + "/data.txt");
        Scanner scanner = new Scanner(dataFile);
        List<DataPoint> data = new ArrayList<>();
        while (scanner.hasNextLine())
            data.add(new DataPoint(scanner.nextLine()));
        return data;
    }

    private static class ShutdownTask extends Thread {
        @Override
        public void run() {
            try {
                StringBuilder iContent = new StringBuilder();
                List<DataPoint> sortedData = Main.getInstance().data;
                Collections.sort(sortedData);
                for (DataPoint point : sortedData)
                    iContent.append(point + "\n");
                String content = iContent.substring(0, iContent.length() - 1);
                System.out.println(content);
                FileWriter fileWriter = new FileWriter(Main.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI().getPath().substring(0, Main.class.getProtectionDomain().getCodeSource().getLocation()
                                .toURI().getPath().length() - 42) + "/data.txt");
                fileWriter.write(content);
                fileWriter.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}