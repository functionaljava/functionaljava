
package object fj {
  implicit def Function1F[A, B](g: A => B): F[A, B] = new F[A, B] {
    def apply(a: A) = g(a)
  }

  implicit def Function2F[A, B, C](g: (A, B) => C): F[A, F[B, C]] = new F[A, F[B, C]] {
    def apply(a: A) = new F[B, C] {
      def apply(b: B) = g(a, b)
    }
  }
}