import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    private int[] bounds;
    private double[] com;              // Center of mass
    private int mass;               // Total mass
    private ArrayList<Body> bodies;
    private int totalSize;

    private static final double THRESHOLD_RATIO = 1.2;
    private static final double MIN_SIZE = 3;
    private static final double G_CONSTANT = 0.05;

    /**
     * Initializes a QuadTree for n-body simulation.
     * @param bodies the ArrayList of all bodies to simulate
     * @param size the size of the screen
     */
    public QuadTree(Body[] bodies, int size) {
        this.children = new QuadTree[4];
        this.bodies = new ArrayList<>(Arrays.asList(bodies));
        this.bounds = new int[]{0, size, size, 0};
        this.totalSize = size;

        initializeBodies();
    }

    /**
     * Initializes a QuadTree for n-body simulation.
     * @param bodies the ArrayList of all bodies to simulate
     * @param bounds the bounds of the screen
     */
    private QuadTree(Body[] bodies, int[] bounds) {
        this.children = new QuadTree[4];
        this.bodies = new ArrayList<>(Arrays.asList(bodies));
        this.bounds = bounds;

        initializeBodies();
    }

    /**
     * Private, initializes a QuadTree without bodies.
     * Used in initializeBodies() for recursive QuadTree construction and body assignment.
     * @param bounds the bounds of this QuadTree
     */
    private QuadTree(int[] bounds, int totalSize) {
        this.children = new QuadTree[4];
        this.bodies = new ArrayList<>();
        this.bounds = bounds;
        this.totalSize = totalSize;
    }

    /**
     * Initializes this QuadTree's CoM and mass and generates children recursively, if needed.
     */
    private void initializeBodies() {
        if (bodies.size() > 1) {
            // Checks for singularity and size threshold
            if (isSingular(bodies) || bounds[2]-bounds[0] < MIN_SIZE) {
                this.com = new double[]{0, 0};
                for (Body b : bodies) {
                    this.mass += b.getMass();
                    this.com[0] += b.getPosition()[0] * b.getMass();
                    this.com[1] += b.getPosition()[1] * b.getMass();
                }
            } else {
                this.com = new double[2];
                this.mass = 0;

                // Quadrants 0-3 starting from top right, moving clockwise.
                // Bounds numbered 0-3 from top, clockwise

                children[0] = new QuadTree(new int[]{bounds[0], bounds[1],
                        bounds[0] + (bounds[2] - bounds[0]) / 2, bounds[3] + (bounds[1] - bounds[3]) / 2}, totalSize);

                children[1] = new QuadTree(new int[]{bounds[0] + (bounds[2] - bounds[0]) / 2, bounds[1],
                        bounds[2], bounds[3] + (bounds[1] - bounds[3]) / 2}, totalSize);

                children[2] = new QuadTree(new int[]{bounds[0] + (bounds[2] - bounds[0]) / 2,
                        bounds[3] + (bounds[1] - bounds[3]) / 2, bounds[2], bounds[3]}, totalSize);

                children[3] = new QuadTree(new int[]{bounds[0], bounds[3] + (bounds[1] - bounds[3]) / 2,
                        bounds[0] + (bounds[2] - bounds[0]) / 2, bounds[3]}, totalSize);

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
            }
        } else if (bodies.size() == 1) { // If this QuadTree contains only one Body
            this.com = bodies.get(0).getPosition();
            this.mass = bodies.get(0).getMass();
        } else {
            this.com = new double[]{bounds[1]-bounds[3], bounds[2]-bounds[0]};
            this.mass = 0;
        }
    }

    private boolean isSingular(ArrayList<Body> list) {
        double[] checkPos = list.get(0).getPosition();
        for (Body b : list) {
            if (b.getPosition()[0] != checkPos[0] || b.getPosition()[1] != checkPos[1]) {
                return false;
            }
        }
        return true;
    }

    private void addBody(Body b) {
        bodies.add(b);
    }

    private boolean contains(Body body) {
        if (body == null) {
            return false;
        }
        double[] bodyPosition = body.getPosition();
        return (bounds[3] < bodyPosition[0] && // left < pos
                bounds[1] >= bodyPosition[0] && // pos < right
                bounds[0] < bodyPosition[1] && // top < pos
                bounds[2] >= bodyPosition[1]); // pos < bottom
    }

    public double[] calculateNetForce(Body b) {
        double dist = distanceTo(b);
        if (dist == 0) {
            return new double[2];
        }
        double xdist = com[0]-b.getPosition()[0];
        double ydist = com[1]-b.getPosition()[1];
        if (dist < b.getRadius()) {
            dist = b.getRadius();
            xdist = Math.signum(xdist);
            ydist = Math.signum(ydist);
        }


        if (bodies.size() == 0) {
            return new double[2];
        }
        if ((bounds[2]-bounds[0]) / dist < THRESHOLD_RATIO ||
                isSingular(bodies) || bounds[2]-bounds[0] < MIN_SIZE) {
            double totalForce = G_CONSTANT * mass * b.getMass() / (dist*dist*dist);
            return new double[]{totalForce * (xdist), totalForce * (ydist)};
        } else {
            double[] netForce = new double[2];
            for (QuadTree child : children) {
                if (child != null) {
                    double[] childForce = child.calculateNetForce(b);
                    netForce[0] += childForce[0];
                    netForce[1] += childForce[1];
                }
            }
//            if (bodies.size() > 200 && bodies.size() < 500) {
////                System.out.println(Arrays.toString(netForce));
//            }
            return netForce;
        }
    }

    private double distanceTo(Body b) {
        return Math.sqrt((com[0]-b.getPosition()[0])*(com[0]-b.getPosition()[0]) +
                (com[1]-b.getPosition()[1])*(com[1]-b.getPosition()[1]));
    }

    public double[] getCOM() {
        return com;
    }

    public int getMass() {
        return mass;
    }

    public QuadTree[] getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return Arrays.toString(bounds);
    }

    public void paint(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawRect(bounds[3], bounds[0], bounds[1]-bounds[3], bounds[2]-bounds[0]);
        for (QuadTree child : children) {
            if (child != null) {
                child.paint(g2d);
            }
        }
    }

    public void paintUsed(Graphics2D g2d, Body b) {
        g2d.setColor(Color.BLACK);

        double dist = distanceTo(b);
        if (dist == 0) {
            return;
        }

        if (bodies.size() == 0) {
            return;
        }

        if ((bounds[2]-bounds[0]) / dist < THRESHOLD_RATIO ||
                isSingular(bodies) || bounds[2]-bounds[0] < MIN_SIZE) {
            g2d.drawRect(bounds[3], bounds[0], bounds[1]-bounds[3], bounds[2]-bounds[0]);
        } else {
            for (QuadTree child : children) {
                if (child != null) {
                    child.paintUsed(g2d, b);
                }
            }
        }
    }
}
