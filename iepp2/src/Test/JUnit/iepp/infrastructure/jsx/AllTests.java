/*Cree par GHILES Damien
 * ----------------------
 * 
 * Edite par GHILES Damien & BOUCHIKHI Mohamed-Amine
 */

package Test.JUnit.iepp.infrastructure.jsx;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for iepp.infrastructure.jsx");
		//$JUnit-BEGIN$
		suite.addTestSuite(ChargeurDPTest.class);
		//$JUnit-END$
		return suite;
	}

}
