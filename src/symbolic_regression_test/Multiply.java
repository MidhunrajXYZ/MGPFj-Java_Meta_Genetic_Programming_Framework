package symbolic_regression_test;

import MGPFj.chromosome.Node;
import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;
import MGPFj.terminal.operation.Operation;

public class Multiply implements Operation {

    @Override
    public String getSymbolName() {
        return "*";
    }

    @Override
    public Object evaluate(Arguments arguments, Assignments assignments) {

        Object number1;
        Object number2;

        number1 = arguments.getArg(0).evaluate(assignments);
        number2 = arguments.getArg(1).evaluate(assignments);

        if (!(number1 instanceof Integer) || !(number2 instanceof Integer)) {
            throw new RuntimeException("Evaluation failed");
        }

        return (Integer)number1 * (Integer) number2;
    }

    @Override
    public String toString() {
        return getSymbolName();
    }

    @Override
    public Node simplify(Arguments arguments) {
        return null;
    }
}
