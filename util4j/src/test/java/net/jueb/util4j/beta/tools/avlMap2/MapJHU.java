package net.jueb.util4j.beta.tools.avlMap2;

//MapJHU.java
//Yu-chi Chang, Allan Wang
//ychang64, awang53
//EN.600.226.01/02
//P3 (for part B)

import java.util.Map;
import java.util.Set;
import java.util.Collection;

/** Custom MapJHU interface, based on Java Map.
 *  @author CS226 Staff, Spring 2016
 *  @param <K> the base type of the keys
 *  @param <V> the base type of the values
 */
public interface MapJHU<K, V> {

    /** Get the number of (actual) entries in the Map.
     *  @return the size
     */
    int size();

    /** Remove all entries from the Map.
     */
    void clear();

    /** Find out if the Map has any entries.
     *  @return true if no entries, false otherwise
     */
    boolean isEmpty();

    /** Find out if a key is in the map.
     *  @param key the key being searched for
     *  @return true if found, false otherwise
     */
    boolean hasKey(K key);

    /** Find out if a value is in the map.
     *  @param value the value to search for
     *  @return true if found, false otherwise
     */
    boolean hasValue(V value);

    /** Get the value associated with a key if there.
     *  @param key the key being searched for
     *  @return the value associated with key, or null if not found
     */
    V get(K key);

    /** Associate a value with a key, replacing the old value if key exists.
     *  @param key the key for the entry
     *  @param value the value for the entry
     *  @return the old value associated with the key, or null if new entry
     */
    V put(K key, V value);

    /** Remove the entry associated with a key.
     *  @param key the key for the entry being deleted
     *  @return the value associated with the key, or null if key not there
     */
    V remove(K key);

    /** Get a set of all the entries in the map.
     *  @return the set
     */
    Set<Map.Entry<K, V>> entries();

    /** Get a set of all the keys in the map.
     *  @return the set
     */
    Set<K> keys();

    /** Get a collection of all the values in the map.
     *  @return the collection
     */
    Collection<V> values();

}
