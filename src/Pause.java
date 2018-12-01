import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Pause {

    private Simulator simulator;
    private static JButton pauseButton = new JButton("Pause");

    Pause(Simulator simulator){
        this.simulator = simulator;
    }

    JButton makeButton() {
        pauseButton.addActionListener(new PauseButton());
        pauseButton.setSize(150,50);

        return pauseButton;
    }

    private class PauseButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (pauseButton.getText().equals("Pause") || pauseButton.getText().equals("Restart")) {
                pauseButton.setText("Resume");
                simulator.getTimer().stop();
            } else if (pauseButton.getText().equals("Resume")){
                simulator.getTimer().start();
                pauseButton.setText("Pause");
            }
        }
    }

}
