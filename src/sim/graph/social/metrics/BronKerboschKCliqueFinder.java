/*
 * @(#)BronKerboschKCliqueFinder.java    %I%    %G%
 * @author biggie
 * 
 */

package sim.graph.social.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.BronKerboschCliqueFinder;
import edu.uci.ics.jung.graph.Graph;





/**
 * @author biggie
 * @param <V>
 * @param <E>
 * 
 */
public class BronKerboschKCliqueFinder<V, E> extends BronKerboschCliqueFinder<V, E> {

    /**
     * @author biggie BronKerboschKCliqueFinder
     */
    public BronKerboschKCliqueFinder(Graph<V, E> graph_) {
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
	Collection<Set<V>> maxClique = getAllMaximalCliques();
	Collection<Set<V>> kCliques = new ArrayList<Set<V>>();

	for (Set<V> clique : maxClique) {
	    if (clique.size() == _k) {
		kCliques.add(clique);
	    }
	}
	return kCliques;
    }

}
