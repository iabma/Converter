public abstract class Converter {
    public static double convert(double value, UOM original, int index) {
        double converted = value;

        switch (index) {
            case 0: // TSH
                converted = value * 1000;
                break;
            case 1: // FT4
                if (original == UOM.NG_ML)
                    converted = value * 1287.2001;
                else if (original == UOM.NG_DL || original == UOM.NG_100ML || original == UOM.NGPCT)
                    converted = value * 12.872;
                else if (original == UOM.NG_L || original == UOM.PG_ML)
                    converted = value * 1.2872;
                break;
            case 2: // FT3
                if (original == UOM.NG_DL)
                    converted = value * 15.361;
                else if (original == UOM.NG_L || original == UOM.PG_ML)
                    converted = value * 1.5361;
                else if (original == UOM.PG_DL || original == UOM.PG_100ML || original == UOM.PGPCT)
                    converted = value * .015361;
                break;
            case 3: // TT3
                if (original == UOM.NG_ML || original == UOM.MUG_L)
                    converted = value * 100;
                else if (original == UOM.NG_DL || original == UOM.NG_100ML || original == UOM.NGPCT)
                    converted = value;
                else if (original == UOM.NG_L)
                    converted = value * .1;
                break;
            case 4: // RT3
                converted = value;
                break;
        }

        return converted;
    }
}
