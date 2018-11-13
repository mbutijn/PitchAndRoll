import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by martin on 7-7-2017.
 */
class Restart {

    private Simulator simulator;
    private static JButton restartButton;

    Restart(Simulator simulator){
        this.simulator = simulator;
    }

    JButton makeButton() {
        restartButton = new JButton("Start");
        restartButton.addActionListener(new Restarter());
        restartButton.setSize(150,50);

        return restartButton;
    }

    private class Restarter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            simulator.cessnaPitch.resetControls();
            simulator.cessnaPitch.reset();

            if (restartButton.getText().equals("Start") || restartButton.getText().equals("Restart")) {
                restartButton.setText("Stop");
                simulator.pauseButton.pauseButton.setEnabled(true);
                simulator.getTimer().start();
            } else if (restartButton.getText().equals("Stop")){
                simulator.getTimer().stop();
                simulator.pauseButton.pauseButton.setEnabled(false);
                restartButton.setText("Restart");
            }
        }
    }

}