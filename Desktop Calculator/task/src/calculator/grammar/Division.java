package calculator.grammar;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Division implements Expression {
    private final List<Expression> terms;

    public Division(Expression... terms) {
        this.terms = List.of(terms);
    }

    @Override
    public BigDecimal getResult() {
        BigDecimal first = terms.get(0).getResult();
        return terms.stream()
                .skip(1)
                .map(Expression::getResult)
                .reduce(first, BigDecimal::divide);
    }

    @Override
    public String toString() {
        return terms.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(",", "DIV(", ")"));
    }
}
