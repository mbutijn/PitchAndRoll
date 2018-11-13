import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Simulator {

    private Indicators indicators;
    public static ControlSignal controlSignal;
    private AircraftSymbol aircraftSymbol;
    private Timer timer;
    CessnaDynamics cessnaPitch;

    private static final int SAMPLE_FREQUENCY = 100; // Hz
    private TextField textField;
    private double theta, phi; // Euler angles body frame
    static final int screenHeight = 800, screenWidth = 1000;
    static private int deg2pxl = 20;
    private double left, right, halfBarLength, midX, midY;
    private ArrayList<PitchLine> pitchLines = new ArrayList<>();
    private ArrayList<RollLine> rollLines = new ArrayList<>();
    Pause pauseButton;

    public static void main (String[] arg){
        JFrame frame = new JFrame("Pitch and roll simulator");
        Simulator simulator = new Simulator();
        simulator.makeUI(frame, simulator);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(screenWidth, screenHeight);
        frame.setResizable(false);
    }

    private void makeUI(JFrame frame, Simulator simulator){
        indicators = new Indicators();
        frame.getContentPane().add(BorderLayout.CENTER, indicators);

        textField = new TextField();
        textField.setEditable(false);
        controlSignal = new ControlSignal(frame);
        calculateSides();
        aircraftSymbol = new AircraftSymbol(left,right);

        // Add pitch ladder
        PitchLine horizon = new PitchLine(1000,0);
        pitchLines.add(horizon);
        addPitchLines(-30.0, 30.0, 10, 200); // every 10 degrees
        addPitchLines(-25.0, 25.0, 10, 140); // every 5 degrees
        addPitchLines(-17.5, 17.5, 5, 80); // every 2.5 degrees

        // Add roll indicator
        addRollLines(-80, 80, 20, 50);
        addRollLines(-70, 70, 20, 25);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6,1));
        panel.add(new Restart(simulator).makeButton());
        pauseButton = new Pause(simulator);
        panel.add(pauseButton.makeButton());

        JPanel panel2 = new JPanel();
        panel2.add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(BorderLayout.NORTH, textField);
        frame.getContentPane().add(BorderLayout.EAST, panel2);

        cessnaPitch = new CessnaDynamics(SAMPLE_FREQUENCY);
        timer = new Timer(1000 / SAMPLE_FREQUENCY, actionListener);
        timer.setRepeats(true);

    }

    private void addPitchLines(double start, double end, int pitchSpacing, int length){
        // Conversion from degrees to pixels
        int spacing = pitchSpacing * deg2pxl;
        double startPitch = start * deg2pxl;
        double endPitch = end * deg2pxl + spacing;

        for (double p1 = startPitch; p1 < endPitch; p1 += spacing){
            if (p1 != 0) {
                pitchLines.add(new PitchLine(length, p1));
            }
        }

    }

    private void addRollLines(int start, int end, int spacing, int length){
        for (int r1 = start; r1 < end + spacing; r1 += spacing){
            rollLines.add(new RollLine(length,Math.toRadians(r1)));
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

            phi = cessnaPitch.performRollCalculation(controlSignal.getAileron());
            theta = cessnaPitch.performPitchCalculation(Math.cos(phi)*controlSignal.getElevator());

            textField.setText("theta = " + String.format("%.1f",theta) + " deg; phi = " +
                    String.format("%.1f",Math.toDegrees(phi)) + " deg; delta_e = " +
                    String.format("%.1f",controlSignal.getElevator()) + " deg; delta_a = " +
                    String.format("%.1f",controlSignal.getAileron()) + " deg");

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

            int xm = (int) (midX + Math.pow(Math.sin(phi),1) * theta * deg2pxl);
            int ym = (int) (midY + Math.pow(Math.cos(phi),1) * theta * deg2pxl);

            for (PitchLine pitchLine:pitchLines){
                pitchLine.draw(graphics2d, xm, ym, phi); // Draw the pitch angle indications
            }

            // Draw roll indication
            graphics.drawArc((int)(midX-0.75*halfBarLength), (int)(midY-0.75*halfBarLength), (int)(1.5*halfBarLength), (int)(1.5*halfBarLength), (int)(Math.round(Math.toDegrees(phi)+10)),160);
            graphics2d.drawLine((int)(midX-10),50,(int)(midX),70);
            graphics2d.drawLine((int)(midX),70,(int)(midX+10),50);

            for (RollLine rollLine:rollLines){
                rollLine.draw(graphics2d, midX, midY, 0.75*halfBarLength, phi);
            }

            // Draw the edges
            graphics2d.drawRect((int)left,50,(int)(right-left),screenHeight-200);

            //Draw the aircraft Symbol
            aircraftSymbol.makeSymbol(graphics2d);
        }

    }

}
