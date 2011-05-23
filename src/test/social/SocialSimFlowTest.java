/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */

package test.social;

import java.awt.Dimension;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.graph.social.algorithms.BronKerboschKCliqueFinder;
import sim.graph.social.algorithms.CPMCommunityFinder;
import sim.graph.social.algorithms.commTracker.Community;
import sim.graph.social.algorithms.commTracker.TimeLineList;
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.HypergraphLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicHypergraphRenderer;

/**
 * TODO Purpose
 * 
 * @author biggie
 */
public class SocialSimFlowTest {
    private static final int CAPTURE_WINDOW = 1;
    private static final int K_CLIQUE_SIZE = 4;
    private static final long SEED = 320320486;

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    public static void main(String[] args) {
	SocialSim sim = new SocialSim(SEED);
	List<Graph<Agent, FriendLink>> graphList = sim.runSim(args, CAPTURE_WINDOW);

	System.out.print("Finding Communities");
	TimeLineList<Agent> evolution = findCommunities(graphList);
	System.out.println("\nWriting results");
	evolution.writeMetrics();
	System.out.println("Done.");
	System.out.println("Painting Random Hypergraph.");
	paintHyperGraph(graphList, evolution);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return TimeLineList<Agent>
     * @author biggie
     */
    private static TimeLineList<Agent> findCommunities(List<Graph<Agent, FriendLink>> graphList) {
	TimeLineList<Agent> evolution = new TimeLineList<Agent>(graphList.size());
	int snapshot = 1;
	for (Graph<Agent, FriendLink> graph : graphList) {
	    if (null != graph) {
		System.out.print(".");
		Collection<Set<Agent>> kComs = findCommunities(graph, K_CLIQUE_SIZE);

		for (Set<Agent> community : kComs) {
		    evolution.add(snapshot, community, graph);
		}
	    }
	    snapshot++;
	}
	return evolution;
    }

    /**
     * @param <V>
     * @param <E>
     * @param graphEvol
     * @param evolution
     */
    private static <V, E> void paintHyperGraph(List<Graph<V, E>> graphEvol, TimeLineList<V> evolution) {
	JFrame frame = new JFrame("Simple Graph View");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	Hypergraph<V, Integer> hg = populateHg(graphEvol.get(2), evolution.getSnapshot(2));
	HypergraphLayout<V, Integer> l = new HypergraphLayout<V, Integer>(hg, CircleLayout.class);

	VisualizationViewer<V, Integer> v = new VisualizationViewer<V, Integer>(l, new Dimension(600, 600));

	v.setRenderer(new BasicHypergraphRenderer<V, Integer>());
	frame.getContentPane().add(v);
	frame.pack();
	frame.setVisible(true);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return Collection<Set<Integer>>
     * @author biggie
     */
    private static <V, E> Collection<Set<V>> findCommunities(Graph<V, E> graph, int k_) {
	BronKerboschKCliqueFinder<V, E> maxCliques = new BronKerboschKCliqueFinder<V, E>(graph);
	Collection<Set<V>> kCliques = maxCliques.getAllMaxKCliques(k_);

	// Conjunto de communidades en ese snapshot
	CPMCommunityFinder<V> cpm = new CPMCommunityFinder<V>(kCliques);
	Collection<Set<V>> kComs = cpm.findCommunities(k_);
	return kComs;
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     * @param <V>
     * @param <E>
     */
    private static <V, E> Hypergraph<V, Integer> populateHg(Graph<V, E> graph_, List<Community<V>> list_) {
	Hypergraph<V, Integer> hg = new SetHypergraph<V, Integer>();
	for (V node : graph_.getVertices()) {
	    hg.addVertex(node);
	}
	int cliqueCount = 0;
	if (null != list_) {
	    for (Community<V> kclique : list_) {
		hg.addEdge(cliqueCount++, kclique.getAllNodes());
	    }
	}
	return hg;
    }

}
