package MGPFj.holder;

import MGPFj.chromosome.Node;

import java.util.Arrays;
import java.util.List;

public class Arguments {
    private final Node[] args;

    public static Arguments createArguments(Node... args) {
        return new Arguments(args);
    }

    public static Arguments createArguments(List<? extends Node> args) {
        return new Arguments(args.toArray(new Node[0]));
    }

    private Arguments(Node... args) {
        this.args = Arrays.copyOf(args, args.length); //todo make it in Utils
    }

    public int size() {
        return this.args.length;
    }

    public Node[] getArgs() {
        return args;
    }

    /**
     *
     * @param i
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    public Node getArg(int i) {
        return args[i];
    }

    public Node firstArg () {
        return args[0];
    }

    public Node secondArg () {
        return args[1];
    }

    public Node thirdArg () {
        return args[2];
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
