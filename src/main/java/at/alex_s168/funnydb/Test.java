package at.alex_s168.funnydb;

import at.alex_s168.funnydb.format.Format;

public class Test {

    public static void main(String[] args) throws Exception {

        FDataBase db = new FDataBase("E:/githubcontributeprojects/funnydb/run/test.fdb");
        db.createIfEmpty();
        db.load();

        FDataTable users = db.get("users");
        users.format().reset().c("username", Format.STRING).c("password",Format.STRING).c("created",Format.LONG);

        users.append().set("alex_s168", "AAAFFBB", 544482269331L);

        System.out.println(users.search().r("username","alex_s168").find().first().row().get("password").get());

        db.save();

    }

}
