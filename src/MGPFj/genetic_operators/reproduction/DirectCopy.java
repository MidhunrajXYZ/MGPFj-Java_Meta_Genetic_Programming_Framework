package MGPFj.genetic_operators.reproduction;

import MGPFj.chromosome.Node;
import MGPFj.genetic_operators.GeneticOperator;
import MGPFj.utils.Util;

import java.util.List;

/**
 * This genetic operator just copies the first node and returns
 * But the operation is known as reproduction.
 */
public class DirectCopy implements GeneticOperator {


    @Override
    public List<Node> evolve(List<Node> nodes) {
        Node resultNode = nodes.get(0).copyTree(null);
        return Util.createList(resultNode);
    }
}
