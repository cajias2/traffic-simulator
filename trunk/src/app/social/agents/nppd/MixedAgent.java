package app.social.agents.nppd;

import sim.engine.SimState;

/**
 * TODO Purpose
 *
 * @author biggie 
 * @date Nov 25, 2011
 */
public class MixedAgent extends NPDAgent {

    private static final long serialVersionUID = 1916755588408757519L;
    /**
     * TODO Purpose
     * @param 
     * @author biggie 
     */
    public MixedAgent(SimState state_) {
	super(state_);
	// TODO Auto-generated constructor stub
    }

    private PlayerType _type = PlayerType.MIXED;
    /* (non-Javadoc)
     * @see app.social.agents.nppd.NPDAgent#setType(app.social.agents.nppd.NPDAgent.PlayerType)
     */
    @Override
    protected void setType(PlayerType type_) {
	_type = type_;

    }

    /* (non-Javadoc)
     * @see app.social.agents.nppd.NPDAgent#getType()
     */
    @Override
    protected PlayerType getType() {
	return _type;
    }

}
