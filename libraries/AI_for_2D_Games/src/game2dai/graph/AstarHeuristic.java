package game2dai.graph;

/**
 * Interface for all A* heurtic classes
 * 
 * @see		AshCrowFlight
 * @see		AshManhattan
 * 
 * @author Peter Lager
 */
public interface AstarHeuristic {

	/**
	 * Estimate the cost between the node and the target.
	 */
	public double getCost(GraphNode node, GraphNode target);
	
}
