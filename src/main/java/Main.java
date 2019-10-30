import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;


public class Main {

    public static <T, R> Function<T, R> memoize(Function<T, R> function) {

        final var argumentResultMap = new ConcurrentHashMap<T, R>();

        return t -> argumentResultMap.computeIfAbsent(t, function);
    }

    public static Duration measureTimeOf(Runnable task) {

        final var start = System.nanoTime();

        task.run();

        final var end = System.nanoTime();

        return Duration.ofNanos(end - start);
    }

    public static void main(String[] args) {

        final Function<Double, Double> sinusCosinus = x -> Math.pow(x, 3) * Math.acos(Math.atan(Math.sin(Math.cos(x))));

        final var sinusCosinusMemoized = memoize(sinusCosinus);

        final Supplier<Stream<Double>> doublesSupplier = () -> DoubleStream
                .iterate(1.0d, v -> v < 100.0d ? v + 0.01d : 0.0d)
                .limit(100000000)
                .boxed();

        final Consumer<Double> nothing = d -> {};

        var firstDuration = measureTimeOf(() -> doublesSupplier.get().map(sinusCosinus).forEach(nothing));
        var secondDuration = measureTimeOf(() -> doublesSupplier.get().map(sinusCosinusMemoized).forEach(nothing));

        System.out.println("First case: " + firstDuration);
        System.out.println("Second case: " + secondDuration);

    }
}
