import java.awt.*;

public class PitchLine {

    private int halfwidth, pitchdistance;

    protected PitchLine(int width, int distance){
        halfwidth = width;
        pitchdistance = distance;
    }

    public void draw(Graphics2D graphics2d, double xm, double ym, double phi){

        int xl = (int) (xm - halfwidth * Math.cos(phi) - pitchdistance * Math.sin(phi)); // left
        int xr = (int) (xm + halfwidth * Math.cos(phi) - pitchdistance * Math.sin(phi)); // right
        int yl = (int) (ym + halfwidth * Math.sin(phi) - pitchdistance * Math.cos(phi));
        int yr = (int) (ym - halfwidth * Math.sin(phi) - pitchdistance * Math.cos(phi));

        graphics2d.drawLine(xl, yl, xr, yr);
    }
}
