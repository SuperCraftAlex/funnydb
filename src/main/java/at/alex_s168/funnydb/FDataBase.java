package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.encryption.BypassEncryptionHandler;
import at.alex_s168.funnydb.encryption.EncryptionHandler;
import at.alex_s168.funnydb.exception.FDataBaseSaveException;
import at.alex_s168.funnydb.exception.FEncryptionException;
import at.alex_s168.funnydb.exception.FInvalidDataBaseException;
import at.alex_s168.funnydb.exception.FInvalidDataTableException;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FDataBase {

    private final String path;

    private List<FDataTable> tables;
    private int version;
    private EncryptionHandler encrypter;
    private String[] errors = new String[]{""};

    public FDataBase(String path) {
        this.path = path;
        tables = new ArrayList<>();
        encrypter = new BypassEncryptionHandler();
    }

    public List<FDataTable> getTables() {
        return tables;
    }

    public String getPath() {
        return path;
    }

    public FDataBase setHandler(EncryptionHandler handler) {
        this.encrypter = handler;
        return this;
    }

    /**
     * Adds a error to the error list (only use for small problems and warnings)
     */
    public void error(FDataTable table, String message) {
        errors[errors.length-1] = "In: "+table+" "+message;
    }

    /**
     * Returns a formatted error list with the last x elements
     */
    public String errorList(int amount) {
        amount = Math.min(amount, errors.length);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            b.append(errors[errors.length - amount + i]).append("\n");
        }
        return b.toString();
    }

    public void resetErrors() {
        this.errors = new String[]{""};
    }

    /**
     * Gets a Table in the database by name
     */
    public FDataTable get(String name) throws FInvalidDataTableException {
        for (FDataTable t : tables) {
            if(t.info().name().equals(name)) {
                return t;
            }
        }
        throw new FInvalidDataTableException(name);
    }

    /**
     * Adds a Table into the database with a name
     */
    public FDataTable add(String name) {
        tables.add(new FDataTable(this, name));
        return tables.get(tables.size()-1);
    }

    /**
     * Adds a Table into the database with a name
     */
    public FDataTable addOrGet(String name) {
        try {
            return get(name);
        } catch (FInvalidDataTableException e) {
            return add(name);
        }
    }

    public void remove(FDataTable table) {
        tables.remove(table);
    }

    public FDataBase createIfEmpty() throws Exception {
        Path p = new File(path).toPath();
        tables = new ArrayList<>();
        if(Files.exists(p)) {
            if(Files.readAllBytes(p).length == 0) {
                version = FDBDEF.VERSION;
                save();
            }
        } else {
            Files.createFile(p);
            version = FDBDEF.VERSION;
            save();
        }
        return this;
    }

    public FDataBase load() throws FInvalidDataBaseException, FEncryptionException {
        tables = new ArrayList<>();
        byte[] bytes;
        try {
            bytes = encrypter.decrypt(Files.readAllBytes(new File(path).toPath()));
        } catch (IOException e) {
            throw new FInvalidDataBaseException(path);
        }
        SimpleBuffer buf = new SimpleBuffer(Unpooled.wrappedBuffer(bytes));
        version = buf.readVarInt();
        int a = buf.readVarInt();
        for (int i = 0; i < a; i++) {
            tables.add(new FDataTable(buf, this));
        }
        System.out.println("Loaded db!");
        buf.clear();
        return this;
    }

    public void save() throws FEncryptionException, FDataBaseSaveException {
        SimpleBuffer buf = new SimpleBuffer(Unpooled.buffer(estimateSize()));
        buf.writeVarInt(version);
        buf.writeVarInt(tables.size());
        tables.forEach((r)->{
            r.save(buf);
        });
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        try {
            Files.write(new File(path).toPath(), encrypter.encrypt(bytes));
        } catch (IOException e) {
            throw new FDataBaseSaveException(e);
        }
    }

    public int estimateSize() {
        AtomicInteger size = new AtomicInteger(2);
        tables.forEach((t)->size.addAndGet(t.estimateSize()));
        return size.get();
    }

    public Info info() {
        return new Info(tables.size(), version);
    }

    public record Info(int tables, int version) {}

}
