/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.novelty;

import lt.lb.noveltybois.HierachicalNoveltyEvaluator;
import java.util.ArrayList;
import lt.lb.commons.F;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.iteration.ReadOnlyIterator;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author Lemmin
 */
public class TSPLinksNoveltyEvaluator extends HierachicalNoveltyEvaluator<TSPAgent> {

    @Override
    public double distance(TSPAgent b1, TSPAgent b2) {

        ArrayList<GLink> intersection = F.intersection(b1.links.get(), b2.links.get(), GLink::equalNodesBidirectional);

        return 1 - (double) intersection.size()/ b2.links.get().size();
    }



}
