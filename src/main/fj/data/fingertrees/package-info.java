/**
 * Provides 2-3 finger trees, a functional representation of persistent sequences supporting access to the ends in
 * amortized O(1) time. Concatenation and splitting time is O(log n) in the size of the smaller piece.
 * A general purpose data structure that can serve as a sequence, priority queue, search tree, priority search queue
 * and more.

 * Based on "Finger trees: a simple general-purpose data structure", by Ralf Hinze and Ross Paterson.
 *
 * @version %build.number%
 */
package fj.data.fingertrees;
