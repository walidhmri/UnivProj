import java.util.ArrayList;
import java.util.List;

public class SyntacticAnalyzer {
    enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, LITERAL, PUNCTUATION
    }

    static class Token {
        TokenType type;
        String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    public static boolean analyzeExpression(List<Token> tokens) {
        try {
            return parseDeclaration(tokens);
        } catch (Exception e) {
            System.err.println("Syntactic Error: " + e.getMessage());
            return false;
        }
    }

    private static boolean parseDeclaration(List<Token> tokens) {
        if (tokens.size() < 4) return false;

        if (tokens.get(0).type != TokenType.KEYWORD) return false;
        if (tokens.get(1).type != TokenType.IDENTIFIER) return false;
        if (tokens.get(2).type != TokenType.OPERATOR || !tokens.get(2).value.equals("=")) return false;
        if (tokens.get(3).type != TokenType.LITERAL && tokens.get(3).type != TokenType.IDENTIFIER) return false;
        if (tokens.get(4).type != TokenType.PUNCTUATION || !tokens.get(4).value.equals(";")) return false;

        return true;
    }

    private static boolean parseFunctionDeclaration(List<Token> tokens) {
        if (tokens.size() < 6) return false;

        if (tokens.get(0).type != TokenType.KEYWORD) return false;
        if (tokens.get(1).type != TokenType.IDENTIFIER) return false;
        if (tokens.get(2).type != TokenType.PUNCTUATION || !tokens.get(2).value.equals("(")) return false;
        if (tokens.get(3).type != TokenType.PUNCTUATION || !tokens.get(3).value.equals(")")) return false;
        if (tokens.get(4).type != TokenType.PUNCTUATION || !tokens.get(4).value.equals("{")) return false;
        if (tokens.get(5).type != TokenType.PUNCTUATION || !tokens.get(5).value.equals("}")) return false;

        return true;
    }

    public static void main(String[] args) {
        List<Token> validDeclaration = new ArrayList<>();
        validDeclaration.add(new Token(TokenType.KEYWORD, "int"));
        validDeclaration.add(new Token(TokenType.IDENTIFIER, "x"));
        validDeclaration.add(new Token(TokenType.OPERATOR, "="));
        validDeclaration.add(new Token(TokenType.LITERAL, "5"));
        validDeclaration.add(new Token(TokenType.PUNCTUATION, ";"));

        List<Token> validFunction = new ArrayList<>();
        validFunction.add(new Token(TokenType.KEYWORD, "int"));
        validFunction.add(new Token(TokenType.IDENTIFIER, "main"));
        validFunction.add(new Token(TokenType.PUNCTUATION, "("));
        validFunction.add(new Token(TokenType.PUNCTUATION, ")"));
        validFunction.add(new Token(TokenType.PUNCTUATION, "{"));
        validFunction.add(new Token(TokenType.PUNCTUATION, "}"));

        System.out.println("Valid Declaration: " + analyzeExpression(validDeclaration));
        System.out.println("Valid Function: " + parseFunctionDeclaration(validFunction));
    }
}
