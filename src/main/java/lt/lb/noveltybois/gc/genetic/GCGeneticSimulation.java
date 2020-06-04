package lt.lb.noveltybois.gc.genetic;

import lt.lb.noveltybois.tsp.genetic.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.threads.executors.FastWaitingExecutor;
import lt.lb.commons.threads.sync.WaitTime;
import lt.lb.neurevol.evolution.NEAT.NeatPool;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.NeatConfigBase;
import lt.lb.noveltybois.gc.genetic.original.GCAgentBreeder;
import lt.lb.noveltybois.gc.genetic.original.GCAgentMutator;
import lt.lb.noveltybois.gc.genetic.original.GCAgentSimilarityEvaluator;
import lt.lb.noveltybois.gc.genetic.original.GCAgentSorter;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentBreeder;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentMutator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSimilarityEvaluator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSorter;

/**
 *
 * @author laim0nas100
 */
public class GCGeneticSimulation {

    public NeatPool<GCAgent> pool;
    public int improvements = 0;

    public double allTimeBest;

    public GCGeneticSimulation(Orgraph graph, ThreadLocal<RandomDistribution> uniform, TSPGeneticSimulationParams info) {
        GCAgentBreeder graphAgentBreeder = new GCAgentBreeder(graph, uniform.get(), info.crossoverChance, info.population);
        GCAgentMutator graphAgentMutator = new GCAgentMutator(graph, uniform.get());
        GCAgentSorter graphAgentSorter = new GCAgentSorter();
        GCAgentSimilarityEvaluator graphAgentSimilarityEvaluator = new GCAgentSimilarityEvaluator();
        Executor fastExe = new FastWaitingExecutor(1, WaitTime.ofSeconds(5));
        NeatConfigBase<GCAgent> config = new NeatConfigBase<>();
        config.agentBreeder = graphAgentBreeder;
        config.agentMutator = graphAgentMutator;
        config.agentSimilarityEvaluator = graphAgentSimilarityEvaluator;
        config.agentSorter = graphAgentSorter;
        config.executor = fastExe;

        pool = new NeatPool(config);
        config.pool = pool;
        pool.debug = objs -> {
            Log.print(objs);
            return pool.debug;
        };
        pool.similarity = info.initSimilarity;
        pool.distinctSpecies = info.distinctSpecies;
        pool.maxSpecies = info.maxSpecies;

        Log.print("Initial population");
        F.iterate(pool.getPopulation(), (index, g) -> {
            Log.print(g);
        });
        graph.sanityCheck();
        ArrayList<GCAgent> bestByGeneration = new ArrayList<>();
        int stagnation = 0;
        for (int i = 0; i < info.iterations; i++) {
            GCAgent oldBest = pool.allTimeBest;

//            Log.main().disable = true;
            pool.newGeneration();
            GCAgent currentBest = (GCAgent) pool.allTimeBest;

            if (oldBest == currentBest) {
                stagnation++;
            } else {
                improvements++;
                stagnation = 0;
            }
            bestByGeneration.add(currentBest);
//            Log.main().disable = false;
            Log.print("Iteration " + i, "Stagnation " + stagnation);
            if (stagnation > info.maxStagnation) {
                break;
            }
        }
        Log.print("Bests:");
        Log.printLines(bestByGeneration);
        Log.print("Species:" + pool.getSubpopulations().size());
        Log.println("", "All time best:", "Length:" + graphAgentSorter.evaluateFitness(pool.allTimeBest), "Path:" + pool.allTimeBest.coloring);
        FloatFitness ff = graphAgentSorter.evaluateFitness(pool.allTimeBest);
        this.allTimeBest = ff.get().doubleValue();

        Log.print("Links:", graph.links.size());
        Log.print("Links bidirectional:", graph.bidirectionalLinkCount());
        Log.print("Nodes:", graph.nodes.size());
        Log.print("Fitness computed", graphAgentSorter.fitnessComputed.get());

    }

}
