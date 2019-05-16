
package app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import core.Interpreter;

/**
 *
 * @author dpf
 */
public class Funciones {
	
	private static final boolean READ_FILE = false;
	private static final String INPUT_FILE= "test.txt";

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		if(READ_FILE) {
			args = new String[1];
			args[0] = INPUT_FILE;
		}
		
		if(args.length>0) {
			FileReader in = null;
			try {
				in = new FileReader(args[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		    BufferedReader br = new BufferedReader(in);
			new Interpreter(br).run();
		}else {
			new Interpreter(new BufferedReader(new InputStreamReader(System.in))).run();
		}
		
	}
	
}
