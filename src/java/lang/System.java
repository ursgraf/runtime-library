package java.lang;

public class System {

	private System() {

	}
	
//	public static void arraycopy(Object src, int srcPos, Object dest,
//			int destPos, int length) {
//		System.arraycopy(src, srcPos, dest, destPos, length);
//	}

	public static void chararraycopy(char[] src, int srcPos, char[] dest,
			int destPos, int length) {

		if (src.length - 1 >= srcPos && src.length >= srcPos + length
				&& dest.length - 1 >= destPos
				&& dest.length >= destPos + length) {
			for (int i = 0; i < length; i++) {
				dest[destPos + i] = src[srcPos + i];
			}
		}
	}
	
	public static void test(int[] c) {
		
	}
}
