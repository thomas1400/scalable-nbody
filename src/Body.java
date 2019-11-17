import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class Body {
    private int mass;
    private int[] position;
    private int[] velocity;

    Body(int mass, int[] position, int[] velocity) throws InvalidParameterException {
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
    void update(int[] force) {
        Arrays.setAll(velocity, i -> velocity[i] + force[i]/mass);
        Arrays.setAll(position, i -> position[i] + velocity[i]);
    }

    public int getMass() {
        return mass;
    }

    public int[] getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "{" + mass + ", " + Arrays.toString(position) + ", " + Arrays.toString(velocity) + "}";
    }

    public void paint(Graphics2D g2d) {
        int size = (int) Math.ceil(Math.sqrt(mass) * 10);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(position[0] - size/2, position[1] - size/2, size, size);
    }
}
