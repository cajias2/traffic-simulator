package sim.graph.utils;

public class Edge<T> {

    private final boolean _isCreateEdge;
    private T v1;
    private T v2;

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

    public Edge(T a, T b, boolean creation_) {
	this(creation_);
	v1 = a;
	v2 = b;
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
    
    public T getFrom() {
	return v1;
    }
    
    public T getTo(){
	return v2;
    }
}
