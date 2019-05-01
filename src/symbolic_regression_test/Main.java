package symbolic_regression_test;

import MGPFj.chromosome.RankedCandidate;
import MGPFj.engine.DefaultLazyEngine;
import MGPFj.fitness.FitnessFunction;
import MGPFj.grammar.*;
import MGPFj.terminal.constant.Constant;
import MGPFj.terminal.operation.Operation;
import MGPFj.terminal.variable.Variable;
import MGPFj.utils.Util;

import java.util.*;

public class Main{


    public static void main(String[] args) {

        //CREATE CFG
        Operation plus = new Plus();
        Operation minus = new Minus();
        Operation multiply = new Multiply();

        Constant[] c = Util.createIntegerConstants(0, 10);

        Variable[] v = Variable.createVariables("v0");

        NonTerminal e = new NonTerminal("E");
        NonTerminal f = new NonTerminal("F");

        Production e_p_e = new Production(e, e, plus, e);
        Production e_m_e = new Production(e, e, minus, e);
        Production e_x_e = new Production(e, e, multiply, e);
        Production e_p_f = new Production(e, e, plus, f);
        Production e_f = new Production(e, f);
        Production[] f_v = Production.createVariableProductions(f, v);
        Production[] f_c = Production.createConstantProductions(f, c);


        CFG cfg = CFG.startBuilding()
                .addNonTerminals(e, f)
                .addTerminals(v)
                .addTerminals(c)
                .addTerminals(minus, multiply, plus)
                .addProductions(e_m_e, e_p_e, e_x_e, e_f, e_p_f)
                .addProductions(f_c)
                .addProductions(f_v)
                .setStartSymbol(e)
                .build();

        //FITNESS FUNCTION
        FitnessFunction fitnessFunction = new SymbolicFitness(createDataSet());

        //CALL THE ENGINE
        DefaultLazyEngine de = DefaultLazyEngine.start(cfg, fitnessFunction, 5, 500, 100)
                .setDefaultProbabilities(5, 90, 3)
                .setTerminateOnMaxFitness(false)
                .finish();

        RankedCandidate[] candidates = de.run(true);

        System.out.println(candidates[0].getFitness() + ": " +candidates[0].getNode().getTerminalNotation());

    }

    private static Map<Integer, Integer> createDataSet() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (int i = -10; i < 12; i++) {

            // x*x + 2x + 1
            int result = i*i + 2*i + 1;

            map.put(i, result);
        }

        return map;
    }


}
