package at.alex_s168.funnydb;

import at.alex_s168.buffer.SimpleBuffer;
import at.alex_s168.funnydb.encryption.BypassEncryptionHandler;
import at.alex_s168.funnydb.encryption.EncryptionHandler;
import io.netty.buffer.Unpooled;

import java.io.File;
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
     * Gets a Table in the database by name. If it does not exist, it creates a new one.
     */
    public FDataTable get(String name) {
        for (FDataTable t : tables) {
            if(t.info().name().equals(name)) {
                return t;
            }
        }
        FDataTable t = new FDataTable(this, name);
        tables.add(t);
        return t;
    }

    public FDataTable add(String name) {
        tables.add(new FDataTable(this, name));
        return tables.get(tables.size()-1);
    }

    public FDataBase remove(FDataTable table) {
        tables.remove(table);
        return this;
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

    public FDataBase load() throws Exception {
        tables = new ArrayList<>();
        byte[] bytes = encrypter.decrypt(Files.readAllBytes(new File(path).toPath()));
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

    public void save() throws Exception {
        SimpleBuffer buf = new SimpleBuffer(Unpooled.buffer(estimateSize()));
        buf.writeVarInt(version);
        buf.writeVarInt(tables.size());
        tables.forEach((r)->{
            r.save(buf);
        });
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        Files.write(new File(path).toPath(), encrypter.encrypt(bytes));
    }

    public int estimateSize() {
        AtomicInteger size = new AtomicInteger(2);
        tables.forEach((t)->size.addAndGet(t.estimateSize()));
        return size.get();
    }

    public Info info() {
        return new Info(tables.size(), version);
    }

    record Info(int tables, int version) {}

}
