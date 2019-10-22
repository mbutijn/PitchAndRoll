import java.awt.*;

public class Thrust {
    public void draw(Graphics2D graphics2d, int currentThrust) {
        graphics2d.drawLine(800, 450, 800, 600);
        int level = 500 - 2 * currentThrust;
        graphics2d.drawLine(800, level, 820, level);
    }
}
