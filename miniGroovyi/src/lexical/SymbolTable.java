package lexical;

import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

	private Map<String, TokenType> st;

	public SymbolTable() {
		st = new HashMap<String, TokenType>();

		// Symbols
		st.put(";", TokenType.SEMICOLON);
		st.put("=", TokenType.ASSIGN);
        st.put("{", TokenType.OPEN_CURLY_BRACES);
        st.put("}",TokenType.CLOSE_CURLY_BRACES);
        st.put("(",TokenType.OPEN_PARENTHESES);
        st.put(")",TokenType.CLOSE_PARENTHESES);
        st.put("[",TokenType.OPEN_BRACKETS);
        st.put("]",TokenType.CLOSE_BRACKETS);
        st.put(":",TokenType.COLON);
        st.put("'",TokenType.PRIME);
        st.put("\"",TokenType.DOUBLE_PRIME);
        st.put(",",TokenType.COMMA);
		st.put(".",TokenType.DOT);
		st.put("->",TokenType.RIGTH_ARROW);

		// Logic operators
		st.put("==", TokenType.EQUAL);
		st.put("!=", TokenType.NOT_EQUAL);
		st.put("<", TokenType.LOWER);
		st.put("<=", TokenType.LOWER_EQUAL);
		st.put(">", TokenType.GREATER);
		st.put(">=", TokenType.GREATER_EQUAL);
        st.put("!",TokenType.NOT);
		st.put("&&",TokenType.AND);
		st.put("||",TokenType.OR);
		st.put("in",TokenType.CONTAIN);
		st.put("!in",TokenType.NOT_CONTAIN);
		st.put("as",TokenType.AS);

		// Arithmetic operators
		st.put("+", TokenType.ADD);
		st.put("-", TokenType.SUB);
		st.put("*", TokenType.MUL);
		st.put("/", TokenType.DIV);
		st.put("%", TokenType.MOD);
		st.put("**",TokenType.POWER);
        st.put("+=",TokenType.ASSING_ADD);
		st.put("-=",TokenType.SUB);
		st.put("*=",TokenType.ASSING_MUL);
		st.put("/=",TokenType.ASSING_DIV);
		st.put("%=",TokenType.ASSING_MOD);
		st.put("*=",TokenType.ASSING_POWER);

		// Keywords
		st.put("def",TokenType.DEF);
		st.put("while", TokenType.WHILE);
		st.put("do", TokenType.DO);
		st.put("for",TokenType.FOR);
		st.put("done", TokenType.DONE);
		st.put("if", TokenType.IF);
		st.put("then", TokenType.THEN);
		st.put("else", TokenType.ELSE);
		st.put("output", TokenType.OUTPUT);
		st.put("true", TokenType.TRUE);
		st.put("false", TokenType.FALSE);
		st.put("read", TokenType.READ);
		st.put("not", TokenType.NOT);
        st.put("size",TokenType.SIZE);
        st.put("keys",TokenType.KEYS);
        st.put("switch",TokenType.SWITCH);
        st.put("in",TokenType.IN);
        st.put("foreach",TokenType.FOREACH);
        st.put("case",TokenType.CASE);
        st.put("println",TokenType.PRINTLN);
		st.put("print",TokenType.PRINT);
		st.put("Boolean",TokenType.BOOLEAN);
		st.put("Integer",TokenType.INTEGER);
		st.put("String",TokenType.STRING);
		st.put("null",TokenType.NULL);
		st.put("empty",TokenType.EMPTY);
		st.put("values",TokenType.VALUES);
		st.put("default",TokenType.DEFAULT);





		//Outhers
	}

	public boolean contains(String token) {
		return st.containsKey(token);
	}

	public TokenType find(String token) {
		return this.contains(token) ?
					st.get(token) : TokenType.VAR;
	}
}