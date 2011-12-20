/**
 * 
 */
package eu.scape_project.core.test.helpers;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * A test helper class that tests the overloaded <code>Object.equals(Object)</code>, &amp;
 * <code>Object.hashCode()</code> methods for any class.
 * <p/>
 * Specifically the class will test that Object.equals() is:
 * <ul>
 * <li>Reflexive: for any non-null reference value x, {@code x.equals(x) == true}</li>
 * <li>Symmetric: for any non-null reference values x and y {@code x.equals(y) == true} if and only if
 * {@code y.equals(x) == true}</li>
 * <li>Transitive: for any non-null reference values x, y, and z
 * {@code if (x.equals(y) && y.equals(z)) then x.equals(z)}</li>
 * <li>Consistent: for any non-null reference values x and y {@code x.equals(y)} consistently returns true, or
 * consistently returns false, provided no information used in equals comparisons on the objects is modified.</li>
 * <li>For any non-null reference value x, {@code x.equals(null)} must return false.</li>
 * </ul>
 * It will also test that hashCode() is:
 * <ul>
 * <li>Self Consistent: for a non-null reference value x, {@code x.hashCode() == x.hashCode()}, provided no information
 * used in equals comparisons on the objects is modified.</li>
 * <li>Consistent with equals: for any non-null reference values x and y
 * {@code if (x.equals(y)) then x.hashCode() == y.hashCode()}</li>
 * </ul>
 * 
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a> <a
 *         href="http://sourceforge.net/users/carlwilson-bl">carlwilson-bl AT SourceForge</a> <a
 *         href="https://github.com/carlwilson-bl">carlwilson-bl AT github</a>
 * @version 0.1
 * 
 *          Created Dec 20, 2011:12:31:11 PM
 */
@Ignore
@RunWith(Theories.class)
public abstract class ObjectEqualityHashHelper {
	/**
	 * Reflexive: for a non-null reference value x, {@code x.equals(x) == true}
	 * 
	 * @param x
	 *            the object to test
	 */
	@Theory
	public void equalsIsReflexive(Object x) {
		assumeThat(x, is(not(equalTo(null))));
		assertThat(x.equals(x), is(true));
	}

	/**
	 * Symmetric: for any non-null reference values x and y {@code x.equals(y) == true} if and only if
	 * {@code y.equals(x) == true}
	 * 
	 * @param x
	 *            reference value x for test
	 * @param y
	 *            reference value y for test
	 */
	@Theory
	public void equalsIsSymmetric(Object x, Object y) {
		assumeThat(x, is(not(equalTo(null))));
		assumeThat(y, is(not(equalTo(null))));
		assertThat(x.equals(y) == y.equals(x), is(true));
	}

	/**
	 * Transitive: for any non-null reference values x, y, and z
	 * {@code if (x.equals(y) && y.equals(z)) then x.equals(z)}
	 * 
	 * @param x
	 *            reference value x for test
	 * @param y
	 *            reference value y for test
	 * @param z
	 *            reference value z for test
	 */
	@Theory
	public void equalsIsTransitive(Object x, Object y, Object z) {
		assumeThat(x, is(not(equalTo(null))));
		assumeThat(y, is(not(equalTo(null))));
		assumeThat(z, is(not(equalTo(null))));
		assumeThat(x.equals(y) && y.equals(z), is(true));
		assertThat(z.equals(x), is(true));
	}

	/**
	 * Consistent: for any non-null reference values x and y {@code x.equals(y)} consistently returns true, or
	 * consistently returns false, provided no information used in equals comparisons on the objects is modified.
	 * 
	 * @param x
	 *            reference value x for test
	 * @param y
	 *            reference value y for test
	 */
	@Theory
	public void equalsIsConsistent(Object x, Object y) {
		assumeThat(x, is(not(equalTo(null))));
		boolean alwaysTheSame = x.equals(y);

		for (int i = 0; i < 30; i++) {
			assertThat(x.equals(y), is(alwaysTheSame));
		}
	}

	/**
	 * For any non-null reference value x, {@code x.equals(null)} must return false.
	 * 
	 * @param x
	 *            reference value x for test
	 */
	@Theory
	public void equalsReturnFalseOnNull(Object x) {
		assumeThat(x, is(not(equalTo(null))));
		assertThat(x.equals(null), is(false));
	}

	/**
	 * Self Consistent: for a non-null reference value x, {@code x.hashCode() == x.hashCode()}, provided no
	 * information used in equals comparisons on the objects is modified.
	 * 
	 * @param x
	 *            reference value x for test
	 */
	@Theory
	public void hashCodeIsSelfConsistent(Object x) {
		assumeThat(x, is(not(equalTo(null))));
		int alwaysTheSame = x.hashCode();

		for (int i = 0; i < 30; i++) {
			assertThat(x.hashCode(), is(alwaysTheSame));
		}
	}

	/**
	 * Consistent with equals: for any non-null reference values x and y {@code if (x.equals(y)) then x.hashCode() == y.hashCode()}
	 * 
	 * @param x
	 *            reference value x for test
	 * @param y
	 *            reference value y for test
	 */
	@Theory
	public void hashCodeIsConsistentWithEquals(Object x, Object y) {
		assumeThat(x, is(not(equalTo(null))));
		assumeThat(x.equals(y), is(true));
		assertThat(x.hashCode(), is(equalTo(y.hashCode())));
	}
}
