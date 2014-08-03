
3.2

4.0

4.1

4.2

Enhancements

* Added Java 8 examples
* Added new website
* Added Option.none_()
* Gradle 2.0 support with wrapper
* Added to Strings: isNullOrEmpty, isNullOrBlank and isNotNullOrBlank
* Added Try with arity 0-8 for lambdas that throw exceptions
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

Fixes
* Javadoc fixes
* Fixed large inserts into TreeMap
* Fixed Javadoc support for Java 8
* Fixed null parameter NullPointerException for Show.anyShow
* Fixed exception propagation in test data generators
* Fixed product memoisation for arities 1 to 8
* Fixed ClassCastException in class Java