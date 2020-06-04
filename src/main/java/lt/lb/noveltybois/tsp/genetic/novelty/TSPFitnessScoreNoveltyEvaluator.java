package lt.lb.noveltybois.tsp.genetic.novelty;

import lt.lb.noveltybois.HierachicalNoveltyEvaluator;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;
import lt.lb.noveltybois.tsp.genetic.original.TSPAgentSorter;

public class TSPFitnessScoreNoveltyEvaluator extends HierachicalNoveltyEvaluator<TSPAgent> {

    public TSPAgentSorter sorter;

    @Override
    public double distance(TSPAgent b1, TSPAgent b2) {
        float f1 = Math.abs(sorter.evaluateFitness(b1).get());
        float f2 = Math.abs(sorter.evaluateFitness(b2).get());
        float diff = Math.abs(f1 - f2);
        float max = Math.max(f1, f2);
        float min = Math.min(f1, f2);
        return 4 * (1 - min / max);
    }
}
