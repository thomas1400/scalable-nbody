import java.util.ArrayList;

/**
 * Splits 2D space into quadrants recursively, such that each QuadTree contains four smaller QuadTrees.
 * At the end of construction, each QuadTree contains just a single Body.
 *
 * This construction simplifies n-body simulation from an O(n^2) problem to an O(n*log n) problem. The force on a Body
 * from far away Bodies is approximated by grouping the Bodies into QuadTrees and calculating the net force using a
 * single, large particle with the average center of mass and total mass of all Bodies in that QuadTree.
 */
public class QuadTree {
    private QuadTree[] children;    // Array of 4 children, 0-3 from top right moving clockwise
    private int[] center;
    private int width;
    private int[] com;              // Center of mass
    private int mass;               // Total mass
    private ArrayList<Body> bodies;

    /**
     * Initializes a QuadTree for n-body simulation.
     * @param bodies the ArrayList of all bodies to simulate
     * @param center the center of the screen
     * @param width the width of the screen
     */
    public QuadTree(ArrayList<Body> bodies, int[] center, int width) {
        this.children = new QuadTree[4];
        this.bodies = bodies;
        this.center = center;
        this.width = width;

        initializeBodies();
    }

    /**
     * Private, initializes a QuadTree without bodies.
     * Used in initializeBodies() for recursive QuadTree construction and body assignment.
     * @param center the center of this QuadTree
     * @param width the width of this QuadTree
     */
    private QuadTree(int[] center, int width) {
        this.children = new QuadTree[4];
        this.bodies = new ArrayList<>();
        this.center = center;
        this.width = width;
    }

    /**
     * Initializes this QuadTree's CoM and mass and generates children recursively, if needed.
     */
    private void initializeBodies() {
        if (bodies.size() > 1) {
            this.com = new int[2];
            this.mass = 0;

            // Quadrants 0-3 starting from top right, moving clockwise.
            children[0]= new QuadTree(new int[]{center[0] - width/4, center[1] - width/4}, width/2);
            children[1] = new QuadTree(new int[]{center[0] - width/4, center[1] + width/4}, width/2);
            children[2] = new QuadTree(new int[]{center[0] + width/4, center[1] - width/4}, width/2);
            children[3] = new QuadTree(new int[]{center[0] + width/4, center[1] + width/4}, width/2);

            for (Body b : bodies) {
                this.mass += b.getMass();
                this.com[0] += b.getPosition()[0] * b.getMass();
                this.com[1] += b.getPosition()[1] * b.getMass();

                for (QuadTree child : children) {
                    if (child.contains(b)) {
                        child.addBody(b);
                    }
                }
            }
            this.com[0] /= this.mass;
            this.com[1] /= this.mass;

            for (QuadTree child : children) {
                child.initializeBodies(); // Recursive call
            }
        } else { // If this QuadTree contains only one Body
            this.com = bodies.get(0).getPosition();
            this.mass = bodies.get(0).getMass();
        }
    }

    private void addBody(Body b) {
        bodies.add(b);
    }

    private boolean contains(Body body) {
        if (body == null) {
            return false;
        }
        int[] bodyPosition = body.getPosition();
        return (center[0] - width/2 <= bodyPosition[0] && // left < pos
                center[0] + width/2 > bodyPosition[0] && // pos < right
                center[1] - width/2 <= bodyPosition[1] && // top < pos
                center[1] + width/2 > bodyPosition[1]); // pos < bottom
    }

    public int[] getCOM() {
        return com;
    }

    public int getMass() {
        return mass;
    }

    public QuadTree[] getChildren() {
        return children;
    }

}
