/**
 * 
 */
package app.social.agents.nppd;

import sim.engine.SimState;

/**
 * @author biggie
 *
 */
public class CoOpAgent extends NPDAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 7373744118030789751L;
    private PlayerType _type = PlayerType.COOP;

    /**
     * @param state_
     */
    public CoOpAgent(final SimState state_) {
	super(state_);
    }

    /* (non-Javadoc)
     * @see app.social.agents.NPDAgent#setType(app.social.agents.NPDAgent.PlayerType)
     */
    @Override
    protected void setType(PlayerType type_) {
	_type = type_;

    }

    /* (non-Javadoc)
     * @see app.social.agents.NPDAgent#getType()
     */
    @Override
    protected PlayerType getType() {
	return _type;
    }

}
