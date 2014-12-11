package fj
package data


object EqualInstances {

  implicit val equalLens: Equal[Lens[Int, Int]] =
    Equal.equal[Lens[Int, Int]]((a1: Lens[Int, Int]) ⇒ (a2: Lens[Int, Int]) ⇒
      a1.get(0) == a2.get(0): java.lang.Boolean)

  implicit val equalBoolean: Equal[java.lang.Boolean] =
    Equal.booleanEqual

  implicit val equalInt: Equal[Int] =
    Equal.intEqual.comap[Int]((x: Int) ⇒ x: java.lang.Integer)

  implicit val equalString: Equal[String] =
    Equal.stringEqual

  implicit val equalUnit: Equal[Unit] =
    Equal.equal[Unit]((u1: Unit) ⇒ (u2: Unit) ⇒ true: java.lang.Boolean)

  implicit def equalP2[A: Equal, B: Equal]: Equal[P2[A, B]] =
    Equal.p2Equal(implicitly, implicitly)

  implicit def equalEither[A: Equal, B: Equal]: Equal[Either[A, B]] =
    Equal.eitherEqual(implicitly, implicitly)

  implicit def equalOption[A: Equal]: Equal[Option[A]] =
    Equal.optionEqual(implicitly)

  implicit def equalSet[A: Equal]: Equal[Set[A]] =
    Equal.setEqual(implicitly)

  implicit def equalTreeMap[K: Ord, V: Equal]: Equal[TreeMap[K, V]] =
    Equal.listEqual(Equal.p2Equal[K, V](implicitly[Ord[K]].equal(), implicitly))
      .comap[TreeMap[K, V]]((m: TreeMap[K, V]) ⇒ map2list(m))

  private def map2list[K, V](m: TreeMap[K, V]) = {
    val buffer = List.Buffer.empty[P2[K, V]]()
    val iter = m.iterator()
    while(iter.hasNext)
      buffer.snoc(iter.next())
    buffer.toList
  }
}
