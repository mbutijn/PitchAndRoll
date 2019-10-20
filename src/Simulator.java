import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Simulator {

    private Indicators indicators = new Indicators();
    ControlSignal controlSignal;
    private AircraftSymbol aircraftSymbol;
    private Timer timer;
    PitchDynamics pitchDynamics;
    RollDynamics rollDynamics;
    private static String controlMessage = "";

    private static final int SAMPLE_FREQUENCY = 100; // Hertz
    private TextField textField = new TextField();
    private double alfa, theta, phi, beta, psi; // Euler angles body frame
    static final int screenHeight = 800, screenWidth = 1000;
    static public int deg2pxl = 20; // One degree is 20 pixels
    private double left, right, halfBarLength;
    public static double midX, midY;
    private ArrayList<PitchLine> pitchLines = new ArrayList<>();
    private ArrayList<RollLine> rollLines = new ArrayList<>();
    private ArrayList<HeadingLine> headingLines = new ArrayList<>();
    static JButton pauseButton;

    public static void main (String[] arg){
        JFrame frame = new JFrame("Cessna pitch and roll simulator");
        Simulator simulator = new Simulator();
        simulator.makeUI(frame, simulator);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(screenWidth, screenHeight);
        frame.setResizable(false);
    }

    private void makeUI(JFrame frame, Simulator simulator){
        frame.getContentPane().add(BorderLayout.CENTER, indicators);

        textField.setEditable(false);
        controlSignal = new ControlSignal(frame);
        calculateSides();
        aircraftSymbol = new AircraftSymbol(left, right);

        // Add pitch ladder
        PitchLine horizon = new PitchLine(1000, 0);
        pitchLines.add(horizon);
        addPitchLines(-40.0, 40.0, 10, 200); // every 10 degrees
        addPitchLines(-35.0, 35.0, 10, 133); // every 5 degrees
        addPitchLines(-17.5, 17.5, 5, 67); // every 2.5 degrees

        // Add roll indicator
        addRollLines(-80, 80, 20, 50);
        addRollLines(-70, 70, 20, 25);

        // Add heading indicator
        addHeadingLines(0, 270, 90, 50);
        addHeadingLines(10, 350, 10, 20);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        panel.add(new Restart(simulator).makeButton());
        pauseButton = new Pause(simulator).makeButton();
        panel.add(pauseButton);

        JPanel panel2 = new JPanel();
        panel2.add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(BorderLayout.NORTH, textField);
        frame.getContentPane().add(BorderLayout.EAST, panel2);

        pitchDynamics = new PitchDynamics(SAMPLE_FREQUENCY);
        rollDynamics = new RollDynamics(SAMPLE_FREQUENCY);

        timer = new Timer(1000 / SAMPLE_FREQUENCY, actionListener);
        timer.setRepeats(true);

    }

    private void addPitchLines(double start, double end, int spacing, int length){
        for (double pitch = start; pitch < end + spacing; pitch += spacing){
            if (pitch != 0) {
                pitchLines.add(new PitchLine(length, pitch));
            }
        }
    }

    private void addRollLines(int start, int end, int spacing, int length){
        for (int roll = start; roll < end + spacing; roll += spacing){
            rollLines.add(new RollLine(length, Math.toRadians(roll)));
        }
    }

    private void addHeadingLines(int start, int end, int spacing, int length){
        for (int heading = start; heading < end + spacing + spacing; heading += spacing){
            headingLines.add(new HeadingLine(length, heading));
        }
    }

    private void calculateSides() {
        left = 70;
        right = 800;
        halfBarLength = 0.5 * (right - left);
        midX = left + halfBarLength;
        midY = 0.5 * screenHeight;
    }

    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            // Get the controls
            double elevator = controlSignal.getElevator();
            double aileron = controlSignal.getAileron();

            // Calculate the attitude angles

            //theta = pitchDynamics.updateThetaOld(-Math.cos(phi)*elevator);

            pitchDynamics.updateU(Math.cos(phi)*elevator);
            pitchDynamics.updateAlfa(elevator);
            pitchDynamics.updateTheta();
            pitchDynamics.updateQC_over_V(Math.cos(phi)*elevator);

            theta = pitchDynamics.getTheta();
            alfa = pitchDynamics.getAlfa();

            //phi = rollDynamics.updatePhiOld(-aileron);
            rollDynamics.updateBeta(aileron);
            rollDynamics.updatePhi(aileron);
            rollDynamics.updatePb_over_2V(aileron);
            rollDynamics.updateRb_over_2V(aileron);

            phi = rollDynamics.getPhi();
            beta = rollDynamics.getBeta();
            psi = Math.toDegrees(rollDynamics.getPsi());

            // Update the text field
            textField.setText(String.format("theta = %.1f, deg; phi = %.1f, deg; psi = %.1f, deg; u = %.1f, m/s; delta_e = %.1f, deg; delta_a = %.1f deg%s",
                    theta, Math.toDegrees(phi), psi, pitchDynamics.u, elevator, aileron, getControlMessage()));

            // Repaint
            indicators.repaint();
        }
    };

    Timer getTimer() {
        return timer;
    }

    private class Indicators extends JPanel {
        @Override
        public void paintComponent(Graphics graphics){
            super.paintComponent(graphics);
            Graphics2D graphics2d = (Graphics2D) graphics;
            graphics2d.setColor(Color.black);

            int xm = (int) (midX + Math.sin(phi) * theta * deg2pxl);
            int ym = (int) (midY + Math.cos(phi) * theta * deg2pxl);

            // Draw the pitch angle indications
            for (PitchLine pitchLine : pitchLines){
                pitchLine.draw(graphics2d, xm, ym, phi);
            }

            // Draw roll indication
            graphics.drawArc((int)(midX - 0.75*halfBarLength), (int)(midY - 0.75*halfBarLength), (int)(1.5*halfBarLength), (int)(1.5*halfBarLength), (int)(Math.round(Math.toDegrees(phi) + 10)), 160);
            graphics2d.drawLine((int)(midX - 10), 50, (int)(midX), 70);
            graphics2d.drawLine((int)(midX), 70, (int)(midX+10), 50);

            // Draw the roll angle indications
            for (RollLine rollLine : rollLines){
                rollLine.draw(graphics2d, 0.75 * halfBarLength, phi);
            }

            // Draw the edges
            //graphics2d.drawRect((int)left,50,(int)(right - left), screenHeight - 200);
            graphics.drawLine(0, 50, screenWidth, 50);

            for (HeadingLine headingLine : headingLines){
                headingLine.draw(graphics2d, psi);
            }

            //Draw the aircraft Symbol
            aircraftSymbol.makeSymbol(graphics2d);

            // Draw the flight path indication
            aircraftSymbol.makeFlightPathIndicator(graphics2d, deg2pxl*alfa, deg2pxl*beta);
        }
    }

    static void setControlMessage(String message){
        controlMessage = message;
    }

    private String getControlMessage() {
        return controlMessage;
    }

}
