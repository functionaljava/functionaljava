
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

Administrivia:

* Sources have been moved to GitHub: https://github.com/functionaljava/functionaljava
* Built has been converted to SBT

New features:

* List.mapMOption - list traversal in the Option monad.
* List.allEqual - Returns whether or not all elements in the list are equal.
* Added a monoid for Double.
* Tree.bottomUp - Grows a tree from another tree by folding it from the bottom up.
* Strings.isNotNullOrEmpty - does what it says.
* Ord.hashOrd - an order instance that uses hashCode() for the order.
* Ord.hashEqualsOrd - same as above but also uses .equals()
* Set.set - A vararg Set factory method.
* Added first-class functions for Set intersect, union, and minus.
* First-class LazyString.toString.
* Added hashCode() and equals() to Option.
* Iteratees and an IO monad.
* Trampoline - a general solution for tail-call elimination in Java.
* List.equals(), List.hashCode(), and List.toString().

Bug fixes:

* Stream.inits should always be nonempty.
* Stream was not compiling in Eclipse.
* Stream.length is now tail-recursive.
* TreeZipper.delete was flipping lefts and rights.
* Fixed naturalOrd Javadoc.

3.0 
===
Released: Jun 2010

A crucial change has been made to the F interfaces, which are now abstract classes and containing useful methods. This means some other methods (fj.Function) are redundant and will be removed in a release soon (perhaps with a @Deprecated first).

There are other minor additions and bug fixes.

2.23
====
Released: TODO

Changes: TODO

2.22
====
Released: March 2010

* Bug fixes
* Documentation fixes
* Built against JDK 1.5 (not 1.6)

2.21
====
Released Feb, 2010

* Bug fixes
* Immutable 2-3 finger tree

2.20
===
Released: July 2009

The highlight of this release is a parallel module built on top of
actors for doing some very clever and high-level parallel programming.
e.g. An implementation of the parallel map/reduce algorithm
(parFoldMap) and APIs for making use of actors easier for you, the user.

Other new bits includes:

* A heap of bug fixes, particularly on Stream (and therefore, many of
its dependencies)
* LazyString -- a lazy sequence of characters
* Function Wrappers
* Improvements to the Tree and Zipper implementations
* Other tidbits and additional APIs

2.19
===
Released March 2009

* Comonadic operations on Stream, Tree and others
* Database monad (java.sql.Connection as a functor)
* Natural Number data type
* The Constant Arrow ($)
* Immutable Tree Map
* A parallel quick-sort using Functional Java actors

