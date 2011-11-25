/**
 * 
 */
package app.social.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import ec.util.MersenneTwisterFast;

/**
 * @author biggie
 */
public abstract class NPDAgent extends Agent {

    protected enum PlayerType {
	COOP, DEFECT, MIXED
    };

    private static final double EPSILON = 0.9;
    private static final double UPDATE_PROB = 0.0001;
    private static final int PAYOFF = 5;
    private static final int COST = 3;
    private static final double ALPHA = 1.5;
    private static final double BETA = 0.1;
    private static final int GAME_SIZE = 10;
    private static MersenneTwisterFast _rand;
    private static long _lastStepUpdated = 0;

    private double _utility = 0;
    private Set<NPDAgent> _game;
    private Map<NPDAgent, Boolean> _agentPlay;

    /**
     * @param state_
     */
    public NPDAgent(final SimState state_) {
	super(state_);
	_rand = state_.random;
    }

    @Override
    protected void beforeStep(SocialSimBatchRunner<Agent, String> state_) {
	if (null == _game) {
	    _agentPlay = new HashMap<NPDAgent, Boolean>();
	    _game = createGame();
	    setGame();
	}
	if (_lastStepUpdated < state_.schedule.getSteps()) {
	    updateStrategies();
	    _lastStepUpdated = state_.schedule.getSteps();
	}
    }

    /**
     * 
     */
    private void updateStrategies() {
	for (int i = 0; i < _agentList.size() * UPDATE_PROB; i++) {
	    NPDAgent agentA = (NPDAgent) _agentList.get(_rand.nextInt(_agentList.size()));
	    NPDAgent agentB = (NPDAgent) _agentList.get(_rand.nextInt(_agentList.size()));
	    while (agentA == agentB) {
		agentB = (NPDAgent) _agentList.get(_rand.nextInt(_agentList.size()));
	    }

	    if (agentA.getUtility() > agentB.getUtility()) {
		agentB.setType(agentA.getType());
	    } else {
		agentA.setType(agentB.getType());
	    }
	}
    }

    /**
     * @param type_
     */
    protected abstract void setType(PlayerType type_);

    /**
     * @return
     */
    protected abstract PlayerType getType();

    @Override
    protected void afterStep(SocialSimBatchRunner<Agent, String> state_) {
	_game = null;
	_agentPlay = null;
    }

    /**
     * 
     */
    @Override
    public void step(final SimState state_) {
	beforeStep((SocialSimBatchRunner) state_);
	_agentPlay.put(this, play());
	if (allPlayersPlayed()) {
	    updateLinks();
	    updateGameUtility();
	}
	afterStep((SocialSimBatchRunner) state_);
    }

    /**
     * 
     */
    private void updateGameUtility() {
	int coopCount = 0;
	for (Boolean isCoopPlay : _agentPlay.values()) {
	    coopCount += isCoopPlay ? 1 : 0;
	}

	double coopUtilForGame = getGameUtility(true, _agentPlay.size(), coopCount);
	double defectUtilForGame = getGameUtility(false, _agentPlay.size(), coopCount);
	double utility = 0;
	for (Entry<NPDAgent, Boolean> player : _agentPlay.entrySet()) {
	    utility = player.getValue() ? coopUtilForGame : defectUtilForGame;
	    player.getKey().addToUtil(utility);
	}
    }

    /**
     * @param gameUtility
     */
    private void addToUtil(double gameUtility) {
	_utility += gameUtility;

    }

    /**
     * 
     */
    private void updateLinks() {
	for (NPDAgent p1 : _game) {
	    for (NPDAgent p2 : _game) {
		if (p1 != p2) {
		    updateLinks(p1, p2);
		}
	    }
	}
    }

    /**
     * @param p1_
     * @param p2_
     */
    private void updateLinks(NPDAgent p1_, NPDAgent p2_) {
	if (_agentPlay.get(p1_) && _agentPlay.get(p2_)) {
	    if (!p1_.isFriend(p2_)) {
		p1_.befriend(p2_, 1);
	    } else {
		p1_.updateWeight(p2_, getEdgeWeight(p2_) + 1);
	    }
	} else {
	    p1_.unfriend(p2_);
	}

    }

    /**
     * @return
     */
    private boolean allPlayersPlayed() {
	return _game.size() == _agentPlay.size();
    }

    /**
     * 
     */
    private void setGame() {
	for (NPDAgent player : _game) {
	    if (this != player) {
		player.setGame(_game);
		player.setPlayBoard(_agentPlay);
	    }
	}
    }

    /**
     * @param agentPlay_
     */
    private void setPlayBoard(Map<NPDAgent, Boolean> agentPlay_) {
	_agentPlay = agentPlay_;

    }

    /**
     * @param agents_
     * @return
     */
    protected boolean play() {
	boolean willCoop = false;
	if (getType() == PlayerType.DEFECT) {
	    willCoop = false;
	} else if (getType() == PlayerType.COOP) {
	    willCoop = true;
	} else {
	    willCoop = _rand.nextBoolean(getCoOpProb());
	}
	return willCoop;
    }

    /**
     * @return
     */
    private float getCoOpProb() {
	float probablity = 0;
	double avgWPowAlpha = Math.pow(getAvgGameWeight(), ALPHA);
	double numerator = avgWPowAlpha + BETA;
	double denomminator = avgWPowAlpha + BETA + 1;
	probablity = (float) (numerator / denomminator);
	return probablity;
    }

    /**
     * @param game_
     */
    protected void setGame(Set<NPDAgent> game_) {
	_game = game_;
    }

    /**
     * @return
     */
    protected Set<NPDAgent> createGame() {
	Set<NPDAgent> game = new HashSet<NPDAgent>();
	Collection<Agent> neighbours = null;
	if (null != getNeighbours()) {
	    neighbours = new ArrayList<Agent>(getNeighbours());
	}

	for (int i = 0; game.size() < GAME_SIZE - 1; i++) {
	    if (_rand.nextFloat() < EPSILON) {
		game.add((NPDAgent) _agentList.get(_rand.nextInt(_agentList.size())));
	    } else {
		if (null != neighbours && !neighbours.isEmpty()) {
		    NPDAgent player = (NPDAgent) neighbours.toArray()[_rand.nextInt(neighbours.size())];
		    neighbours.remove(player);
		    game.add(player);
		} else {
		    game.add((NPDAgent) _agentList.get(_rand.nextInt(_agentList.size())));
		}
	    }
	}
	game.add(this);
	return game;
    }

    /**
     * @return
     */
    private double getAvgGameWeight() {
	double totalEdgeWeight = 0;
	for (NPDAgent player : _game) {
	    if (this != player && isFriend(player)) {
		totalEdgeWeight += getEdgeWeight(player);
	    }
	}
	return totalEdgeWeight / _game.size();
    }

    /**
     * @param gameSize_
     * @param coopNum
     * @return
     */
    private double getGameUtility(boolean isPlayCoop_, int gameSize_, int coopNum) {
	double utility = 0.0;
	if (isPlayCoop_) {
	    utility = (PAYOFF * coopNum) / gameSize_ - COST;
	} else {
	    utility = PAYOFF * coopNum / gameSize_;
	}
	return utility;
    }

    /**
     * @return the utility
     */
    public double getUtility() {
	return _utility;
    }

}
