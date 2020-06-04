/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.novelty;

import lt.lb.noveltybois.HierachicalNoveltyEvaluator;
import lt.lb.noveltybois.NamedMethod;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;

/**
 *
 * @author Lemmin
 */
public class TSPNoveltyEvaluator extends HierachicalNoveltyEvaluator<TSPAgent> {

    public final NamedMethod<Double> method;

    public TSPNoveltyEvaluator(NamedMethod<Double> distance) {
        this.method = distance;
    }

    @Override
    public double distance(TSPAgent b1, TSPAgent b2) {
        return method.invoke(b1, b2);
    }

}
