import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * A class to generate and write simulation scenarios to a .txt file for later reading.
 */
public class SimulationWriter {

    private static final int NUM_BODIES = 100000;
    private static final int SIZE = 700;
    private static final double SPEED_MULTIPLIER = 0.0007;

    public static void generateSimulation(String filename) {
        // Scenario 1: Black hole with accretion disk
//        Body[] bodies = new Body[NUM_BODIES];
//        bodies[0] = new Body(1000, new double[]{SIZE/2.0, SIZE/2.0}, new double[2]);
//        Random rand = new Random();
//        for (int i = 1; i < NUM_BODIES; i++) {
//            int radius = rand.nextInt(SIZE/2 - 10) + 1;
//            double theta = rand.nextDouble()*2*Math.PI;
//            double[] position = new double[]{radius * Math.cos(theta)+SIZE/2.0, radius * Math.sin(theta)+SIZE/2.0};
//            double[] velocity = new double[]{SPEED_MULTIPLIER * Math.sin(theta), -1*SPEED_MULTIPLIER * Math.cos(theta)};
//            bodies[i] = new Body(1, position, velocity);
//        }
//        writeSimulation(bodies, "accretion_disk_1.txt");

        // Scenario 2: Circular motion
        Body[] bodies = new Body[NUM_BODIES];
        Random rand = new Random();
        for (int i = 0; i < NUM_BODIES; i++) {
            int radius = rand.nextInt(SIZE/2 - 210) + 200;
            double theta = rand.nextDouble()*2*Math.PI;
            double[] position = new double[]{radius * Math.cos(theta)+SIZE/2.0, radius * Math.sin(theta)+SIZE/2.0};
            double[] velocity = new double[]{SPEED_MULTIPLIER * Math.pow(radius, 1.5) * Math.sin(theta),
                                             -1*SPEED_MULTIPLIER * Math.pow(radius, 1.5) * Math.cos(theta)};
            bodies[i] = new Body(1, position, velocity);
        }
        writeSimulation(bodies, "data/circulation.txt");
    }

    private static void writeSimulation(Body[] bodies, String filename) {
        try {
            PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8);
            writer.println(bodies.length);
            for (Body b : bodies) {
                writer.println(b.toString());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SimulationWriter.generateSimulation("data/accretion_disk_1.txt");
    }
}
