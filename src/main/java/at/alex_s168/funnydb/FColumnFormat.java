package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.format.Format;

public record FColumnFormat(String name, int type, int pos) {

    public void save(SimpleBuffer buf) {
        buf.writeString(name);
        buf.writeVarInt(type);
    }

    public FColumnFormat(SimpleBuffer buf, int pos) {
        this(buf.readString(32), buf.readVarInt(), pos);
    }

    public FColumnFormat(String name, Format format, int pos) {
        this(name, format.getType(), pos);
    }

    /**
     * Returns the format. Format is read-only!
     */
    public Format format() {
        for(Format f : Format.values()) {
            if(f.getType() == type) {
                return f;
            }
        }
        return null;
    }

}
