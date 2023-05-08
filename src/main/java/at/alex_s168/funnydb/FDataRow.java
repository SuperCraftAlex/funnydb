package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.exception.FFormatException;
import at.alex_s168.funnydb.util.FDataElementList;

import java.util.Optional;

public class FDataRow {

    public FDataElementList getData() {
        return data;
    }

    private final FDataElementList data;
    private final FDataTable table;

    public FDataRow(FDataElementList data, FDataTable table) {
        this.data = data;
        this.table = table;
    }

    public FDataRow(SimpleBuffer buf, FDataTable table) {
        this.data = new FDataElementList();
        this.table = table;
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            data.add(new FDataElement(buf, this, i));
        }
    }

    public FDataElement get(String index) {
        Optional<FDataElement> e = data.stream().filter((d)-> d.getCategoryName().equals(index)).findFirst();
        return e.orElseGet(() -> new FDataElement(table.format().get(index).pos(), this));
    }

    public FDataElement get(int index) {
        Optional<FDataElement> e = data.stream().filter((d)-> d.getCategory()==index).findFirst();
        return e.orElseGet(() -> new FDataElement(index, this));
    }

    public FDataElement getByValue(Object value) {
        Optional<FDataElement> e = data.stream().filter((d)->d.get().equals(value)).findFirst();
        return e.orElseGet(() -> new FDataElement(this));
    }

    public FDataTable table() {
        return this.table;
    }

    /**
     * Sets a value / creates it
     */
    @Deprecated
    public FDataRow set(String cat, Object value) {
        Optional<FDataElement> o = data.stream().filter((d)->d.getCategoryName().equals(cat)).findFirst();
        o.ifPresentOrElse(e -> e.set(value), ()->data.add(new FDataElement(table.format().get(cat).pos(), value, this)));
        return this;
    }

    /**
     * Sets a value / creates it
     */
    public FDataRow set(int cat, Object value) {
        Optional<FDataElement> o = data.stream().filter((d)->d.getCategory()==cat).findFirst();
        o.ifPresentOrElse(e -> e.set(value), ()->data.add(new FDataElement(cat, value,this)));
        return this;
    }

    /**
     * resets all values and sets them to this:
     */
    public FDataRow set(Object... values) throws FFormatException {
        data.clear();
        if(values.length != table.format().length()) {
            throw new FFormatException();
        }
        int i = 0;
        for(Object v : values) {
            data.add(new FDataElement(i, v, this));
            i++;
        }
        return this;
    }

    public int getRID() {
        // todo: no indexOf
        return table.getRows().indexOf(this);
    }

    public void remove() {
        table.remove(this);
    }

    public void remove(int pos) {
        data.remove(pos);
    }

    public void save(SimpleBuffer buf) {
        buf.writeVarInt(data.size());
        data.forEach((d) -> d.save(buf));
    }

}
