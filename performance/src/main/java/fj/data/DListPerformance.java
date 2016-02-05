/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fj.data;

import fj.data.DList;
import fj.data.List;
import fj.data.Seq;
import fj.data.Stream;

/**
 *
 * @author clintonselke
 */
public class DListPerformance {

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
            return Seq.seq(List.range(from, to));
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
            return DList.fromList(List.range(from, to));
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
        for (int n = 0; n < 10; ++n) {
            final C xs = methods.range(0, 100);
            C r = xs;
            for (int i = 1; i < 2000; ++i) {
                r = methods.append(r, xs);
            }
            List<Integer> r2 = methods.unbox(r);
            for (Integer x : r2) {}
        }
        long msEnd = System.currentTimeMillis();
        return (msEnd - msStart) / 10.0;
    }

    public static void main(String[] params) {
        // warm up
        benchmark(listMethods);
        benchmark(seqMethods);
        benchmark(dListMethods);
        // actual run
        double listTime = benchmark(listMethods);
        double seqTime = benchmark(seqMethods);
        double dListTime = benchmark(dListMethods);
        System.out.println("Average over 10 runs...");
        System.out.println("List:  " + listTime + "ms");
        System.out.println("Seq:   " + seqTime + "ms");
        System.out.println("DList: " + dListTime + "ms");
    }
}
