public class Row {
    private UOM selectedUnit;
    private double initialValue;
    private double converted;
    private int index;

    public Row(UOM[] potentialUnits, int index) {
        selectedUnit = potentialUnits[0];
        initialValue = 0;
        converted = 0;
        this.index = index;
    }

    public Row(String compressedData) {
        String[] segments = compressedData.split(":");
        selectedUnit = UOM.valueOf(segments[0]);
        initialValue = Double.parseDouble(segments[1]);
        converted = Double.parseDouble(segments[2]);
        index = Integer.parseInt(segments[3]);
    }

    public void setSelectedUnit(UOM selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public void setInitialValue(double initialValue) {
        this.initialValue = initialValue;
    }

    public double convertInitialValue(double initialValue) {
        converted = Converter.convert(initialValue, selectedUnit, index);
        return converted;
    }

    public UOM getSelectedUnit() {
        return selectedUnit;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public double getConverted() {
        return converted;
    }

    @Override
    public String toString() {
        return String.format("%s:%f:%f:%d", selectedUnit, initialValue, converted, index);
    }
}
