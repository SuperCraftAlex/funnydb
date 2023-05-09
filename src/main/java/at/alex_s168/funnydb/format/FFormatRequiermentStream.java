package at.alex_s168.funnydb.format;

import at.alex_s168.funnydb.exception.FFormatException;

import java.util.ArrayList;
import java.util.List;

public class FFormatRequiermentStream {

    private final List<FColumnFormat> columns;

    public FTableFormat getTableFormat() {
        return fTableFormat;
    }

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
            columns.forEach(f::add);
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
     * opens a column for the format requirement
     */
    public FColumnFormat column(String name, Format format) {
        return new FColumnFormat(name, format, columns.size(), this);
    }

    /**
     * opens a column for the format requirement
     */
    public FColumnFormat column(String name, int format) {
        return new FColumnFormat(name, format, columns.size(), this);
    }

    public void add(FColumnFormat f) {
        this.columns.add(f);
    }

}

