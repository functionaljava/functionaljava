import fj.Equal
import fj.data.{Option, List}
package object fj {
  implicit def Function1F[A, B](g: A => B): F[A, B] = new F[A, B] {
    def f(a: A) = g(a)
  }

  implicit def Function2F[A, B, C](g: (A, B) => C): F[A, F[B, C]] = new F[A, F[B, C]] {
    def f(a: A) = new F[B, C] {
      def f(b: B) = g(a, b)
    }
  }
  
  implicit def stringEqual: Equal[String] = Equal.stringEqual
  
  implicit def intEqual: Equal[Int] = Equal.equal({(i1:Int, i2:Int) => Boolean.box((i1 == i2))})

  implicit def unitEqual: Equal[Unit] = Equal.anyEqual()

  implicit def listEqual[A](implicit aEq: Equal[A]): Equal[List[A]] = Equal.listEqual(aEq)

  implicit def optionEqual[A](implicit aEq: Equal[A]): Equal[Option[A]] = Equal.optionEqual(aEq)

  implicit def p2Equal[A, B](implicit aEq: Equal[A], bEq: Equal[B]): Equal[P2[A, B]] = Equal.p2Equal(aEq, bEq)
  
}