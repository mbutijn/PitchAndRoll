import java.awt.*;

class RollLine {

    private int length;
    private double rollAngle;

    RollLine(int width, double distance){
        length = width;
        rollAngle = distance;
    }

    void draw(Graphics2D graphics2d, double midX, double midY, double radius, double phi) {
        int x1 = (int) (midX - radius * Math.sin(phi + rollAngle));
        int x2 = (int) (midX - (radius + length) * Math.sin(phi + rollAngle));
        int y1 = (int) (midY - radius * Math.cos(phi + rollAngle));
        int y2 = (int) (midY - (radius + length) * Math.cos(phi + rollAngle));
        graphics2d.drawLine(x1, y1, x2, y2);
    }
}
