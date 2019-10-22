import static java.lang.Math.PI;

class RollDynamics extends Dynamics {
    private final double b; // span width
    private double yb, yphi, yp, yr, yda, ydr;
    private double lb, lp, lr, lda, ldr;
    private double nb, np, nr, nda, ndr;
    private double beta, betadot, phi, phidot, pb_over_2V, pb_over_2Vdot, rb_over_2V, rb_over_2Vdot, psi; // Asymmetric motion

    RollDynamics(int frequency) {
        super(frequency);
        b = 13.36;
        double CL = 1.1360;
        double mub = 15.5;

        double K2X = 0.012;
        double K2Z = 0.037;
        double KXZ = 0.002;
        double K2XZ = KXZ * KXZ;

        double CYb = -0.9896;
        double Clb = -0.0772;
        double Cnb = 0.1638;
        double CYp = -0.0870;
        double Clp = -0.3444;
        double Cnp = -0.0108;
        double CYr = 0.4300;
        double Clr = 0.2800;
        double Cnr = -0.1930;
        double CYda = 0;
        double Clda = -0.2349;
        double Cnda = 0.0286;
        double CYdr = 0.3037;
        double Cldr = 0.0286;
        double Cndr = -0.1261;

        yb = (V/b) * (CYb/(2*mub));
        yphi = (V/b) * (CL/(2*mub));
        yp = (V/b) * (CYp/(2*mub));
        yr = (V/b) * ((CYr-4*mub)/(2*mub));
        yda = (V/b) * (CYda/(2*mub));
        ydr = (V/b) * (CYdr/(2*mub));

        lb = (V/b) * (Clb*K2Z + Cnb * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lp = (V/b) * (Clp*K2Z + Cnp * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lr = (V/b) * (Clr*K2Z + Cnr * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lda = (V/b) * (Clda*K2Z + Cnda * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        ldr = (V/b) * (Cldr*K2Z + Cndr * KXZ)/(4*mub*(K2X*K2Z-K2XZ));

        nb = (V/b) * (Cnb*KXZ + Cnb * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        np = (V/b) * (Cnp*KXZ + Cnp * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        nr = (V/b) * (Cnr*KXZ + Cnr * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        nda = (V/b) * (Cnda*KXZ + Cnda * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        ndr = (V/b) * (Cndr*KXZ + Cndr * K2X)/(4*mub*(K2X*K2Z-K2XZ));
    }

    void updateBeta(double delta_r){
        betadot = yb * beta + yphi * phi + yp * pb_over_2V + yr * rb_over_2V + ydr * delta_r;
        beta = integrate(beta, betadot);
    }

    public double getBeta(){
        return beta;
    }

    void updatePhi(boolean toggleRoll){
        phidot = 2 * (V/b) * pb_over_2V;
        phi = integrate(phi, phidot);

        if(toggleRoll){
            phi += PI;
            System.out.println("toggleRoll in roll");
        }

        if (phi < -PI){
            phi += 2 * PI;
        } else if (phi > PI){
            phi -= 2 * PI;
        }
    }

    public double getPhi(){
        return phi;
    }

    void updatePb_over_2V(double delta_a, double delta_r){
        pb_over_2Vdot = lb * beta + lp * pb_over_2V + lr * rb_over_2V + lda * delta_a + ldr * delta_r;
        pb_over_2V = integrate(pb_over_2V, pb_over_2Vdot);
    }

    public double getPb_over_2V(){
        return pb_over_2V;
    }

    void updateRb_over_2V(double delta_a, double delta_r){
        rb_over_2Vdot = nb * beta + np * pb_over_2V + nr * rb_over_2V + nda * delta_a + ndr * delta_r;
        rb_over_2V = integrate(rb_over_2V, rb_over_2Vdot);
    }

    public double getRb_over_2V(){
        return rb_over_2V;
    }

    public double getPsi(boolean toggleRoll){
        psi = integrate(psi, rb_over_2V * (2 * V) / b);
        if(toggleRoll){
            psi += PI;
            System.out.println("toggleRoll in heading");
        }

        if (psi < 0){
            psi += 2 * PI;
        } else if (psi > 2 * PI){
            psi -= 2 * PI;
        }
        return psi;
    }

    void reset(){
        beta = 0;
        betadot = 0;
        phi = 0;
        phidot = 0;
        pb_over_2V = 0;
        pb_over_2Vdot = 0;
        rb_over_2V = 0;
        rb_over_2Vdot = 0;
        psi = 0;

        System.out.println("Cessna roll reset");
    }
}
