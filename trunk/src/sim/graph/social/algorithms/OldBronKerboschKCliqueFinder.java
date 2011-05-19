/*
 * @(#)BronKerboschKCliqueFinder.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.graph.social.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;





/**
 * @author biggie
 * @param <V>
 * @param <E>
 * 
 */
public class OldBronKerboschKCliqueFinder<V, E> extends BronKerboschCliqueFinder<V, E> {

    /**
     * @author biggie BronKerboschKCliqueFinder
     */
    public OldBronKerboschKCliqueFinder(Graph<V, E> graph_) {
	super(graph_);
    }

    /**
     * 
     * @author biggie
     * @name getKMaxClique Purpose TODO
     * 
     * @param
     * @return Collection<Set<Agent>>
     */
    public Collection<Set<V>> getAllMaxKCliques(int _k) {
	Collection<Set<V>> maxClique = getBiggestMaximalCliques();
	Collection<Set<V>> kCliques = new ArrayList<Set<V>>();

	for (Set<V> clique : maxClique) {
	    if (clique.size() >= _k) {
		kCliques.add(clique);
	    }
	}
	return kCliques;
    }

}
