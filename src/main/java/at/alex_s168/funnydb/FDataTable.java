package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.format.FTableFormat;
import at.alex_s168.funnydb.search.FSearchStream;
import at.alex_s168.funnydb.util.FDataElementList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FDataTable {

    private List<FDataRow> rows;
    private final FDataBase db;
    private final String name;
    private final FTableFormat format;

    public List<FDataRow> getRows() {
        return rows;
    }

    public FDataTable(FDataBase db, String name) {
        this.db = db;
        this.name = name;
        this.format = new FTableFormat();
        this.rows = new ArrayList<>();
    }

    public FDataTable(SimpleBuffer buf, FDataBase db) {
        this.db = db;
        name = buf.readString(32);
        this.format = new FTableFormat(buf);
        this.rows = new ArrayList<>();
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            rows.add(new FDataRow(buf, this, i));
        }
    }

    public void save(SimpleBuffer buf) {
        buf.writeString(name);
        this.format.save(buf);
        buf.writeVarInt(rows.size());
        rows.forEach((r)->{
            r.save(buf);
        });
    }

    public int estimateSize() {
        AtomicInteger size = new AtomicInteger(1+name.length()*2);
        for(FDataRow r : rows) {
            size.addAndGet(2);
            r.getData().forEach((e)->{
                size.addAndGet(2 * e.getCategoryName().length());
                size.addAndGet(FDBDEF.ELEMENT_VALUE_MAX);
            });
        }
        return size.get();
    }

    public FDataRow row(int index) {
        return rows.get(index);
    }

    /**
     * Returns the Table format
     */
    public FTableFormat format() {
        return this.format;
    }

    /**
     * Opens a search stream
     */
    public FSearchStream search() {
        return new FSearchStream(this);
    }

    /**
     * Removes the row at position "index"
     */
    public FDataTable remove(int index) {
        rows.remove(index);
        return this;
    }

    void remove(FDataRow row) {
        rows.remove(row);
    }

    /**
     * Removes this table
     */
    void remove() {
        db.remove(this);
    }

    /**
     * Adds a row "row" at the end
     */
    public FDataRow append(FDataRow row) {
        rows.add(row);
        return rows.get(rows.size()-1);
    }

    /**
     * Adds a new row at the end
     */
    public FDataRow append() {
        int rs = rows.size();
        rows.add(new FDataRow(new FDataElementList(), this, rs));
        return rows.get(rs);
    }

    public Info info() {
        return new Info(rows.size(), name);
    }

    record Info(int rows, String name) {}

}
