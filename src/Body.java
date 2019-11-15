import java.security.InvalidParameterException;
import java.util.Arrays;

public class Body {
    private int mass;
    private int[] position;
    private int[] velocity;

    public Body(int mass, int[] position, int[] velocity) throws InvalidParameterException {
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
    public void update(int[] force) {
        Arrays.setAll(velocity, i -> velocity[i] + force[i]/mass);
        Arrays.setAll(position, i -> position[i] + velocity[i]);
    }

    public int getMass() {
        return mass;
    }

    public int[] getPosition() {
        return position;
    }
}
