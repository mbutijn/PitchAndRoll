import java.awt.*;

public class HeadingLine extends IndicationLine{

    HeadingLine(int length, int distance){
        super(length, distance);
    }

    public void draw(Graphics2D graphics2d, double psi){
        int offset = 0;
        if (psi < 40 && distance > 320){
            offset = -360;
        } else if (psi > 320 && distance < 40){
            offset = 360;
        }
        int x = (int) (Simulator.midX + (distance + offset - psi) * Simulator.deg2pxl);

        graphics2d.drawLine(x, 50, x, 50 - length);
    }
}
