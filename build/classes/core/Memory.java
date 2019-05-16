
package core;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author dpf
 */
public class Memory {
	
	public static final Memory TABLE = new Memory();
	
	private final Map<String, Expression> memTable;

	private Memory() {
		memTable = new HashMap<>();
	}
	
	public void addEntry(String alias, String value) {
		if(alias.matches(RegEx.VARIABLE)) {
			memTable.put(alias, new Expression(value));
		}else {
			throw new SyntaxErrorException(alias);
		}
	}
	
	public Expression get(String alias) {
		return memTable.get(alias);
	}
	
	@SuppressWarnings("serial")
	private class SyntaxErrorException extends RuntimeException {

		private SyntaxErrorException(String alias) {
			super("Error during initializating " + alias);
		}

	}	
	
}
