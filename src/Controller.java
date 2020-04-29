import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class Controller {
    @FXML
    private AnchorPane root;
    @FXML
    private DatePicker picker;
    @FXML
    private TextField TSH_IV, FT4_IV, FT3_IV, TT3_IV, RT3_IV, TSH_CV, FT4_CV, FT3_CV, TT3_CV, RT3_CV;
    @FXML
    private MenuButton TSH_PU, FT4_PU, FT3_PU, TT3_PU, RT3_PU, graphChooser;

    @FXML
    private LineChart<String, Number> chart;

    private Main main;
    private DataPoint currentPoint;
    private XYChart.Series currentSeries;
    private int currentSeriesIndex;

    public void initialize() {
        main = Main.getInstance();
        picker.setValue(LocalDate.now());
        currentPoint = main.getDataPoint(picker.getValue());
        if (currentPoint == null) {
            currentPoint = new DataPoint(picker.getValue());
            main.addDataPoint(currentPoint);
        }

        checkButtons(TSH_PU);
        checkButtons(FT4_PU);
        checkButtons(FT3_PU);
        checkButtons(TT3_PU);
        checkButtons(RT3_PU);
        checkButtons(graphChooser);

        EventHandler<KeyEvent> handler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                double[] inputs = {Double.parseDouble(TSH_IV.getText()), Double.parseDouble(FT4_IV.getText()), Double.parseDouble(FT3_IV.getText()), Double.parseDouble(TT3_IV.getText()), Double.parseDouble(RT3_IV.getText())};
                UOM[] selectedUnits = {textToUOM(TSH_PU.getText()), textToUOM(FT4_PU.getText()), textToUOM(FT3_PU.getText()), textToUOM(TT3_PU.getText()), textToUOM(RT3_PU.getText())};
                updateConvertedValues(inputs, selectedUnits);
            }
        };

        picker.setOnAction(a -> updateVisualDataPoint());

        if (root.getScene() != null)
            root.getScene().addEventHandler(KeyEvent.KEY_PRESSED, handler);
        else {
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null)
                    root.getScene().addEventHandler(KeyEvent.KEY_PRESSED, handler);
            });
        }

        updateVisualDataPoint();
    }

    private void updateVisualDataPoint() {
        currentPoint = main.getDataPoint(picker.getValue());

        if (currentPoint == null) {
            currentPoint = new DataPoint(picker.getValue());
            main.addDataPoint(currentPoint);
        }

        updateButtons(TSH_PU, 0);
        updateButtons(FT4_PU, 1);
        updateButtons(FT3_PU, 2);
        updateButtons(TT3_PU, 3);
        updateButtons(RT3_PU, 4);
        updateButtons(graphChooser, -1);

        double[] inputs = new double[5];
        UOM[] selectedUnits = new UOM[5];

        for (int i = 0; i < 5; i++) {
            selectedUnits[i] = currentPoint.getData().get(i).getSelectedUnit();
            inputs[i] = currentPoint.getData().get(i).getInitialValue();
        }

        TSH_IV.setText(String.valueOf(inputs[0]));
        TSH_PU.setText(String.valueOf(selectedUnits[0]));
        FT4_IV.setText(String.valueOf(inputs[1]));
        FT4_PU.setText(String.valueOf(selectedUnits[1]));
        FT3_IV.setText(String.valueOf(inputs[2]));
        FT3_PU.setText(String.valueOf(selectedUnits[2]));
        TT3_IV.setText(String.valueOf(inputs[3]));
        TT3_PU.setText(String.valueOf(selectedUnits[3]));
        RT3_IV.setText(String.valueOf(inputs[4]));
        RT3_PU.setText(String.valueOf(selectedUnits[4]));

        updateConvertedValues(inputs, selectedUnits);
    }

    private void updateConvertedValues(double[] inputs, UOM[] selectedUnits) {
        double[] convertedValues = new double[5];

        for (int i = 0; i < inputs.length; i++) {
            currentPoint.getData().get(i).setSelectedUnit(selectedUnits[i]);
            currentPoint.getData().get(i).setInitialValue(inputs[i]);
            convertedValues[i] = currentPoint.getData().get(i).convertInitialValue(inputs[i]);
            //System.out.println("data: " + currentPoint.getData().get(i).getSelectedUnit() + currentPoint.getData().get(i).getInitialValue());
        }

        TSH_CV.setText(String.valueOf(convertedValues[0]));
        FT4_CV.setText(String.valueOf(convertedValues[1]));
        FT3_CV.setText(String.valueOf(convertedValues[2]));
        TT3_CV.setText(String.valueOf(convertedValues[3]));
        RT3_CV.setText(String.valueOf(convertedValues[4]));

        updateChart();
    }

    private void updateChart() {
        chart.getData().clear();

        for (DataPoint point : main.getData())
            currentSeries.getData().add(new XYChart.Data(point.getPrettyDate(), point.getData().get(currentSeriesIndex).getConverted()));

        chart.getData().add(currentSeries);
    }

    private void checkButtons(MenuButton dropdown) {
        for (MenuItem option : dropdown.getItems()) {
            option.setOnAction(event -> {
                dropdown.setText(option.getText());
                option.setStyle("-fx-text-fill: #339642");
                for (MenuItem soption : dropdown.getItems()) {
                    if (soption != option)
                        soption.setStyle("-fx-text-fill: black");
                }
                if (dropdown.getId().equals("graphChooser")) {
                    currentSeries = new XYChart.Series();
                    currentSeries.setName(option.getText());
                    switch (option.getText()) {
                        case "TSH":
                            currentSeriesIndex = 0;
                            break;
                        case "FT4":
                            currentSeriesIndex = 1;
                            break;
                        case "FT3":
                            currentSeriesIndex = 2;
                            break;
                        case "TT3":
                            currentSeriesIndex = 3;
                            break;
                        case "RT3":
                            currentSeriesIndex = 4;
                            break;
                    }
                    //updateVisualDataPoint();
                }
            });
        }
    }

    private void updateButtons(MenuButton dropdown, int index) {
        if (dropdown.getId().equals("graphChooser")) {
            currentSeries = new XYChart.Series();
            currentSeries.setName("TSH");
            currentSeriesIndex = 0;
        } else {
            for (MenuItem option : dropdown.getItems()) {
                if (option.getText().equals(UOMtoText(currentPoint.getData().get(index).getSelectedUnit()))) {
                    option.setStyle("-fx-text-fill: #339642");
                    dropdown.setText(option.getText());
                } else {
                    option.setStyle("-fx-text-fill: black");
                }
            }
        }
    }

    private UOM textToUOM(String text) {
        UOM fromText = UOM.MUG_L;

        switch (text) {
            case "μg/L":
                fromText = UOM.MUG_L;
                break;
            case "pg/mL":
                fromText = UOM.PG_ML;
                break;
            case "pg/dL":
                fromText = UOM.PG_DL;
                break;
            case "pg/100mL":
                fromText = UOM.PG_100ML;
                break;
            case "pg%":
                fromText = UOM.PGPCT;
                break;
            case "ng/mL":
                fromText = UOM.NG_ML;
                break;
            case "ng/dL":
                fromText = UOM.NG_DL;
                break;
            case "ng/100mL":
                fromText = UOM.NG_100ML;
                break;
            case "ng/L":
                fromText = UOM.NG_L;
                break;
            case "ng%":
                fromText = UOM.NGPCT;
                break;
            case "μIU/mL":
                fromText = UOM.MUIU_ML;
                break;
            case "mIU/L":
                fromText = UOM.MIU_L;
                break;
            case "IU/L":
                fromText = UOM.IU_L;
                break;
            case "pmol/L":
                fromText = UOM.PMOL_L;
                break;
        }

        return fromText;
    }

    private String UOMtoText(UOM uom) {
        String fromUOM = "μg/L";

        switch (uom) {
            case MUG_L:
                fromUOM = "μg/L";
                break;
            case PG_ML:
                fromUOM = "pg/mL";
                break;
            case PG_DL:
                fromUOM = "pg/dL";
                break;
            case PG_100ML:
                fromUOM = "pg/100mL";
                break;
            case PGPCT:
                fromUOM = "pg%";
                break;
            case NG_ML:
                fromUOM = "ng/mL";
                break;
            case NG_DL:
                fromUOM = "ng/dL";
                break;
            case NG_100ML:
                fromUOM = "ng/100mL";
                break;
            case NG_L:
                fromUOM = "ng/L";
                break;
            case NGPCT:
                fromUOM = "ng%";
                break;
            case MUIU_ML:
                fromUOM = "μIU/mL";
                break;
            case MIU_L:
                fromUOM = "mIU/L";
                break;
            case IU_L:
                fromUOM = "IU/L";
                break;
            case PMOL_L:
                fromUOM = "pmol/L";
                break;
        }

        return fromUOM;
    }
}