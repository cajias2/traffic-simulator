package sim.graph.utils;

public class Edge {

    private final boolean _isCreateEdge;

    /**
     * 
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public Edge(boolean creation_) {
	_isCreateEdge = creation_;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return boolean
     * @author antonio
     */
    public boolean isCreate() {
	return _isCreateEdge;
    }
}
