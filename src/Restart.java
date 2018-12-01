import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by martin on 7-7-2017.
 */
class Restart {

    private Simulator simulator;
    private static JButton restartButton = new JButton("Start");

    Restart(Simulator simulator){
        this.simulator = simulator;
    }

    JButton makeButton() {
        restartButton.addActionListener(new Restarter());
        restartButton.setSize(150,50);

        return restartButton;
    }

    private class Restarter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            simulator.controlSignal.reset();
            simulator.pitchDynamics.reset();
            simulator.rollDynamics.reset();

            if (restartButton.getText().equals("Start") || restartButton.getText().equals("Restart")) {
                JButton pauseButton = Simulator.pauseButton;
                restartButton.setText("Stop");
                pauseButton.setEnabled(true);
                pauseButton.setText("Pause");
                simulator.getTimer().start();
            } else if (restartButton.getText().equals("Stop")){
                simulator.getTimer().stop();
                Simulator.pauseButton.setEnabled(false);
                restartButton.setText("Restart");
            }
        }
    }

}