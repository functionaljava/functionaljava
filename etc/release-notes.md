
Release Notes
=============

4.3
===
Target date: To be decided

* Enhancements
    * To be decided
* Fixes
    * To be decided

4.2
===
Released: 20 December, 2014

* Enhancements
    * Added Java 8 examples
    * Added new website
    * Added Option.none_()
    * Gradle 2.2.1 support with wrapper
    * Added to Strings: isNullOrEmpty, isNullOrBlank and isNotNullOrBlank
    * Added Try with arity 0-8 for lambdas that throw exceptions
    * Added Effect with arity 0-8 for lambdas with a void return
    * Added TryEffect with arity 0-8 for lambdas with a void return that throw an Exception.
    * Conversion from Java 8 types to Functional Java types and vice-versa
    * Added monadic IO methods
    * Support instantiation of IO with lambdas
    * Added first class cons_ for List
    * Added partial application of first parameter for F 1-8
    * Added SafeIO that encodes IOExceptions into Validations
    * Added simple RNG
    * Added Reader, Writer and State
    * Deprecated $._
    * Added Array.scan family of methods
    * Support instantiating P1 values using lambdas to P.lazy
    * Added toString for Validation, P arities 1 to 8, Either
    * Added vending machine demo
    * Added Option.toValidation
    * Added map and contramap for F and F2.
    * Added partial application for F1.
    * Added filter and uncurried foldLeft and foldRight to FingerTree.
    * Added foldLeft, foldRight and map to Seq.
    * Accumulate Validation errors in a List.
    * Convert from fj.data.Stream to java.util.Stream and vice versa.
    * Added groupBy on List.
* Fixes
    * Various Javadoc issues
    * Large inserts into TreeMap
    * Javadoc support for Java 8
    * Null parameter NullPointerException for Show.anyShow
    * Exception propagation in test data generators
    * Product memoisation for arities 1 to 8
    * ClassCastException in class fj.data.Java
    * Fixed performance of Set.member.

4.1
===
Released: 30 May, 2014

* Support Java 7 by removing default methods.  Methods on class C with default methods moved to static methods on new class ${C}Functions.

4.0
===
Released: 30 May, 2014

* Merged changes from 3.2
* Minimal changes to support Java 8.
* Changed abstract classes to interfaces with default methods (P1, F1 to F8).

3.2
===
Released: 30 May, 2014

* Added methods to HashMap: toList, toStream, toOption, toArray, map, mapKeys, foreach, convert to/from java.util.Map.
* Convert from java.util.List to List.
* Fixed findChild method in TreeZipper to handle empty stream.
* Fixed stack overflow when sorting and zipping.
* Fixed stack overflow in Stream's bind method.
* Small Javadoc fixes.

3.1
===
Released: May 2012

3.0 
===
Released: Jun 2010

2.21
====
Released Feb, 2010

* Bug fixes
* Immutable 2-3 finger tree

2.20
===
Unknown

2.19
===
Released March 2009

* Comonadic operations on Stream, Tree and others
* Database monad (java.sql.Connection as a functor)
* Natural Number data type
* The Constant Arrow ($)
* Immutable Tree Map
* A parallel quick-sort using Functional Java actors

