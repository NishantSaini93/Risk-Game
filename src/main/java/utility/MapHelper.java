package utility;

import com.sun.jmx.remote.internal.ArrayQueue;
import model.Continent;
import model.Country;
import model.GameMap;
import model.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class related to map
 */
public class MapHelper {

    /**
     * Implements breadth first search to check connectivity
     *
     * @param nodeHashMap hashmap of counties as node
     * @param node        root of graph
     * @return true if all nodes are traversed else false
     */
    public static boolean bfs(HashMap<Integer, Node> nodeHashMap, Node node) {
        node.color = 1;
        node.distance = 0;
        node.parent = null;
        ArrayQueue<Node> queue = new ArrayQueue<>(nodeHashMap.size());
        queue.add(node);
        while (!queue.isEmpty()) {
            Node dequeuedNode = queue.remove(0);
            if (!GameMap.getInstance().countryGraph.get(dequeuedNode.id).isEmpty()) {
                for (Country country : GameMap.getInstance().countryGraph.get(dequeuedNode.id)) {
                    Node neigbour = nodeHashMap.get(country.id);
                    if (neigbour != null) {
                        if (neigbour.color == 0) {
                            neigbour.color = 1;
                            neigbour.distance = dequeuedNode.distance + 1;
                            neigbour.parent = dequeuedNode;
                            queue.add(neigbour);
                        }
                    }

                }
            }
            dequeuedNode.color = 2;
        }

        for (Map.Entry<Integer, Node> entry : nodeHashMap.entrySet()) {
            if (entry.getValue().color != 2) {
                return false;
            }
        }

        return true;

    }

    /**
     * checks if map is valid
     * Case : All the countries are connected
     *
     * @return true if connected map else false
     */
    public static boolean validateMap() {
        HashMap<Integer, Node> nodeHashMap = createNodeGraphFromCountries(GameMap.getInstance().countries.values());
        return bfs(nodeHashMap, nodeHashMap.get(nodeHashMap.keySet().toArray()[0]));
    }

    /**
     * checks if map is valid
     * Case : All the countries in Continent are connected
     *
     * @return true if connected continent else false
     */
    public static boolean validateContinentGraph() {
        boolean result = false;

        for (Map.Entry<Integer, Continent> entry : GameMap.getInstance().continents.entrySet()) {
            HashMap<Integer, Node> nodeHashMap = createNodeGraphFromCountries(entry.getValue().countries);
            result = bfs(nodeHashMap, nodeHashMap.get(nodeHashMap.keySet().toArray()[0]));
            if (!result) {
                return false;
            }
        }
        return result;
    }

    /**
     * Create a nodeHashMap from collection of countries
     *
     * @param countries Collection of countries
     * @return nodeHashMap hashmap of nodes
     */
    public static HashMap<Integer, Node> createNodeGraphFromCountries(Collection<Country> countries) {
        HashMap<Integer, Node> nodeHashMap = new HashMap<>();
        for (Country country : countries) {
            Node countryNode = new Node(country.id);
            nodeHashMap.put(country.id, countryNode);
        }
        return nodeHashMap;
    }


}
