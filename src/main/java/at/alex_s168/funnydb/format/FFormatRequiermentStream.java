package at.alex_s168.funnydb.format;

import at.alex_s168.funnydb.FColumnFormat;
import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.exception.FFormatException;

import java.util.ArrayList;
import java.util.List;

public class FFormatRequiermentStream {

    private List<FColumnFormat> columns;
    private final FTableFormat fTableFormat;

    public FFormatRequiermentStream(FTableFormat fTableFormat) {
        this.fTableFormat = fTableFormat;
        this.columns = new ArrayList<>();
    }

    /**
     * Returns true if the format is correct
     */
    public boolean check() {
        int i = 0;
        for(FColumnFormat c : columns) {
            if(c.type()!=fTableFormat.get(i).type() || !c.name().equals(fTableFormat.get(i).name())) {
                return false;
            }
            i++;
        }
        return true;
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
     * Adds a column to the format
     */
    public FFormatRequiermentStream c(String name, Format format) {
        this.columns.add(new FColumnFormat(name, format, this.columns.size()));
        return this;
    }

}

