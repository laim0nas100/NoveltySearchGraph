/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic;

import java.util.ArrayList;
import lt.lb.noveltybois.NamedMethod;

/**
 *
 * @author laim0nas100
 */
public class TSPGeneticSimulationParams {
    public int maxStagnation = 50;
    public int iterations = 100;
    public int population = 40;
    public double initSimilarity = 0.5;
    public int maxSpecies = 10;
    public int distinctSpecies = 5;
    public double crossoverChance = 0.5;
    
    public NamedMethod<TSPAgent> mutator;
    public NamedMethod<ArrayList<TSPAgent>> crossover;
    public NamedMethod<Double> similarity;
}
