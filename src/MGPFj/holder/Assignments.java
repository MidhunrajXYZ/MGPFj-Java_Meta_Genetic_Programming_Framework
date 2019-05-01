package MGPFj.holder;

import java.util.Arrays;

public class Assignments {
    private final Object[] values;

    /**
     * creates assignments with an array of values. Each variable holds one of these values during the evaluation.
     * @param values An array of values. Could be any form of data since it is of the type Object.
     */
    private Assignments(Object[] values) {
        this.values = Arrays.copyOf(values, values.length);
    }

    /**
     * creates assignments with an array of values. Each variable holds one of these values during the evaluation.
     * @param values A set of values. Could be any form of data since it is of the type Object.
     */
    public static Assignments createAssignments(Object... values) {
        return new Assignments(values);
    }


    public Object[] getAssignments() {
        return values;
    }

    /**
     * @throws ArrayIndexOutOfBoundsException
     * @param index the index of the assignment.
     * @return the respective assignment
     */
    public Object getAssignment(int index) {
        return values[index];
    }
}
