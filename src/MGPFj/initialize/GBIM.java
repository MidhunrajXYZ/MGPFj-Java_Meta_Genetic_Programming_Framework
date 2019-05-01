package MGPFj.initialize;

import MGPFj.chromosome.Node;
import MGPFj.grammar.*;

import java.util.*;

/**
 * Grammar Based Initialization Method todo
 *
 */
public class GBIM implements Initializer {

    private final CFG cfg;
    private final int maxDepth;
    private final Map<Production, Integer> productionLengths = new HashMap<Production, Integer>();
    private final Map<NonTerminal, Integer> nonTerminalLengths = new HashMap<NonTerminal, Integer>();

    /**
     * @param cfg reference to the cfg
     * @param maxDepth maximum allowed depth of the trees
     */
    public GBIM(CFG cfg, int maxDepth) {

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
    public List<Node> generate(int currentDepth, int popSize, NonTerminal rootSymbol) {
        List<Node> nodes = new ArrayList<Node>();

        if (rootSymbol == null) rootSymbol = cfg.getStartSymbol();

        //calculate the length of axiom so that (length <= currentDepth)
        if (this.nonTerminalLengths.get(rootSymbol) > this.maxDepth - currentDepth) {
            //return empty population
            throw new RuntimeException("Cannot start nodes with this depth");
        }

        //generating popSize number of trees
        for (int i = 0; i < popSize; i++) {
            nodes.add(generateTree(null, currentDepth, rootSymbol));
        }

        return nodes;
    }

    /**
     * Generate a new tree with the specified root
     * @param parentNode: The parent Node
     * @param currentSize: the current size of the tree
     * @param currentNonTerminal: The current NonTerminal which is going to generate
     * @return a Node
     */
    private Node generateTree(Node parentNode, Integer currentSize, NonTerminal currentNonTerminal) {

        List<Production> productionList = new ArrayList<Production>();

        //selecting those productions which satisfies the depth condition
        for (Production production : cfg.getProductionsOf(currentNonTerminal)) {
            if (this.productionLengths.get(production) + currentSize <= this.maxDepth) {
                productionList.add(production);
            }
        }

        if (productionList.size() == 0) {
            throw new RuntimeException("0 size!!!");
        }

        //randomly choose one
        Collections.shuffle(productionList);

        //container for child Nodes
        List<Node> childNodes = new ArrayList<Node>();

        //the current node. ie, the parent node
        Node node = new Node(parentNode, currentNonTerminal, childNodes);

        //JAVA is great!! I do not need to pass the updated childNodes to the node as long us I didn't change the reference.
        for (Symbol symbol : productionList.get(0).getRhs()) {
            if (symbol instanceof NonTerminal) {
                childNodes.add(generateTree(node, currentSize + 1, (NonTerminal) symbol));
            } else {
                childNodes.add(new Node(node, symbol, new ArrayList<Node>()));
            }
        }

        return node;
    }
}
