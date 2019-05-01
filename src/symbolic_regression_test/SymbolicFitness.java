package symbolic_regression_test;

import MGPFj.chromosome.Node;
import MGPFj.fitness.FitnessFunction;
import MGPFj.holder.Assignments;

import java.util.Map;

public class SymbolicFitness implements FitnessFunction {

    private final Map<Integer, Integer> dataSet;

    public SymbolicFitness(Map<Integer, Integer> dataSet) {
        this.dataSet = dataSet;
    }


    /**
     * @param node the tree being evaluated
     * @return the MGPFj.fitness. This is obtained by checking the data set against the input node.
     *      and averaging all the output-to-actual value difference.
     */
    @Override
    public double evaluate(Node node) {

        double total = 0;

        for (Integer key: dataSet.keySet()) {

            Assignments assignments = Assignments.createAssignments(key);

            Integer res = (Integer) node.evaluate(assignments);

            Integer actualRes = dataSet.get(key);

            Integer diff = Math.abs(actualRes - res);

            total += diff;
        }

//        return total / dataSet.keySet().size();
        return total;
    }

}
