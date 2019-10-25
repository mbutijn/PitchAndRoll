import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Simulator extends JFrame {

    static final int screenHeight = 800, screenWidth = 1000;
    static int deg2pxl = 20; // One degree is 20 pixels
    static double midX, midY;
    private Indicators indicators = new Indicators();
    ControlSignal controlSignal;
    private AircraftSymbol aircraft;
    private Timer timer;
    SymmetricMotion pitchDynamics;
    AsymmetricMotion rollDynamics;
    private static String controlMessage = "";
    private Label climbRateIndicator = new Label("0 feet/min");
    private Label airSpeedIndicator = new Label("116.4 knts");
    private Label altiudeIndicator = new Label("10000 feet");
    private static final int SAMPLE_FREQUENCY = 100; // Hertz
    private static final double MPS_TO_KNTS = 1.9438, MPS_TO_FEETPMIN = 196.850, M_TO_FEET = 3.2808;
    private TextField textField = new TextField("                                                                                      ");
    private double alfa, theta, phi, beta, psi; // Euler angles body frame
    private double left, right, halfBarLength;
    private ArrayList<PitchLine> pitchLines = new ArrayList<>();
    private ArrayList<RollLine> rollLines = new ArrayList<>();
    private ArrayList<HeadingLine> headingLines = new ArrayList<>();
    private Rudder rudderIndicator;
    private Thrust thrustIndicator;

    public static void main (String[] arg){
        new Simulator().makeUI();
    }

    private void makeUI(){
        setTitle("Cessna flight simulator");

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(screenWidth, screenHeight);
        setResizable(false);
        getContentPane().add(BorderLayout.CENTER, indicators);

        Panel eastPanel = new Panel();
        eastPanel.setPreferredSize(new Dimension(90, 70));
        Panel eastPanel_small = new Panel();
        eastPanel_small.setLayout(new GridLayout(3,1));
        eastPanel_small.setPreferredSize(new Dimension(90, 70));
        eastPanel_small.add(climbRateIndicator);
        eastPanel_small.add(airSpeedIndicator);
        eastPanel_small.add(altiudeIndicator);
        eastPanel.add(eastPanel_small);
        getContentPane().add(BorderLayout.EAST, eastPanel);

        textField.setEditable(false);
        controlSignal = new ControlSignal(this);
        setKeyBoardListeners(controlSignal);
        calculateSides();
        aircraft = new AircraftSymbol(left, right);

        // Add pitch ladder
        PitchLine horizon = new PitchLine(1000, 0);
        pitchLines.add(horizon);
        addPitchLines(-110.0, 110.0, 10, 200); // every 10 degrees
        addPitchLines(-105.0, 105.0, 10, 133); // every 5 degrees
        addPitchLines(-17.5, 17.5, 5, 67); // every 2.5 degrees

        // Add roll indicator
        addRollLines(-80, 80, 20, 50);
        addRollLines(-70, 70, 20, 25);

        // Add heading indicator
        addHeadingLines(0, 270, 90, 50);
        addHeadingLines(10, 350, 10, 20);

        // Add the rudder
        rudderIndicator = new Rudder();

        // Add the Thrust lever
        thrustIndicator = new Thrust();

        JPanel buttonPanel = new JPanel();
        JButton restart = new Restart(this).makeButton();
        restart.setFocusable(false);
        buttonPanel.add(restart);

        JPanel northPanel = new JPanel();
        northPanel.add(textField);
        northPanel.add(buttonPanel);

        getContentPane().add(BorderLayout.NORTH, northPanel);
        pitchDynamics = new SymmetricMotion(SAMPLE_FREQUENCY);
        rollDynamics = new AsymmetricMotion(SAMPLE_FREQUENCY);

        timer = new Timer(1000 / SAMPLE_FREQUENCY, actionListener);
        timer.setRepeats(true);
    }

    private void setKeyBoardListeners(ControlSignal cs) {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(cs);
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
            double elevator = controlSignal.getElevator(); // symmetric control inputs
            double thrust = controlSignal.getThrust();
            double aileron = controlSignal.getAileron(); // asymmetric control inputs
            double rudder = controlSignal.getRudderValue();

            // Evaluate symmetric EOM
            pitchDynamics.updateU(elevator, thrust);
            pitchDynamics.updateAlfa(elevator, thrust);
            boolean toggleRoll = pitchDynamics.updateTheta();
            pitchDynamics.updateQC_over_V(Math.cos(phi)*elevator, thrust, toggleRoll);

            theta = pitchDynamics.getTheta();
            alfa = pitchDynamics.getAlfa();

            // Evaluate asymmetric EOM
            rollDynamics.updateBeta(rudder);
            rollDynamics.updatePhi(toggleRoll);
            rollDynamics.updatePb_over_2V(aileron, rudder);
            rollDynamics.updateRb_over_2V(aileron, rudder);

            phi = rollDynamics.getPhi();
            beta = rollDynamics.getBeta();
            psi = Math.toDegrees(rollDynamics.getPsi(toggleRoll));

            // Update the text field
            textField.setText(String.format("θ = %.1f°; φ = %.1f°; ψ = %.0f°; δ_e = %.1f°; δ_a = %.1f°; δ_r = %.1f°%s",
                    theta, Math.toDegrees(phi), psi, elevator, aileron, rudder, getControlMessage()));

            // Update airspeed
            airSpeedIndicator.setText(String.format("%.1f knts", pitchDynamics.getAirspeed() * MPS_TO_KNTS));

            // Update climbRate
            climbRateIndicator.setText(String.format("%.0f feet/min", pitchDynamics.getClimbRate() * MPS_TO_FEETPMIN));

            // Update altitude
            altiudeIndicator.setText(String.format("%.0f feet", pitchDynamics.getAltitude() * M_TO_FEET));

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
            int size = (int)(1.5*halfBarLength);
            graphics2d.drawArc((int)(midX - 0.75*halfBarLength), (int)(midY - 0.75*halfBarLength), size, size, (int)(Math.round(Math.toDegrees(phi) + 10)), 160);
            graphics2d.drawLine((int)(midX - 10), 50, (int)(midX), 70);
            graphics2d.drawLine((int)(midX), 70, (int)(midX+10), 50);

            // Draw the roll angle indications
            for (RollLine rollLine : rollLines){
                rollLine.draw(graphics2d, 0.75 * halfBarLength, phi);
            }

            // Draw heading indicator
            graphics2d.drawLine(0, 50, screenWidth, 50);
            for (HeadingLine headingLine : headingLines){
                headingLine.draw(graphics2d, psi);
            }

            //Draw the pitch symbol
            aircraft.makePitchSymbol(graphics2d);

            // Draw the flight path indication
            aircraft.makeFlightPathIndicator(graphics2d, deg2pxl*(Math.cos(-phi)*beta - Math.sin(-phi)*alfa), deg2pxl*(Math.sin(-phi)*beta + Math.cos(-phi)*alfa));

            // Draw the rudder indication
            rudderIndicator.draw(graphics2d, controlSignal.getRudder());

            // Draw the thrust indication
            thrustIndicator.draw(graphics2d, controlSignal.getThrust());
        }
    }

    static void setControlMessage(String message){
        controlMessage = message;
    }

    private String getControlMessage() {
        return controlMessage;
    }

}
