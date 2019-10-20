import static java.lang.Math.PI;

class RollDynamics extends Dynamics {
    private static final double K_delta_a = 4.627, T_1 = 2.038, T_2 = 4.646, T_3 = 7.937; // Roll parameters
    private double phi, phidot, phidotdot, phidotdotdot, delta_a_old, delta_adot; // Roll dynamics
    private double beta, pb_over_2V, rb_over_2V, betadot, pb_over_2Vdot, rb_over_2Vdot, psi;
    protected double b, CL, mub, K2X, K2Z, KXZ, K2XZ, CYb, Clb, Cnb, CYp, Clp, Cnp, CYr, Clr, Cnr, CYda, Clda, Cnda, CYdr, Cldr, Cndr;
    private double yb, yphi, yp, yr, yda;
    private double lb, lp, lr, lda;
    private double nb, np, nr, nda;

    RollDynamics(int frequency) {
        super(frequency);

        b = 13.36;
        CL = 1.1360;
        mub = 15.5;
        K2X = 0.012;
        K2Z = 0.037;
        KXZ = 0.002;
        K2XZ = KXZ * KXZ;

        CYb = -0.9896;
        Clb = -0.0772;
        Cnb = 0.1638;
        CYp = -0.0870;
        Clp = -0.3444;
        Cnp = -0.0108;
        CYr = 0.4300;
        Clr = 0.2800;
        Cnr = -0.1930;
        CYda = 0;
        Clda = -0.2349;
        Cnda = 0.0286;
        CYdr = 0.3037;
        Cldr = 0.0286;
        Cndr = -0.1261;

        yb = (V/b) * (CYb/(2*mub));
        yphi = (V/b) * (CL/(2*mub));
        yp = (V/b) * (CYp/(2*mub));
        yr = (V/b) * ((CYr-4*mub)/(2*mub));
        yda = (V/b) * (CYda/(2*mub));

        lb = (V/b) * (Clb*K2Z + Cnb * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lp = (V/b) * (Clp*K2Z + Cnp * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lr = (V/b) * (Clr*K2Z + Cnr * KXZ)/(4*mub*(K2X*K2Z-K2XZ));
        lda = (V/b) * (Clda*K2Z + Cnda * KXZ)/(4*mub*(K2X*K2Z-K2XZ));

        nb = (V/b) * (Clb*KXZ + Cnb * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        np = (V/b) * (Clp*KXZ + Cnp * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        nr = (V/b) * (Clr*KXZ + Cnr * K2X)/(4*mub*(K2X*K2Z-K2XZ));
        nda = (V/b) * (Clda*KXZ + Cnda * K2X)/(4*mub*(K2X*K2Z-K2XZ));
    }

    double updatePhiOld(double delta_a){
        delta_adot = differentiate(delta_a_old, delta_a); // Differentiate aileron deflection
        delta_a_old = delta_a; // Store the previous value
        phidotdotdot = -T_2 *phidotdot - T_3 *phidot + K_delta_a*delta_adot + T_1 *delta_a; // Perform differential equation for roll

        // Integrate (3 times)
        phidotdot = integrate(phidotdot, phidotdotdot);
        phidot = integrate(phidot, phidotdot);
        phi = integrate(phi, phidot);
        return phi;
    }

    void updateBeta(double delta_a){
        betadot = yb * beta + yphi * phi + yp * pb_over_2V + yr * rb_over_2V;
        beta = integrate(beta, betadot);
    }

    public double getBeta(){
        return beta;
    }

    void updatePhi(double delta_a){
        phidot = 2 * (V/b) * pb_over_2V;
        phi = integrate(phi, phidot);
    }

    public double getPhi(){
        return phi;
    }

    void updatePb_over_2V(double delta_a){
        pb_over_2Vdot = lb * beta + lp * pb_over_2V + lr * rb_over_2V + lda * delta_a;
        pb_over_2V = integrate(pb_over_2V, pb_over_2Vdot);
    }

    public double getPb_over_2V(){
        return pb_over_2V;
    }

    void updateRb_over_2V(double delta_a){
        rb_over_2Vdot = nb * beta + np * pb_over_2V + nr * rb_over_2V + nda * delta_a;
        rb_over_2V = integrate(rb_over_2V, rb_over_2Vdot);
    }

    public double getRb_over_2V(){
        return rb_over_2V;
    }

    public double getPsi(){
        double r = rb_over_2V * (2 * V) / b;
        psi = integrate(psi, r);
        if (psi < 0){
            psi += 2 * PI;
        } else if (psi > 2 * PI){
            psi -= 2 * PI;
        }
        return psi;
    }

    void reset(){
        phi = 0;
        phidot = 0;
        phidotdot = 0;
        phidotdotdot = 0;
        delta_adot = 0;
        delta_a_old = 0;

        System.out.println("Cessna roll reset");
    }
}
