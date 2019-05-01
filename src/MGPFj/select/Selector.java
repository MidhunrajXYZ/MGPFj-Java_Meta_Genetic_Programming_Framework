package MGPFj.select;


import MGPFj.chromosome.Node;

/**
 * Select a ranked candidate from the pool
 */
public interface Selector {

    /**
     * Selects the next node from the pool
     * @return
     */
    Node next();
}
