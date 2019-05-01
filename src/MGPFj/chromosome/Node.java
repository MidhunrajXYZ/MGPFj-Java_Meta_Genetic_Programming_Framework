package MGPFj.chromosome;

import MGPFj.grammar.*;
import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;
import MGPFj.terminal.constant.Constant;
import MGPFj.terminal.operation.Operation;
import MGPFj.terminal.variable.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree
 * Each Node contains reference to all its child nodes and parent node.
 */
public class Node {
    private final Node parent; //todo: remove this?
    private final Symbol symbol;
    private final List<Node> children;

    /**
     *
     * @param parent    the reference to the parent Node of this Node
     * @param symbol    the symbol which this node contains. It could be a NonTerminal, Operation, Constant or Variable.
     * @param children  the child nodes of this node.
     */
    public Node(Node parent, Symbol symbol, List<Node> children) {
        this.parent = parent;
        this.children = children;
        this.symbol = symbol;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Production getProduction() {

        if (this.getSymbol() instanceof Terminal) {
            return null;
        }

        NonTerminal lhs = (NonTerminal) this.getSymbol();

        Symbol[] rhs = new Symbol[getChildren().size()];

        for (int i = 0; i < rhs.length; i++) {
            rhs[i] = this.getChildren().get(i).getSymbol();
        }

        return new Production(lhs, rhs);
    }


    /**
     * get the depth of this node from the root node of the entire tree
     * @return
     */
    public int getDepth() {
        if (this.getParent() == null) {
            return 0;
        }
        return this.getParent().getDepth() + 1;
    }


    /**
     * Finds the depth of the tree from this node. (starts from 0)
     * @return the tree depth
     */
    public int getTreeDepth() {

        if (this.getSymbol() instanceof Terminal) {
            return 0;
        }

        int depth = 0;

        for (Node node : getChildren()) {
            depth = Math.max(depth, node.getTreeDepth());
        }
        return depth + 1;
    }

    /**
     * Get the number of nodes in this branch of the tree.
     * @return Total number of nodes
     */
    public Integer getNodeCount() {

        if (this.getSymbol() instanceof Terminal) {
            return 1;
        }

        int result = 1;

        for (Node child:this.getChildren()) {
            result += child.getNodeCount();
        }

        return result;
    }

    /**
     * walk through all the sub nodes and takes the symbol names
     * @return a text version of the tree
     */
    public String getTreeNotation() {
        String s = this.symbol.getSymbolName();

        if (this.children.size() > 0) {

            StringBuilder s2 = new StringBuilder();

            for (Node node : children) {
                s2.append(node.getTreeNotation());
            }

            s = s + " (" + s2 + ")";
        }

        return s;
    }

    public String getTerminalNotation() {
        StringBuilder s2 = new StringBuilder();

        if (this.symbol instanceof Terminal) {
            return this.symbol.getSymbolName();
        }

        else if (this.children.size() > 0) {


            for (Node node : children) {
                s2.append(node.getTerminalNotation());
            }
        }

        return s2.toString();
    }

    /**
     * Given two Nodes, swap them in their respective trees. [immutable] <p/>
     * [NOTE: both nodes must need parent nodes. Thus they cannot be root-nodes.]
     * @param node1 first node
     * @param node2 second node
     */
    public static void swapNodes (Node node1, Node node2) {

        //find p1, p2, pos1, and pos2
        Node p1 = node1.getParent();
        Node p2 = node2.getParent();

        int pos1 = -1;
        int pos2 = -1;

        for (int i = 0; i < p1.getChildren().size(); i++) {
            if (p1.getChildren().get(i) == node1) {
                pos1 = i;
                break;
            }
        }

        for (int i = 0; i < p2.getChildren().size(); i++) {
            if (p2.getChildren().get(i) == node2) {
                pos2 = i;
                break;
            }
        }

        if (pos1 == -1 || pos2 == -1) {
            throw new RuntimeException("Node Exchange failed");
        }

        //create copies of node1 and node2 with parents swapped
        Node node1C = node1.copyTree(p2);
        Node node2C = node2.copyTree(p1);

        //chance the children nodes of p1 and p2.
        p1.getChildren().add(pos1, node2C);
        p1.getChildren().remove(pos1+1);
        p2.getChildren().add(pos2, node1C);
        p2.getChildren().remove(pos2+1);
    }

    /**
     * This replaces a particular node with another one within its parent [immutable]<p/>
     * [NOTE: the first parameter must need a parent node. Thus it cannot be root-node.]
     * @param replacedNode the node which is going to get replaced
     * @param replacingNode the new node.
     */
    public static void replaceNode(Node replacedNode, Node replacingNode) {
        //find p1, p2, pos1, and pos2
        Node p = replacedNode.getParent();

        int pos1 = -1;

        for (int i = 0; i < p.getChildren().size(); i++) {
            if (p.getChildren().get(i) == replacedNode) {
                pos1 = i;
                break;
            }
        }

        if (pos1 == -1) {
            throw new RuntimeException("Node Exchange failed");
        }

        //create copies of node1 and node2 with parents swapped
        Node node2C  = replacingNode.copyTree(p);

        //chance the children nodes of p1 and p2.
        p.getChildren().add(pos1, node2C);
        p.getChildren().remove(pos1+1);

    }



    /**
     * Evaluates a node, based on what type of symbol it is holding.
     * @param assignments values that are used to assign to Variables
     * @return the result of the evaluation
     */
    public Object evaluate(Assignments assignments) {

        if (this.symbol instanceof NonTerminal) {
            //Note: All non-MGPFj.terminal nodes have child nodes.

            if (this.children == null || this.children.size() == 0) {
                throw new RuntimeException("Non-Terminal Node with no children");
            }
            else if (this.children.size() == 1) {
                //only one child = something like E =:: F
                if (this.children.get(0).getSymbol() instanceof Operation) {
                    throw new RuntimeException("Only one operation is not allowed");
                }
                return this.children.get(0).evaluate(assignments);
            }
            else {
                //Here, we first get the child nodes from index 1 to last,
                //then they are given to 1st child, which is the operation node.
                if (!(this.children.get(0).getSymbol() instanceof Operation)) {
                    throw new RuntimeException("No Operation at the start");
                }

                List<Node> args = new ArrayList<Node>();
                for (int i = 1; i < this.children.size(); i++) {
                    args.add(this.children.get(i));
                }
                return this.children.get(0).getSymbol().evaluate(Arguments.createArguments(args), assignments);
            }
        }
        else if (this.symbol instanceof Constant) {

            return this.symbol.evaluate(null, null);

        } else if (this.symbol instanceof Variable){

            return this.symbol.evaluate(null, assignments);

        } else {

            System.out.println("THIS IS UNREACHABLE RIGHT?");
            return null;
        }
    }


    @Override
    public String toString() {
        return getTreeNotation();
    }


    /**
     * Copy this node along with all its children
     * @param parent
     * @return
     */
    public Node copyTree(Node parent) {

        List<Node> newChildren = new ArrayList<Node>();

        Node newNode = new Node(parent, this.getSymbol(), newChildren);

        if (this.getSymbol() instanceof NonTerminal) {
            for (Node node:this.getChildren()) {
                newChildren.add(node.copyTree(newNode));
            }
        }

        return newNode;
    }

}
