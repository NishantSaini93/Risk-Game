package model;

/**
 * Graph node representation of country for BFS
 */
public class Node {
    /**
     * Color of node
     */
    public int color;
    /**
     * Distance of node
     */
    public int distance;
    /**
     * Parent of current node
     */
    public Node parent;
    /**
     * Node id
     */
    public int id;

    /**
     * Initializes node with given id
     *
     * @param id id of country
     */
    public Node(int id) {
        this.color = 0;
        this.distance = Integer.MAX_VALUE;
        this.id = id;
        this.parent = null;
    }
}
