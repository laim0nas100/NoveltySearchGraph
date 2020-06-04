package lt.lb.noveltybois.tsp.genetic;

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
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentBreeder;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentMutator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSimilarityEvaluator;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSorter;

/**
 *
 * @author laim0nas100
 */
public class TSPGeneticSimulation {

    public NeatPool<TSPAgent> pool;
    public int improvements = 0;

    public String result;
    public double allTimeBest;

    public TSPGeneticSimulation(Orgraph graph, ThreadLocal<RandomDistribution> uniform, TSPGeneticSimulationParams info) {
        TSPAgentBreeder graphAgentBreeder = new TSPAgentBreeder(graph, uniform.get(), info.crossoverChance, info.population, info.crossover);
        TSPAgentMutator graphAgentMutator = new TSPAgentMutator(graph, uniform.get(), info.mutator);
        TSPAgentSorter graphAgentSorter = new TSPAgentSorter();
        TSPAgentSimilarityEvaluator graphAgentSimilarityEvaluator = new TSPAgentSimilarityEvaluator();
        Executor fastExe = new FastWaitingExecutor(1, WaitTime.ofSeconds(5));
        NeatConfigBase<TSPAgent> config = new NeatConfigBase<>();
        config.agentBreeder = graphAgentBreeder;
        config.agentMutator = graphAgentMutator;
        config.agentSimilarityEvaluator = graphAgentSimilarityEvaluator;
        config.agentSorter = graphAgentSorter;
        config.executor = fastExe;

        pool = new NeatPool(config);
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
        for (int i = 0; i < info.iterations; i++) {
            TSPAgent oldBest = pool.allTimeBest;

//            Log.main().disable = true;
            pool.newGeneration();
            TSPAgent currentBest = (TSPAgent) pool.allTimeBest;

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
//        Log.print("Bests:");
//        Log.printLines(bestByGeneration);
//        Log.print("Species:" + pool.getSubpopulations().size());
//        Log.println("", "All time best:", "Length:" + graphAgentSorter.evaluateFitness(pool.allTimeBest), "Path:" + pool.allTimeBest.path);
//        Log.print("Is path valid though?", API.isPathValid(graph, pool.allTimeBest.path));

        FloatFitness ff = graphAgentSorter.evaluateFitness(pool.allTimeBest);
        this.allTimeBest = ff.get().doubleValue();

//        Log.print("Links:", graph.links.size());
//        Log.print("Links bidirectional:", graph.bidirectionalLinkCount());
//        Log.print("Nodes:", graph.nodes.size());
//        Log.print("Fitness computed", graphAgentSorter.fitnessComputed.get());
        result = info.crossover.name + " " + info.mutator.name + "\n";
        result += "Fitness computed " + graphAgentSorter.fitnessComputed.get() + " all time best " + this.allTimeBest;
    }

}
