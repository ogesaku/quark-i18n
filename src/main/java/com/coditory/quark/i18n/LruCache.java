package com.coditory.quark.i18n;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.coditory.quark.i18n.Preconditions.expect;

final class LruCache<K, V> {
    private final ConcurrentHashMap<K, Node> map = new ConcurrentHashMap<>();
    private final int capacity;
    private Node first;
    private Node last;

    public LruCache() {
        this(1024);
    }

    public LruCache(int capacity) {
        expect(capacity > 0, "Expected capacity > 0. Got: " + capacity);
        this.capacity = capacity;
    }

    synchronized void put(K key, V value) {
        if (value == null) {
            return;
        }
        if (map.size() == capacity) {
            remove(first.key);
        }
        Node node = new Node(key, value);
        map.put(key, node);
        if (last != null) {
            last.prev = node;
            node.next = last;
        } else {
            first = node;
        }
        last = node;
    }

    synchronized void remove(K key) {
        Node node = map.remove(key);
        if (node == null) {
            return;
        }
        if (node == first && node == last) {
            first = null;
            last = null;
        } else if (node == first) {
            first = node.prev;
            if (first != null) {
                first.next = null;
            }
        } else if (node == last) {
            last = node.next;
            if (last != null) {
                last.prev = null;
            }
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    synchronized void clear() {
        map.clear();
        first = null;
        last = null;
    }

    V get(K key) {
        Node node = map.get(key);
        if (node == null) {
            return null;
        }
        if (map.size() >= capacity) {
            // up-voting is eventually consistent
            upvote(key);
        }
        return node.value;
    }

    V computeIfAbsent(K key, Function<K, V> creator) {
        V value = get(key);
        // creator can be executed multiple times in case of race condition
        if (value == null) {
            value = creator.apply(key);
            put(key, value);
        }
        return value;
    }

    private synchronized void upvote(K key) {
        Node node = map.get(key);
        if (node == last) {
            return;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node == first) {
            first = node.prev;
        }
        last.next = node;
        node.prev = last;
        last = node;
    }

    private class Node {
        final K key;
        final V value;
        Node next;
        Node prev;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
