import lexical.LexicalAnalysis;
import lexical.TokenType;
import interpreter.command.Command;
import lexical.Lexeme;
import syntatic.SyntaticAnalysis;

public class MiniGroovyi {

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java miniGroovy [source file]");
			return;
		}

		try (LexicalAnalysis l = new LexicalAnalysis(args[0])) {
			//Lexeme lex;
			//do {
			//	lex = l.nextToken();
			//	System.out.println(lex);
				
			//	} while (!lex.type.equals(TokenType.END_OF_FILE));
			SyntaticAnalysis s = new SyntaticAnalysis(l);
			Command c = s.start();
			c.execute();
			
		} catch (Exception e) {
			System.err.println("Internal error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
