package at.alex_s168.funnydb.format;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.FDataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FTableFormat {

    private List<FColumnFormat> columns;
    private final FDataTable table;

    public FTableFormat(FDataTable table) {
        this.table = table;
        columns = new ArrayList<>();
    }

    public FTableFormat(SimpleBuffer buf, FDataTable table) {
        this.table = table;
        columns = new ArrayList<>();
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            columns.add(new FColumnFormat(buf, this));
        }
    }

    public FDataTable table() {
        return this.table;
    }

    public void save(SimpleBuffer buf) {
        buf.writeVarInt(columns.size());
        columns.forEach((c)->c.save(buf));
    }

    public List<FColumnFormat> get() {
        return columns;
    }

    public FColumnFormat get(int i) {
        return columns.get(i);
    }

    public FColumnFormat get(String i) {
        return columns.stream().filter((e)-> Objects.equals(e.name(), i)).findFirst().get();
    }

    public int pos(FColumnFormat f) {
        return columns.indexOf(f);
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
    public FColumnFormat column(String name, Format format) {
        return new FColumnFormat(name, format, this);
    }

    /**
     * Adds a column to the format
     */
    public FColumnFormat column(String name, int format) {
        return new FColumnFormat(name, format, this);
    }

    public void add(FColumnFormat f) {
        this.columns.add(f);
    }

    public int length() {
        return columns.size();
    }

}
