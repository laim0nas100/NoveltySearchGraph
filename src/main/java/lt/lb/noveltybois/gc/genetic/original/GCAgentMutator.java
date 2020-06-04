/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.gc.genetic.original;

import lt.lb.noveltybois.tsp.genetic.original.*;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMutator;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.gc.genetic.GCAgent;
import lt.lb.noveltybois.tsp.TSPSolution;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class GCAgentMutator implements AgentMutator<GCAgent> {

    public Orgraph gr;
    public RandomDistribution rnd;

    public GCAgentMutator(Orgraph g, RandomDistribution r) {
        rnd = r;
        gr = g;
    }

    @Override
    public void mutate(GCAgent agent) {
        
    }

}
