package MGPFj.chromosome;

import MGPFj.fitness.FitnessFunction;

import java.util.Arrays;
import java.util.List;

public class RankedCandidate implements Comparable {

    private final Node node;
    private final double fitness;

    private RankedCandidate(Node node, double fitness) {
        this.node = node;
        this.fitness = fitness;
    }

    public Node getNode() {
        return node;
    }

    public double getFitness() {
        return fitness;
    }


    public static RankedCandidate[] rankAndSort (List<Node> nodes, FitnessFunction fitnessFunction) {

        RankedCandidate[] rankedCandidates = new RankedCandidate[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            double fitness = fitnessFunction.evaluate(node);

            rankedCandidates[i] = new RankedCandidate(node, fitness);
        }

        Arrays.sort(rankedCandidates);

        return rankedCandidates;
    }


    @Override
    public String toString() {
        return this.fitness + ": " +this.node.getTreeNotation();
    }

    @Override
    public int compareTo(Object o) {
        int result = Double.compare(this.fitness, ((RankedCandidate) o).fitness);

        if (result == 0) {
            return Double.compare(this.getNode().getNodeCount().doubleValue(), ((RankedCandidate) o).getNode().getNodeCount().doubleValue());
        }

        return result;
    }
}
