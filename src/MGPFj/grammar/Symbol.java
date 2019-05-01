package MGPFj.grammar;

import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;


public interface Symbol {

    String getSymbolName();

    Object evaluate(Arguments arguments, Assignments assignments);
}
