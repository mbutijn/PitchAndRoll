import java.awt.*;

public class Rudder {
    public void draw(Graphics2D graphics2d, int currentRudder) {
        graphics2d.drawLine(700, 650, 900, 650);
        graphics2d.drawLine(800 + 2*currentRudder, 650, 800 + 2*currentRudder, 670);
    }
}
