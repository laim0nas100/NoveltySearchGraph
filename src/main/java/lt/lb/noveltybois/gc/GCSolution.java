/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.gc;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lt.lb.commons.F;
import lt.lb.commons.containers.values.IntegerValue;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.ComparatorBuilder;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.noveltybois.gc.genetic.GCAgent;

/**
 *
 * @author Lemmin
 */
public class GCSolution {
    
    
    /*
    * Crossovers
    */
    
    public static ArrayList<GCAgent> crossoverPartition(RandomDistribution rnd, Orgraph gr, GCAgent g1, GCAgent g2) {
        
        return new ArrayList<>();
    }
    

    public static int degreeOfSaturation(GCAgent g, Long id) {
        GNode node = g.graph.getNode(id).get();
        Set<Integer> usedColors = new HashSet<>();
        List<GLink> resolveLinkedTo = g.graph.resolveLinkedTo(node, p -> true);
        for (GLink link : resolveLinkedTo) {
            if (g.coloring.containsKey(link.nodeTo)) {
                usedColors.add(g.coloring.get(link.nodeTo));
            }
        }

        return usedColors.size();
    }

    public static int degree(GCAgent g, Long id) {
        GNode node = g.graph.getNode(id).get();
        return node.linksTo.size();
    }

    public static Set<Integer> linkedUsedColorSet(GCAgent g, Long id) {
        return g.graph.getNode(id).get().linksTo.stream().map(n -> {
            if (g.coloring.containsKey(n)) {
                return g.coloring.get(n);
            } else {
                return -1;
            }
        }).filter(c -> c >= 0).collect(Collectors.toSet());
    }

    public static int smallestColor(GCAgent g, Long id) {
        //check if colored
        if (g.coloring.containsKey(id)) {
            throw new Error(id + " is allready colored");
        }
        int color = 0;
        Set<Integer> linkedUsedColorSet = linkedUsedColorSet(g, id);
        while (linkedUsedColorSet.contains(color)) {
            color++;
        }

        return color;
    }

    public static ArrayList<Long> DSatur(RandomDistribution rnd, Orgraph gr, GCAgent g) {

        Set<Long> nodes = Sets.newHashSet(gr.nodes.keySet());

        ArrayList<Long> order = new ArrayList<>();
        Comparator<Long> cmp = new ComparatorBuilder<Long>()
                .thenComparingValue(n -> degreeOfSaturation(g, n)).reverse()
                .thenComparingValue(n -> degree(g, n)).reverse()
                .build();
        while (true) {
            //find biggest saturation
            Optional<Long> findFirst = nodes.stream().sorted(cmp).findFirst();

            if (findFirst.isEmpty()) {
                // assume graph is colored
                break;
            } else {
                Long nodeID = findFirst.get();
                int smallestColor = smallestColor(g, nodeID);
                g.coloring.put(nodeID, smallestColor);
                nodes.remove(nodeID);
                order.add(nodeID);
            }

        }
        
        return order;

    }

    /*
     * Similarity functions
     */
    public static double simLength(GCAgent a1, GCAgent a2) {
        double f1 = a1.colorsUsed();
        double f2 = a2.colorsUsed();
        double diff = Math.abs(f1 - f2);
        double max = Math.max(f1, f2);
        double min = Math.min(f1, f2);
        return Math.min(1, 4 * (1 - min / max));
    }

    public static double simColorPos(GCAgent a1, GCAgent a2) {
        IntegerValue sum = new IntegerValue(0);
        F.iterate(a1.coloring, (id, color) -> {
            if (Objects.equals(a2.coloring.get(id), color)) {
                sum.incrementAndGet();
            }
        });
        return sum.get() / a1.nodeSize();
    }

    public static double simIsomorficness(GCAgent a1, GCAgent a2) {

        Map<Integer, Set<Long>> colorSet1 = new HashMap<>();

        F.iterate(a1.coloring, (id, color) -> {
            colorSet1.computeIfAbsent(color, k -> new HashSet<>()).add(id);
        });

        Map<Integer, Set<Long>> colorSet2 = new HashMap<>();

        F.iterate(a2.coloring, (id, color) -> {
            colorSet2.computeIfAbsent(color, k -> new HashSet<>()).add(id);
        });
        HashSet<Set<Long>> commonSets = F.intersection(colorSet1.values(), colorSet1.values());
        double maxColor = (double) Math.max(colorSet1.size(), colorSet2.size());
        return commonSets.size() / maxColor;

    }
}
