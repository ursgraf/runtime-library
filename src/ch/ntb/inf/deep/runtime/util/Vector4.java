// TODO @Adam License, JavaDoc
package ch.ntb.inf.deep.runtime.util;

public class Vector4 extends Vector
{
	public Vector4() { super(4); }
	
	public Vector4(double x, double y, double z, double s)
	{
		super(4);
		set(x, y, z, s);
	}
	
	public void set(double x, double y, double z, double s)
	{
		set(1, x);
		set(2, y);
		set(3, z);
		set(4, s);
	}
}
