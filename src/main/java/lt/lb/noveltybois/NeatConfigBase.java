/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois;

import java.util.concurrent.Executor;
import lt.lb.neurevol.evolution.Control.NEATConfig;
import lt.lb.neurevol.evolution.NEAT.Agent;
import lt.lb.neurevol.evolution.NEAT.Species;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentBreeder;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMutator;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSimilarityEvaluator;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSorter;
import lt.lb.neurevol.evolution.NEAT.interfaces.Pool;

/**
 *
 * @author Lemmin
 */
public class NeatConfigBase<T extends Agent> implements NEATConfig<T> {

    public Pool<T> pool;
    public AgentBreeder<T> agentBreeder;
    public AgentMutator<T> agentMutator;
    public AgentSorter<T> agentSorter;
    public AgentSimilarityEvaluator<T> agentSimilarityEvaluator;
    public Executor executor;

    @Override
    public Pool<T> getPool() {
        return pool;
    }

    @Override
    public AgentBreeder<T> getBreeder() {
        return agentBreeder;
    }

    @Override
    public AgentMutator<T> getMutator() {
        return agentMutator;
    }

    @Override
    public AgentSorter<T> getSorter() {
        return agentSorter;
    }

    @Override
    public AgentSimilarityEvaluator<T> getSimilarityEvaluator() {
        return agentSimilarityEvaluator;
    }

    @Override
    public Species<T> newSpecies() {
        Species<T> s = new Species<>();
        s.conf = this;
        return s;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

}
