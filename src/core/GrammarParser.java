package core;

import java.util.ArrayList;
import java.util.List;

public abstract class GrammarParser<T> {
	
	protected String str;
	protected int pos, ch;
	
	public abstract T parse();
	private List<String> errormsg;
	
	protected void init() {
		this.errormsg = new ArrayList<>();
		pos = -1;
		nextChar();
	}
	
	protected final void nextChar() {
		ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	}

	protected final boolean eat(int charToEat) {
		if (ch == charToEat) {
			nextChar();
			return true;
		}
		return false;
	}

	protected final void addError(String error) {
		errormsg.add(error);
	}
	
	public String[] getErrorMsg() {
		String[] err = new String[errormsg.size()];
		for(int i = 0; i<errormsg.size(); i++) {
			err[i] = errormsg.get(i);
		}
		return err;
	}
	
}
