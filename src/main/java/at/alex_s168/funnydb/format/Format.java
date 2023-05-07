package at.alex_s168.funnydb.format;

public enum Format {
    INT(0),
    LONG(1),
    STRING(2),
    BYTE_ARRAY(3),
    STRING_ARRAY(4);

    private final int type;

    Format(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
