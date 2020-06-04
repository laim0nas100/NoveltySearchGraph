/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.gc.genetic.original;

import lt.lb.noveltybois.tsp.genetic.original.*;
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
import lt.lb.noveltybois.gc.genetic.GCAgent;

/**
 *
 * @author laim0nas100
 */
public class GCAgentBreeder implements AgentBreeder<GCAgent> {

    public double crossover_chance = 0.6;
    public RandomDistribution uniform;
    public RandomDistribution low;
    public Orgraph gr;
    public int population;

    public GCAgentBreeder(Orgraph g, RandomDistribution uniform, double cross, int population) {
        this.uniform = uniform;
        gr = g;
        this.crossover_chance = cross;
        this.population = population;
    }

    @Override
    public List<GCAgent> breedChild(List<GCAgent> agents) {
        int size = agents.size();
        GCAgent child;
        if (size > 1 && uniform.nextDouble() > crossover_chance) {
            ArrayList<GCAgent> crossover = partiallyMappedCrossover(agents);
            if (!crossover.isEmpty()) {
                return crossover;
            }
        }
        GCAgent get = agents.get(uniform.nextInt(size));
        child = new GCAgent(get);

        return Arrays.asList(child);
    }

    private ArrayList<GCAgent> partiallyMappedCrossover(List<GCAgent> list) {
        LinkedList<GCAgent> parents = uniform.pickRandom(list, 2);
        
        return Lists.newArrayList();

    }

   @Override
    public Collection<GCAgent> initializeGeneration() {
        ArrayList<GCAgent> list = new ArrayList<>();
        return list;
    }

}


/*
7, 53, 81, 90, 12, 39, 49, 67, 18, 29, 48, 55, 42
*/
