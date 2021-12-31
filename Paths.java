package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Edge;
import models.Node;
import models.ReturnStage;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */

public class Paths {

	/** Return the shortest path from start to end, or the empty list if a path
	 * does not exist.
	 * Note: The empty list is NOT "null"; it is a list with 0 elements. */
	public static List<Node> shortestPath(ReturnStage state, Node start, Node end) {
		/* TODO Read note A7 FAQs on the course piazza for ALL details. */
		Heap<Node> F= new Heap<Node>(); // As in lecture slides

		// map contains an entry for each node in S or F. Thus,
		// |map| = |S| + |F|.
		// For each such key-node, the value part contains the shortest known
		// distance to the node and the node's backpointer on that shortest path.
		HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

		F.add(start, 0);
		map.put(start, new SFdata(0.0, null, 0));

		//invariant: as in lecture slides, together with def of F and map
		//Ends when Frontier is empty 
		while (F.size() != 0) {
			Node f= F.poll();

			//loop must terminate at last node 
			if (f == end) return constructPath(end, map);

			//initialize time 
			double ftime= map.get(f).time;

			// for each neighbor w of f  
			for (Edge e : f.getExits()) {
				Node w= e.getOther(f);
				// time is distance/speed
				double newWtime= ftime + (e.length/ state.getSpeed());
				SFdata wData= map.get(w);
				
				//update number of hostile planets
				int numhp = (w.isHostile() ? map.get(f).hostile +1 : map.get(f).hostile);

				//change priority of hostile planets to very high 
				if (numhp >= 2) {
					newWtime= 100000000+newWtime;
				}

				//if node not in HashSet, visit it 
				if (wData == null) { 			
					map.put(w, new SFdata(newWtime, f, numhp));
					F.add(w, newWtime);
				//what if the new time is shorter than original time 
				} else if (newWtime < wData.time) {
					wData.time = newWtime;
					wData.backPointer= f;
					F.updatePriority(w, newWtime);
				}
			}
		}
		
		// no path from start to end
		return new LinkedList<Node>();
	}

	/** Return the path from the start node to node end.
	 *  Precondition: nData contains all the necessary information about
	 *  the path. */
	public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
		LinkedList<Node> path= new LinkedList<Node>();
		Node p= end;
		// invariant: All the nodes from p's successor to the end are in
		//            path, in reverse order.
		while (p != null) {
			path.addFirst(p);
			p= nData.get(p).backPointer;
		}
		return path;
	}

	/** Return the sum of the weights of the edges on path path. */
	public static int pathDistance(List<Node> path) {
		if (path.size() == 0) return 0;
		synchronized(path) {
			Iterator<Node> iter= path.iterator();
			Node p= iter.next();  // First node on path
			int s= 0;
			// invariant: s = sum of weights of edges from start to p
			while (iter.hasNext()) {
				Node q= iter.next();
				s= s + p.getConnect(q).length;
				p= q;
			}
			return s;
		}
	}

	/** An instance contains information about a node: the previous node
	 *  on a shortest path from the start node to this node, the time
	 *  of this node from the start node, and the number of hostile planets. */
	private static class SFdata {
		private Node backPointer; //backpointer on path from start node to this one
		private double time;   //time from start node to this one 
		private int hostile;  //hostile planets from start node to this one 

		/** Constructor: an instance with distance d from the start node and
		 *  backpointer p.*/
		private SFdata(double t, Node p, int h) {
			time= t;     //time from start node to this one.
			backPointer= p;  //backpointer on the path (null if start node)
			hostile = h;    //hostile planets from start node to this one 
		}

		/** return a representation of this instance. */
		public String toString() {
			return "dist " + time + ", bckptr " + backPointer;
		}
	}
}
