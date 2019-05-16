
package core;

import java.io.BufferedReader;
import java.io.IOException;

public class Interpreter extends GrammarParser<String> implements Runnable {
	
	public static final char ASSIGN = '=';
	public static final char LINE_COMMNENT = '#';
	
	public static final String OP_EXIT = "EXIT";
	public static final String OP_HELP = "HELP";
	public static final String OP_HELP_TEXT = "Help text";
	
	private long timer;
	private int linec;
	private String variable;
	private final BufferedReader br;
	
	public Interpreter(BufferedReader br) {
		this.br = br;
	}
	
	@Override
	public void run() {
		timer = System.currentTimeMillis();
		String expression;
		try {
			str = br.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(str != null) {
			str = str.replaceAll(" ", "");
			if(!"".equals(str)) {
				expression = parse();				
				if("".equals(expression)) { // comment
					// Do nothing
				} else if("".equals(variable)) {
					if(expression.equals(OP_EXIT.toUpperCase())) { // EXIT
						break;
					} else if(expression.equals(OP_HELP.toUpperCase())) { // HELP
						System.out.println(OP_HELP_TEXT);
					} else if(expression.matches(RegEx.EXPRESSION)) { // expression
						Expression expr = new Expression(expression);
						double value = expr.parse();
						if(expr.getErrorMsg().length>0) {
							for(String error: expr.getErrorMsg()) {
								System.err.println(error);
							}
						}else {
							System.out.println(value);
						}
					} else { // ERROR
						throwSyntaxErrorException();
					}					
				} else {
					if(variable.matches(RegEx.VARIABLE) && expression.matches(RegEx.EXPRESSION)) { // variable `=` expression
						Memory.TABLE.addEntry(variable, expression);
					}
				}
				
			}
			try {
				str = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			linec++;
		}
		double time = (System.currentTimeMillis()-timer);
		time = time/1000;
		System.out.println("EXECUTION FINNISHED ("+time+" s).");
	}


	@Override
	public String parse() {
		variable = "";
		init();
		return parseLine();
	}
	
	/*
	 * GRAMMAR
	 * 
	 * line : 
	 * `#` comment | 
	 * variable `=` expression |
	 * expression |
	 * ERROR
	 * 
	 */
	
	private String parseLine() {
		
		if(eat(LINE_COMMNENT)) { // `#` comment
			return "";
		}
		
		int startPos = pos;
		while (pos<str.length()) {
			if(eat(ASSIGN)) { //  variable `=` expression
				variable = str.substring(startPos, pos-1);
				startPos = pos;
			}
			nextChar();
		} // expression
		if(pos>str.length()) {
			pos=str.length();
		}
		return str.substring(startPos, pos);
	}
	
	private void throwSyntaxErrorException() {
		throwError("Error in line "+linec+": "+str);	
	}
	
	public static void throwError(String  msg) {
		System.err.println(msg);
	}
	
}
