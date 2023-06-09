package at.alex_s168.funnydb;

import at.alex_s168.funnydb.encryption.AESEncryptionHandler;
import at.alex_s168.funnydb.format.Format;

import javax.crypto.spec.SecretKeySpec;

public class Test {

    // random test key
    private static final byte[] key = new byte[]{ -68, -43, -47, -114, -80, -28, 40, -23, -47, -102, -99, -125, 119, 90, -75, 72 };

    public static void main(String[] args) throws Exception {

        FDataBase db = new FDataBase("E:/githubcontributeprojects/funnydb/run/test.fdb")
                .setHandler(new AESEncryptionHandler(new SecretKeySpec(key, "AES")))
                .createIfEmpty()
                .load();

        FDataTable users = db.addOrGet("users");
        users.format().require()
                .column("username", Format.STRING).next()
                .column("password", Format.STRING).next()
                .column("created", Format.LONG).next()
                .enforce();

        users.append().setAll("alex_s168", "AAAFFBB", 544482269331L);

        System.out.println(users.search().r("username","alex_s168").find().first().row().get("password").get());

        db.save();

    }

}
