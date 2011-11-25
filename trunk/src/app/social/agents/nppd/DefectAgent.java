package app.social.agents.nppd;

import sim.engine.SimState;

/**
 * TODO Purpose
 *
 * @author biggie 
 * @date Nov 25, 2011
 */
public class DefectAgent extends NPDAgent {

    private static final long serialVersionUID = 4319889063389977244L;
    /**
     * TODO Purpose
     * 
     * @param
     * @author biggie
     */
    public DefectAgent(SimState state_) {
	super(state_);
    }

    private PlayerType _type = PlayerType.DEFECT;
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
	// TODO Auto-generated method stub
	return _type;
    }

}
