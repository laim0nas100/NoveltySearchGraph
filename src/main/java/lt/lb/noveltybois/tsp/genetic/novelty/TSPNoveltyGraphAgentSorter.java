/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.novelty;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;
import lt.lb.neurevol.evolution.NEAT.interfaces.CachingAgentSorter;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;
import lt.lb.noveltybois.NoveltyEvaluator;

/**
 *
 * @author laim0nas100
 */
public class TSPNoveltyGraphAgentSorter extends CachingAgentSorter<TSPAgent,FloatFitness> {

    public Supplier<? extends Collection<TSPAgent>> populationSupplier;
    public NoveltyEvaluator<TSPAgent> noveltyEval;

    public ConcurrentHashMap<String, Double> noveltyCache = new ConcurrentHashMap<>();


    public Double testNovelty(TSPAgent g) {
        return noveltyCache.computeIfAbsent(g.id, (id) -> {
            return noveltyEval.testNovelty(g);
        });
    }

    public AtomicLong fitnessComputed = new AtomicLong();
    
    @Override
    public FloatFitness computeFitenss(TSPAgent agent) {
        fitnessComputed.incrementAndGet();
        return new FloatFitness(testNovelty(agent).floatValue());
        
    }
    
    

}
