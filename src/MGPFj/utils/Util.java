package MGPFj.utils;

import MGPFj.chromosome.Node;
import MGPFj.terminal.constant.Constant;

import java.util.*;

/**
 * Various helper functions
 */
public class Util {

    /**
     * Creates a HashSet from the given arguments.
     * @param elements
     * @param <T>
     * @return
     */
    public static <T> Set<T> createSet(T... elements) {
        Set<T> tSet = new HashSet<T>();
        Collections.addAll(tSet, elements);
        return tSet;
    }

    /**
     * Creates a List from the given arguments.
     * @param elements
     * @param <T>
     * @return
     */
    public static <T> List<T> createList(T... elements) {
        List<T> tList = new ArrayList<T>();
        Collections.addAll(tList, elements);
        return tList;
    }

    public static void displayNodes(List<Node> nodes) {
        for (Node node:nodes) {
            System.out.println(node.getTreeNotation());
        }
    }

    public static Constant[] createIntegerConstants(int from, int to) {
        Constant[] constants = new Constant[to-from+1];

        if (from > to) {
            from = from + to;
            to = from - to;
            from = from - to;
        }

        for (int i = from; i < to+1; i++) {
            constants[i] = new Constant("c" + i, i);
        }

        return constants;
    }

}
