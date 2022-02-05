package calculator.grammar;

import java.math.BigDecimal;

public class Power implements Expression {
    private final Expression left;
    private final Expression right;

    public Power(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigDecimal getResult() {
        double dLeft = left.getResult().doubleValue();
        double dRight = right.getResult().doubleValue();

        return BigDecimal.valueOf(Math.pow(dLeft, dRight));
    }

    @Override
    public String toString() {
        return "POW(" + left.toString() + "," + right.toString() + ")";
    }
}
