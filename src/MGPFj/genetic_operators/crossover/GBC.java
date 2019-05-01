package MGPFj.genetic_operators.crossover;


import MGPFj.chromosome.Node;
import MGPFj.genetic_operators.GeneticOperator;
import MGPFj.grammar.*;

import java.util.*;

/**
 * GBC - Grammar Based Crossover Operator
 */
public class GBC implements GeneticOperator {

    private final CFG cfg;
    private final int maxDepth;

    public GBC(CFG cfg, int maxDepth) {
        this.cfg = cfg;
        this.maxDepth = maxDepth;
    }


    @Override
    public List<Node> evolve(List<Node> nodes) {

        List<Node> result = new ArrayList<Node>();

        Node p1 = nodes.get(0).copyTree(null);
        Node p2 = nodes.get(1).copyTree(null);

        /*STEP 1: start the NT set of first parent, except the root nodes
        NT set contains all the non MGPFj.terminal nodes*/
        List<Node> ntSet = generateNTSet(p1, cfg.getNonTerminals());
        ntSet.remove(p1);

        //STEP 2: MGPFj.select one node at random
        while (ntSet.size() > 0) {

            Collections.shuffle(ntSet);
            Node CN1 = ntSet.get(0);

            //STEP 3: start the production set of the parent node, R
            List<Production> rList = cfg.getProductionsOf((NonTerminal) CN1.getParent().getSymbol());
            Production mainProduction = CN1.getParent().getProduction();

            //STEP 4: calculate the position of the node in the main derivation.
            int nodePosition = CN1.getParent().getChildren().indexOf(CN1);

            if (nodePosition == -1) {
                throw new RuntimeException("Something went wrong!");
            }

            //STEP 5 & 6: remove all productions from R (rList) which have different size than the main production
            // and remove those productions which all symbols (except this symbol) matches with main production
            for (int i = rList.size() - 1; i >= 0; i--) {
                if (rList.get(i).getRhs().length != mainProduction.getRhs().length) {
                    rList.remove(i);
                    continue;
                }

                boolean allSame = true;

                for (int j = 0; j < mainProduction.getRhs().length; j++) {
                    //'except this' part
                    if (j == nodePosition) continue;

                    if (rList.get(i).getRhs()[j] != mainProduction.getRhs()[j]) {
                        allSame = false;
                        break;
                    }
                }

                if (!allSame) {
                    rList.remove(i);
                }
            }

            //STEP 7: find all the non-terminals in the nodePosition, X, from R
            List<NonTerminal> xList = new ArrayList<NonTerminal>();
            for (Production production : rList) {
                xList.add((NonTerminal) production.getRhs()[nodePosition]);
            }

            //STEP 8 + some of 9: corresponding to each non-MGPFj.terminal in xList, find the ntSet in second parent
            List<Node> ntSet2 = new ArrayList<Node>(generateNTSet(p2, xList));
            //removing the root node from the array
            ntSet2.remove(p2); // TODO: 27/4/19 what happens if the root nodes are included?

            //STEP 9: Select one node at random
            while (ntSet2.size() > 0) {
                Collections.shuffle(ntSet2);
                Node CN2 = ntSet2.get(0);


                //STEP 10: find the depth of both trees. if exceeds maxDepth remove CN2 and go back to step 9
                if (CN1.getDepth() + CN2.getTreeDepth() > maxDepth || CN2.getDepth() + CN1.getTreeDepth() > maxDepth) {
                    ntSet2.remove(0);
                    continue;
                }

                //STEP 11-13
                //Find the position of CN2 in its parent.
                int nodePosition2 = CN2.getParent().getChildren().indexOf(CN2);

                if (nodePosition2 == -1) {
                    //this cannot be wrong right?
                    throw new RuntimeException("Something went wrong!");
                }

                //STEP 12 if the non-MGPFj.terminal symbols of CN1 and CN2 do not match,
                //substitute CN1 symbol to the parent production and check if the production is defined in the cfg.
                if (CN1.getSymbol() != CN2.getSymbol()) {
                    Production p2Production = CN2.getParent().getProduction();

                    Symbol[] rhs = Arrays.copyOf(p2Production.getRhs(), p2Production.getRhs().length);
                    rhs[nodePosition2] = CN1.getSymbol();

                    Production newTestProduction = new Production(p2Production.getLhs(), rhs);

                    if (!cfg.getProductions().contains(newTestProduction)) {
                        ntSet2.remove(0);
                        continue;
                    }
                }


                //STEP 13. replace the children CN1 and CN2
                Node.swapNodes(CN1, CN2);

                result.add(p1);
                result.add(p2);

                return result;
            }

            //some of STEP 8: if this part is reached, it means ntSet2 is empty
            //then remove the CN1 from ntSet1 and goto step 2
            ntSet.remove(0);
        }

        //If reached here, no swapping is possible
        return result;
    }

    private List<Node> generateNTSet(Node node, List<NonTerminal> xList) {
        List<Node> ntSet = new ArrayList<Node>();

        if (node.getSymbol() instanceof Terminal) {
            return ntSet;
        }

        if (xList.contains((NonTerminal) node.getSymbol())) {
            ntSet.add(node);
        }

        for (Node childNode: node.getChildren()) {
            ntSet.addAll(generateNTSet(childNode, xList));
        }

        return ntSet;
    }


}
