package at.alex_s168.funnydb.search;

import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.FDataTable;

public interface FSearchRequirement {

    boolean appliesElement(FDataElement r);

    boolean appliesColumn(int ci, FDataTable t);

    boolean filtersColumn();

}
