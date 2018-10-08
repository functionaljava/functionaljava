4.8.1
-----

### Enhancements

- Add Trampoline.suspend(final F0<Trampoline<A>> a). (see [#367](https://github.com/functionaljava/functionaljava/pull/367));

### Fixes

- Fix regression in lifted semigroup sum. Fix #365 (see [#366](https://github.com/functionaljava/functionaljava/pull/366));

### Internal

- Fix compile under jdk11. Enable jdk11 travis build. (see [#361](https://github.com/functionaljava/functionaljava/pull/361));
- Fix warnings (see [#369](https://github.com/functionaljava/functionaljava/pull/369));
- Add P tests (see [#360](https://github.com/functionaljava/functionaljava/pull/360));
- Exclude consume/ from coverage (see [#357](https://github.com/functionaljava/functionaljava/pull/357));
- Add DList tests (see [#356](https://github.com/functionaljava/functionaljava/pull/356));
- Add Visitor tests (see [#354](https://github.com/functionaljava/functionaljava/pull/354));

4.8
---

### Enhancements

- Enable upload of snapshot artifacts. (see [`e834e8b`](https://github.com/functionaljava/functionaljava/commit/e834e8b));
- Add append methods to all Px classes. Fix #326 (see [`065ed43`](https://github.com/functionaljava/functionaljava/commit/065ed43));
- Introduce the Eval monad (see [`98294fc`](https://github.com/functionaljava/functionaljava/commit/98294fc));
- Fluent Equal/Ord construction (see [#333](https://github.com/functionaljava/functionaljava/pull/333));
- Implement Zipper Eq and Hash and add tests (see [#343](https://github.com/functionaljava/functionaljava/pull/343));
- Implement Vector equals (see [#350](https://github.com/functionaljava/functionaljava/pull/350));

### Fixes

- Fixed a bug in the NonEmptyList Semigroup implementation that resulted in the same NonEmptyList appended to itself. (Regression in 4.7, see [`07f94fa`](https://github.com/functionaljava/functionaljava/commit/07f94fa));
- Fixes #334: exception in Either.LeftProjection.traverseIO (see [#335](https://github.com/functionaljava/functionaljava/pull/335));

### Internal

- Added Scalacheck Arbitrary implementations for Natural and NonEmptyList. (see [`405c3ec`](https://github.com/functionaljava/functionaljava/commit/405c3ec));
- Added unit test coverage for Semigroup implementations.  The StringBuffer and StringBuilder tests fail because both of those types are mutable.  The IO test fails because the ArbitraryIO implementation does not implement equals. (see [`ef81130`](https://github.com/functionaljava/functionaljava/commit/ef81130));
- Fixed the ArbitraryIO implementation and created a Properties object for testing the IO semigroup. (see [`a8e979f`](https://github.com/functionaljava/functionaljava/commit/a8e979f));
- Equal: remove reference to static field of LazyString. Fix #321 (see [`6c6dabd`](https://github.com/functionaljava/functionaljava/commit/6c6dabd));
- Add IOFunctions tests (see [#340](https://github.com/functionaljava/functionaljava/pull/340));
- Add Stream tests (see [#341](https://github.com/functionaljava/functionaljava/pull/341));
- Add tests for Try, F, FW, Digit (see [#346](https://github.com/functionaljava/functionaljava/pull/346));
- Add Vector tests (see [#347](https://github.com/functionaljava/functionaljava/pull/347));
- Add Optic tests (see [#348](https://github.com/functionaljava/functionaljava/pull/348));
- Add Parser tests (see [#349](https://github.com/functionaljava/functionaljava/pull/349));
- Add FingerTree tests (see [#351](https://github.com/functionaljava/functionaljava/pull/351));
- Add TreeZipper tests (see [#352](https://github.com/functionaljava/functionaljava/pull/352));
- Add Reader/Writer tests (see [#353](https://github.com/functionaljava/functionaljava/pull/353));
