import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Simulator {

    private Indicators indicators;
    private ControlSignal controlSignal;
    private AircraftSymbol aircraftSymbol;
    private Timer timer;
    public CessnaDynamics cessnaPitch;

    private static final int SAMPLE_FREQUENCY = 100;
    private TextField textField;
    private double theta, phi; // Euler angles body frame
    protected static final int screenHeight = 800, screenWidth = 1000;
    private double left, right, halfBarLength, midX, midY;
    public static double psi_i,theta_i; // Euler angle inertial frame
    private ArrayList<PitchLine> pitchLines = new ArrayList<>();
    private ArrayList<RollLine> rollLines = new ArrayList<>();

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

        addPitchLines();
        addRollLines();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6,1));
        Restart restart = new Restart();
        JButton restartButton = restart.makeButton(simulator);
        panel.add(restartButton);

        JPanel panel2 = new JPanel();
        panel2.add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(BorderLayout.NORTH, textField);
        frame.getContentPane().add(BorderLayout.EAST, panel2);

        cessnaPitch = new CessnaDynamics(SAMPLE_FREQUENCY);
        timer = new Timer(1000 / SAMPLE_FREQUENCY, actionListener);
        timer.setRepeats(true);

    }

    private void addPitchLines(){
        PitchLine horizon = new PitchLine(1000,0);
        pitchLines.add(horizon);

        for (int p1 = -500; p1 < 700; p1 += 200){
            pitchLines.add(new PitchLine(100,p1));
        }

        for (int p2 = -400; p2 < 600; p2 += 200){
            if (p2 != 0) {
                pitchLines.add(new PitchLine(200, p2));
            }
        }

    }

    private void addRollLines(){
        for (int r1 = -80; r1 < 100; r1 += 20){
            rollLines.add(new RollLine(50,Math.toRadians(r1)));
        }

        for (int r2 = -70; r2 < 90; r2 += 20){
            rollLines.add(new RollLine(25,Math.toRadians(r2)));
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

            textField.setText("theta = " + String.format("%.1f",2.86*theta) + " deg; phi = " +
                    String.format("%.1f",Math.toDegrees(phi)) + " deg; delta_e = " +
                    String.format("%.1f",controlSignal.getElevator()) + " deg; delta_a = " +
                    String.format("%.1f",controlSignal.getAileron()) + " deg");

            cessnaPitch.performPitchCalculation(controlSignal.getElevator());
            cessnaPitch.performRollCalculation(controlSignal.getAileron());

            theta = cessnaPitch.getTheta();
            phi = cessnaPitch.getPhi();

            indicators.repaint();
        }
    };

    protected Timer getTimer() {
        return timer;
    }

    private class Indicators extends JPanel {
        @Override
        public void paintComponent(Graphics graphics){
            super.paintComponent(graphics);
            Graphics2D graphics2d = (Graphics2D) graphics;
            graphics2d.setColor(Color.black);

            int xm = (int) (midX + Math.pow(Math.sin(phi),1) * Math.toDegrees(theta));
            int ym = (int) (midY + Math.pow(Math.cos(phi),2) * Math.toDegrees(theta));

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
