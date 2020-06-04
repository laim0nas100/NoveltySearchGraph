package lt.lb.noveltybois.tsp.genetic;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lt.lb.commons.UUIDgenerator;
import lt.lb.commons.containers.caching.LazyValue;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.neurevol.evolution.NEAT.Agent;
import lt.lb.noveltybois.API;

/**
 *
 * @author laim0nas100
 */
public class TSPAgent extends Agent {

    public Orgraph graph;
    public Set<Long> nodes;
    public List<Long> path;
    public List<Long> tspPath;
    public LazyValue<List<GLink>> links;
    
    public Double pathWeight;

    public int pathSimpleLength() {
        return path.size();
    }

    public static TSPAgent ofInts(Collection<Integer> path, Orgraph gr) {
        return new TSPAgent(path.stream().mapToLong(a -> a).boxed().collect(Collectors.toList()), gr);
    }

    public TSPAgent(Collection<Long> path, Orgraph gr) {
        super();
        id = UUIDgenerator.nextUUID("GraphGenome");
        nodes = new HashSet<>(path);
        this.path = new LinkedList<>(path);
        tspPath = Lists.newArrayList(path);
        tspPath.add(tspPath.get(0));

        links = new LazyValue<>(() -> API.getLinks(this.tspPath, gr));
        this.graph = gr;

    }

    public TSPAgent(TSPAgent agent) {
        this(agent.path, agent.graph);
    }

    public String toString() {
        return path.toString();
    }

    public boolean isValid() {
        return path.size() == nodes.size();
    }

}
