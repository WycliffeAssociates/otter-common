package org.wycliffeassociates.otter.common.collections.multimap

class MultiMap<K, V> : HashMap<K, MutableSet<V>>() {
    fun put(key: K, value: V): Boolean {
        return this.getOrPut(key) { mutableSetOf() }.add(value)
    }

    fun kvSequence(): Sequence<Pair<K, V>> {
        return asSequence()
            .flatMap { (k, vs) -> vs.map { v -> k to v }.asSequence() }
    }
}
