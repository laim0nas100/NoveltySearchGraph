package lt.lb.noveltybois.gc.genetic;

import java.util.HashMap;
import java.util.Map;
import lt.lb.commons.UUIDgenerator;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.neurevol.evolution.NEAT.Agent;

/**
 *
 * @author laim0nas100
 */
public class GCAgent extends Agent {

    public Orgraph graph;
    public Map<Long, Integer> coloring;

    public int nodeSize() {
        return coloring.size();
    }

    public int colorsUsed() {
        return (int) coloring.values().stream().distinct().count();
    }

    public static GCAgent ofInts(Orgraph gr, Integer... colors) {
        HashMap<Long, Integer> map = new HashMap<>();
        for (int i = 0; i < colors.length; i++) {
            map.put((long) i, colors[i]);
        }

        return new GCAgent(map, gr);

    }

    public GCAgent(Map<Long, Integer> coloring, Orgraph gr) {
        super();

        id = UUIDgenerator.nextUUID("GraphGenome");
        this.coloring = new HashMap<>(coloring);
        this.graph = gr;

    }

    public GCAgent(GCAgent agent) {
        this(agent.coloring, agent.graph);
    }

    public String toString() {
        return coloring.toString();
    }

}
