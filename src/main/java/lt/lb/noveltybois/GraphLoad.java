/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.util.List;
import java.util.Random;
import lt.lb.commons.F;
import lt.lb.commons.containers.caching.LazyValue;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;

/**
 *
 * @author Lemmin
 */
public class GraphLoad {

    public static LazyValue<Orgraph> ft53 = new LazyValue<>(() -> {
        return F.unsafeCall(() -> {
            List<String> lines = Resources.readLines(GraphLoad.class.getResource("/ft53.txt"), Charsets.UTF_8);
            return GraphReadingAPI.readFullMatrix(lines, "9999999", 53);
        });

    });

    public static Orgraph simpleGraph(int size, int seed) {
        Orgraph gr = new Orgraph();
        RandomDistribution rng = RandomDistribution.uniform(new Random(seed));

        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                    int w = rng.nextInt(100, 500);
                    gr.add2wayLink(gr.newLink(i, j, w));
            }
        }

        return gr;
    }
    
    public static Orgraph specialGraph(int k, int M){
        return new Orgraph();
    }
}
