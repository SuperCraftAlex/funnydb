package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
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
            data.add(new FDataElement(buf, this));
        }
    }

    public FDataElement get(String index) {
        Optional<FDataElement> e = data.stream().filter((d)-> d.getName().equals(index)).findFirst();
        return e.orElseGet(() -> new FDataElement(index, this));
    }

    public FDataElement getByValue(Object value) {
        Optional<FDataElement> e = data.stream().filter((d)->d.get().equals(value)).findFirst();
        return e.orElseGet(() -> new FDataElement(this));
    }

    /**
     * Sets a value / creates it
     */
    public FDataRow set(String name, Object value) {
        Optional<FDataElement> o = data.stream().filter((d)->d.getName().equals(name)).findFirst();
        o.ifPresentOrElse(e -> e.set(value), ()->data.add(new FDataElement(name, value,this)));
        return this;
    }

    /**
     * resets all values and sets them to this:
     */
    public FDataRow set(Object... values) {
        data.clear();
        int i = 0;
        for(Object v : values) {
            data.add(new FDataElement(this.table.format().get(i).name(), v, this));
            i++;
        }
        return this;
    }

    public int getRID() {
        return table.getRows().indexOf(this);
    }

    public void remove() {
        table.remove(this);
    }

    public void remove(String name) {
        data.removeIf((e)->e.getName().equals(name));
    }

    public void save(SimpleBuffer buf) {
        buf.writeVarInt(data.size());
        data.forEach((d) -> d.save(buf));
    }

}
