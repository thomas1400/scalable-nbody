import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class NBodyDriver extends JPanel implements MouseListener {

    private Body[] bodies;
    private QuadTree qt;
    private static final int size = 700;
    private final int numBodies = 10000;

    private NBodyDriver(String filepath) throws FileNotFoundException {
        File scenario = new File(filepath);
        Scanner s = new Scanner(scenario);
        bodies = new Body[Integer.parseInt(s.nextLine().strip())];
        int i = 0;
        while (s.hasNextLine()) {
            double[] line = Arrays.stream(s.nextLine().strip().split(",")).mapToDouble(Double::parseDouble).toArray();
            bodies[i] = new Body((int)line[0], new double[]{line[1], line[2]}, new double[]{line[3], line[4]});
            i++;
        }

        qt = new QuadTree(bodies, size);
    }

    private NBodyDriver() {
        Random rand = new Random();
        bodies = new Body[numBodies];
        for (int i = 0; i < numBodies; i++) {
            bodies[i] = new Body(1, new double[]{rand.nextInt(size), rand.nextInt(size)},
                                    new double[]{0*rand.nextDouble(), 0*rand.nextDouble()});
        }
        qt = new QuadTree(bodies, size);
    }

    /**
     * Updates the simulation.
     */
    private void update() {
        // For every Body:
        //  Traverse the QuadTree:
        //  If the COM of a cell is sufficiently far from this Body, compute force with that COM and mass. Return.
        //  If the COM is too close, check the children.
        //  If the cell is singular or at the minimum size, calculate with the COM and mass. Return.
        for (Body b : bodies) {
            double[] force = qt.calculateNetForce(b);
            b.update(force);
        }
        qt = new QuadTree(bodies, size);
    }

    /**
     * Renders the simulation using Graphics2D.
     * @param graphics a Graphics instance
     */
    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        for (Body b : bodies) {
            b.paint(g2d);
        }
//        qt.paintUsed(g2d, new Body(0, new double[]{size/2.0, size/2.0}, new double[2]));
    }

    /**
     * Renders a String in the window using Graphics2D.
     * @param g a Graphics2D instance
     * @param text the String to render
     * @param size the font size
     * @param outline the width of the outline
     * @param x the center x of the text
     * @param y the center y of the text
     */
    public static void renderString(Graphics2D g, String text, int size, int outline, int x, int y) {

        Graphics2D g2d = (Graphics2D) g.create();

        Font f = new Font("Cambria", Font.PLAIN, size);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        g2d.setFont(f);

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout tl = new TextLayout(text, g2d.getFont(), frc);

        int descent = (int) tl.getDescent();
        int ascent = (int) tl.getAscent();

        int center_x = x - (int) tl.getBounds().getCenterX();
        int center_y = y - (2 * descent - ascent) / 2;

        AffineTransform at = new AffineTransform();
        at.translate(center_x, center_y);
        Shape text_shape = tl.getOutline(null);

        g2d.transform(at);
        g2d.setStroke(new BasicStroke(outline, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(Color.WHITE);
        g2d.draw(text_shape);

        g2d.setColor(Color.BLACK);
        g2d.fill(text_shape);

        g2d.dispose();
    }

    public static void main(String[] args) throws FileNotFoundException {
        // Create JFrame, Game, add Game to JFrame and set settings
        JFrame frame = new JFrame("N-Body Simulation");

        SimulationWriter.generateSimulation("");

        NBodyDriver nbd = new NBodyDriver("data/circulation.txt");
        //NBodyDriver nbd = new NBodyDriver();
        nbd.setPreferredSize(new Dimension(size, size));

        frame.add(nbd);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(nbd);

        // Create update and repaint timer and start it.
        Timer t = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nbd.update();
                nbd.repaint();
            }
        });

        t.start();

        // Create WindowListener to stop timer on close, ending the process.
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                t.stop();
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Body[] newBodies = new Body[bodies.length + 1];
        System.arraycopy(bodies, 0, newBodies, 0, bodies.length);
        newBodies[newBodies.length-1] = new Body(1, new double[]{e.getX(), e.getY()}, new double[]{0.1, 0});
        bodies = newBodies;
        this.update();
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
