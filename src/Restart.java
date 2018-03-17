import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by martin on 7-7-2017.
 */
public class Restart {

    private Simulator simulator;
    public static JButton restartButton;

    protected JButton makeButton(Simulator simulator) {
        this.simulator = simulator;
        restartButton = new JButton("Start");
        restartButton.addActionListener( new Restarter());
        restartButton.setSize(100,50);

        return restartButton;
    }

    private class Restarter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            simulator.cessnaPitch.reset();

            if (restartButton.getText().equals("Start") || restartButton.getText().equals("Restart") || restartButton.getText().equals("Hold current")) {
                restartButton.setText("Stop");
                simulator.getTimer().start();
            } else if (restartButton.getText().equals("Stop")){
                simulator.getTimer().stop();
                restartButton.setText("Restart");
            }
        }
    }

}