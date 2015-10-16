package fj
package data

import Equal.{listEqual, anyEqual}
import Unit.unit
import fj.Show.{listShow, anyShow}
import fj.data.IOFunctions._
import fj.data.Iteratee.IterV
import java.io.{File, BufferedWriter, OutputStreamWriter, FileOutputStream}
import java.nio.charset.Charset
import java.nio.charset.Charset.{availableCharsets, defaultCharset}
import org.scalacheck.Prop._
import org.scalacheck.Gen
import org.scalacheck.Properties
import org.scalacheck.Arbitrary
import ArbitraryList.arbitraryList
import ArbitraryOption.arbitraryOption
import java.lang.Character
import scala.{Array => SArray}

/**
 * Checks for {@link IO}.
 * 
 * @author Martin Grotzke
 */
object CheckIO extends Properties("IO") {

  // implicit conversion of the j.u.Collection did not work properly, thus it written manually...
  implicit def charsets: Arbitrary[Charset] = Arbitrary(Gen.oneOf(availableCharsets().values().toArray(scala.Array[Charset]()).toList.filter(
     cs => cs.name.contains("ISO-8") || cs.name.contains("UTF")
    )))

  property("enumFileLines") = forAll((a: List[Int], c: Option[Charset]) =>
    withFileContent(c, a) {
      (f) =>
      val actual: List[Int] = enumFileLines(f, c, IterV.list()).run().run().reverse().map[Int]((x: String) => java.lang.Integer.parseInt(x))
      listEqual(anyEqual[Int]).eq(actual, a) :| wrongResult(listShow(anyShow[Int]).showS(actual), listShow(anyShow[Int]).showS(a))
    })

  property("enumFileCharChunks") = forAll((a: List[Int], c: Option[Charset]) =>
    withFileContent(c, a) {
      (f) =>
      val actual: List[SArray[Char]] = enumFileCharChunks(f, c, IterV.list[SArray[Char]]()).run().run().reverse()
      (joinAsString(actual) == toStringWithNewLines(a)) :| wrongResult(joinAsString(actual), toStringWithNewLines(a))
    })

  property("enumFileChars") = forAll((a: List[Int], c: Option[Charset]) =>
    withFileContent(c, a) {
      (f) =>
      val actual: List[Character] = enumFileChars(f, c, IterV.list[Character]()).run().run().reverse()
      (List.asString(actual) == toStringWithNewLines(a)) :| wrongResult(List.asString(actual), toStringWithNewLines(a))
    })
    
  private def wrongResult(actual: String, expected: String): String = {
    "Wrong result:\n>>>\n" + actual + "\n===\nExpected:\n"+ expected +"\n<<<"
  }
  
  private def withFileContent[E, A](fileEncoding: Option[Charset], lines: List[E])(f: File => A): A = {
    val file = writeTmpFile("tmpFile", lines, fileEncoding)
    try {
      f(file)
    } finally {
      file.delete
    }
  }
  
  private def writeTmpFile[E](name: String, lines: List[E], fileEncoding: Option[Charset]): File = {
    val result = File.createTempFile(name, ".tmp")
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), fileEncoding.orSome(defaultCharset)))
    writer.write(toStringWithNewLines(lines))
    writer.close()
    result
  }
  
  private def toStringWithNewLines[E](lines: List[E]): String = {
    lines.foldLeft({
      (sb: StringBuilder, line: E) =>
        if(sb.length > 0) sb.append('\n')
        sb.append(line)
      }, new StringBuilder).toString
  }
  
  private def joinAsString(charChunks: List[SArray[Char]]): String = {
    charChunks.foldLeft({
      (sb: StringBuilder, chunk: SArray[Char]) =>
        sb.appendAll(chunk)
      }, new StringBuilder).toString
  }
    
}
