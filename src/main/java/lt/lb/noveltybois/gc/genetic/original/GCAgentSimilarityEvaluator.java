/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.gc.genetic.original;

import lt.lb.noveltybois.tsp.genetic.original.*;
import java.util.Collection;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSimilarityEvaluator;
import lt.lb.noveltybois.gc.genetic.GCAgent;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author laim0nas100
 */
public class GCAgentSimilarityEvaluator implements AgentSimilarityEvaluator<GCAgent> {

    public int intersection(Collection c1, Collection c2) {
        int count = 0;
        for (Object ob : c1) {
            count += c2.contains(ob) ? 1 : 0;
        }
        return count;
    }

    // use common nodes
    @Override
    public double similarity(GCAgent g1, GCAgent g2) {
//        int combinedNodeCount = g1.nodes.size() + g2.nodes.size();
//        double inter = intersection(g1.nodes, g2.nodes);
//        double iRatio = combinedNodeCount / (inter + 1);
// all the same
        return 1;

    }

}
