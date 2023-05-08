package at.alex_s168.funnydb.search;

import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.FDataTable;
import at.alex_s168.funnydb.util.FDataElementList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FSearchStream {

    private final FDataTable table;
    private final List<FSearchRequirement> req;

    public FSearchStream(FDataTable table) {
        this.table = table;
        this.req = new ArrayList<>();
    }

    /**
     * Adds a requirement (condition)
     */
    public FSearchStream r(FSearchRequirement cond) {
        req.add(cond);
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    @Deprecated
    public FSearchStream r(String comlumn, Object value) {
        req.add(new FSearchRequirement() {
            @Override
            public boolean appliesElement(FDataElement r) {
                return r.get().equals(value);
            }

            @Override
            public boolean appliesColumn(int ci, FDataTable t) {
                return t.format().get(comlumn).pos()==ci;
            }

            @Override
            public boolean filtersColumn() {
                return true;
            }
        });
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    public FSearchStream r(int comlumn, Object value) {
        req.add(new FSearchRequirement() {
            @Override
            public boolean appliesElement(FDataElement r) {
                return r.get().equals(value);
            }

            @Override
            public boolean appliesColumn(int ci, FDataTable t) {
                return comlumn==ci;
            }

            @Override
            public boolean filtersColumn() {
                return true;
            }
        });
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    @Deprecated
    public FSearchStream rColumn(String comlumn) {
        req.add(new FSearchRequirement() {
            @Override
            public boolean appliesElement(FDataElement r) {
                return true;
            }

            @Override
            public boolean appliesColumn(int ci, FDataTable t) {
                return t.format().get(comlumn).pos()==ci;
            }

            @Override
            public boolean filtersColumn() {
                return true;
            }
        });
        return this;
    }

    /**
     * Adds a requirement (column = column & value = value)
     */
    public FSearchStream rColumn(int comlumn) {
        req.add(new FSearchRequirement() {
            @Override
            public boolean appliesElement(FDataElement r) {
                return true;
            }

            @Override
            public boolean appliesColumn(int ci, FDataTable t) {
                return comlumn==ci;
            }

            @Override
            public boolean filtersColumn() {
                return true;
            }
        });
        return this;
    }

    /**
     * Adds a requirement (row = row)
     */
    public FSearchStream rRow(int row) {
        req.add(new FSearchRequirement() {
            @Override
            public boolean appliesElement(FDataElement r) {
                return r.row().getID() == row;
            }

            @Override
            public boolean appliesColumn(int ci, FDataTable t) {
                return true;
            }

            @Override
            public boolean filtersColumn() {
                return false;
            }
        });
        return this;
    }

    @Deprecated
    public FDataElementList findOld() {
        final FDataElementList o = new FDataElementList();
        final int cl = table.format().length();
        List<Integer> columns = new ArrayList<>();
        for (int i = 0; i < cl; i++) {
            for(FSearchRequirement r : req.stream().filter(FSearchRequirement::filtersColumn).toList()) {
                if (!r.appliesColumn(i, table)) {
                    break;
                }
                columns.add(i);
            }
        }
        req.forEach((e)-> table.getRows().forEach((r)->{
            columns.forEach((c)->{
                if(e.appliesElement(r.get(c))) {
                    o.add(r.get(c));
                }
            });
        }));
        return o;
    }

    public FDataElementList find() {
        final int cl = table.format().length();
        List<Integer> columns = IntStream.range(0, cl)
                .filter(i -> req.stream().anyMatch(r -> r.appliesColumn(i, table)))
                .boxed()
                .toList();

        return req.parallelStream()
                .flatMap(e -> table.getRows().parallelStream()
                        .flatMap(r -> columns.stream()
                                .filter(c -> e.appliesElement(r.get(c)))
                                .map(r::get)))
                .collect(Collectors.toCollection(FDataElementList::new));
    }


}
