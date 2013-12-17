// TODO @Adam License, JavaDoc
package ch.ntb.inf.deep.runtime.util;

public class Vector3 extends Vector
{
	public Vector3() { super(3); }
	
	public Vector3(double x, double y, double z)
	{
		super(3);
		set(x, y, z);
	}
	
	public void set(double x, double y, double z)
	{
		set(1, x);
		set(2, y);
		set(3, z);
	}
}
