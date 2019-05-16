package core;

/**
 * Class to represent mathematical expressions. Some examples: - 3*x^4-x*log(512*x+1)-pi^x |
 * 4*x-sin(pi)-e^(x-1) | 5*x^3-2*x^2+4*x-2
 *
 * Keywords: - The available operators are: +, -, *, /, ^. - The available
 * math functions are: log, ln, sqrt, sin, cos, tan. - The available numbers
 * are: pi, e.
 *
 * @author dpf
 */
public class Expression extends GrammarParser<Double>{

	// parenthesis
	public static final char PAR_O = '(';
	public static final char PAR_C = ')';

	// Operators
	public static final char SUM = '+';
	public static final char MIN = '-';
	public static final char MULT = '*';
	public static final char DIV = '/';
	public static final char POW = '^';
	public static final char FACTORIAL = '!';

	// Known functions
	public static final String LOG = "log";
	public static final String LN = "ln";
	public static final String SQRT = "sqrt";
	public static final String SIN = "sin";
	public static final String COS = "cos";
	public static final String TAN = "tan";

	// Known numbers
	public static final String PI = "pi";
	public static final String E = "e";
	
	private String expression;
	
	/**
	 * Check the class documentation for info.
	 *
	 * @param expr
	 *            String with the formatted function.
	 */
	public Expression(String expr) {
		this.expression = expr;
		expr = expr.replaceAll(" ", "");
		if(!expr.matches(RegEx.EXPRESSION)) {
			throwFormatErrorException(expr);
		}
		this.str = expr;
	}
	
	@Override
	public String toString() {
		return expression;
	}	
	
	@Override
	public Double parse() {
		init();
		double x = parseExpression();
		if (pos < str.length()) {
			throwSyntaxErrorException((char) ch);
		}
		return x;
	}
	
	/*
	 * GRAMMAR 
	 * 
	 * expression : 
	 * term `+` term | 
	 * term `-` term |
	 * term 
	 * 
	 * term : 
	 * factor `*` factor | 
	 * factor `/` factor |
	 * factor
	 * 
	 * factor : 
	 * (
	 * `+` factor | 
	 * `-` factor |
	 * `(` expression `)` | 
	 * function |
	 * DOUBLE |
	 * ERROR
	 * )
	 * (
	 * `!` |
	 * `^` factor
	 * )?
	 * 
	 * function :
	 * variable |
	 * knownnumber | 
	 * knownfunction `(` expression `)` |
	 * ERROR
	 * 
	 */
	
	/**
	 * expression :
	 */
	private double parseExpression() {
		double x = parseTerm();
		while (true) {
			if (eat(SUM)) { // term `+` term
				x += parseTerm();
			} else if (eat(MIN)) { // term `-` term 
				x -= parseTerm();
			} else { // term 
				return x;
			}
		}
	}

	/**
	 * term :
	 */
	private double parseTerm() {
		double x = parseFactor();
		while (true) {
			if (eat(MULT)) { // factor `*` factor 
				x *= parseFactor();
			} else if (eat(DIV)) { // factor `/` factor
				x /= parseFactor();
			} else { // factor
				return x;
			}
		}
	}

	/**
	 * factor :
	 */
	private double parseFactor() {
		double x=0;
		int startPos = this.pos;
		// (
		if (eat(SUM)) { // `+` factor
			x = parseFactor();
		} else if (eat(MIN)) { // `-` factor
			x = -parseFactor();
		} else if (eat(PAR_O)) { // `(` expression `)` 
			x = parseExpression();
			if(!eat(PAR_C)) {
				throwParenthesisErrorException();
			}			
		} else if (ch >= 'a' && ch <= 'z') { // function
			x = parseFunction();
		} else if ((ch >= '0' && ch <= '9') || ch == '.') { // DOUBLE
			while ((ch >= '0' && ch <= '9') || ch == '.') {
				nextChar();
			}
			x = Double.parseDouble(str.substring(startPos, pos));
		} else { // ERROR
			throwSyntaxErrorException((char) ch);
		}
		// )
		// (
		if (eat(FACTORIAL)) { // `!`
			long x_ = 1;
			for (long i = (long) x; i > 1; i--) {
				x_ *= i;
			}
			x = (double) x_;	
		} else if (eat(POW)) { // `^` factor
			x = Math.pow(x, parseFactor());
		} 
		// )?
		return x;
	}
	
	/**
	 * function :
	 */
	private double parseFunction() {
		int startPos = pos;
		nextChar();
		while(pos<str.length() && str.substring(startPos, pos+1).matches(RegEx.VARIABLE)) {
			nextChar();
		}
		String function = str.substring(startPos, pos);
		
		
		if(function.matches(RegEx.VARIABLE)) { // variable
			Expression me = Memory.TABLE.get(function);
			if(me != null) {
				double value = me.parse();
				for(String error: me.getErrorMsg()) {
					addError(error);
				}
				return value;
			}
		}
		
		switch (function) { // knownnumber
		case PI:
			return Math.PI;
		case E:
			return Math.E;
		}
		
		double x = 0;		
		if (eat(PAR_O)) { // knownfunction `(` expression `)` 
			x = parseExpression();
			if(!eat(PAR_C)) {
				throwParenthesisErrorException();
			}
		}
		switch (function) {		
		case SQRT:
			x = Math.sqrt(x);
			break;
		case SIN:
			x = Math.sin(Math.toRadians(x));
			break;
		case COS:
			x = Math.cos(Math.toRadians(x));
			break;
		case TAN:
			x = Math.tan(Math.toRadians(x));
			break;
		case LOG:
			x = Math.log10(x);
			break;
		case LN:
			x = Math.log(x);
			break;
		default: // ERROR
			throwSyntaxErrorException(function);
		}
		return x;
	}
	
	private void throwSyntaxErrorException(String function) {
		addError("Unknown function: " + function);
	}

	private void throwSyntaxErrorException(char ch) {
		addError("Unexpected: " + ch);
	}
	
	private void throwParenthesisErrorException() {
		addError("Parenthesis not closed");
	}
	
	private void throwFormatErrorException(String function) {
		addError("Unrecognized function format: " + function);
	}
	
}
