/**
 * 
 */
package sim.graph.social.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import app.social.links.SimpleFriendLink;

import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * TODO Purpose
 * 
 * @author biggie
 * @param <V>
 */
public class CPMCommunityFinder<V> {

    private final Graph<Set<V>, FriendLink> _cliqueGraph;

    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public CPMCommunityFinder(Collection<Set<V>> kCliques_) {
	_cliqueGraph = new UndirectedSparseGraph<Set<V>, FriendLink>();
	initializeCliqueGraph(kCliques_);
    }

    /**
     * TODO Purpose
     * 
     * @param k_
     *            TODO
     * @param
     * @return Collection<Set<V>>
     * @author biggie
     */
    public Collection<Set<V>> findCommunities(int k_) {

	for (Set<V> kCliqueA : _cliqueGraph.getVertices()) {
	    for (Set<V> kCliqueB : _cliqueGraph.getVertices()) {

		if (kCliqueA != kCliqueB && adjecent(kCliqueA, kCliqueB, k_)) {
		    try {
			_cliqueGraph.addEdge(new SimpleFriendLink(), kCliqueA, kCliqueB);
		    } catch (OutOfMemoryError e) {
			System.err.println(e.getMessage());
		    }
		}
	    }
	}

	EdgeBetweennessClusterer<Set<V>, FriendLink> ebc = new EdgeBetweennessClusterer<Set<V>, FriendLink>(0);

	return unifyConnectedComponents(ebc.transform(_cliqueGraph));
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    private void initializeCliqueGraph(Collection<Set<V>> kCliques_) {
	if (kCliques_ != null) {
	    for (Set<V> kClique : kCliques_) {
		_cliqueGraph.addVertex(kClique);
	    }
	}

    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return Collection<Set<V>> Set of connected components
     * @author biggie
     */
    private Collection<Set<V>> unifyConnectedComponents(Collection<Set<Set<V>>> conCompSet_) {
	Collection<Set<V>> unifiedSet = new ArrayList<Set<V>>();

	Iterator<Set<Set<V>>> comIter = conCompSet_.iterator();
	while (comIter.hasNext()) {
	    Set<Set<V>> currCom = comIter.next();
	    // Unify Community
	    unifiedSet.add(unifyCommunity(currCom));
	}
	return unifiedSet;
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return Set<V>
     * @author biggie
     */
    private Set<V> unifyCommunity(Set<Set<V>> currCom_) {
	Set<V> community = new HashSet<V>();

	for (Set<V> currClique : currCom_) {
	    community.addAll(currClique);
	}
	return community;
    }

    /**
     * Compares two sets and deems them adjecent if they share n-1 elements.
     * 
     * @param k_
     *            TODO
     * @param
     * @return boolean
     * @author biggie
     */
    private boolean adjecent(Set<V> currClq_, Set<V> nxtClq_, int k_) {
	int matched = 0;
	boolean isAdj = false;
	for (V ag : currClq_) {
	    if (nxtClq_.contains(ag)) {
		matched++;
		if (matched == (k_ - 1)) {
		    isAdj = true;
		    break;
		}
	    }
	}
	return isAdj;
    }

}
