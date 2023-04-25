package fj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created with IntelliJ IDEA.
 * User: MarkPerry
 * Date: 19/11/13
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionalJavaJUnitTest {

  @Test
  void runScalacheckTests() {
//		System.out.println("Hello world");
    Assertions.assertTrue(true);
//		new Tests$().main(null);
    Tests.main(null);
  }

}
