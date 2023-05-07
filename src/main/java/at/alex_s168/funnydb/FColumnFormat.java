package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.format.Format;

public record FColumnFormat(String name, int type) {

    public void save(SimpleBuffer buf) {
        buf.writeString(name);
        buf.writeVarInt(type);
    }

    public FColumnFormat(SimpleBuffer buf) {
        this(buf.readString(32), buf.readVarInt());
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

    public FColumnFormat(String name, Format format) {
        this(name, format.getType());
    }

}
