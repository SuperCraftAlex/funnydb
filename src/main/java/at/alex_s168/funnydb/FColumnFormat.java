package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.exception.FFormatStreamTableException;
import at.alex_s168.funnydb.format.FFormatRequiermentStream;
import at.alex_s168.funnydb.format.FTableFormat;
import at.alex_s168.funnydb.format.Format;

public record FColumnFormat(String name, int type, FTableFormat tableFormat, int pos) {

    public void save(SimpleBuffer buf) {
        buf.writeString(name);
        buf.writeVarInt(type);
    }

    public FColumnFormat(SimpleBuffer buf, FTableFormat tableFormat) {
        this(buf.readString(32), buf.readVarInt(), tableFormat, tableFormat.get().size());
    }

    public FColumnFormat(String name, Format format, FTableFormat tableFormat) {
        this(name, format.getType(), tableFormat, tableFormat.get().size());
    }

    public FColumnFormat(String name, int type, FTableFormat tableFormat) {
        this(name, type, tableFormat, tableFormat.get().size());
    }

    public FColumnFormat(String name, Format format, int pos) {
        this(name, format.getType(), null, pos);
    }

    public FColumnFormat(String name, int type, int pos) {
        this(name, type, null, pos);
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

    /**
     * Stores the format in FTableFormat (do not use in FFormatRequirementStream!!!)
     */
    public FTableFormat store() throws FFormatStreamTableException {
        if(tableFormat == null) {
            throw new FFormatStreamTableException();
        }
        tableFormat.add(this);
        return tableFormat;
    }

}
