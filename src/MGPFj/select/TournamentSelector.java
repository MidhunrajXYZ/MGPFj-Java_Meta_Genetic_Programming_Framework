package MGPFj.select;

import MGPFj.chromosome.Node;
import MGPFj.chromosome.RankedCandidate;

import java.util.Random;

public class TournamentSelector implements Selector {

    private final RankedCandidate[] candidates;
    private final int size;
    private final double sum;

    public TournamentSelector(RankedCandidate[] candidates) {
        this.candidates = candidates;
        this.size = candidates.length;
        long s = 0;
        for (int i = 1; i <= size; i++) {
            s += i;
        }
        sum = s; //s = 1+2+3+...n
    }


    @Override
    public Node next() {
        final double r = new Random().nextDouble(); //0.0 <= r < 1.0
        double p = 0;
        for (int i = 0; i < this.size; i++) {
            p += (this.size - i) / this.sum;
            if (r < p) {
                RankedCandidate c = this.candidates[i];
                return c.getNode();
            }
        }

        // should only get here if rounding error - default to selecting the best candidate
        return candidates[0].getNode();
    }
}
