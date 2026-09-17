package idv.const_x.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Description <pre>
 *   活跃数据缓存map
 * </pre>
 * @Author const.x
 * @Date 2025-12-04
 */
public class ActiveCacheMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxCached;


    private final Set<K> allKeys = new HashSet<>();

    /**
     * 活跃数据缓存map
     * @param maxCached 最大缓存数量 超过则清除最旧的数据
     */
    public ActiveCacheMap(int maxCached) {
        // accessOrder = true 表示按访问顺序排序（LRU）
        super(16, 0.75f, true);
        this.maxCached = maxCached;
    }

    public ActiveCacheMap(int initialCapacity,int maxCached) {
        // accessOrder = true 表示按访问顺序排序（LRU）
        super(initialCapacity, 0.75f, true);
        this.maxCached = maxCached;
    }


    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean remove = size() > maxCached;
        if (remove) {
            allKeys.remove(eldest.getKey());
        }
        return remove; // 超出容量时自动删除最旧 entry
    }

    @Override
    public void clear() {
        super.clear();
        allKeys.clear();
    }

    @Override
    public V put(K key, V value) {
        allKeys.add(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        allKeys.addAll(m.keySet());
        super.putAll(m);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V result = super.putIfAbsent(key, value);
        allKeys.add(key);
        return result;
    }

    @Override
    public V remove(Object key) {
        V result = super.remove(key);
        allKeys.remove(key);
        return result;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            allKeys.remove(key);
        }
        return removed;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V result = super.computeIfAbsent(key, mappingFunction);
        if (containsKey(key)) {
            allKeys.add(key);
        } else {
            allKeys.remove(key);
        }
        return result;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V result = super.computeIfPresent(key, remappingFunction);
        if (containsKey(key)) {
            allKeys.add(key);
        } else {
            allKeys.remove(key);
        }
        return result;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        V result = super.compute(key, remappingFunction);
        if (containsKey(key)) {
            allKeys.add(key);
        } else {
            allKeys.remove(key);
        }
        return result;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        V result = super.merge(key, value, remappingFunction);
        if (containsKey(key)) {
            allKeys.add(key);
        } else {
            allKeys.remove(key);
        }
        return result;
    }

    /**
     * 总共缓存过的key数量
     * @return
     */
    public int maxKeySize(){
        return allKeys.size();
    }
}
