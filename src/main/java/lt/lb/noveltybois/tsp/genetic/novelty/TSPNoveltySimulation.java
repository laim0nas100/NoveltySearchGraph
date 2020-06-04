/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.novelty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executor;
import lt.lb.commons.F;
import lt.lb.commons.Java;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.threads.executors.FastWaitingExecutor;
import lt.lb.commons.threads.sync.WaitTime;
import lt.lb.neurevol.evolution.NEAT.NeatPool;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.Main;
import lt.lb.noveltybois.NeatConfigBase;
import lt.lb.noveltybois.tsp.genetic.TSPGeneticSimulationParams;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;
import lt.lb.noveltybois.NoveltyEvaluator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentBreeder;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentMutator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSimilarityEvaluator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSorter;

/**
 *
 * @author laim0nas100
 */
public class TSPNoveltySimulation {

    public NeatPool<TSPAgent> pool;
    public int improvements = 0;

    public double allTimeBest;
    public String result;

    public TSPNoveltySimulation(Orgraph graph, ThreadLocal<RandomDistribution> uniform, TSPGeneticSimulationParams info) {
        TSPAgentBreeder graphAgentBreeder = new TSPAgentBreeder(graph, uniform.get(), info.crossoverChance, info.population, info.crossover);
        TSPAgentMutator graphAgentMutator = new TSPAgentMutator(graph, uniform.get(), info.mutator);
        TSPNoveltyGraphAgentSorter graphAgentSorter = new TSPNoveltyGraphAgentSorter();
        TSPAgentSorter fitnessSorter = new TSPAgentSorter();

        TSPNoveltyEvaluator evaluator = new TSPNoveltyEvaluator(info.similarity);

        evaluator.alpha = 1d;
        evaluator.beta = 0.05d;

        graphAgentSorter.noveltyEval = evaluator;
        graphAgentSorter.populationSupplier = () -> pool.getPopulation();

        TSPAgentSimilarityEvaluator graphAgentSimilarityEvaluator = new TSPAgentSimilarityEvaluator();

        NeatConfigBase<TSPAgent> config = new NeatConfigBase<>();
        config.agentBreeder = graphAgentBreeder;
        config.agentMutator = graphAgentMutator;
        config.agentSimilarityEvaluator = graphAgentSimilarityEvaluator;
        config.agentSorter = graphAgentSorter;
        Executor fastExe = new FastWaitingExecutor(Java.getAvailableProcessors(), WaitTime.ofSeconds(5));
        config.executor = fastExe;

        pool = new NeatPool(config) {
            @Override
            public void beforeNewGeneration() {
                Log.print("Clear cache");
                graphAgentSorter.noveltyCache.clear();
//                pool.getPopulation().stream().parallel().forEach(a -> {
//                    graphAgentSorter.testNovelty(a);
//                });

            }

        };
        config.pool = pool;
//        pool.debug = objs -> {
//            Log.print(objs);
//            return pool.debug;
//        };
        pool.similarity = info.initSimilarity;
        pool.distinctSpecies = info.distinctSpecies;
        pool.maxSpecies = info.maxSpecies;

        Log.print("Initial population");
//        F.iterate(pool.getPopulation(), (index, g) -> {
//            Log.print(g, API.isPathValid(graph, g.path));
//        });
        graph.sanityCheck();
        ArrayList<TSPAgent> bestByGeneration = new ArrayList<>();
        int stagnation = 0;
        TSPAgent oldBest = null;
        Comparator<TSPAgent> fitCmp = fitnessSorter.getComparator().reversed();
        for (int i = 0; i < info.iterations; i++) {

//            Log.main().disable = true;
            pool.newGeneration();
            pool.getPopulation().stream().parallel().forEach(a -> {
                graphAgentSorter.testNovelty(a);
            });

            TSPAgent currentBest = pool.getPopulation()
                    .stream().sorted(fitCmp).findFirst().get();

            if (oldBest == currentBest) {
                stagnation++;
            } else {
                oldBest = currentBest;
                improvements++;
                stagnation = 0;
            }
            bestByGeneration.add(currentBest);
//            Log.main().disable = false;
            Log.print("Iteration " + i, "Stagnation " + stagnation);
            if (stagnation > info.maxStagnation) {
                break;
            }

//            evaluator.finishedEvaluation();
//
//            Log.print("Archive size:" + evaluator.archive.size());
            Log.print("Archive size:" + evaluator.size());

        }
//        Log.print("Bests:");
//        Log.printLines(bestByGeneration);
//        Log.print("Species:" + pool.getSubpopulations().size());
//        TSPAgent bestAgent = bestByGeneration.stream().sorted(fitCmp).findFirst().get();
//        Log.println("", "All time best:", "Length:" + graphAgentSorter.evaluateFitness(pool.allTimeBest), "Path:" + pool.allTimeBest.path);
//        Log.print("Is path valid though?", API.isPathValid(graph, pool.allTimeBest.path));

        FloatFitness ff = fitnessSorter.evaluateFitness(pool.allTimeBest);
        this.allTimeBest = ff.get().doubleValue();
//        Log.print("Links:", graph.links.size());
//        Log.print("Links bidirectional:", graph.bidirectionalLinkCount());
//        Log.print("Nodes:", graph.nodes.size());
//        Log.print("Fitness computed", graphAgentSorter.fitnessComputed.get());

        result = info.crossover.name + " " + info.mutator.name + " " + info.similarity.name + "\n";
        result += "Novelty computed " + graphAgentSorter.fitnessComputed.get();
        result += " Fitness computed " + fitnessSorter.fitnessComputed.get();
        result += " Distance computed " + evaluator.timesDistanceComputed();
        result += " all time best " + this.allTimeBest;
    }

}
