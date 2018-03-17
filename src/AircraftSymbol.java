import java.awt.*;

/**
 * Created by martin on 6-7-2017.
 */
public class AircraftSymbol {
    private double midY,left1,left2,left3,right1,right2,right3;

    public AircraftSymbol(double l1, double r3){
        double midX = 0.5 * (l1 + r3);
        midY = 0.5 * Simulator.screenHeight;

        left1 = l1 + 200;
        left2 = midX - 30;
        left3 = midX - 10;
        right1 = midX + 10;
        right2 = midX + 30;
        right3 = r3 - 200;
    }

    public void makeSymbol(Graphics2D graphics2d) {

        graphics2d.drawLine((int)left1, (int) (midY), (int)left2, (int) (midY));
        graphics2d.drawLine((int)right2, (int) (midY), (int)right3, (int) (midY));

        graphics2d.drawLine((int)left2, (int) (midY), (int)left2, (int) (midY + 20));
        graphics2d.drawLine((int)right2, (int) (midY), (int)right2, (int) (midY + 20));

        graphics2d.drawLine((int)left3, (int) (midY), (int)right1, (int) (midY));
    }

}