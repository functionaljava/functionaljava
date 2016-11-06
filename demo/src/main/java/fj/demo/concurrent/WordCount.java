package fj.demo.concurrent;

import fj.F;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.control.parallel.ParModule;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.IOFunctions;
import fj.data.Iteratee.Input;
import fj.data.Iteratee.IterV;
import fj.data.List;
import fj.data.Option;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static fj.Monoid.monoid;
import static fj.control.parallel.ParModule.parModule;
import static fj.data.List.nil;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Reads words and their counts from files ({@link #getWordsAndCountsFromFiles} in a single thread
 * and {@link #getWordsAndCountsFromFilesInParallel} in multiple threads). The files are created
 * initially and populated with some sample content.
 *
 * @author Martin Grotzke
 */
public class WordCount {

  // Integers.add.f(1) caused an SOE...
  private static final F<Integer,Integer> addOne = a -> a.intValue() + 1;

  private static <K, V> Map<K, V> update(Map<K, V> map, K key, F<V, V> valueFunction,
      V initialValue) {
    V value = map.get(key);
    if(value == null) {
      value = initialValue;
    }
    map.put(key, valueFunction.f(value));
    return map;
  }

  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharChunkIteratee = fileName -> {
      try {
        return IOFunctions.enumFileCharChunks(new File(fileName), Option.none(), wordCountsFromCharChunks()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
  };

  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharChunk2Iteratee = fileName -> {
      try {
        return IOFunctions.enumFileChars(new File(fileName), Option.none(), wordCountsFromChars()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
  };

  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharIteratee = fileName -> {
      try {
        return IOFunctions.enumFileChars(new File(fileName), Option.none(), wordCountsFromChars()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
  };

  /** An iteratee that consumes char chunks and calculates word counts */
  public static final <E> IterV<char[], Map<String, Integer>> wordCountsFromCharChunks() {
    final F<P2<StringBuilder,Map<String, Integer>>, F<Input<char[]>, IterV<char[], Map<String, Integer>>>> step =
      new F<P2<StringBuilder,Map<String, Integer>>, F<Input<char[]>, IterV<char[], Map<String, Integer>>>>() {
        final F<P2<StringBuilder,Map<String, Integer>>, F<Input<char[]>, IterV<char[], Map<String, Integer>>>> step = this;

        @Override
        public F<Input<char[]>, IterV<char[], Map<String, Integer>>> f(final P2<StringBuilder,Map<String, Integer>> acc) {
          final P1<IterV<char[], Map<String, Integer>>> empty =
            P.lazy(() -> IterV.cont(step.f(acc)));
          final P1<F<char[], IterV<char[], Map<String, Integer>>>> el =
            new P1<F<char[], IterV<char[], Map<String, Integer>>>>() {
              @Override
              public F<char[], IterV<char[], Map<String, Integer>>> _1() {
                return e -> {
                  StringBuilder sb = acc._1();
                  Map<String, Integer> map = acc._2();
                  for(char c : e) {
                    if(Character.isWhitespace(c)) {
                      if(sb.length() > 0) {
                        map = update(map, sb.toString(), addOne, Integer.valueOf(0));
                        sb = new StringBuilder();
                      }
                    }
                    else {
                      sb.append(c);
                    }
                  }
                  return IterV.cont(step.f(P.p(sb, map)));
                };
              }
            };
          final P1<IterV<char[], Map<String, Integer>>> eof =
            P.lazy(() -> {
                final StringBuilder sb = acc._1();
                if(sb.length() > 0) {
                  final Map<String, Integer> map = update(acc._2(), sb.toString(), addOne, Integer.valueOf(0));
                  return IterV.done(map, Input.eof());
                }
                return IterV.done(acc._2(), Input.eof());
              });

          return s -> s.apply(empty, el, eof);
        }
      };
    return IterV.cont(step.f(P.p(new StringBuilder(), (Map<String, Integer>)new HashMap<String, Integer>())));
  }

  /** An iteratee that consumes chars and calculates word counts */
  public static final <E> IterV<Character, Map<String, Integer>> wordCountsFromChars() {
    final F<P2<StringBuilder,Map<String, Integer>>, F<Input<Character>, IterV<Character, Map<String, Integer>>>> step =
      new F<P2<StringBuilder,Map<String, Integer>>, F<Input<Character>, IterV<Character, Map<String, Integer>>>>() {
        final F<P2<StringBuilder,Map<String, Integer>>, F<Input<Character>, IterV<Character, Map<String, Integer>>>> step = this;

        @Override
        public F<Input<Character>, IterV<Character, Map<String, Integer>>> f(final P2<StringBuilder,Map<String, Integer>> acc) {
          final P1<IterV<Character, Map<String, Integer>>> empty = P.lazy(() -> IterV.cont(step.f(acc)));
          final P1<F<Character, IterV<Character, Map<String, Integer>>>> el =
            P.lazy(() -> e -> {
                if(Character.isWhitespace(e.charValue())) {
                  final StringBuilder sb = acc._1();
                  if(sb.length() > 0) {
                    final Map<String, Integer> map = update(acc._2(), sb.toString(), addOne, Integer.valueOf(0));
                    return IterV.cont(step.f(P.p(new StringBuilder(), map)));
                  }
                  else {
                    // another whitespace char, no word to push to the map
                    return IterV.cont(step.f(acc));
                  }
                }
                else {
                  acc._1().append(e);
                  return IterV.cont(step.f(acc));
                }
            });
          final P1<IterV<Character, Map<String, Integer>>> eof = P.lazy(() -> {
                final StringBuilder sb = acc._1();
                if(sb.length() > 0) {
                  final Map<String, Integer> map = update(acc._2(), sb.toString(), addOne, Integer.valueOf(0));
                  return IterV.done(map, Input.eof());
                }
                return IterV.done(acc._2(), Input.eof());
              }
            );
          return s -> s.apply(empty, el, eof);
        }
      };
    return IterV.cont(step.f(P.p(new StringBuilder(), (Map<String, Integer>)new HashMap<String, Integer>())));
  }

  public static void main(String[] args) throws IOException {

    // setup
    int numFiles = 1;
    int numSharedWords = 5000000;

    final P2<List<String>, Map<String, Integer>> result = writeSampleFiles(numFiles, numSharedWords);
    final List<String> fileNames = result._1();
    final Map<String, Integer> expectedWordsAndCounts = result._2();
    long avgSize = fileNames.foldLeft((a, file) -> a.longValue() + new File(file).length(), 0l) / fileNames.length();
    System.out.println("Processing " + numFiles + " files with ~"+numSharedWords+" words and an avg size of " + avgSize + " bytes.");

    // warmup
    for(int i = 0; i < 1; i++) {
      // getWordsAndCountsFromFiles(fileNames.take(1)).size();
      getWordsAndCountsFromFilesWithIteratee(fileNames.take(1), fileNameToWordsAndCountsWithCharIteratee);
      getWordsAndCountsFromFilesWithIteratee(fileNames.take(1), fileNameToWordsAndCountsWithCharChunkIteratee);
      getWordsAndCountsFromFilesWithIteratee(fileNames.take(1), fileNameToWordsAndCountsWithCharChunk2Iteratee);
      getWordsAndCountsFromFilesWithIteratee(fileNames.take(1), fileNameToWordsAndCountsWithCharChunk2Iteratee);
      // getWordsAndCountsFromFilesInParallel(fileNames.take(1), fileNameToWordsAndCounts, 8);
      getWordsAndCountsFromFilesInParallel(fileNames.take(1), fileNameToWordsAndCountsWithCharIteratee, 8);
      getWordsAndCountsFromFilesInParallel(fileNames.take(1), fileNameToWordsAndCountsWithCharChunkIteratee, 8);
    }

    System.gc();

    // get word counts sequentially / single threaded
    long start = System.currentTimeMillis();
    Map<String, Integer> wordsAndCountsFromFiles = null;//getWordsAndCountsFromFiles(fileNames);
//    System.out.println("Getting word counts in 1 thread took " + (System.currentTimeMillis() - start) + " ms.");
//    assertTrue(wordsAndCountsFromFiles != null);
//    assertTrue(wordsAndCountsFromFiles.size() == numFiles + numSharedWords);
//    assertTrue(wordsAndCountsFromFiles.equals(expectedWordsAndCounts));

    // get word counts sequentially / single threaded \w iteratee
    start = System.currentTimeMillis();
    wordsAndCountsFromFiles = getWordsAndCountsFromFilesWithIteratee(fileNames, fileNameToWordsAndCountsWithCharIteratee);
    System.out.println("Getting word counts in 1 thread using char iteratee took " + (System.currentTimeMillis() - start) + " ms.");
    assertTrue(wordsAndCountsFromFiles != null);
    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    System.gc();

    // get word counts sequentially / single threaded \w iteratee
    start = System.currentTimeMillis();
    wordsAndCountsFromFiles = getWordsAndCountsFromFilesWithIteratee(fileNames, fileNameToWordsAndCountsWithCharChunkIteratee);
    System.out.println("Getting word counts in 1 thread using char chunk iteratee took " + (System.currentTimeMillis() - start) + " ms.");
    assertTrue(wordsAndCountsFromFiles != null);
    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    System.gc();

    // get word counts sequentially / single threaded \w iteratee
    start = System.currentTimeMillis();
    wordsAndCountsFromFiles = getWordsAndCountsFromFilesWithIteratee(fileNames, fileNameToWordsAndCountsWithCharChunk2Iteratee);
    System.out.println("Getting word counts in 1 thread using char chunk2 iteratee took " + (System.currentTimeMillis() - start) + " ms.");
    assertTrue(wordsAndCountsFromFiles != null);
    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    System.gc();

//    start = System.currentTimeMillis();
//    wordsAndCountsFromFiles = getWordsAndCountsFromFilesInParallel(fileNames, fileNameToWordsAndCounts, 8);
//    System.out.println("Getting word counts in 8 threads took " + (System.currentTimeMillis() - start) + " ms.");
//    assertTrue(wordsAndCountsFromFiles != null);
//    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
//    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    start = System.currentTimeMillis();
    wordsAndCountsFromFiles = getWordsAndCountsFromFilesInParallel(fileNames, fileNameToWordsAndCountsWithCharIteratee, 32);
    System.out.println("Getting word counts in 32 threads with char iteratee took " + (System.currentTimeMillis() - start) + " ms.");
    assertTrue(wordsAndCountsFromFiles != null);
    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    System.gc();

    start = System.currentTimeMillis();
    wordsAndCountsFromFiles = getWordsAndCountsFromFilesInParallel(fileNames, fileNameToWordsAndCountsWithCharChunkIteratee, 32);
    System.out.println("Getting word counts in 32 threads with char chunk iteratee took " + (System.currentTimeMillis() - start) + " ms.");
    assertTrue(wordsAndCountsFromFiles != null);
    assertEquals(wordsAndCountsFromFiles.size(), numFiles + numSharedWords);
    assertEquals(wordsAndCountsFromFiles, expectedWordsAndCounts);

    // we have tmpfiles, but still want to be sure not to leave rubbish
    fileNames.foreachDoEffect(a -> new File(a).delete());
  }

  @SuppressWarnings("unused")
  private static void print(Map<String, Integer> wordsAndCountsFromFiles) {
    for(final Map.Entry<String, Integer> entry : wordsAndCountsFromFiles.entrySet()) {
      System.out.println("Have " + entry.getKey() + ": " + entry.getValue());
    }
  }

  private static P2<List<String>, Map<String, Integer>> writeSampleFiles(
      int numFiles, int numSharedWords) throws IOException {
    final Map<String, Integer> expectedWordsAndCounts = new HashMap<>();
    List<String> fileNames = nil();
    for(int i = 0; i < numFiles; i++) {
      final File file = File.createTempFile("wordcount-"+ i + "-", ".txt");
      final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write("File" + i + "\n");
      expectedWordsAndCounts.put("File" + i, 1);
      for(int j = 0; j < numSharedWords; j++) {
        writer.write("\nsomeword" + j);
        expectedWordsAndCounts.put("someword" + j, numFiles);
      }
      writer.close();
      fileNames = fileNames.cons(file.getAbsolutePath());
    }
    return P.p(fileNames, expectedWordsAndCounts);
  }

  public static Map<String, Integer> getWordsAndCountsFromFilesWithIteratee(final List<String> fileNames,
      final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithIteratee) {
    final List<Map<String, Integer>> maps = fileNames.map(fileNameToWordsAndCountsWithIteratee);
    return maps.foldLeft(WordCount::plus, new HashMap<String, Integer>());
  }

  public static Map<String, Integer> getWordsAndCountsFromFilesInParallel(
      final List<String> fileNames, final F<String, Map<String, Integer>> fileNameToWordsAndCounts, int numThreads) {
    final ExecutorService pool = newFixedThreadPool(numThreads);
    final ParModule m = parModule(Strategy.executorStrategy(pool));

    // Long wordCount = countWords(fileNames.map(readFile), m).claim();    
    final Map<String, Integer> result = getWordsAndCountsFromFiles(fileNames, fileNameToWordsAndCounts, m).claim();

    pool.shutdown();

    return result;
  }

  // Read documents and extract words and word counts of documents
  public static Promise<Map<String, Integer>> getWordsAndCountsFromFiles(
      final List<String> fileNames, final F<String, Map<String, Integer>> fileNameToWordsAndCounts, final ParModule m) {
    final Monoid<Map<String, Integer>> monoid = monoid(WordCount::plus, Collections.emptyMap());
    return m.parFoldMap(fileNames, fileNameToWordsAndCounts, monoid);
  }

  private static Map<String, Integer> plus(Map<String, Integer> a, Map<String, Integer> b) {
    final Map<String, Integer> result = new HashMap<>(a);
    for(Map.Entry<String, Integer> entry : b.entrySet()) {
      final Integer num = result.get(entry.getKey());
      result.put(entry.getKey(), num != null ? num.intValue() + entry.getValue() : entry.getValue());
    }
    return result;
  }

  @SuppressWarnings("unused")
  private static String readFileToString(File file) throws IOException {
        Reader reader = null;
        try {
            reader = new FileReader(file);
            final Writer sw = new StringWriter((int)file.length());
            copy(reader, sw);
            return sw.toString();
        } finally {
          reader.close();
        }
    }

  private static void copy(Reader reader, Writer writer) throws IOException {
    char[] buffer = new char[1024 * 4];
    int n = 0;
    while (-1 != (n = reader.read(buffer))) {
        writer.write(buffer, 0, n);
    }
  }

  static void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError();
    }
  }

  static void assertEquals(Object actual, Object expected) {
    if (!expected.equals(actual)) {
      throw new IllegalArgumentException("Not equals. Expected: " + expected + ", actual: " + actual);
    }
  }

}
