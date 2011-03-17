/**
 * 
 */
package sim.graph.social.metrics;

/**
 * @author biggie
 *
 */
public class KCliquePercolationMethod {
    /**
     * # ---- K-clique percolator ----
     * def getKCliqueComponents(net,k):
     * """
     * Returns community structure calculated with k-clique percolation.
     * """
     * def evaluateAtEnd(edges):
     * for edge in edges:
     * yield edge
     * yield EvaluationEvent()
     * edgesAndEvaluations=evaluateAtEnd(net.edges)
     * kcliques=kcliquesByEdges(edgesAndEvaluations,k) #unweighted clique
     * percolation
     * for community in communitiesByKCliques(kcliques):
     * return community
     * class KClique(object):
     * """
     * A class for presenting cliques of size k. Realizations
     * of this class just hold a sorted list of nodes in the clique.
     * """
     * def __init__(self,nodelist,notSorted=True):
     * self.nodes=nodelist
     * if notSorted:
     * self.nodes.sort()
     * self.hash=None
     * def __hash__(self):
     * if self.hash==None:
     * self.hash=hash(reduce(mul,map(self.nodes[0].__class__.__hash__,self.nodes
     * )))
     * return self.hash
     * def __iter__(self):
     * for node in self.nodes:
     * yield node
     * def __cmp__(self,kclique):
     * if self.nodes==kclique.nodes:
     * return 0
     * else:
     * return 1
     * def __add__(self,kclique):
     * return KClique(self.nodes+kclique.nodes)
     * def getSubcliques(self):
     * for i in range(0,len(self.nodes)):
     * yield KClique(self.nodes[:i]+self.nodes[(i+1):],notSorted=False)
     * def __str__(self):
     * return str(self.nodes)
     * def getEdges(self):
     * for node in self.nodes:
     * for othernode in self.nodes:
     * if node!=othernode:
     * yield (node,othernode)
     * def getK(self):
     * return len(self.nodes)
     * def getIntensity(kclique,net):
     * intensity=1
     * for edge in kclique.getEdges():
     * intensity*=net[edge[0],edge[1]]
     * return pow(intensity,1.0/float(kclique.getK()))
     * class EvaluationEvent:
     * def __init__(self,threshold=None,addedElements=None):
     * self.threshold=threshold
     * self.addedElements=addedElements
     * def kcliquesAtSubnet(nodes,net,k):
     * """
     * List all k-cliques at a given network. Any implementation is fine,
     * but as this routine is a part of a clique percolator anyway we
     * will use itself to find cliques larger than 2. Cliques of size 1 and
     * 2 are trivial.
     * """
     * if len(nodes)>=k:
     * if k==1:
     * for node in nodes:
     * yield KClique([node])
     * elif k==2:
     * subnet=getSubnet(net,nodes)
     * for edge in subnet.edges:
     * yield KClique([edge[0],edge[1]])
     * else:
     * subnet=getSubnet(net,nodes)
     * for kclique in kcliquesByEdges(subnet.edges,k):
     * yield kclique
     * def kcliquesByEdges(edges,k):
     * """
     * Phase I in the SCP-algorithm.
     * Generator function that generates a list of cliques of size k in the
     * order they
     * are formed when edges are added in the order defined by the 'edges'
     * argument.
     * If many cliques is formed by adding one edge, the order of the cliques is
     * arbitrary.
     * This generator will pass through any EvaluationEvent objects that are
     * passed to
     * it in the 'edges' generator.
     * """
     * newNet=SymmNet() # Edges are added to a empty network one by one
     * for edge in edges:
     * if isinstance(edge,EvaluationEvent):
     * yield edge
     * else:
     * # First we find all new triangles that are born when the new edge is
     * added
     * triangleEnds=set() # We keep track of the tip nodes of the new triangles
     * for adjacendNode in newNet[edge[0]]: # Neighbor of one node of the edge
     * ...
     * if newNet[adjacendNode,edge[1]]!=0: #...is a neighbor of the other node
     * of the edge...
     * triangleEnds.add(adjacendNode) #...then the neighbor is a tip of a new
     * triangle
     * # New k-cliques are now (k-2)-cliques at the triangle end points plus
     * # the two nodes at the tips of the edge we are adding to the network
     * for kclique in kcliquesAtSubnet(triangleEnds,newNet,k-2):
     * yield kclique+KClique([edge[0],edge[1]])
     * newNet[edge[0],edge[1]]=edge[2] # Finally we add the new edge to the
     * network
     * def kcliquesWeight(net,k,weightFunction):
     * kcliques=list(kcliquesByEdges(net.edges,k))
     * kcliques.sort(lambda x,y:
     * cmp(weightFunction(x,net),weightFunction(y,net)))
     * for kclique in kcliques:
     * yield kclique
     * def communitiesByKCliques(kcliques):
     * """
     * Phase II in the SCP algorithm. Finds communities in the order they
     * appear as the cliques are added to the network.
     * """
     * # Calculate the neighboring relations
     * krTree=Ktree()
     * for kclique in kcliques:
     * if isinstance(kclique,EvaluationEvent):
     * communityStructure=krTree.getCommStruct().getCollapsed()
     * communityStructure.threshold=kclique.threshold
     * yield communityStructure
     * else:
     * #for fewer operations at ktree, names of new cliques should be saved
     * #and given to ktree when merging the sets
     * krcliques=list(kclique.getSubcliques()) #list all k-1 cliques that are
     * subcliques
     * krTree.mergeSetsWithElements(krcliques) #merge the sets of k-1 cliques at
     * the list
     * def kcliquePercolator(net,k,start,stop,evaluations,reverse=False,
     * weightFunction=None):
     * """
     * K-clique percolator. This sorts the edges and combines the phases I-II.
     * See
     * helpstring below for explanation of the arguments.
     * """
     * if weightFunction==None: #unweighted clique percolation with thresholding
     * edges=list(net.edges)
     * edges.sort(lambda x, y: cmp(x[2],y[2]),reverse=reverse)
     * edgesAndEvaluations=EvaluationList(edges)
     * edgesAndEvaluations.setLinearEvaluations(start,stop,evaluations)
     * kcliques=kcliquesByEdges(edgesAndEvaluations,k)
     * else: #weighted clique percolation
     * kcliques=EvaluationList(kcliquesWeight(net,k,weightFunction),
     * weightFunction=lambda x:getIntensity(x,net))
     * kcliques.setLinearEvaluations(start,stop,evaluations)
     * for community in communitiesByKCliques(kcliques):
     * yield community
     * # ---- Main program and parsing arguments ----
     */

}
