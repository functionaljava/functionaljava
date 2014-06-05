/**
 * Reductio is a software package that provides <em>automated specification-based testing</em> and
 * is intended to replace traditional testing techniques that have very little automation. Reductio
 * encourages users to express <em>properties</em> or <em>theorems</em> about their software, and
 * the testing and reporting of the status of those properties occurs by automating various aspects.
 * For example, if a property is found to be false (i.e. a test fails), then the counter-example may
 * be reduced while still falsifying the property so that the reported counter-example is the
 * simplest found (this is called {@link fj.test.Shrink}). The expression of the property also
 * serves as very rigorous documentation for the code under test; far surpassing that provided by
 * traditional testing techniques.
 *
 * Many of the concepts of Reductio originated with a paper called QuickCheck: A Lightweight Tool
 * for Random Testing of Haskell Programs by Koen Claassen and John Hughes from Chalmers University
 * of Technology. Reductio also borrows ideas from ScalaCheck by Rickard Nilsson.
 *
 * @version %build.number%
 */
package fj.test;
