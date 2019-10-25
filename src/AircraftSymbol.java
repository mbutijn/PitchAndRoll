import java.awt.*;

/**
 * Created by martin on 6-7-2017.
 */
class AircraftSymbol {
    private int midY,left1,left2,left3,right1,right2,right3, leftFP, rightFP;

    AircraftSymbol(double l1, double r3){
        double midX = 0.5 * (l1 + r3);
        midY = (int) (0.5 * Simulator.screenHeight);

        left1 = (int) (l1 + 200);
        left2 = (int) (midX - 30);
        left3 = (int) (midX - 10);
        right1 = (int) (midX + 10);
        right2 = (int) (midX + 30);
        right3 = (int) (r3 - 200);

        leftFP = (int) midX - 50;
        rightFP = (int) midX + 50;
    }

    void makePitchSymbol(Graphics2D graphics2d) {

        graphics2d.drawLine(left1, midY, left2, midY);
        graphics2d.drawLine(right2, midY, right3, midY);

        graphics2d.drawLine(left2, midY, left2, midY + 20);
        graphics2d.drawLine(right2,midY, right2, midY + 20);

        graphics2d.drawLine(left3, midY, right1, midY);
    }

    void makeFlightPathIndicator(Graphics2D graphics2d, double x, double y){
        graphics2d.drawLine((int)Math.round(leftFP + x), (int)Math.round(midY + y), (int)Math.round(rightFP + x), (int)Math.round(midY + y));
    }

}