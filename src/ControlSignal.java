import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by martin on 6-7-2017.
 */
class ControlSignal {

    private JFrame frame;
    private boolean controlling = true;
    private double lastAileron, lastElevator = 0;

    ControlSignal(JFrame f) {
        frame = f;
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlling = !controlling;
                if (controlling) {
                    Simulator.setControlMessage("");
                } else {
                    Simulator.setControlMessage(", Click on the screen for mouse control");
                }
                System.out.println("controlling pitch and roll: " + controlling);
            }
        });
    }

    double getElevator() {
        double mousePositionY = getPoint().getY() - frame.getY();

        if(mousePositionY < 0) {
            mousePositionY = 0;
        } else if (mousePositionY > Simulator.screenHeight){
            mousePositionY = Simulator.screenHeight;
        }
        if(isControlling()) {
           lastElevator = 0.03 * (mousePositionY - 0.5 * Simulator.screenHeight);
        }
        return lastElevator;
    }

    double getAileron() {
        double mousePositionX = getPoint().getX() - frame.getX();

        if(mousePositionX < 0) {
            mousePositionX = 0;
        } else if (mousePositionX > Simulator.screenWidth){
            mousePositionX = Simulator.screenWidth;
        }
        if(isControlling()) {
            lastAileron = 0.005 * (mousePositionX - 0.5 * Simulator.screenWidth);
        }
        return lastAileron;
    }

    private Point getPoint(){
        return MouseInfo.getPointerInfo().getLocation();
    }

    void reset(){
        lastAileron = 0;
        lastElevator = 0;
    }

    private boolean isControlling() {
        return controlling;
    }

}