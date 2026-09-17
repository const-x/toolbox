package idv.const_x.utils;

public class ActiveCacheMapTest {

    public static void main(String[] args) {
        shouldRemoveKeysFromKeyCountWhenExplicitlyRemoved();
        shouldRemoveEvictedKeyFromKeyCount();
        shouldKeepKeyCountInSyncForComputeAndMergeOperations();
    }

    private static void shouldRemoveKeysFromKeyCountWhenExplicitlyRemoved() {
        ActiveCacheMap<String, Integer> cache = new ActiveCacheMap<>(3);
        cache.put("a", 1);
        cache.put("b", 2);

        check(cache.maxKeySize() == 2);
        check(Integer.valueOf(1).equals(cache.remove("a")));
        check(cache.maxKeySize() == 1);
        check(!cache.containsKey("a"));

        check(cache.remove("b", 2));
        check(cache.maxKeySize() == 0);
    }

    private static void shouldRemoveEvictedKeyFromKeyCount() {
        ActiveCacheMap<String, Integer> cache = new ActiveCacheMap<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        check(cache.maxKeySize() == 2);
        check(!cache.containsKey("a"));
        check(cache.containsKey("b"));
        check(cache.containsKey("c"));
    }

    private static void shouldKeepKeyCountInSyncForComputeAndMergeOperations() {
        ActiveCacheMap<String, Integer> cache = new ActiveCacheMap<>(4);

        cache.computeIfAbsent("a", key -> 1);
        cache.computeIfAbsent("missing", key -> null);
        check(cache.maxKeySize() == 1);

        cache.computeIfPresent("a", (key, value) -> null);
        cache.compute("b", (key, value) -> 2);
        cache.compute("b", (key, value) -> null);
        cache.merge("c", 3, Integer::sum);
        cache.merge("c", 1, (left, right) -> null);

        check(cache.maxKeySize() == 0);
    }

    private static void check(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }
}
