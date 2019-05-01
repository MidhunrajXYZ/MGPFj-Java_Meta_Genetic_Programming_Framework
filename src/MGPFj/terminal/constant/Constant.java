package MGPFj.terminal.constant;

import MGPFj.grammar.Terminal;
import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;


public class Constant implements Terminal {

    private final String symbolName;
    private final Object value;

    public Constant(String symbolName, Object value) {
        this.symbolName = symbolName;
        this.value = value;
    }

    @Override
    public Object evaluate(Arguments arguments, Assignments assignments) {
        return this.value;
    }

    @Override
    public String getSymbolName() {
        return this.symbolName;
    }


    @Override
    public String toString() {
        return getSymbolName();
    }
}
