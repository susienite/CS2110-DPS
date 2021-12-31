package student;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;  
import java.util.List;

import student.Paths;
import models.Node;

import models.NodeStatus;
import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;

/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {

	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * 
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */

	private HashSet<Long> visited = new HashSet<Long>(); //HashSet of visited planets' ID
	
	@Override
	public void rescue(RescueStage state) {
		// TODO : Find the missing spaceship
		
		//Search for Planet X 
		DFS(state);

		return;
	}

	/**Depth First Search recursively from Earth to find Planet X. 
	 * If spaceship finds Planet X, returns Planet X.
	 * If spaceship does not find Planet X, leave spaceship in current location.  */
	public void DFS(RescueStage state) { 
		
		//Reached Planet X 
		if(state.foundSpaceship()){
			return;
		}
		
		//Add current location to the Hashset
		long prev = state.currentLocation();
		visited.add(state.currentLocation());
		
		//Sort list of planets
		ArrayList<NodeStatus> listOfPlanets = new ArrayList<NodeStatus>();
		for(NodeStatus neighbor : state.neighbors()) {
			listOfPlanets.add(neighbor);
		}
		Collections.sort(listOfPlanets);

		//Run thru list of planets 
		for(NodeStatus current : listOfPlanets) {
			if(! visited.contains(current.getId())) {
				state.moveTo(current.getId());
				DFS(state);
				if(state.foundSpaceship()){
					return;
				}
				state.moveTo(prev);
			}
			
		}		
	}

	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ReturnStage. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth

		List<Node> paths = Paths.shortestPath(state, state.currentNode(), state.getEarth()); 
		paths.remove(0);  //remove first node 

		//loop through shortest path and move spaceship to those planets 
		for (Node planet : paths) {

			state.moveTo(planet);
			
			//grab speed upgrade if true 
			if (planet.hasSpeedUpgrade()) {
				state.grabSpeedUpgrade();
			} 	
			
		} return; 

	}

}