/**
 * Provides 2-3 finger trees, a functional representation of persistent sequences supporting access to the ends in
 * amortized O(1) time. Concatenation and splitting time is O(log n) in the size of the smaller piece.
 * A general purpose data structure that can serve as a sequence, priority queue, search tree, priority search queue
 * and more.

 * Based on "Finger trees: a simple general-purpose data structure", by Ralf Hinze and Ross Paterson.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 348 $</li>
 *          <li>$LastChangedDate: 2010-03-19 20:52:52 +1000 (Fri, 19 Mar 2010) $</li>
 *          </ul>
 */
package fj.data.fingertrees;
