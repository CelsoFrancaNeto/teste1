package interpreter.command;

import interpreter.expr.Expr;

public class IfCommand extends Command {

    private Expr expr;
    private Command thenCmds;
    private Command elseCmds;

	protected IfCommand(int line, Expr expr, Command thenCmds) {
		super(line);
		
        this.expr = expr;
        this.thenCmds = thenCmds;
	}

    public void setElseCommands(Command elseCmds){
        // TODO
    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}