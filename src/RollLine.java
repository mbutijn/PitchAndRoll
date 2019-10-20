import java.awt.*;

class RollLine extends IndicationLine{

    RollLine(int length, double distance){
        super(length, distance);
    }

    void draw(Graphics2D graphics2d, double radius, double phi) {
        int x1 = (int) (Simulator.midX - radius * Math.sin(phi + distance));
        int x2 = (int) (Simulator.midX - (radius + length) * Math.sin(phi + distance));
        int y1 = (int) (Simulator.midY - radius * Math.cos(phi + distance));
        int y2 = (int) (Simulator.midY - (radius + length) * Math.cos(phi + distance));
        graphics2d.drawLine(x1, y1, x2, y2);
    }
}
