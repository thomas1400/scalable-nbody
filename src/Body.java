import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;

public class Body {
    private int mass;
    private double[] position;
    private double[] velocity;

    Body(int mass, double[] position, double[] velocity) throws InvalidParameterException {
        if (mass < 0 || position.length != 2 || position[0] < 0 || position[1] < 0) {
            throw new InvalidParameterException();
        }

        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
    }

    /**
     * Updates the velocity and position of this Body given net force in x and y directions.
     * @param force the net force on this Body
     */
    void update(double[] force) {
        for (int i = 0; i < position.length; i++) {
            velocity[i] += force[i]/mass;
            position[i] += velocity[i];
        }
    }

    public int getMass() {
        return mass;
    }

    public double[] getPosition() {
        return position;
    }

    public int getRadius() {
        return (int) Math.ceil(Math.sqrt(2*mass));
    }

    @Override
    public String toString() {
        return "" + mass + "," + position[0]+","+position[1]+","+velocity[0]+","+velocity[1];
    }

    public void paint(Graphics2D g2d) {
        int size = (int) Math.ceil(Math.sqrt(2*mass));
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int)position[0] - size/2, (int)position[1] - size/2, size, size);
    }
}
