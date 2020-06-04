package lt.lb.noveltybois.gc.genetic.original;

import java.util.concurrent.atomic.AtomicLong;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;
import lt.lb.neurevol.evolution.NEAT.interfaces.CachingAgentSorter;
import lt.lb.noveltybois.gc.genetic.GCAgent;

/**
 *
 * @author laim0nas100
 */
public class GCAgentSorter extends CachingAgentSorter<GCAgent, FloatFitness> {

    public AtomicLong fitnessComputed = new AtomicLong();

    @Override
    public FloatFitness computeFitenss(GCAgent agent) {
        fitnessComputed.incrementAndGet();
        return new FloatFitness(agent.colorsUsed() * -1f);

    }

}
