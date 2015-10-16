package fj
package control
package parallel

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.const
import Strategy.executorStrategy
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent._

object ArbitraryStrategy {
  private val executor = newFixedThreadPool(2, new ThreadFactory {
    val default = Executors.defaultThreadFactory
    override def newThread(r: Runnable) = {
      val t = default.newThread(r)
      t setDaemon true
      t
    }
  })

  implicit def arbitraryStrategy[A]: Arbitrary[Strategy[A]] =
    Arbitrary(const(executorStrategy[A](executor)))
}
