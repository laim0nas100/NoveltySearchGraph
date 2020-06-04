package lt.lb.noveltybois;

import java.util.Deque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lt.lb.commons.F;
import lt.lb.commons.iteration.ReadOnlyIterator;
import lt.lb.commons.iteration.ChildrenIteratorProvider;
import lt.lb.commons.misc.rng.RandomDistribution;

/**
 * @author Lemmin
 */
public abstract class HierachicalNoveltyEvaluator<Behaviour> implements NoveltyEvaluator<Behaviour>, ChildrenIteratorProvider<HierachicalNoveltyEvaluator.NoveltyNode<Behaviour>> {

    public double alpha = 1;
    public double beta = 0.2;
    public double epsilon = 0.05;
    public int maxPerLeaf = 50;
    private AtomicBoolean settingRoot = new AtomicBoolean(false);
    public AtomicLong distanceComputed = new AtomicLong(0);
    CompletableFuture<NoveltyNode<Behaviour>> rootNode = new CompletableFuture<>();
    private NoveltyNode<Behaviour> root;
    public static class NoveltyNode<B> {

        public final B node;
        public volatile Deque<NoveltyNode<B>> children = new ConcurrentLinkedDeque<>();

        public NoveltyNode(B node) {
            this.node = node;
        }

        public int size() {
            int size = 1;
            for (NoveltyNode<B> n : children) {
                size += n.size();
            }
            return size;
        }
    }

    public abstract double distance(Behaviour b1, Behaviour b2);

    @Override
    public long timesDistanceComputed() {
        return distanceComputed.get();
    }

    
    
    private double mainDistance(Behaviour b1, Behaviour b2){
        distanceComputed.incrementAndGet();
        return distance(b1, b2);
    }
    
    
    @Override
    public double testNovelty(Behaviour b) {
        if(root != null){
            return testNovelty(b, root, 0, distance(b, root.node));
        }
        if(!rootNode.isDone()){
            if(settingRoot.compareAndSet(false,true)){
                root = new NoveltyNode<>(b);
                rootNode.complete(root);
                
                return 1d;
            }
        }

        return F.unsafeCall(()->{
            return testNovelty(b, rootNode.get(), 0, distance(b, rootNode.get().node));
        });

    }

    private double testNovelty(Behaviour b, NoveltyNode<Behaviour> node, int level, double parentDistance) {
        Double threshold = alpha - (level * beta);// too similar to be usefull
        if(epsilon > threshold){
            return epsilon;
        }
        if (node.children.isEmpty()) {
            node.children.add(new NoveltyNode<>(b));
            if(level == 0){
                return parentDistance;
            }else{
                return parentDistance * level;
            }
        }
        Double avgDist = 0d;
        Double minDistance = null;
        Double maxDistance = null;
        NoveltyNode<Behaviour> maxSimilar = null;
        int size = 0;
        for (NoveltyNode<Behaviour> n : node.children) {
            double dis = mainDistance(b, n.node);
            if (minDistance == null) {
                minDistance = dis;
                maxDistance = dis;
                maxSimilar = n;

            }

            if (minDistance > dis) {
                minDistance = dis;
                maxSimilar = n;
            }

            if (maxDistance < dis) {
                maxDistance = dis;
            }

            size++;
            avgDist += dis;

        }
        avgDist = avgDist / size;
//        Log.print("Params Level:" + level + " Thresh:" + threshold, " Avg:" + avgDist + " Min:" + minDistance+" Size:"+size);
        if (minDistance <= threshold && threshold > 0) {
            return testNovelty(b, maxSimilar, level + 1, minDistance);
        } else {
            if (minDistance <= epsilon || size >= maxPerLeaf) {
//                Log.print("Dont include");
                return minDistance; // found something very similar dont include
            } else {
//                Log.print("Include");
                node.children.add(new NoveltyNode<>(b)); // not good enough to exp
                return avgDist*level;
            }

        }

    }

    @Override
    public int size() {
        if (rootNode.isDone()) {
            return F.unsafeCall(()->{
                return rootNode.get().size();
            });

        } else {
            return 0;
        }
    }

    public ReadOnlyIterator<NoveltyNode<Behaviour>> getChildrenIterator(NoveltyNode<Behaviour> item) {
        return ReadOnlyIterator.of(item.children);
    }


}
