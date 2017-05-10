package fj

object Tests {
  def tests = List(
    CheckP2.properties,
    CheckSemigroup.properties,
    fj.data.CheckArray.properties,
    fj.data.CheckIO.properties,
    fj.data.CheckIteratee.properties,
    fj.data.CheckList.properties,
    fj.data.CheckStream.properties,
    fj.data.CheckOption.properties,
    fj.data.CheckTree.properties,
    fj.data.CheckHashMap.properties,
    fj.data.CheckHashSet.properties,
    fj.data.CheckSet.properties,
    fj.control.parallel.CheckStrategy.properties,
    fj.control.parallel.CheckParModule.properties
  ).flatten

  def main(args: Array[String]) {
    run(tests)
    //    System.exit(0)
  }

  import org.scalacheck.Test.check
  import org.scalacheck.{Prop, Test}

  def run(tests: List[(String, Prop)]) =
    tests foreach { case (name, p) => {
      val c = check(new Test.Parameters.Default {
        override val maxSize = 20
      }, p)
      c.status match {
        case Test.Passed => println("Passed " + name)
        case Test.Proved(_) => println("Proved " + name)
        case f@Test.Failed(_, _) => sys.error(name + ": " + f)
        case Test.Exhausted => println("Exhausted " + name)
        case f@Test.PropException(_, e, _) => {
          e.printStackTrace
          sys.error(name + ": " + f)
        }
      }
    }
    }
}
