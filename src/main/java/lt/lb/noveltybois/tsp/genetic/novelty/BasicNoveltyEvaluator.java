/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.noveltybois.tsp.genetic.novelty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.noveltybois.NoveltyEvaluator;

/**
 *
 * @author Lemmin
 */
public abstract class BasicNoveltyEvaluator<Behaviour> implements NoveltyEvaluator<Behaviour> {

    public ThreadLocal<RandomDistribution> rng;
    Collection<Behaviour> toArchive = new LinkedBlockingDeque<>();
    public List<Behaviour> archive = new ArrayList<>();
    public int k = 30;
    public double archiveThreshold = 0;
    public double archiveThresholdChangeFactor = 1.0;
    public double archiveThresholdMin;

    public double defaultThreashold;
    public int noNewArchiveCount; // count number of generations in a row for which no individual added to archive.
    public int noNewArchiveGenerationsThreshold = 10;
    public int tooManyArchiveAdditionsThreshold;
    public double addProbability;

    
    
    public double testNovelty(Behaviour b, Collection<Behaviour> population) {
        // System.err.println(b);
        int totalSize = archive.size() + population.size();
        double[] dist = new double[totalSize];
        int i = 0;
        int inArchiveCount = 0;
        for (Behaviour b2 : archive) {
            dist[i] = distance(b, b2);
            assert (dist[i] >= 0 && dist[i] <= 1) : "Values returned by implementations of Behaviour.distanceFrom() must be in the range [0, 1] but a value of " + dist[i] + " was found.";
            if (dist[i] < 0.0000001) {
                inArchiveCount++;
            }
            i++;
        }
        assert population.size() > 0 : "The current population in NoveltySearch has zero size.";
        for (Behaviour b2 : population) {
            dist[i] = distance(b, b2);
            assert (dist[i] >= 0 && dist[i] <= 1) : "Values returned by implementations of Behaviour.distanceFrom() must be in the range [0, 1] but a value of " + dist[i] + " was found.";
            i++;
        }
        int kTemp = Math.min(totalSize, this.k); // how many neighbours to consider
        Arrays.sort(dist);
        double avgDist = 0;
        for (i = 0; i < kTemp; i++) {
            avgDist += dist[i];
        }
        avgDist /= kTemp;
        assert (avgDist >= 0 && avgDist <= 1) : "Values returned by testNovelty must be in the range [0, 1] but a value of " + avgDist + " was found.";

        // Don't add it if it's already in the archive k times (at which point adding it more times will have no 
        // effect on the average distance calculation but will consume resources (memory, cpu).
        if (inArchiveCount < k) {
            // If using probabilistic archive addition method.
            if (addProbability > 0) {
                if (rng.get().nextDouble() < addProbability) {
                    toArchive.add(b);
                }
            } else { // Using threshold archive addition method.
                if (archiveThreshold == 0) {
                    archiveThreshold = defaultThreashold;
                    archiveThresholdMin = archiveThreshold * 0.05;

                }
                // If the archive and toArchive queue don't contain a similar behaviour, add it to the archive.
                if (!containsSimilar(toArchive, b, archiveThreshold) && !containsSimilar(archive, b, archiveThreshold)) {
                    toArchive.add(b);
                }
            }
        }

        return avgDist;
    }

    public abstract double distance(Behaviour b1, Behaviour b2);

    protected boolean containsSimilar(Collection<Behaviour> behaviours, Behaviour b1, double threshold) {
        if (behaviours.isEmpty()) {
            return false;
        }
        for (Behaviour b2 : behaviours) {
            if (distance(b1, b2) < threshold) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method must be called when the population has been evaluated.
     * Individuals with novelty greater than the threshold (see
     * {@link NoveltySearch#ARCHIVE_THRESHOLD}) will be added to the archive.
     * The archive threshold is adjusted if no new individuals have been added
     * for 10 generations or if more than (popSize/100) individuals have been
     * added. The record of behaviours for the current population is cleared.
     */
    public void finishedEvaluation() {
        // If not using probabilistic archive addition method, adjust threshold if necessary to maintain desired
        // addition rate.
        if (addProbability == 0) {
            if (toArchive.isEmpty()) {
                noNewArchiveCount++;
                if (noNewArchiveCount == noNewArchiveGenerationsThreshold) {
                    archiveThreshold /= archiveThresholdChangeFactor;
                    if (archiveThreshold < archiveThresholdMin) {
                        archiveThreshold = archiveThresholdMin;
                    }
                    noNewArchiveCount = 0;
                    // System.err.println("atd: " + archiveThreshold + "    (" + archiveThresholdMin + ")");
                }

            } else {
                noNewArchiveCount = 0;
                if (toArchive.size() > tooManyArchiveAdditionsThreshold) {
                    archiveThreshold *= archiveThresholdChangeFactor;
                    // System.err.println("ati: " + archiveThreshold);
                }
            }
        }

        archive.addAll(toArchive);

        toArchive.clear();
    }

    public int getArchiveSize() {
        return archive.size();
    }
}
