
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.noveltybois.GraphLoad;
import static lt.lb.noveltybois.Main.rng;
import lt.lb.noveltybois.tsp.genetic.TSPAgent;
import lt.lb.noveltybois.tsp.genetic.TSPGeneticSimulation;
import lt.lb.noveltybois.tsp.TSPSolution;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Lemmin
 */
public class TSPTest {

    private static Orgraph gr = GraphLoad.simpleGraph(10, 137);
    private static RandomDistribution rng = RandomDistribution.uniform(new Random());

    @BeforeClass
    public static void setup() {
        Log.main().async = false;
    }

    @AfterClass
    public static void close() {
        Log.close();
    }

    @Test
    public void pmxTest() {
        Log.print("pmxTest");

        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        TSPAgent t2 = TSPAgent.ofInts(List.of(3, 7, 5, 1, 6, 8, 2, 4), gr);

        Log.println("", t1, t2);

        Log.printLines(TSPSolution.crossoverPartiallyMapped(rng, gr, t1, t2));
    }

    @Test
    public void omTest() {
        Log.print("omTest");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        TSPAgent t2 = TSPAgent.ofInts(List.of(2, 4, 6, 8, 7, 5, 3, 1), gr);

        Log.println("", t1, t2);

        Log.printLines(TSPSolution.crossoverOrder(rng, gr, t1, t2));
    }

    @Test
    public void cxTest() {
        Log.print("cxTest");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        TSPAgent t2 = TSPAgent.ofInts(List.of(8, 5, 2, 1, 3, 6, 4, 7), gr);

        Log.println("", t1, t2);

        Log.printLines(TSPSolution.crossoverCycle(rng, gr, t1, t2));
    }

    @Test
    public void mutationNodeMove() {
        Log.print("mutationNodeMove");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        Log.print(TSPSolution.mutationNodeMove(rng, gr, t1));
    }

    @Test
    public void mutationNodeSwap() {
        Log.print("mutationNodeSwap");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        Log.print(TSPSolution.mutationNodeSwap(rng, gr, t1));
    }

    @Test
    public void mutationCentralInversion() {
        Log.print("mutationCentralInversion");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        Log.print(TSPSolution.mutationCentralInversion(rng, gr, t1));
    }

    @Test
    public void mutationInnerSequenceInversion() {
        Log.print("mutationInnerSequenceInversion");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        Log.print(TSPSolution.mutationInnerSequenceInversion(rng, gr, t1));
    }

    @Test
    public void mutationPathCutoff() {
        Log.print("mutationPathCutoff");
        TSPAgent t1 = TSPAgent.ofInts(List.of(1, 2, 3, 4, 5, 6, 7, 8), gr);
        Log.print(TSPSolution.mutationPathCutoff(rng, gr, t1));
    }

}
