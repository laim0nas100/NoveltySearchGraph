package lt.lb.noveltybois.tsp.genetic.original;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentBreeder;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.NamedMethod;
import lt.lb.noveltybois.tsp.TSPSolution;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class TSPAgentBreeder implements AgentBreeder<TSPAgent> {

    public double crossover_chance = 0.6;
    public RandomDistribution uniform;
    public RandomDistribution low;
    public Orgraph gr;
    public int population;
    public NamedMethod<ArrayList<TSPAgent>> method;
    

    public TSPAgentBreeder(Orgraph g, RandomDistribution uniform, double cross, int population, NamedMethod<ArrayList<TSPAgent>> method) {
        this.uniform = uniform;
        gr = g;
        this.crossover_chance = cross;
        this.population = population;
        this.method = method;
    }

    @Override
    public List<TSPAgent> breedChild(List<TSPAgent> agents) {
        int size = agents.size();
        TSPAgent child;
        if (size > 1 && uniform.nextDouble() > crossover_chance) {
            ArrayList<TSPAgent> crossover = crossover(agents);
            if (!crossover.isEmpty()) {
                return crossover;
            }
        }
        TSPAgent get = agents.get(uniform.nextInt(size));
        child = new TSPAgent(get);

        return Arrays.asList(child);
    }

    private ArrayList<TSPAgent> crossover(List<TSPAgent> list) {
        LinkedList<TSPAgent> parents = uniform.pickRandom(list, 2);
        TSPAgent p1 = parents.peekFirst();
        TSPAgent p2 = parents.peekLast();
        
        return this.method.invoke(uniform, gr, p1, p2);

    }

   @Override
    public Collection<TSPAgent> initializeGeneration() {
        ArrayList<TSPAgent> list = new ArrayList<>();
        for (int i = 0; i < population; i++) {
            List<GLink> path = PathGenerator.generateLongPathBidirectional(gr, uniform.pickRandom(gr.nodes.keySet()), PathGenerator.nearestNeighbour());
            TSPAgent agent = new TSPAgent(API.getNodesIDs(path), gr);
            list.add(agent);
            Log.print("is valid?", API.isPathValid(gr, agent.path), agent);
        }
        return list;
    }

}

