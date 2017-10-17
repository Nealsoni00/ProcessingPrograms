package game2dai.graph;

/**
 * This class is used to calculate the heuristic estimated-cost-to-goal. <br>
 * 
 * It estimates the cost to goal as the Euclidean (as the crow flies) distance 
 * between the current node and the goal. <br>
 * 
 * It is also possible to apply a scaling factor to the heuristic. <br>
 * 
 * @author Peter Lager
 *
 */
public class AshCrowFlight implements AstarHeuristic {

	private double factor = 1.0;

	/**
	 * Will use a factor of 1.0 to calculate the estimated cost 
	 * between nodes
	 */
	public AshCrowFlight() {
		factor = 1.0;
	}

	/**
	 * Create the heuristic.
	 * @param factor scaling factor
	 */
	public AshCrowFlight(double factor) {
		this.factor = factor;
	}

	/**
	 * Estimate the cost between the node and the target.
	 */
	public double getCost(GraphNode node, GraphNode target) {
		double dx = target.x - node.x;
		double dy = target.y - node.y;
		double dz = target.z - node.z;
		
		return factor * Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

}
