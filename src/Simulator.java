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
        PitchLine five = new PitchLine(100, 100);
        PitchLine minfive = new PitchLine(100, -100);
        PitchLine ten = new PitchLine(200, 200);
        PitchLine minten = new PitchLine(200, -200);
        PitchLine fifteen = new PitchLine(100, 300);
        PitchLine minfifteen = new PitchLine(100, -300);
        PitchLine twenty = new PitchLine(200, 400);
        PitchLine mintwenty = new PitchLine(200, -400);
        PitchLine twentyfive = new PitchLine(100, 500);
        PitchLine mintwentyfive = new PitchLine(100, -500);

        pitchLines.add(horizon);
        pitchLines.add(five);
        pitchLines.add(minfive);
        pitchLines.add(ten);
        pitchLines.add(minten);
        pitchLines.add(fifteen);
        pitchLines.add(minfifteen);
        pitchLines.add(twenty);
        pitchLines.add(mintwenty);
        pitchLines.add(twentyfive);
        pitchLines.add(mintwentyfive);
    }

    private void addRollLines(){
        RollLine zero = new RollLine(50,0);
        RollLine ten = new RollLine(25,Math.toRadians(10));
        RollLine minten = new RollLine(25,Math.toRadians(-10));
        RollLine twenty = new RollLine(50,Math.toRadians(20));
        RollLine mintwenty = new RollLine(50,Math.toRadians(-20));
        RollLine thirty = new RollLine(25,Math.toRadians(30));
        RollLine minthirty = new RollLine(25,Math.toRadians(-30));
        RollLine fourty = new RollLine(50,Math.toRadians(40));
        RollLine minfourty = new RollLine(50,Math.toRadians(-40));
        RollLine fifty = new RollLine(25,Math.toRadians(50));
        RollLine minfifty = new RollLine(25,Math.toRadians(-50));
        RollLine sixty = new RollLine(50,Math.toRadians(60));
        RollLine minsixty = new RollLine(50,Math.toRadians(-60));
        RollLine seventy = new RollLine(25,Math.toRadians(70));
        RollLine minseventy = new RollLine(25,Math.toRadians(-70));
        RollLine eighty = new RollLine(50,Math.toRadians(80));
        RollLine mineighty = new RollLine(50,Math.toRadians(-80));

        rollLines.add(zero);
        rollLines.add(ten);
        rollLines.add(minten);
        rollLines.add(twenty);
        rollLines.add(mintwenty);
        rollLines.add(thirty);
        rollLines.add(minthirty);
        rollLines.add(fourty);
        rollLines.add(minfourty);
        rollLines.add(fifty);
        rollLines.add(minfifty);
        rollLines.add(sixty);
        rollLines.add(minsixty);
        rollLines.add(seventy);
        rollLines.add(minseventy);
        rollLines.add(eighty);
        rollLines.add(mineighty);
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

            int xm = (int) (midX + Math.pow(Math.sin(phi),2) * Math.toDegrees(theta));
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
