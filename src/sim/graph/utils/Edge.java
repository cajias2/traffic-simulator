package sim.graph.utils;

public class Edge<T> {

    private final boolean _isCreateEdge;
    public T v1;
    public T v2;

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
