package calculator.grammar;

import java.util.List;

public class Operation implements Comparable<Operation> {

    public enum Type {
        ADD, SUB, MUL, DIV, SQRT, SQR, POW
    }

    public static final Operation ADD = new Operation(Type.ADD);
    public static final Operation SUB = new Operation(Type.SUB);
    public static final Operation MUL = new Operation(Type.MUL);
    public static final Operation DIV = new Operation(Type.DIV);
    public static final Operation SQRT = new Operation(Type.SQRT);
    public static final Operation SQR = new Operation(Type.SQR);
    public static final Operation POW = new Operation(Type.POW);

    private final Type type;

    public Operation(Type type) {
        this.type = type;
    }

    public static final List<Operation> all = List.of(
            new Operation(Type.ADD),
            new Operation(Type.SUB),
            new Operation(Type.MUL),
            new Operation(Type.DIV),
            new Operation(Type.SQRT),
            new Operation(Type.SQR),
            new Operation(Type.POW)
    );

    public Type getType() {
        return type;
    }

    public boolean isArithmetic() {
        return type == Type.ADD || type == Type.SUB || type == Type.MUL ||
                type == Type.DIV || type == Type.SQRT || type == Type.POW;
    }

    @Override
    public String toString() {
        switch (type) {
            case ADD: return "\u002B";
            case SUB: return "-";
            case MUL: return "\u00D7";
            case DIV: return "\u00F7";
            case SQRT: return "\u221A";
            case POW: return "^";
            case SQR: return "^(2)";
        }
        throw new NumberFormatException();
    }

    @Override
    public int compareTo(Operation operation) {
        if (operation.type == Type.POW) {
            return 1;
        } else if (type == Type.POW) {
            return -1;
        } else if ((type == Type.ADD || type == Type.SUB) && (operation.type == Type.ADD || operation.type == Type.SUB)) {
            return 0;
        } else if ((type == Type.MUL || type == Type.DIV) && (operation.type == Type.MUL || operation.type == Type.DIV)) {
            return 0;
        } else if (type == Type.ADD || type == Type.SUB) {
            return 1;
        } else {
            return -1;
        }
    }
}
