package MGPFj.meta_gp;


import MGPFj.grammar.CFG;
import MGPFj.grammar.NonTerminal;
import MGPFj.grammar.Production;
import MGPFj.terminal.constant.Constant;
import MGPFj.terminal.operation.Operation;
import MGPFj.terminal.variable.Variable;
import MGPFj.utils.Util;
import symbolic_regression_test.Minus;
import symbolic_regression_test.Multiply;
import symbolic_regression_test.Plus;

import java.util.ArrayList;
import java.util.List;

/**
 * MetaGP evolves some of the parameters of GP,
 * ever each iteration of the main Engine. these values are used to run the GP Engine.
 *
 *
 * The steps are:
 *
 *
 */
public class MetaGpLevel1 {
    private final CFG cfg;

    public MetaGpLevel1() {
        this.cfg = createMetaCFG();
    }

    private static CFG createMetaCFG() {

        Operation plus = new Plus();
        Operation minus = new Minus();

        Constant[] c = Util.createIntegerConstants(0, 10);

        Variable[] v = Variable.createVariables("v0", "v1", "v2");

        NonTerminal e = new NonTerminal("E");
        NonTerminal f = new NonTerminal("F");

        Production p1 = new Production(e, e, plus, e);
        Production p2 = new Production(e, e, minus, e);
        Production p3 = new Production(e, e, plus, f);
        Production p4 = new Production(e, f);
        Production[] p5 = Production.createVariableProductions(f, v);
        Production[] p6 = Production.createConstantProductions(f, c);


        CFG cfg = CFG.startBuilding()
                .addNonTerminals(e, f)
                .addTerminals(v)
                .addTerminals(c)
                .addTerminals(minus, plus)
                .addProductions(p1,p2, p3, p4)
                .addProductions(p5)
                .addProductions(p6)
                .setStartSymbol(e)
                .build();

        return cfg;
    }

    public List<Object> evolve(List<Object> args) {
        List<Object> results = new ArrayList<Object>();
        return results;
    }
}
