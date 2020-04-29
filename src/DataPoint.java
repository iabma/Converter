import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataPoint implements Comparable<DataPoint> {
    private LocalDate date;
    private List<Row> data;
    private UOM[][] allPotentialUnits = {
            {UOM.MUIU_ML, UOM.MIU_L}, // TSH
            {UOM.NG_DL, UOM.NG_100ML, UOM.NGPCT, UOM.NG_ML, UOM.NG_L, UOM.PG_ML}, // FT4
            {UOM.PG_ML, UOM.PG_DL, UOM.PG_100ML, UOM.PGPCT, UOM.NG_DL, UOM.NG_L}, // FT3
            {UOM.NG_ML, UOM.NG_DL, UOM.NG_100ML, UOM.NGPCT, UOM.NG_L, UOM.MUG_L}, // TT3—NMOL_L?
            {UOM.NG_DL}}; // RT3

    public DataPoint(LocalDate date) {
        this.date = date;
        data = new ArrayList<>();
        data.add(new Row(allPotentialUnits[0], 0));
        data.add(new Row(allPotentialUnits[1], 1));
        data.add(new Row(allPotentialUnits[2], 2));
        data.add(new Row(allPotentialUnits[3], 3));
        data.add(new Row(allPotentialUnits[4], 4));
    }

    public DataPoint(String compressedData) {
        String[] segments = compressedData.split("!");
        date = LocalDate.parse(segments[0]);

        data = new ArrayList<>();
        for (int i = 1; i < segments.length; i++) {
            data.add(new Row(segments[i]));
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPrettyDate() {
        return date.format(DateTimeFormatter.ofPattern("MM/dd"));
    }

    public List<Row> getData() {
        return data;
    }

    @Override
    public int compareTo(DataPoint toCompare) {
        return date.isBefore(toCompare.date) ? -1 : date.isAfter(toCompare.date) ? 1 : 0;
    }

    @Override
    public String toString() {
        return String.format("%s!%s!%s!%s!%s!%s", date, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4));
    }
}
