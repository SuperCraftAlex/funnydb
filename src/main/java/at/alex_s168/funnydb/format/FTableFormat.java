package at.alex_s168.funnydb.format;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.FColumnFormat;

import java.util.ArrayList;
import java.util.List;

public class FTableFormat {

    private List<FColumnFormat> columns;

    public FTableFormat() {
        columns = new ArrayList<>();
    }

    public FTableFormat(SimpleBuffer buf) {
        columns = new ArrayList<>();
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            columns.add(new FColumnFormat(buf));
        }
    }

    public void save(SimpleBuffer buf) {
        buf.writeVarInt(columns.size());
        columns.forEach((c)->c.save(buf));
    }

    public FColumnFormat get(int i) {
        return columns.get(i);
    }

    /**
     * Resets the format
     */
    public FTableFormat reset() {
        this.columns = new ArrayList<>();
        return this;
    }

    public FFormatRequiermentStream require() {
        return new FFormatRequiermentStream(this);
    }

    /**
     * Adds a column to the format
     */
    public FTableFormat c(String name, Format format) {
        this.columns.add(new FColumnFormat(name, format));
        return this;
    }

    public int length() {
        return columns.size();
    }

}
