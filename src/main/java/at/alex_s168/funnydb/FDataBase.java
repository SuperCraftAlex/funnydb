package at.alex_s168.funnydb;

import at.alex_s168.reverse.api.universal.network.RPacketBuffer;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FDataBase {

    private final String path;

    public List<FDataRow> getRows() {
        return rows;
    }

    private List<FDataRow> rows;

    public FDataBase(String path) {
        this.path = path;
    }

    public void createIfEmpty() throws Exception {
        Path p = new File(path).toPath();
        rows = new ArrayList<>();
        if(Files.exists(p)) {
            if(Files.readAllBytes(p).length == 0) {
                save();
            }
        } else {
            Files.createFile(p);
            save();
        }
    }

    public void load() throws Exception {
        rows = new ArrayList<>();
        RPacketBuffer buff = new RPacketBuffer(Unpooled.wrappedBuffer(Files.readAllBytes(new File(path).toPath())));
        int a = buff.readVarInt();
        for (int i = 0; i < a; i++) {
            rows.add(new FDataRow(buff, this));
        }
        System.out.println("Loaded db!");
        buff.clear();
    }

    public void save() throws Exception {
        RPacketBuffer buf = new RPacketBuffer(Unpooled.buffer(estimateSize()));
        buf.writeVarInt(rows.size());
        rows.forEach((r)->{
            r.save(buf);
        });
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        Files.write(new File(path).toPath(), bytes);
    }

    private Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        Arrays.setAll(bytes, n -> bytesPrim[n]);
        return bytes;
    }

    public int estimateSize() {
        AtomicInteger size = new AtomicInteger(1);
        for(FDataRow r : rows) {
            size.addAndGet(2);
            r.getData().forEach((e)->{
                size.addAndGet(2 * e.getName().length());
                size.addAndGet(FDBDEF.ELEMENT_VALUE_MAX);
            });
        }
        return size.get();
    }

    public FDataRow row(int index) {
        return rows.get(index);
    }

    public void remove(int index) {
        rows.remove(index);
    }

    public void remove(FDataRow row) {
        rows.remove(row);
    }

    /**
     * Adds a row "row" at position "pos"
     */
    public FDataRow insert(FDataRow row, int pos) {
        rows.add(pos, row);
        return rows.get(rows.size()-1);
    }

    /**
     * Adds a new row at position "pos"
     */
    public FDataRow insert(int pos) {
        rows.add(pos, new FDataRow(new ArrayList<>(), this));
        return rows.get(rows.size()-1);
    }

    /**
     * Adds a row "row" at the end
     */
    public FDataRow append(FDataRow row) {
        rows.add(row);
        return rows.get(rows.size()-1);
    }

    /**
     * Adds a new row at the end
     */
    public FDataRow append() {
        rows.add(new FDataRow(new ArrayList<>(), this));
        return rows.get(rows.size()-1);
    }

    public Info info() {
        return new Info(rows.size());
    }

    static class Info {
        private final int rows;

        public Info(int rows) {

            this.rows = rows;
        }

        public int rows() {
            return rows;
        }
    }

}
