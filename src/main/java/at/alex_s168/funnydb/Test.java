package at.alex_s168.funnydb;

public class Test {

    public static void main(String[] args) {
        try {

            FDataBase db = new FDataBase("E:/githubcontributeprojects/funnydb/run/test.fdb");
            db.createIfEmpty();
            db.load();
            
            db.save();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
