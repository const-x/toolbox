package idv.const_x.utils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description <pre>
 * 活跃数据缓存器
 * </pre>
 * @Author const.x
 * @Date 2025-11-19
 */
public class ActiveCacheMap<K, V> {
    private final int capacity;
    private final Map<K, V> dataMap;
    private final LinkedHashSet<K> activeKeys; // 保持插入/访问顺序，且无重复

    public ActiveCacheMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.dataMap = new HashMap<>();
        this.activeKeys = new LinkedHashSet<>();
    }

    public V get(K key) {
        if (!dataMap.containsKey(key)) {
            return null;
        }
        // 更新活跃顺序：先移除再添加到末尾
        activeKeys.remove(key);
        activeKeys.add(key);
        return dataMap.get(key);
    }

    public void put(K key, V value) {
        if (dataMap.containsKey(key)) {
            // 已存在：更新值，并更新活跃顺序
            dataMap.put(key, value);
            activeKeys.remove(key);
            activeKeys.add(key);
        } else {
            // 新增：检查容量
            if (activeKeys.size() >= capacity) {
                // 移除最不活跃的 key（即第一个）
                K oldest = activeKeys.iterator().next();
                activeKeys.remove(oldest);
                dataMap.remove(oldest);
            }
            dataMap.put(key, value);
            activeKeys.add(key);
        }
    }

    public boolean containsKey(K key) {
        return dataMap.containsKey(key);
    }

    public int size() {
        return dataMap.size();
    }

    public Set<K> getActiveKeysInOrder() {
        // 返回从最不活跃 → 最活跃的顺序（也可反转）
        return new LinkedHashSet<>(activeKeys);
    }

    public void remove(K key) {
        dataMap.remove(key);
        activeKeys.remove(key);
    }

    public void clear() {
        dataMap.clear();
        activeKeys.clear();
    }
    
}