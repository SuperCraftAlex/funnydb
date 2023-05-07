package at.alex_s168.funnydb.util;

import at.alex_s168.funnydb.FDataElement;

import java.util.ArrayList;
import java.util.List;

public class FDataElementList extends ArrayList<FDataElement> {

    public FDataElement first() {
        return this.get(0);
    }

    public FDataElement last() {
        return this.get(this.size()-1);
    }

    public boolean isPresent() {
        return this.size() > 0;
    }

    public void extend(List<FDataElement> l) {
        this.addAll(l);
    }

    @Override
    public FDataElement get(int index) {
        try {
            return super.get(index);
        } catch (IndexOutOfBoundsException ex) {
            return new FDataElement(null);
        }
    }

}
