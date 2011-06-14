/**
 * 
 */
package app.social.utils;

import org.apache.commons.collections15.Factory;

import app.social.links.SimpleFriendLink;

import sim.graph.social.link.FriendLink;

/**
 * @author biggie
 */
public class SimpleFriendLinkFactory {

    // This class simply makes a factory for our edges. The create() function
    // makes a new edge and increments e, so that
    // each edge will have a different id. Check the edge class's constructor.
    class EdgeFactory implements Factory {
	public FriendLink create() {
	    return (new SimpleFriendLink());
	}

    }
}