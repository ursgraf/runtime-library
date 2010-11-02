package java.lang;


/**
 * Class which handles special integer operations.
 * 
 * @author simon.pertschy@ntb.ch
 * Creation: October 2009
 *
 */
public class Integer {

private static char str[] = new char[11];
	
	/**
	 * Converts a <code>integer</code> to a String.
	 * @param val the <code>integer</code> to convert.
	 * @return the converted <code>integer</code>
	 */
	public static String toString(int val){
		
		int i = str.length;
		boolean neg = false;
		if(val < 0){
			neg = true;
			val = -val;
		}
		do{
			str[--i] = (char) ((val % 10) + '0');
			val /= 10;
		}while(val > 0);
		if(neg) str[--i] = '-';
		return new String(str, i, str.length - i);
	}
	
	/**
	 * Converts <code>integer</code> to a char array.
	 * @param string the <code>char</code> array who represents the string.
	 * @param off the start position of the <code>char</code> array.
	 * @param val the <code>integer</code> to convert.
	 * @return the length of the string
	 */
	public static int toCharArray(char string[], int off, int val){
		int i = str.length;
		boolean neg = false;
		if(val < 0){
			neg = true;
			val = -val;
		}
		do{
			str[--i] = (char) ((val % 10) + '0');
			val /= 10;
		}while(val > 0);
		if(neg) str[--i] = '-';
		int len = str.length -i;
		for(int j = 0; j < len; j++){
			string[j + off] = str[j+i];
		}	
		return len + off;
	}
	
//	public static void printf(char ... s){
//		int i = s.length;
//		int b = s[0];
//	}
	
}
