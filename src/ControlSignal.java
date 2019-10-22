import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by martin on 6-7-2017.
 */
class ControlSignal implements KeyListener {

    private Simulator simulator;
    private boolean controlling = true;
    private double lastAileron, lastElevator = 0;
    private int thrust = 0;
    private int rudder = 0;

    ControlSignal(Simulator simulator) {
        this.simulator = simulator;
        simulator.addMouseListener(new MouseAdapter() {
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
        double mousePositionY = getPoint().getY() - simulator.getY();

        if(mousePositionY < 0) {
            mousePositionY = 0;
        } else if (mousePositionY > Simulator.screenHeight){
            mousePositionY = Simulator.screenHeight;
        }
        if(isControlling()) {
           lastElevator = -0.01 * (mousePositionY - 0.5 * Simulator.screenHeight);
        }
        return lastElevator;
    }

    double getAileron() {
        double mousePositionX = getPoint().getX() - simulator.getX();

        if(mousePositionX < 0) {
            mousePositionX = 0;
        } else if (mousePositionX > Simulator.screenWidth){
            mousePositionX = Simulator.screenWidth;
        }
        if(isControlling()) {
            lastAileron = -0.001 * (mousePositionX - 0.5 * Simulator.screenWidth);
        }
        return lastAileron;
    }

    private Point getPoint(){
        return MouseInfo.getPointerInfo().getLocation();
    }

    void reset(){
        lastAileron = 0;
        lastElevator = 0;
        rudder = 0;
        thrust = 0;
    }

    public int getRudder(){
        return rudder;
    }

    public double getRudderValue(){
        return -0.1 * rudder;
    }

    public int getThrust(){
        return thrust;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_RIGHT) && rudder < 50){
            rudder++;
        } else if ((code == KeyEvent.VK_LEFT) && rudder > -50){
            rudder--;
        } else if ((code == KeyEvent.VK_PAGE_UP) && thrust < 25){
            thrust++;
        } else if ((code == KeyEvent.VK_PAGE_DOWN) && thrust > -50){
            thrust--;
        } else if (code == KeyEvent.VK_SPACE) {
            Timer timer = simulator.getTimer();
            if (timer.isRunning()) {
                timer.stop();
            } else {
                timer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private boolean isControlling() {
        return controlling;
    }
}