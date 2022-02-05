package calculator.grammar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    private static class Token {
        public enum Type {
            NUMBER, OPERATION, OPEN_BRACE, CLOSE_BRACE
        }

        private final Type type;
        private final String value;

        private final BigDecimal numberValue;
        private final Operation operationValue;

        public Token(Type type) {
            this.type = type;
            this.value = "";
            this.numberValue = null;
            this.operationValue = null;
        }

        public Token(Type type, String value) {
            this.type = type;
            this.value = value;
            this.numberValue = new BigDecimal(value);
            this.operationValue = null;
        }

        public Token(Type type, Operation operation) {
            this.type = type;
            this.value = operation.toString();
            this.numberValue = null;
            this.operationValue = operation;
        }

        public Type getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public BigDecimal asNumber() {
            if (numberValue == null) {
                throw new NumberFormatException();
            }
            return numberValue;
        }

        public Operation asOperation() {
            if (operationValue == null) {
                throw new NumberFormatException();
            }
            return operationValue;
        }
    }

    private static class PartialParseResult {
        private final Token token;
        private final String tail;

        public PartialParseResult(Token token, String tail) {
            this.token = token;
            this.tail = tail;
        }

        public Token getToken() {
            return token;
        }

        public String getTail() {
            return tail;
        }
    }

    public Expression parse(String string) throws GrammarException {
        List<Token> tokenList = createTokenList(string);
        Expression result = parseTokenList(tokenList);

        if (!tokenList.isEmpty())
            throw new GrammarException("invalid expression");

        return result;
    }

    private Expression parseTokenList(List<Token> list) throws GrammarException {
        if (list.size() == 0)
            throw new GrammarException("empty expression");

        Stack<Expression> exprStack = new Stack<>();
        Stack<Operation> opStack = new Stack<>();

        while (!list.isEmpty()) {
            Token token = list.remove(0);
            switch (token.getType()) {
                case NUMBER:
                    exprStack.push(new Number(token.getValue()));
                    break;
                case OPEN_BRACE:
                    exprStack.push(parseTokenList(list));
                    break;
                case CLOSE_BRACE:
                    return constructOperation(exprStack, opStack);
                case OPERATION:
                    if (token.asOperation().getType() == Operation.Type.SQRT) {
                        Token nextToken = list.remove(0);
                        if (nextToken.getType() == Token.Type.NUMBER) {
                            exprStack.push(new Power(new Number(nextToken.getValue()), new Number("0.5")));
                        } else if (nextToken.getType() == Token.Type.OPEN_BRACE) {
                            exprStack.push(new Power(parseTokenList(list), new Number("0.5")));
                        } else {
                            throw new GrammarException("unexpected token type after square root");
                        }
                    } else if (token.asOperation().getType() == Operation.Type.SUB && opStack.isEmpty() && exprStack.isEmpty()) {
                        Token nextToken = list.remove(0);
                        if (nextToken.getType() == Token.Type.NUMBER) {
                            exprStack.push(new Negation(new Number(nextToken.getValue())));
                        } else if (nextToken.getType() == Token.Type.OPEN_BRACE) {
                            exprStack.push(new Negation(parseTokenList(list)));
                        } else {
                            throw new GrammarException("unexpected token type after unary minus");
                        }
                    } else  {
                        opStack.push(token.asOperation());
                    }
                    break;
            }

            tryPerformOperation(exprStack, opStack);
        }

        return constructOperation(exprStack, opStack);
    }

    private void tryPerformOperation(Stack<Expression> exprStack, Stack<Operation> opStack) throws GrammarException {
        if (exprStack.size() == 3 && opStack.size() == 2) {
            Expression right = exprStack.pop();
            Expression mid = exprStack.pop();
            Expression left = exprStack.pop();

            Operation second = opStack.pop();
            Operation first = opStack.pop();

            Expression result;
            if (first.compareTo(second) <= 0) {
                result = buildExpr(left, first, mid);
                exprStack.push(result);
                exprStack.push(right);
                opStack.push(second);
            } else {
                result = buildExpr(mid, second, right);
                exprStack.push(left);
                exprStack.push(result);
                opStack.push(first);
            }
        }
    }

    private Expression constructOperation(Stack<Expression> exprStack, Stack<Operation> opStack) throws GrammarException {
        if (exprStack.size() == 1 && opStack.isEmpty()) {
            return exprStack.pop();
        } else if (exprStack.size() == 1 && opStack.size() == 1 && opStack.get(0).getType() == Operation.Type.SUB) {
            opStack.pop();
            return new Negation(exprStack.pop());
        } else if (exprStack.size() == 2 && opStack.size() == 1) {
            Operation op = opStack.pop();
            Expression right = exprStack.pop();
            Expression left = exprStack.pop();

            return buildExpr(left, op, right);
        } else if (exprStack.size() == 3 && opStack.size() == 2) {
            Expression right = exprStack.pop();
            Expression mid = exprStack.pop();
            Expression left = exprStack.pop();

            Operation first = opStack.pop();
            Operation second = opStack.pop();

            if (first.compareTo(second) <= 0) {
                return buildExpr(buildExpr(left, first, mid), second, right);
            } else {
                return buildExpr(left, first, buildExpr(mid, second, right));
            }
        } else {
            throw new GrammarException("bad stacks configuration");
        }
    }

    private Expression buildExpr(Expression left, Operation operation, Expression right) throws GrammarException {
        if (operation.getType() == Operation.Type.ADD) {
            return new Sum(left, right);
        } else if (operation.getType() == Operation.Type.SUB) {
            return new Sum(left, new Negation(right));
        } else if (operation.getType() == Operation.Type.MUL) {
            return new Product(left, right);
        } else if (operation.getType() == Operation.Type.DIV) {
            return new Division(left, right);
        } else if (operation.getType() == Operation.Type.POW) {
            return new Power(left, right);
        } else {
            throw new GrammarException("unhandled operation type");
        }
    }

    private List<Token> createTokenList(String string) throws GrammarException {
        List<Token> tokenList = new ArrayList<>();

        PartialParseResult parseRes = parseNextToken(string);
        while (parseRes != null) {
            tokenList.add(parseRes.getToken());
            parseRes = parseNextToken(parseRes.getTail());
        }

        return tokenList;
    }

    private PartialParseResult parseNextToken(String string) throws GrammarException {
        string = string.strip();

        if (string.length() == 0)
            return null;

        if (string.matches("^\\d+(\\.\\d*)?.*")) {
            String tail = string.replaceFirst("^\\d+(\\.\\d*)?", "");
            String number = tail.isEmpty() ? string : string.substring(0, string.indexOf(tail));

            return new PartialParseResult(new Token(Token.Type.NUMBER, number), tail);
        } else if (string.indexOf("(") == 0) {
            String tail = string.substring(1);

            return new PartialParseResult(new Token(Token.Type.OPEN_BRACE), tail);
        } else if (string.indexOf(")") == 0) {
            String tail = string.substring(1);

            return new PartialParseResult(new Token(Token.Type.CLOSE_BRACE), tail);
        } else {
            for (var op : Operation.all) {
                if (string.indexOf(op.toString()) == 0 && op.isArithmetic()) {
                    String tail = string.substring(op.toString().length());

                    return new PartialParseResult(new Token(Token.Type.OPERATION, op), tail);
                }
            }
        }

        throw new GrammarException("bad token at \"" + string + "\"");
    }
}
