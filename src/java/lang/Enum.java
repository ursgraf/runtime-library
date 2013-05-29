package java.lang;

import java.io.Serializable;

public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
    private final int ordinal;

    /**
     * Sole constructor.  Programmers cannot invoke this constructor.
     * It is for use by code emitted by the compiler in response to
     * enum type declarations.
     *
     * @param name - The name of this enum constant, which is the identifier
     *               used to declare it.
     * @param ordinal - The ordinal of this enumeration constant (its position
     *         in the enum declaration, where the initial constant is assigned
     *         an ordinal of zero).
     */
    protected Enum(String name, int ordinal) {
    	this.name = name;
    	this.ordinal = ordinal;
    }

    /**
     * Returns the name of this enum constant, exactly as declared in its
     * enum declaration.
     * 
     * @return the name of this enum constant
     */
    public final String name() {
    	return name;
    }

    /**
     * Returns the ordinal of this enumeration constant (its position
     * in its enum declaration, where the initial constant is assigned
     * an ordinal of zero).
     *
     * @return the ordinal of this enumeration constant
     */
    public final int ordinal() {
    	return ordinal;
    }

	  /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    public String toString() {
    	return name;
    }

    /**
     * Returns true if the specified object is equal to this
     * enum constant.
     *
     * @param other the object to be compared for equality with this object.
     * @return  true if the specified object is equal to this
     *          enum constant.
     */
    public final boolean equals(Object other) { 
        return this==other;
    }

    /**
     * Compares this enum with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 
     * Enum constants are only comparable to other enum constants of the
     * same enum type.  The natural order implemented by this
     * method is the order in which the constants are declared.
     */
    public final int compareTo(E o) {
    	Enum<?> other = (Enum<?>)o;
    	Enum<E> self = this;
    	return self.ordinal - other.ordinal;
    }

    /**
     * Returns the enum constant of the specified enum type with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in this type. 
     *
     * @param enumType the <tt>Class</tt> object of the enum type from which
     *      to return a constant
     * @param name the name of the constant to return
     * @return the enum constant of the specified enum type with the
     *      specified name
     * @throws IllegalArgumentException if the specified enum type has
     *         no constant with the specified name, or the specified
     *         class object does not represent an enum type
     * @throws NullPointerException if <tt>enumType</tt> or <tt>name</tt>
     *         is null
     * @since 1.5
     */
        public static <T extends Enum<T>> T valueOf(T[] enumValues, String name) {
    	for (T val : enumValues) {
       		if (val.name().equals(name)) return val;
    	}
		return null;
    }

}
