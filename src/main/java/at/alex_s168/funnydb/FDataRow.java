package at.alex_s168.funnydb;

import at.alex_s168.reverse.api.universal.network.RPacketBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FDataRow {

    public List<FDataElement> getData() {
        return data;
    }

    private final List<FDataElement> data;
    private final FDataBase db;

    public FDataRow(List<FDataElement> data, FDataBase db) {
        this.data = data;
        this.db = db;
    }

    public FDataRow(RPacketBuffer buf, FDataBase db) {
        this.data = new ArrayList<>();
        this.db = db;
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            data.add(new FDataElement(buf, this));
        }
    }

    public FDataElement getByIndex(String index) {
        Optional<FDataElement> e = data.stream().filter((d)-> d.getName().equals(index)).findFirst();
        return e.orElseGet(() -> new FDataElement(index, this));
    }

    public FDataElement getByValue(Object value) {
        Optional<FDataElement> e = data.stream().filter((d)->d.get().equals(value)).findFirst();
        return e.orElseGet(() -> new FDataElement(this));
    }

    public FDataRow add(String name, Object value) {
        data.add(new FDataElement(name, value,this));
        return this;
    }

    public int getRID() {
        return db.getRows().indexOf(this);
    }

    public void remove() {
        db.remove(this);
    }

    public void remove(String name) {
        data.removeIf((e)->e.getName().equals(name));
    }

    public void save(RPacketBuffer buf) {
        buf.writeVarInt(data.size());
        data.forEach((d) -> d.save(buf));
    }

}
