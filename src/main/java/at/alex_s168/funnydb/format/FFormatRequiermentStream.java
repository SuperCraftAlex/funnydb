package at.alex_s168.funnydb.format;

import at.alex_s168.funnydb.FColumnFormat;
import at.alex_s168.funnydb.exception.FFormatException;
import at.alex_s168.funnydb.exception.FFormatStreamTableException;

import java.util.ArrayList;
import java.util.List;

public class FFormatRequiermentStream {

    private final List<FColumnFormat> columns;
    private final FTableFormat fTableFormat;

    public FFormatRequiermentStream(FTableFormat fTableFormat) {
        this.fTableFormat = fTableFormat;
        this.columns = new ArrayList<>();
    }

    /**
     * Returns true if the format is correct
     */
    public boolean check() {
        try {
            int i = 0;
            for (FColumnFormat c : columns) {
                if (c.type() != fTableFormat.get(i).type() || !c.name().equals(fTableFormat.get(i).name())) {
                    return false;
                }
                i++;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the format if not already set
     */
    public void enforce() {
        if(!check()) {
            FTableFormat f = fTableFormat.table().format().reset();
            f.reset();
            columns.forEach((e)->{
                try {
                    f.column(e.name(), e.type()).store();
                } catch (FFormatStreamTableException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    /**
     * Throws a FFormatException when the invalid format
     */
    public void require() throws FFormatException {
        if(!check()) {
            throw new FFormatException();
        }
    }

    /**
     * Adds a column to the format requirement
     */
    public FFormatRequiermentStream column(String name, Format format) {
        this.columns.add(new FColumnFormat(name, format, columns.size()));
        return this;
    }

    /**
     * Adds a column to the format requirement
     */
    public FFormatRequiermentStream column(String name, int format) {
        this.columns.add(new FColumnFormat(name, format, columns.size()));
        return this;
    }
}

