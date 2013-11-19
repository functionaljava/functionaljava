package fj
package control
package parallel

import control._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen.value
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
    Arbitrary(value(executorStrategy[A](executor)))
}
