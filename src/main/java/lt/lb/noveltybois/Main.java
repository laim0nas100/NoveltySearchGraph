/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lt.lb.commons.F;
import lt.lb.commons.Java;
import lt.lb.commons.Log;
import lt.lb.commons.func.StreamMapper.StreamDecorator;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.FastRandom;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.threads.executors.FastExecutor;
import lt.lb.noveltybois.tsp.TSPSolution;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;
import lt.lb.noveltybois.tsp.genetic.novelty.TSPNoveltySimulation;
import lt.lb.noveltybois.tsp.genetic.TSPGeneticSimulation;
import lt.lb.noveltybois.tsp.genetic.TSPGeneticSimulationParams;

/**
 *
 * @author Lemmin
 */
public class Main {

    public static ThreadLocal<RandomDistribution> rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom((int) (Math.random() * 1000))));

    public static void main(String[] args) throws Exception {

        List<NamedMethod<ArrayList<TSPAgent>>> crossover = new ArrayList<>();
        List<NamedMethod<TSPAgent>> mutate = new ArrayList<>();
        List<NamedMethod<Double>> sims = new ArrayList<>();

        crossover.add(new NamedMethod<>(TSPSolution.class, "crossoverPartiallyMapped"));
        crossover.add(new NamedMethod<>(TSPSolution.class, "crossoverOrder"));
        crossover.add(new NamedMethod<>(TSPSolution.class, "crossoverCycle"));

        mutate.add(new NamedMethod<>(TSPSolution.class, "mutationNodeMove"));
        mutate.add(new NamedMethod<>(TSPSolution.class, "mutationNodeSwap"));
        mutate.add(new NamedMethod<>(TSPSolution.class, "mutationCentralInversion"));
        mutate.add(new NamedMethod<>(TSPSolution.class, "mutationInnerSequenceInversion"));
        mutate.add(new NamedMethod<>(TSPSolution.class, "mutationPathCutoff"));
        mutate.add(NamedMethod.combined("combined", rng, mutate));

//        sims.add(new NamedMethod<>(TSPSolution.class, "simLongestCommonsSubseq"));
        sims.add(new NamedMethod<>(TSPSolution.class, "simCommonLinks"));
        sims.add(new NamedMethod<>(TSPSolution.class, "simLength"));

        Orgraph simpleGraph = GraphLoad.simpleGraph(100, 10);
        ConcurrentLinkedDeque<TSPNoveltySimulation> novelty = new ConcurrentLinkedDeque<>();
        ConcurrentLinkedDeque<TSPGeneticSimulation> genetic = new ConcurrentLinkedDeque<>();

        crossover.forEach(cr -> {
            mutate.forEach(mut -> {
                TSPGeneticSimulationParams params = new TSPGeneticSimulationParams();
                params.maxSpecies = 1;
                params.iterations = 500;
                params.maxStagnation = 1000;
                params.population = 50;
                params.mutator = mut;
                params.crossover = cr;
                ExecutorService exe = Executors.newFixedThreadPool(6);
                for (int i = 0; i < 5; i++) {

                    final int seed1 = rng.get().nextInt() + i;
                    Runnable r1 = () -> {
                        rng.set(RandomDistribution.uniform(new FastRandom(seed1)));
                        genetic.add(new TSPGeneticSimulation(simpleGraph, rng, params));

                    };
                    exe.execute(r1);

                }

                exe.shutdown();
                F.unsafeRun(() -> {
                    exe.awaitTermination(1, TimeUnit.DAYS);
                });

            });
        });

        crossover.forEach(cr -> {
            mutate.forEach(mut -> {
                sims.forEach(sim -> {
                    TSPGeneticSimulationParams params = new TSPGeneticSimulationParams();
                    params.maxSpecies = 1;
                    params.iterations = 500;
                    params.maxStagnation = 1000;
                    params.population = 50;
                    params.mutator = mut;
                    params.crossover = cr;
                    params.similarity = sim;
                    ExecutorService exe = Executors.newFixedThreadPool(6);
                    for (int i = 0; i < 5; i++) {

                        final int seed2 = rng.get().nextInt() + i;
                        Runnable r2 = () -> {
                            rng.set(RandomDistribution.uniform(new FastRandom(seed2)));
                            novelty.add(new TSPNoveltySimulation(simpleGraph, rng, params));
                        };
//
                        exe.execute(r2);

                    }

                    exe.shutdown();
                    F.unsafeRun(() -> {
                        exe.awaitTermination(1, TimeUnit.DAYS);
                    });
                });

            });
        });

        /**
         * 50 pop 100 = 9849 1000 = 98049
         *
         * 100 pop 100 19899 1000 198099
         *
         */
        Log.print("Genetic");
        Log.printLines(genetic.stream().map(m -> m.result).collect(Collectors.toList()));
        Log.print("Novelty");
        Log.printLines(novelty.stream().map(m -> m.result).collect(Collectors.toList()));

//        Log.print(graph.toStringLinks());
        Log.close();
    }

    public static double avg(Collection<Double> col) {
        return col.stream().mapToDouble(m -> m).average().orElse(0);
    }

    public static double min(Collection<Double> col) {
        return col.stream().mapToDouble(m -> m).min().orElse(0);
    }

    public static double max(Collection<Double> col) {
        return col.stream().mapToDouble(m -> m).max().orElse(0);
    }
}
