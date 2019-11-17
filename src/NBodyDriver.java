import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class NBodyDriver extends JPanel {

    private Body[] bodies;

    private NBodyDriver(String filepath) throws FileNotFoundException {
        File scenario = new File(filepath);
        Scanner s = new Scanner(scenario);
        bodies = new Body[Integer.parseInt(s.nextLine().strip())];
        int i = 0;
        while (s.hasNextLine()) {
            int[] line = Arrays.stream(s.nextLine().strip().split(",")).mapToInt(Integer::parseInt).toArray();
            bodies[i] = new Body(line[0], new int[]{line[1], line[2]}, new int[]{line[3], line[4]});
            i++;
        }
    }

    /**
     * Updates the simulation.
     */
    private void update() {

    }

    /**
     * Renders the simulation using Graphics2D.
     * @param graphics a Graphics instance
     */
    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        for (Body b : bodies) {
            b.paint(g2d);
        }
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

        NBodyDriver nbd = new NBodyDriver("data/testingscenario.txt");
        nbd.setPreferredSize(new Dimension(600, 600));

        frame.add(nbd);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

}
