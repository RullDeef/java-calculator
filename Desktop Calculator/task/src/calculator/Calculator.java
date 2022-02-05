package calculator;

import calculator.grammar.Operation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Calculator extends JFrame {
    private static final int TOP_MARGIN = 35;
    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 40;
    private static final int CELL_SPACING = 2;
    private static final int GRID_MARGIN = 4;

    private JLabel equationLabel;
    private JLabel resultLabel;

    private final CalcModel model = new CalcModel();

    public Calculator() {
        super("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2 * GRID_MARGIN + 4 * CELL_WIDTH + 3 * CELL_SPACING,
                TOP_MARGIN + 2 * GRID_MARGIN + 8 * CELL_HEIGHT + 7 * CELL_SPACING);
        setLayout(null);
        setBackground(Color.GRAY.darker());

        initUI();

        model.setOnInputStringChange(equationLabel::setText);
        model.setOnResultStringChange(resultLabel::setText);
        model.setOnInputStateChange(this::inputStateChanged);
        inputStateChanged(CalcModel.InputState.GOOD);

        setVisible(true);
    }

    private void initUI() {
        resultLabel = new JLabel();
        resultLabel.setName("ResultLabel");
        resultLabel.setBounds(gridBounds(0, 0, 4));
        resultLabel.setVisible(true);
        add(resultLabel);

        equationLabel = new JLabel();
        equationLabel.setName("EquationLabel");
        equationLabel.setBounds(gridBounds(1, 0, 4));
        equationLabel.setVisible(true);
        add(equationLabel);

        var dotButton = new JButton(".");
        dotButton.setName("Dot");
        dotButton.setBounds(gridBounds(7, 2));
        dotButton.setBackground(Color.LIGHT_GRAY.brighter());
        dotButton.setBorderPainted(false);
        dotButton.addActionListener(evt -> model.insertDot());
        dotButton.setVisible(true);
        add(dotButton);

        var plusButton = new JButton("±");
        plusButton.setName("PlusMinus");
        plusButton.setBounds(gridBounds(7, 0));
        plusButton.setBackground(Color.LIGHT_GRAY.brighter());
        plusButton.setBorderPainted(false);
        plusButton.addActionListener(evt -> model.insertPlusMinus());
        plusButton.setVisible(true);
        add(plusButton);

        var braceButton = new JButton("()");
        braceButton.setName("Parentheses");
        braceButton.setBounds(gridBounds(2, 0));
        braceButton.setBackground(Color.LIGHT_GRAY);
        braceButton.setBorderPainted(false);
        braceButton.addActionListener(evt -> model.insertBrace());
        braceButton.setVisible(true);
        add(braceButton);

        var clearRecentButton = new JButton("CE");
        clearRecentButton.setName("ClearEntry");
        clearRecentButton.setBounds(gridBounds(2, 1));
        clearRecentButton.setBackground(Color.LIGHT_GRAY);
        clearRecentButton.setBorderPainted(false);
        clearRecentButton.addActionListener(evt -> model.clearRecent());
        clearRecentButton.setVisible(true);
        add(clearRecentButton);

        var clearButton = new JButton("C");
        clearButton.setName("Clear");
        clearButton.setBounds(gridBounds(2, 2));
        clearButton.setBackground(Color.LIGHT_GRAY);
        clearButton.setBorderPainted(false);
        clearButton.addActionListener(evt -> model.clear());
        clearButton.setVisible(true);
        add(clearButton);

        var delButton = new JButton("Del");
        delButton.setName("Delete");
        delButton.setBounds(gridBounds(2, 3));
        delButton.setBackground(Color.LIGHT_GRAY);
        delButton.setBorderPainted(false);
        delButton.addActionListener(evt -> model.delete());
        delButton.setVisible(true);
        add(delButton);

        setupDigitButtons();
        setupOperButtons();
    }

    private static Rectangle gridBounds(int row, int col) {
        return gridBounds(row, col, 1);
    }

    private static Rectangle gridBounds(int row, int col, int rowspan) {
        var x = GRID_MARGIN + col * (CELL_WIDTH + CELL_SPACING) - CELL_SPACING;
        var y = GRID_MARGIN + row * (CELL_HEIGHT + CELL_SPACING) - CELL_SPACING;
        var width = rowspan * (CELL_WIDTH + CELL_SPACING) - CELL_SPACING;
        return new Rectangle(x, y, width, CELL_HEIGHT);
    }

    private void setupDigitButtons() {
        String[] names = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        int[][] pos = {{7, 1}, {6, 0}, {6, 1}, {6, 2}, {5, 0},
                {5, 1}, {5, 2}, {4, 0}, {4, 1}, {4, 2}};

        for (int i = 0; i <= 9; i++) {
            int finalI = i;
            String num = String.valueOf(i);
            var button = new JButton(num);
            button.setName(names[i]);
            button.setBounds(gridBounds(pos[i][0], pos[i][1]));
            button.setBackground(Color.LIGHT_GRAY.brighter());
            button.setBorderPainted(false);
            button.addActionListener(evt -> model.insertDigit(finalI));
            button.setVisible(true);
            add(button);
        }
    }

    private void setupOperButtons() {
        String[] names = {"Add", "Subtract", "Divide", "Multiply", "SquareRoot", "PowerTwo", "PowerY", "Equals"};
        String[] opers = {"\u002B", "-", "\u00F7", "\u00D7", "\u221A", "^2", "^", "="};
        int[][] pos = {{6, 3}, {5, 3}, {3, 3}, {4, 3}, {3, 2}, {3, 0}, {3, 1}, {7, 3}};
        ActionListener[] listeners = {
                evt -> model.makeOp(Operation.ADD),
                evt -> model.makeOp(Operation.SUB),
                evt -> model.makeOp(Operation.DIV),
                evt -> model.makeOp(Operation.MUL),
                evt -> model.makeOp(Operation.SQRT),
                evt -> model.makeOp(Operation.SQR),
                evt -> model.makeOp(Operation.POW),
                evt -> model.calculate()
        };

        for (int i = 0; i < names.length; i++) {
            var button = new JButton(opers[i]);
            button.setName(names[i]);
            button.setBounds(gridBounds(pos[i][0], pos[i][1]));
            button.setBackground(i + 1 == names.length ? Color.CYAN.brighter() : Color.LIGHT_GRAY);
            button.setBorderPainted(false);
            button.addActionListener(listeners[i]);
            button.setVisible(true);
            add(button);
        }
    }

    private void inputStateChanged(CalcModel.InputState newState) {
        switch (newState) {
            case GOOD:
                equationLabel.setForeground(Color.GREEN.darker());
                break;
            case INVALID:
                equationLabel.setForeground(Color.RED.darker());
                break;
        }
    }
}
