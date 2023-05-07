package at.alex_s168.funnydb.search;

import at.alex_s168.funnydb.FDataElement;
import at.alex_s168.funnydb.FDataRow;

public interface FSearchRequirementCondition {

    boolean applies(FDataElement e);

}
