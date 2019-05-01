package MGPFj.terminal.operation;

import MGPFj.chromosome.Node;
import MGPFj.grammar.Terminal;
import MGPFj.holder.Arguments;

public interface Operation extends Terminal {

    /**
     * Tries to simplify the operation and reduce the size of the tree.
     * @param arguments the operand nodes
     * @return the simplified node.
     */
    Node simplify(Arguments arguments);

//    TODO: 1/5/19:
//    default Node simplify(Arguments arguments) {
//        return null;
//    }

}
