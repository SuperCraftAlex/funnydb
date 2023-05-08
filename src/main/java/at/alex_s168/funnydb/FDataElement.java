package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.exception.FFormatException;

public class FDataElement {

    private int cat;
    private Object value;

    public int getType() {
        return type;
    }

    private final int type;
    private final FDataRow row;

    public FDataElement(int cat, FDataRow row) {
        this.type=-1;
        this.cat = cat;
        this.value = null;
        this.row = row;
    }

    public FDataElement(FDataRow row) {
        this.type=-1;
        this.cat = -1;
        this.value = null;
        this.row = row;
    }

    public FDataElement(int cat, Object value, FDataRow row) {
        this.cat = cat;
        this.value = value;
        this.row = row;
        if(value instanceof Integer) {type = 0;}
        else if(value instanceof Long) {type = 1;}
        else if(value instanceof byte[] && value.getClass().isArray()) {type = 3;}
        else if(value instanceof String) {
            if(value.getClass().isArray()) {
                type = 4;
            } else {
                type = 2;
            }
        } else {
            throw new RuntimeException(new FFormatException());
        }
    }

    public FDataElement(SimpleBuffer buf, FDataRow row, int pos) {
        type = row.table().format().get(pos).type();
        cat = pos;
        this.row = row;
        try {
            switch (type) {
                case 0 -> value = buf.readVarInt();
                case 1 -> value = buf.readVarLong();
                case 2 -> value = buf.readString(99999);
                case 3 -> value = buf.readByteArray();
                case 4 -> value = buf.readStringArray(buf.readableBytes());
                default -> throw new RuntimeException(new FFormatException());
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        row.remove(cat);
    }

    public FDataRow row() {
        return row;
    }

    public int getEID() {
        return row.getData().indexOf(this);
    }

    public Object get() {
        return this.value;
    }

    public FDataElement set(Object value) {
        if(!isType(value)) {
            throw new RuntimeException("INVALID TYPE!");
        }
        this.value = value;
        return this;
    }

    public boolean isType(Object o) {
        return o.getClass() == value.getClass() || type == -1;
    }

    public boolean exists() {
        return cat !=-1&&value!=null;
    }

    public int getCategory() {
        return cat;
    }

    public String getCategoryName() {
        return row.table().format().get(cat).name();
    }

    public void save(SimpleBuffer buf) {
        try {
            switch (type) {
                case 0 -> buf.writeVarInt((Integer) value);
                case 1 -> buf.writeVarLong((Long) value);
                case 2 -> buf.writeString((String) value);
                case 3 -> buf.writeByteArray((byte[]) value);
                case 4 -> buf.writeStringArray((String[]) value);
                default -> throw new RuntimeException(new FFormatException());
            }
        } catch (Exception e){
            throw new RuntimeException(new FFormatException());
        }
    }

}
