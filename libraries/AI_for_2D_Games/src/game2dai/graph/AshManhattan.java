package game2dai.graph;

/**
 * This class is used to calculate the heuristic estimated-cost-to-goal. <br>
 * 
 * It estimates the cost to goal as the sum of the differences between the 
 * nodes in all there primary directions. So if there were 2 nodes then the
 * estimated-cost between them is<br>
 * <pre>|x1 - x2| + |y1 - y2| + |z1 - z2| </pre><br>
 * 
 * It is also possible to apply a scaling factor to the heuristic. <br>
 * 
 * @author Peter Lager
 *
 */
public class AshManhattan implements AstarHeuristic {

	private double factor = 1.0;
	
	/**
	 * Will use a factor of 1.0 to calculate the estimated cost 
	 * between nodes
	 */
	public AshManhattan() {
		factor = 1.0;
	}

	/**
	 * Create the heuristic.
	 * @param factor scaling factor
	 */
	public AshManhattan(double factor) {
		this.factor = factor;
	}

	/**
	 * Estimate the cost between the node and the target.
	 */
	public double getCost(GraphNode node, GraphNode target) {
		return factor * (target.x - node.x + target.y - node.y + target.z - node.z);
	}

}
