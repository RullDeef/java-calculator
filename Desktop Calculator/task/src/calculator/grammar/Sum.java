package calculator.grammar;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Sum implements Expression {
    private final List<Expression> terms;

    public Sum(Expression... terms) {
        this.terms = List.of(terms);
    }

    @Override
    public BigDecimal getResult() {
        return terms.stream()
                .map(Expression::getResult)
                .reduce(new BigDecimal(0), BigDecimal::add);
    }

    @Override
    public String toString() {
        return terms.stream()
                .map(Expression::toString)
                .collect(Collectors.joining(",", "SUM(", ")"));
    }
}
