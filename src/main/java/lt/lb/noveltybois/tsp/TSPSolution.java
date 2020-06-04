/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp;

import com.google.common.collect.Lists;
import java.util.*;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.graphtheory.*;
import lt.lb.commons.F;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.containers.tuples.Tuples;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.graphtheory.paths.PathGenerator.ILinkPicker;
import lt.lb.commons.iteration.Iter;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.noveltybois.API;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class TSPSolution {

    /*
     *
     * Crossovers
     *
     */
    private static Optional<Long> pairReturnOtherIfEqual(Pair<Long> pair, Long val) {
        if (Objects.equals(pair.g1, val)) {
            return Optional.of(pair.g2);
        }
        if (Objects.equals(pair.g2, val)) {
            return Optional.of(pair.g1);
        }
        return Optional.empty();
    }

    private static Optional<Long> lastValueInPairs_(List<Pair<Long>> pairs, Long starting, Set<Long> visited) {
        visited.add(starting);
        Optional<Pair<Long>> findFirst = pairs.stream().filter(p -> {

            Optional<Long> opt = pairReturnOtherIfEqual(p, starting);
            if (!opt.isPresent()) {
                return false;
            }

            if (visited.contains(opt.get())) {
                return false;
            }
            return true;

        }).findFirst();

        if (!findFirst.isPresent()) {
            return Optional.of(starting);
        } else {
            return findFirst.flatMap(p -> pairReturnOtherIfEqual(p, starting))
                    .flatMap(newStart -> lastValueInPairs_(pairs, newStart, visited));
        }
    }

    private static Optional<Long> lastValueInPairs(List<Pair<Long>> pairs, Long starting) {
        return lastValueInPairs_(pairs, starting, new HashSet<>());
    }

    public static ArrayList<TSPAgent> crossoverPartiallyMapped(RandomDistribution rnd, Orgraph gr, TSPAgent g1, TSPAgent g2) {

        List<Pair<Long>> middlePart = new ArrayList<>();
        int size = g1.pathSimpleLength();
        Integer cut1 = rnd.nextInt(0, size - 1);
        Integer cut2 = rnd.nextInt(cut1, size);

        Long[] c1 = new Long[size];
        Long[] c2 = new Long[size];

        for (int i = cut1; i < cut2; i++) {
            Long id1 = g1.path.get(i);
            Long id2 = g2.path.get(i);
            Pair<Long> pair = new Pair<>(id1, id2);
            middlePart.add(pair);
            c1[i] = id2;
            c2[i] = id1;
        }

        //complete first half
        for (int i = 0; i < cut1; i++) {
            Long id1 = g1.path.get(i);
            Long id2 = g2.path.get(i);
            if (ArrayOp.contains(c1, id1)) {
                id1 = lastValueInPairs(middlePart, id1).get();
            }
            if (ArrayOp.contains(c2, id2)) {
                id2 = lastValueInPairs(middlePart, id2).get();
            }
            c1[i] = id1;
            c2[i] = id2;

        }

        //complete second half
        for (int i = cut2; i < size; i++) {
            Long id1 = g1.path.get(i);
            Long id2 = g2.path.get(i);
            if (ArrayOp.contains(c1, id1)) {
                id1 = lastValueInPairs(middlePart, id1).get();
            }
            if (ArrayOp.contains(c2, id2)) {
                id2 = lastValueInPairs(middlePart, id2).get();
            }
            c1[i] = id1;
            c2[i] = id2;

        }
        ArrayList<TSPAgent> list = new ArrayList<>(2);
        list.add(new TSPAgent(List.of(c1), gr));
        list.add(new TSPAgent(List.of(c2), gr));

        return list;

    }

    private static <T> Optional<Tuple<Integer, T>> findLooping(List<T> list, int midPoint, Iter<T> iter) {
        Optional<Tuple<Integer, T>> find1 = F.find(list, midPoint, -1, iter);
        if (find1.isPresent()) {
            return find1;
        }
        return F.find(list, 0, midPoint, iter);
    }

    public static ArrayList<TSPAgent> crossoverOrder(RandomDistribution rnd, Orgraph gr, TSPAgent g1, TSPAgent g2) {

        int size = g1.pathSimpleLength();
        Integer cut1 = rnd.nextInt(0, size - 1);
        Integer cut2 = rnd.nextInt(cut1, size);

        Long[] c1 = new Long[size];
        Long[] c2 = new Long[size];

        for (int i = cut1; i < cut2; i++) {
            Long id1 = g1.path.get(i);
            Long id2 = g2.path.get(i);
            c1[i] = id1;
            c2[i] = id2;
        }

        //complete second half
        for (int i = cut2; i < size; i++) {
            Long id1 = findLooping(g2.path, i, (index, item) -> {
                return !ArrayOp.contains(c1, item);
            }).map(m -> m.g2).get();

            Long id2 = findLooping(g1.path, i, (index, item) -> {
                return !ArrayOp.contains(c2, item);
            }).map(m -> m.g2).get();

            c1[i] = id1;
            c2[i] = id2;
        }

        //complete first half
        for (int i = 0; i < cut1; i++) {
            Long id1 = findLooping(g2.path, i, (index, item) -> {
                return !ArrayOp.contains(c1, item);
            }).map(m -> m.g2).get();

            Long id2 = findLooping(g1.path, i, (index, item) -> {
                return !ArrayOp.contains(c2, item);
            }).map(m -> m.g2).get();

            c1[i] = id1;
            c2[i] = id2;

        }

        ArrayList<TSPAgent> list = new ArrayList<>(2);
        list.add(new TSPAgent(List.of(c1), gr));
        list.add(new TSPAgent(List.of(c2), gr));

        return list;

    }

    private static Long[] populateCycle(int size, List<Long> p1, List<Long> p2) {
        Long[] c1 = new Long[size];

        c1[0] = p1.get(0);
        int i = 0;
        Long value = p2.get(0);
        while (true) {
            if (ArrayOp.contains(c1, value)) {
                i = ArrayOp.indexOf(c1, null);
                if (i == -1) {
                    break;
                }
                value = p2.get(i);
                c1[i] = value;
            } else {
                i = p1.indexOf(value);
                c1[i] = value;
                value = p2.get(i);
            }

        }
        return c1;
    }

    public static ArrayList<TSPAgent> crossoverCycle(RandomDistribution rnd, Orgraph gr, TSPAgent g1, TSPAgent g2) {

        int size = g1.pathSimpleLength();

        return Lists.newArrayList(new TSPAgent(List.of(TSPSolution.populateCycle(size, g1.path, g2.path)), gr),
                new TSPAgent(List.of(TSPSolution.populateCycle(size, g2.path, g1.path)), gr)
        );

    }

    /*
     * Mutations
     */
    public static TSPAgent mutationNodeMove(RandomDistribution rng, Orgraph gr, TSPAgent g) {
        ArrayList<Long> list = new ArrayList<>(g.path);
        int from = rng.nextInt(list.size());
        Long removed = list.remove(from);
        int to = rng.nextInt(list.size());
        list.add(to, removed);
        return new TSPAgent(list, gr);
    }

    public static TSPAgent mutationNodeSwap(RandomDistribution rng, Orgraph gr, TSPAgent g) {
        ArrayList<Long> list = new ArrayList<>(g.path);
        int from = rng.nextInt(list.size());
        int to = rng.nextInt(list.size());
        F.swap(list, from, to);
        return new TSPAgent(list, gr);
    }

    public static TSPAgent mutationCentralInversion(RandomDistribution rng, Orgraph gr, TSPAgent g) {
        int size = g.pathSimpleLength();
        LinkedList<Long> list = new LinkedList<>();
        int cut = rng.nextInt(size);
        for (int i = cut; i < size; i++) {
            Long node = g.path.get(i);
            list.addFirst(node);
        }
        for (int i = 0; i < cut; i++) {
            Long node = g.path.get(i);
            list.addFirst(node);
        }

        return new TSPAgent(list, gr);
    }

    public static TSPAgent mutationInnerSequenceInversion(RandomDistribution rng, Orgraph gr, TSPAgent g) {
        int size = g.pathSimpleLength();
        ArrayList<Long> list = new ArrayList<>(size);
        int cut1 = rng.nextInt(size - 1);
        int cut2 = rng.nextInt(cut1, size);
        for (int i = 0; i < cut1; i++) {
            list.add(g.path.get(i));

        }
        for (int i = cut2 - 1; i >= cut1; i--) {
            list.add(g.path.get(i));
        }
        for (int i = cut2; i < size; i++) {
            list.add(g.path.get(i));
        }

        return new TSPAgent(list, gr);
    }

    public static TSPAgent mutationPathCutoff(RandomDistribution rnd, Orgraph gr, TSPAgent g) {

//        if(true){
//             return new GraphAgent(g.path, gr);
//        }
        if (g.nodes.isEmpty()) {
            throw new IllegalArgumentException(g.id + g + " is empty");
        }

//        Log.print("\nValid before?", API.isPathValid(gr, g.path), g.path);
        Integer indexOf = rnd.nextInt(g.path.size());
        boolean left = rnd.nextBoolean();
        long startNode = g.path.get(indexOf);
        List<Long> nodes = new ArrayList<>();
        F.iterate(g.path, (i, n) -> {
            if (left) {
                if (i <= indexOf) {
                    nodes.add(n);
                }
            } else if (i >= indexOf) {
                nodes.add(n);
            }
        });
        if (!left) {
            Collections.reverse(nodes);
        }
//        Log.print("Mutation node", startNode, "@", indexOf, "left?", left);
//        Log.print("Mutate:", g.path);
//        Log.print("Cut path", nodes);
        ArrayList<GLink> path = API.getLinks(nodes, gr);
//        Log.print("Got path", path);
        Set<Long> visited = new HashSet<>(nodes);

        List<Tuple<Double, ILinkPicker>> pickers = Arrays.asList(
                Tuples.create(1d, PathGenerator.nearestNeighbour())
        //                ,Tuples.create(1d, PathGenerator.nodeWeightDistributed(rnd, false))
        );

        List<GLink> genericUniquePathVisitContinued = PathGenerator.genericUniquePathVisitContinued(gr, startNode, path, visited, API.probabilityJoinedPickers(pickers, rnd));
        ArrayList<Long> nodesIDs = API.getNodesIDs(genericUniquePathVisitContinued);
//        Log.print("New nodes:", nodesIDs);
        return new TSPAgent(nodesIDs, gr);
    }

    /*
     * Similarity functions
     */
    public static double simLongestCommonsSubseq(TSPAgent a1, TSPAgent a2) {
        int longestCommonSubsequence = Algorithms.longestCommonSubsequence(a1.path, a2.path);
        double size = a1.pathSimpleLength();
        return longestCommonSubsequence / size;
    }

    public static double simCommonLinks(TSPAgent a1, TSPAgent a2) {
        List<GLink> links1 = a1.links.get();
        List<GLink> links2 = a2.links.get();

        int size = F.intersection(links1, links2, GLink::equalNodesBidirectional).size();
        double pathSize = links1.size();
        return size / pathSize;
    }

    public static double simLength(TSPAgent a1, TSPAgent a2) {
        double f1 = a1.pathWeight;
        double f2 = a2.pathWeight;
        double diff = Math.abs(f1 - f2);
        double max = Math.max(f1, f2);
        double min = Math.min(f1, f2);
        return Math.min(1, 4 * (1 - min / max));
    }

}
