
package object fj {
  implicit def Function1F[A, B](g: A => B): F[A, B] = new F[A, B] {
    def f(a: A) = g(a)
  }

  implicit def Function1CF[A, B, C](g: A => B => C): F[A, F[B, C]] = new F[A, F[B, C]] {
    def f(a: A): F[B, C] = new F[B, C] {
      def f(b: B): C = g(a)(b)
    }
  }

  implicit def Function2F[A, B, C](g: (A, B) => C): F[A, F[B, C]] = new F[A, F[B, C]] {
    def f(a: A) = new F[B, C] {
      def f(b: B) = g(a, b)
    }
  }
}
