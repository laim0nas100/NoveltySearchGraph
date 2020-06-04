/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.original;

import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMutator;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.NamedMethod;
import lt.lb.noveltybois.tsp.TSPSolution;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class TSPAgentMutator implements AgentMutator<TSPAgent> {

    public Orgraph gr;
    public RandomDistribution rnd;
    
    public NamedMethod<TSPAgent> method;

    public TSPAgentMutator(Orgraph g, RandomDistribution r,NamedMethod<TSPAgent> method) {
        rnd = r;
        gr = g;
        this.method = method;
    }

    @Override
    public void mutate(TSPAgent agent) {
        TSPAgent mutate = method.invoke(rnd, gr, agent);
        if(mutate.nodes.isEmpty()){
            return;
        }
        
//        Log.print("Valid mutation?",API.isPathValid(gr, mutate.path),new GraphAgent(agent)," => ",mutate);
        agent.links = mutate.links;
        agent.nodes = mutate.nodes;
        agent.path = mutate.path;
        
    }
    
    /**
     * 
     */

}
