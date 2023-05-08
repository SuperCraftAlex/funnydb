package at.alex_s168.funnydb.search;

import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.FDataTable;
import at.alex_s168.funnydb.util.FDataElementList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FSearchStream {

    private final FDataTable table;
    private final List<FSearchRequierment> req;

    public FSearchStream(FDataTable table) {
        this.table = table;
        this.req = new ArrayList<>();
    }

    // TODO: ACH DU HEILIGE SCHEIßE! WAS GEHT HIER AB??? WARUM 1000000 "new FSearchRequirement", also dont search all columns!!!

    /**
     * Adds a requirement (condition)
     */
    public FSearchStream r(FSearchRequirementCondition cond) {
        req.add(new FSearchRequierment(this.table, cond));
        return this;
    }

    /**
     * Adds a requirement (column = column & value condition)
     */
    public FSearchStream r(String comlumn, FSearchRequirementCondition cond) {
        req.add(new FSearchRequierment(this.table, (e)->e.getName().equals(comlumn)&&cond.applies(e)));
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    public FSearchStream r(String comlumn, Object value) {
        req.add(new FSearchRequierment(this.table, (e)->e.getName().equals(comlumn)&&e.get().equals(value)));
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    public FSearchStream r(String comlumn) {
        req.add(new FSearchRequierment(this.table, (e)->
            e.getName().equals(comlumn)
        ));
        return this;
    }

    /**
     * Adds a requirement (row = row)
     */
    public FSearchStream r(int row) {
        req.add(new FSearchRequierment(this.table, (e)->e.row().getRID()==row));
        return this;
    }

    public FDataElementList find() {
        FDataElementList o = new FDataElementList();
        o.addAll(req.get(0).get());
        for(FSearchRequierment r : req) {
            o.retainAll(r.get());
        }
        return o;
    }

}
