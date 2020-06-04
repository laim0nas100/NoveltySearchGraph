package lt.lb.noveltybois.tsp.genetic.original;

import java.util.concurrent.atomic.AtomicLong;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;
import lt.lb.neurevol.evolution.NEAT.interfaces.CachingAgentSorter;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class TSPAgentSorter extends CachingAgentSorter<TSPAgent, FloatFitness> {

    public AtomicLong fitnessComputed = new AtomicLong();
    
    @Override
    public FloatFitness computeFitenss(TSPAgent agent) {
        fitnessComputed.incrementAndGet();
        Double pathWeight = Algorithms.getPathWeight(agent.tspPath, agent.graph);
        agent.pathWeight = pathWeight;
        return new FloatFitness(pathWeight.floatValue()*-1);
        
    }


}
