import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by martin on 6-7-2017.
 */
public class ControlSignal {

    private JFrame frame;
    private boolean controlling = true;
    private double lastAileron = 0, lastElevator = 0;

    public ControlSignal(JFrame f) {
        frame = f;
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlling = !controlling;
                System.out.println("controlling pitch and roll: " + controlling);
            }
        });
    }

    public double getElevator() {
        double mousePositionY = MouseInfo.getPointerInfo().getLocation().getY()-frame.getY();

        if(mousePositionY < 0) {
            mousePositionY = 0;
        } else if (mousePositionY > Simulator.screenHeight){
            mousePositionY = Simulator.screenHeight;
        }
        if(isControlling()) {
           lastElevator = 0.02 * (mousePositionY - 0.5 * Simulator.screenHeight);
        }
        return lastElevator;
    }

    public double getAileron() {
        double mousePositionX = MouseInfo.getPointerInfo().getLocation().getX()-frame.getX();

        if(mousePositionX < 0) {
            mousePositionX = 0;
        } else if (mousePositionX > Simulator.screenWidth){
            mousePositionX = Simulator.screenWidth;
        }
        if(isControlling()) {
            lastAileron = 0.01 * (mousePositionX - 0.5 * Simulator.screenWidth);
        }
        return lastAileron;
    }

    private boolean isControlling() {
        return controlling;
    }

}