package at.alex_s168.funnydb.format;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.exception.FFormatStreamAccesException;

public record FColumnFormat(String name, int type, FTableFormat tableFormat, int pos, FFormatRequiermentStream requiermentStream) {

    public void save(SimpleBuffer buf) {
        buf.writeString(name);
        buf.writeVarInt(type);
    }

    public FColumnFormat(SimpleBuffer buf, FTableFormat tableFormat) {
        this(buf.readString(32), buf.readVarInt(), tableFormat, tableFormat.get().size(), null);
    }

    public FColumnFormat(String name, Format format, FTableFormat tableFormat) {
        this(name, format.getType(), tableFormat, tableFormat.get().size(), null);
    }

    public FColumnFormat(String name, int type, FTableFormat tableFormat) {
        this(name, type, tableFormat, tableFormat.get().size(), null);
    }

    public FColumnFormat(String name, Format format, int pos, FFormatRequiermentStream s) {
        this(name, format.getType(), null, pos, s);
    }

    public FColumnFormat(String name, int type, int pos, FFormatRequiermentStream s) {
        this(name, type, null, pos, s);
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
     * Stores the format in FTableFormat (do NOT use in FFormatRequirementStream!!!)
     */
    public FTableFormat store() throws FFormatStreamAccesException {
        if(tableFormat == null) {
            throw new FFormatStreamAccesException();
        }
        tableFormat.add(this);
        return tableFormat;
    }

    /**
     * Do not ONLY in FFormatRequirementStream!!!
     */
    public FFormatRequiermentStream next() throws FFormatStreamAccesException {
        if(requiermentStream == null) {
            throw new FFormatStreamAccesException();
        }
        requiermentStream.add(this);
        return requiermentStream;
    }

}
