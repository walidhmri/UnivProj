import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzerGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTable tokenTable;
    private DefaultTableModel tableModel;
    private JButton analyzeButton;
    private JTextArea errorArea;

    public enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, LITERAL, PUNCTUATION, ERROR
    }

    public static class Token {
        TokenType type;
        String value;
        int position;

        public Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
    }

    public LexicalAnalyzerGUI() {
        setTitle("Analyseur Lexical");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 50);
        inputTextArea.setLineWrap(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(new JLabel("Code Source:"), BorderLayout.NORTH);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        analyzeButton = new JButton("Analyser");
        analyzeButton.addActionListener(e -> performLexicalAnalysis());
        inputPanel.add(analyzeButton, BorderLayout.SOUTH);

        String[] columnNames = {"Jeton", "Type", "Position"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tokenTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tokenTable);

        errorArea = new JTextArea(5, 50);
        errorArea.setEditable(false);
        errorArea.setForeground(Color.RED);
        JScrollPane errorScrollPane = new JScrollPane(errorArea);

        add(inputPanel, BorderLayout.NORTH);
        add(new JLabel("Tokens:"), BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.CENTER);
        add(new JLabel("Erreurs:"), BorderLayout.SOUTH);
        add(errorScrollPane, BorderLayout.SOUTH);
    }

    private void performLexicalAnalysis() {
        tableModel.setRowCount(0);
        errorArea.setText("");

        String input = inputTextArea.getText();
        List<Token> tokens = tokenize(input);

        for (Token token : tokens) {
            String typeName = getTokenTypeName(token.type);
            tableModel.addRow(new Object[]{token.value, typeName, token.position});

            if (token.type == TokenType.ERROR) {
                errorArea.append("Erreur: Token non reconnu '" + token.value + "' à la position " + token.position + "\n");
            }
        }
    }

    private String getTokenTypeName(TokenType type) {
        switch (type) {
            case KEYWORD: return "mot-clé";
            case IDENTIFIER: return "identifiant";
            case OPERATOR: return "opérateur";
            case LITERAL: return "littéral";
            case PUNCTUATION: return "ponctuation";
            case ERROR: return "erreur";
            default: return "inconnu";
        }
    }

    private List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        int pos = 0;

        while (pos < input.length()) {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }

            if (pos >= input.length()) break;

            if (Character.isLetter(input.charAt(pos))) {
                int start = pos;
                while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)) || input.charAt(pos) == '_')) {
                    pos++;
                }
                String value = input.substring(start, pos);
                TokenType type = isKeyword(value) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
                tokens.add(new Token(type, value, start));
            } else if (Character.isDigit(input.charAt(pos))) {
                int start = pos;
                boolean hasDecimal = false;
                while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                    if (input.charAt(pos) == '.') {
                        if (hasDecimal) {
                            tokens.add(new Token(TokenType.ERROR, input.substring(start, pos + 1), start));
                            break;
                        }
                        hasDecimal = true;
                    }
                    pos++;
                }
                if (tokens.get(tokens.size() - 1).type != TokenType.ERROR) {
                    tokens.add(new Token(TokenType.LITERAL, input.substring(start, pos), start));
                }
            } else if ("+-*/=<>!&|(){}[];,".indexOf(input.charAt(pos)) != -1) {
                int start = pos;
                char current = input.charAt(pos);
                pos++;
                tokens.add(new Token(
                    "(){}[];,".indexOf(current) != -1 ? TokenType.PUNCTUATION : TokenType.OPERATOR,
                    String.valueOf(current),
                    start
                ));
            } else {
                tokens.add(new Token(TokenType.ERROR, String.valueOf(input.charAt(pos)), pos));
                pos++;
            }
        }

        return tokens;
    }

    private static final String[] KEYWORDS = {
        "int", "float", "double", "char", "void",
        "if", "else", "while", "for", "return",
        "break", "continue", "switch", "case", "default"
    };

    private boolean isKeyword(String str) {
        for (String keyword : KEYWORDS) {
            if (keyword.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LexicalAnalyzerGUI gui = new LexicalAnalyzerGUI();
            gui.setVisible(true);
        });
    }
}
