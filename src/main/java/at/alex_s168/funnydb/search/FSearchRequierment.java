package at.alex_s168.funnydb.search;

import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.FDataTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FSearchRequierment {

    private final FDataTable table;
    private final FSearchRequirementCondition cond;

    public FSearchRequierment(FDataTable table, FSearchRequirementCondition c) {
        this.table = table;
        this.cond = c;
    }

    public List<FDataElement> get() {
        AtomicReference<List<FDataElement>> ret = new AtomicReference<>(new ArrayList<>());
        table.getRows().forEach((r)-> r.getData().forEach((d)->{
            if(cond.applies(d)) {
                ret.get().add(d);
            }
        }));
        return ret.get();
    }

}
