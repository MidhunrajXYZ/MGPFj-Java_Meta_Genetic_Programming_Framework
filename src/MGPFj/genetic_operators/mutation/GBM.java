package MGPFj.genetic_operators.mutation;


import MGPFj.chromosome.Node;
import MGPFj.genetic_operators.GeneticOperator;
import MGPFj.grammar.*;

import java.util.*;

/**
 * Grammar Based Mutation
 */
public class GBM implements GeneticOperator {

    private final CFG cfg;
    private final int maxDepth;
    private final Map<Production, Integer> productionLengths = new HashMap<Production, Integer>();
    private final Map<NonTerminal, Integer> nonTerminalLengths = new HashMap<NonTerminal, Integer>();

    public GBM(CFG cfg, int maxDepth) {
        this.cfg = cfg;
        this.maxDepth = maxDepth;

        //finding the length of production rules and non-terminals of the cfg.
        boolean isFinished = false;

        while(!isFinished) {

            isFinished = true;

            //iterating through each productions
            for (Production production : cfg.getProductions()) {

                //if the production length is already evaluated, skip
                if (this.productionLengths.containsKey(production)) {
                    continue;
                }

                //else, iterate through each rhs symbol if the production

                isFinished = false;
                int productionLength = -1;
                boolean areAllTerminal = true;

                for (Symbol symbol : production.getRhs()) {
                    if (symbol instanceof NonTerminal) {
                        areAllTerminal = false;
                        //if the non-MGPFj.terminal length is not evaluated yet, break. else, find the the max.
                        if (this.nonTerminalLengths.containsKey(symbol)) {
                            productionLength = Math.max(productionLength, this.nonTerminalLengths.get(symbol));
                        } else {
                            productionLength = -1;
                            break;
                        }
                    }
                }

                if (productionLength != -1) {
                    productionLength++;
                    this.productionLengths.put(production, productionLength);

                    //set the non-MGPFj.terminal length
                    Integer minValue = Integer.MAX_VALUE;
                    if (this.nonTerminalLengths.containsKey(production.getLhs())) {
                        minValue = this.nonTerminalLengths.get(production.getLhs());
                    }

                    minValue = Math.min(minValue, productionLength);
                    this.nonTerminalLengths.put(production.getLhs(), minValue);
                }

                if (areAllTerminal) {
                    this.productionLengths.put(production, 1);
                    this.nonTerminalLengths.put(production.getLhs(), 1);
                }

            }

        }
    }


    @Override
    public List<Node> evolve(List<Node> nodes) {

        List<Node> result = new ArrayList<Node>();

        Node p = nodes.get(0).copyTree(null);

        //1 to 8 STEPS are similar to GBC

        /*STEP 1: start the NT set of first parent, except the root nodes
        NT set contains all the non MGPFj.terminal nodes*/
        List<Node> ntSet = generateNTSet(p, cfg.getNonTerminals());
        ntSet.remove(p);

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

            //STEP 8: randomly choose one symbol, cs from X.
            while (xList.size() > 0) {

                Collections.shuffle(xList);
                NonTerminal cs = xList.get(0);

                //STEP 9: find the mutation length ML
                int ml = maxDepth - CN1.getDepth();

                //STEP 10: assign value 0 to current depth
                int cd = 0;

                Node newNode = generateTree(CN1.getParent(), cd, ml, cs);
                if (newNode == null) {
                    xList.remove(0);
                    continue;
                }

                //STEP 14: replace CN1 with the newly created node.
                Node.replaceNode(CN1, newNode);

                result.add(p);
                return result;

            }

            //if X is empty remove CN1 and go back to step 2
            ntSet.remove(0);

        }



        return result;
    }

    /**
     * Generate a new tree with the specified root
     * @param parentNode: The parent Node
     * @param currentSize: the current size of the tree
     * @param currentNonTerminal: The current NonTerminal which is going to generate
     * @return a Node
     */
    private Node generateTree(Node parentNode, Integer currentSize, Integer mutationLength, NonTerminal currentNonTerminal) {

        //STEP 11: get the set of productions of cs, pp. if pp is empty, remove cs go back to step 8
        List<Production> productionList = new ArrayList<Production>();
        for (Production production : cfg.getProductionsOf(currentNonTerminal)) {
            if (this.productionLengths.get(production) + currentSize <= mutationLength) {
                productionList.add(production);
            }
        }

        if (productionList.size() == 0) {
            return null;
        }

        //STEP 12 && 13: at this point, there is at-least one possible production that works.
        //randomly choose one
        Collections.shuffle(productionList);

        //container for child Nodes
        List<Node> childNodes = new ArrayList<Node>();

        //the current node. ie, the parent node
        Node node = new Node(parentNode, currentNonTerminal, childNodes);

        //JAVA is great!! I do not need to pass the updated childNodes to the node as long us I didn't change the reference.
        for (Symbol symbol : productionList.get(0).getRhs()) {
            if (symbol instanceof NonTerminal) {
                childNodes.add(generateTree(node, currentSize + 1, mutationLength, (NonTerminal) symbol));
            } else {
                childNodes.add(new Node(node, symbol, new ArrayList<Node>()));
            }
        }

        return node;
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
