package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;

public class FDataElement {

    private String name;
    private Object value;

    public int getType() {
        return type;
    }

    private final int type;
    private final FDataRow row;

    public FDataElement(String name, FDataRow row) {
        this.type=-1;
        this.name = name;
        this.value = null;
        this.row = row;
    }

    public FDataElement(FDataRow row) {
        this.type=-1;
        this.name = null;
        this.value = null;
        this.row = row;
    }

    public FDataElement(String name, Object value, FDataRow row) {
        this.name = name;
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
            throw new RuntimeException("DataElement: No support for: "+value.getClass()+"!");
        }
    }

    public FDataElement(SimpleBuffer buf, FDataRow row) {
        type = buf.readVarInt();
        name = buf.readString(500);
        this.row = row;
        try {
            switch (type) {
                case 0 -> value = buf.readVarInt();
                case 1 -> value = buf.readVarLong();
                case 2 -> value = buf.readString(99999);
                case 3 -> value = buf.readByteArray();
                case 4 -> value = buf.readStringArray(buf.readableBytes());
                default -> throw new RuntimeException("DataElement: No support for ? with <= "+buf.readableBytes()+" bytes!");
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void setIndex(String index) {
        this.name = index;
    }

    public void delete() {
        row.remove(name);
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
        return name!=null&&value!=null;
    }

    public String getName() {
        return name;
    }

    public void save(SimpleBuffer buf) {
        buf.writeVarInt(type);
        buf.writeString(name);
        try {
            switch (type) {
                case 0 -> buf.writeVarInt((Integer) value);
                case 1 -> buf.writeVarLong((Long) value);
                case 2 -> buf.writeString((String) value);
                case 3 -> buf.writeByteArray((byte[]) value);
                case 4 -> buf.writeStringArray((String[]) value);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
