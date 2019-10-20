import java.awt.*;

class PitchLine extends IndicationLine{

    PitchLine(int length, double distance){
        super(length, distance);
    }

    void draw(Graphics2D graphics2d, double xm, double ym, double phi){

        int xl = (int) (xm - length * Math.cos(phi) - distance * Simulator.deg2pxl * Math.sin(phi)); // left
        int xr = (int) (xm + length * Math.cos(phi) - distance * Simulator.deg2pxl * Math.sin(phi)); // right
        int yl = (int) (ym + length * Math.sin(phi) - distance * Simulator.deg2pxl * Math.cos(phi));
        int yr = (int) (ym - length * Math.sin(phi) - distance * Simulator.deg2pxl * Math.cos(phi));

        graphics2d.drawLine(xl, yl, xr, yr);
    }
}
