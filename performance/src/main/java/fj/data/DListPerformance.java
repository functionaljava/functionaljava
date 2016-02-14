package fj.data;

/**
 * Difference List performance benchmarks comparing DList to Seq and List
 * over 10 runs for the methods range, append and unbox.
 * 
 * @author clintonselke
 */
public class DListPerformance {

    static final int TOTAL_RUNS = 10;

    private interface BenchmarkMethods<C> {
        C range(int from, int to);
        C append(C a, C b);
        List<Integer> unbox(C a);
    }

    private static final BenchmarkMethods<List<Integer>> listMethods = new BenchmarkMethods<List<Integer>>() {
        @Override
        public List<Integer> range(int from, int to) {
            return List.range(from, to);
        }
        @Override
        public List<Integer> append(List<Integer> a, List<Integer> b) {
            return a.append(b);
        }
        @Override
        public List<Integer> unbox(List<Integer> a) {
            return a;
        }
    };

    private static final BenchmarkMethods<Seq<Integer>> seqMethods = new BenchmarkMethods<Seq<Integer>>() {
        @Override
        public Seq<Integer> range(int from, int to) {
            return Seq.iterableSeq(List.range(from, to));
        }
        @Override
        public Seq<Integer> append(Seq<Integer> a, Seq<Integer> b) {
            return a.append(b);
        }
        @Override
        public List<Integer> unbox(Seq<Integer> a) {
            return a.toList();
        }
    };

    private static final BenchmarkMethods<DList<Integer>> dListMethods = new BenchmarkMethods<DList<Integer>>() {
        @Override
        public DList<Integer> range(int from, int to) {
            return DList.listDList(List.range(from, to));
        }
        @Override
        public DList<Integer> append(DList<Integer> a, DList<Integer> b) {
            return a.append(b);
        }
        @Override
        public List<Integer> unbox(DList<Integer> a) {
            return a.run();
        }
    };

    private static <C> double benchmark(BenchmarkMethods<C> methods) {
        long msStart = System.currentTimeMillis();

        for (int runNumber = 0; runNumber < TOTAL_RUNS; ++runNumber) {
            final C xs = methods.range(0, 100);
            C r = xs;
            for (int i = 1; i < 2000; ++i) {
                r = methods.append(r, xs);
            }
            List<Integer> r2 = methods.unbox(r);
            for (Integer x : r2) {}
        }
        long msEnd = System.currentTimeMillis();
        return (msEnd - msStart) / ((double) TOTAL_RUNS);
    }

    public static void main(String[] params) {
        System.out.println("Starting difference list (DList) performance benchmark...");
        // warm up
        System.out.println("warm up...");
        benchmark(listMethods);
        benchmark(seqMethods);
        benchmark(dListMethods);
        // actual run
        System.out.println("running benchmark...");
        double listTime = benchmark(listMethods);
        double seqTime = benchmark(seqMethods);
        double dListTime = benchmark(dListMethods);
        System.out.println("Average over " + TOTAL_RUNS + " runs...");
        System.out.println("List:  " + listTime + "ms");
        System.out.println("Seq:   " + seqTime + "ms");
        System.out.println("DList: " + dListTime + "ms");
    }
}
