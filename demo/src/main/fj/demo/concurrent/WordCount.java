package fj.demo.concurrent;

import static fj.Monoid.monoid;
import static fj.control.parallel.ParModule.parModule;
import static fj.data.List.nil;
import static java.util.concurrent.Executors.newFixedThreadPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;
import fj.control.parallel.ParModule;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.IO;
import fj.data.Iteratee;
import fj.data.Iteratee.Input;
import fj.data.Iteratee.IterV;
import fj.data.List;
import fj.data.Option;

/**
 * Reads words and their counts from files ({@link #getWordsAndCountsFromFiles(List)} in a single thread
 * and {@link #getWordsAndCountsFromFilesInParallel(List, int)} in multiple threads). The files are created
 * initially and populated with some sample content.
 * 
 * @author Martin Grotzke
 */
public class WordCount {
  
  // Integers.add.f(1) caused an SOE...
  private static final F<Integer,Integer> addOne = new F<Integer,Integer>() {
    @Override
    public Integer f(Integer a) {
      return a.intValue() + 1;
    }
  };

  private static <K, V> Map<K, V> update(Map<K, V> map, K key, F<V, V> valueFunction,
      V initialValue) {
    V value = map.get(key);
    if(value == null) {
      value = initialValue;
    }
    map.put(key, valueFunction.f(value));
    return map;
  }
  
  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharChunkIteratee = new F<String, Map<String, Integer>>() {
    @Override
    public Map<String, Integer> f(final String fileName) {
      try {
        return IO.enumFileCharChunks(new File(fileName), Option.<Charset> none(), wordCountsFromCharChunks()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  };
  
  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharChunk2Iteratee = new F<String, Map<String, Integer>>() {
    @Override
    public Map<String, Integer> f(final String fileName) {
      try {
        return IO.enumFileChars(new File(fileName), Option.<Charset> none(), wordCountsFromChars()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  };
  
  private static final F<String, Map<String, Integer>> fileNameToWordsAndCountsWithCharIteratee = new F<String, Map<String, Integer>>() {
    @Override
    public Map<String, Integer> f(final String fileName) {
      try {
        return IO.enumFileChars(new File(fileName), Option.<Charset> none(), wordCountsFromChars()).run().run();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
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
            new P1<IterV<char[], Map<String, Integer>>>() {
              @Override
              public IterV<char[], Map<String, Integer>> _1() {
                return IterV.cont(step.f(acc));
              }
            };
          final P1<F<char[], IterV<char[], Map<String, Integer>>>> el =
            new P1<F<char[], IterV<char[], Map<String, Integer>>>>() {
              @Override
              public F<char[], IterV<char[], Map<String, Integer>>> _1() {
                return new F<char[], Iteratee.IterV<char[], Map<String, Integer>>>() {
                  @Override
                  public IterV<char[], Map<String, Integer>> f(final char[] e) {
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
                  }
                };
              }
            };
          final P1<IterV<char[], Map<String, Integer>>> eof =
            new P1<IterV<char[], Map<String, Integer>>>() {
              @Override
              public IterV<char[], Map<String, Integer>> _1() {
                final StringBuilder sb = acc._1();
                if(sb.length() > 0) {
                  final Map<String, Integer> map = update(acc._2(), sb.toString(), addOne, Integer.valueOf(0));
                  return IterV.done(map, Input.<char[]>eof());
                }
                return IterV.done(acc._2(), Input.<char[]>eof());
              }
            };
          return new F<Input<char[]>, IterV<char[], Map<String, Integer>>>() {
            @Override
            public IterV<char[], Map<String, Integer>> f(final Input<char[]> s) {
              return s.apply(empty, el, eof);
            }
          };
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
          final P1<IterV<Character, Map<String, Integer>>> empty =
            new P1<IterV<Character, Map<String, Integer>>>() {
              @Override
              public IterV<Character, Map<String, Integer>> _1() {
                return IterV.cont(step.f(acc));
              }
            };
          final P1<F<Character, IterV<Character, Map<String, Integer>>>> el =
            new P1<F<Character, IterV<Character, Map<String, Integer>>>>() {
              @Override
              public F<Character, IterV<Character, Map<String, Integer>>> _1() {
                return new F<Character, Iteratee.IterV<Character, Map<String, Integer>>>() {
                  @Override
                  public IterV<Character, Map<String, Integer>> f(final Character e) {
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
                  }
                };
              }
            };
          final P1<IterV<Character, Map<String, Integer>>> eof =
            new P1<IterV<Character, Map<String, Integer>>>() {
              @Override
              public IterV<Character, Map<String, Integer>> _1() {
                final StringBuilder sb = acc._1();
                if(sb.length() > 0) {
                  final Map<String, Integer> map = update(acc._2(), sb.toString(), addOne, Integer.valueOf(0));
                  return IterV.done(map, Input.<Character>eof());
                }
                return IterV.done(acc._2(), Input.<Character>eof());
              }
            };
          return new F<Input<Character>, IterV<Character, Map<String, Integer>>>() {
            @Override
            public IterV<Character, Map<String, Integer>> f(final Input<Character> s) {
              return s.apply(empty, el, eof);
            }
          };
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
    long avgSize = fileNames.foldLeft(new F2<Long, String, Long>() {
      @Override
      public Long f(Long a, String file) {
        return a.longValue() + new File(file).length();
      }}, 0l) / fileNames.length();
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
    fileNames.foreach(new Effect<String>() {
      @Override
      public void e(final String a) {
        new File(a).delete();
      }});
  }

  @SuppressWarnings("unused")
  private static void print(Map<String, Integer> wordsAndCountsFromFiles) {
    for(final Map.Entry<String, Integer> entry : wordsAndCountsFromFiles.entrySet()) {
      System.out.println("Have " + entry.getKey() + ": " + entry.getValue());
    }
  }

  private static P2<List<String>, Map<String, Integer>> writeSampleFiles(
      int numFiles, int numSharedWords) throws IOException {
    final Map<String, Integer> expectedWordsAndCounts = new HashMap<String, Integer>();
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
    return maps.foldLeft(new F2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>>() {
      @Override
      public Map<String, Integer> f(Map<String, Integer> a, Map<String, Integer> b) {
        return plus(a, b);
      }
    }, new HashMap<String, Integer>());
  }
  
  public static Map<String, Integer> getWordsAndCountsFromFilesInParallel(
      final List<String> fileNames, final F<String, Map<String, Integer>> fileNameToWordsAndCounts, int numThreads) {
    final ExecutorService pool = newFixedThreadPool(numThreads);
    final ParModule m = parModule(Strategy.<Unit> executorStrategy(pool));

    // Long wordCount = countWords(fileNames.map(readFile), m).claim();    
    final Map<String, Integer> result = getWordsAndCountsFromFiles(fileNames, fileNameToWordsAndCounts, m).claim();

    pool.shutdown();

    return result;
  }
  
  // Read documents and extract words and word counts of documents
  public static Promise<Map<String, Integer>> getWordsAndCountsFromFiles(
      final List<String> fileNames, final F<String, Map<String, Integer>> fileNameToWordsAndCounts, final ParModule m) {
    final F<Map<String, Integer>, F<Map<String, Integer>, Map<String, Integer>>> MapSum =
        new F<Map<String, Integer>, F<Map<String, Integer>, Map<String, Integer>>>() {
      @Override
      public F<Map<String, Integer>, Map<String, Integer>> f(
          final Map<String, Integer> a) {
        return new F<Map<String, Integer>, Map<String, Integer>>() {

          @Override
          public Map<String, Integer> f(final Map<String, Integer> b) {
            return plus(a, b);
          }
          
        };
      }
      
    };
    final Monoid<Map<String, Integer>> monoid = monoid(MapSum,
        new HashMap<String, Integer>());
    return m.parFoldMap(fileNames, fileNameToWordsAndCounts, monoid);
  }
  
  private static Map<String, Integer> plus(Map<String, Integer> a, Map<String, Integer> b) {
    final Map<String, Integer> result = new HashMap<String, Integer>(a);
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
