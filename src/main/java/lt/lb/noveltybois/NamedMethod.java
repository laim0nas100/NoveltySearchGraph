package lt.lb.noveltybois;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;
import lt.lb.commons.F;
import lt.lb.commons.func.Lambda;
import lt.lb.commons.misc.rng.RandomDistribution;

/**
 *
 * @author Lemmin
 */
public class NamedMethod<R> {

    public final String name;
    public final Function<Object[], R> func;

    public NamedMethod(Class cls, String name, String methodName) {
        Method staticMethod = Stream.of(cls.getMethods()).filter(m -> m.getName().equals(methodName))
                .filter(m -> Modifier.isStatic(m.getModifiers())).findFirst().get();
        this.name = name;
        this.func = (args) -> {
            return (R) F.unsafeCall(() -> {
                return staticMethod.invoke(null, args);
            });
        };

    }

    public NamedMethod(Class cls, String methodName) {
        this(cls, methodName, methodName);
    }

    public NamedMethod(String name, Function<Object[], R> func) {
        this.name = name;
        this.func = func;
    }

    public static <R> NamedMethod<R> combined(String name, ThreadLocal<RandomDistribution> rng, Iterable<NamedMethod<R>> combined) {
        ArrayList<NamedMethod<R>> methods = Lists.newArrayList(combined);
        Function<Object[], R> func = (args) -> {
            return rng.get().pickRandom(methods).invoke(args);
        };

        return new NamedMethod<>(name, func);
    }

    public R invoke(Object... args) {
        return func.apply(args);
    }
}
