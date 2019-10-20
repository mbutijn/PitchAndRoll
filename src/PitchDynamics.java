class PitchDynamics extends Dynamics {

    private static final double Ka = 10.6189, T1 = 0.9906, T2 = 2.7565, T3 = 7.6122, Ks = 0.29, K = Ka * Ks; // Parameters
    protected double u;
    private double udot;
    private double alfa, alfadot;
    private double theta, thetadot, thetadotdot, thetadotdotdot, delta_edot, delta_e_old; // Dynamics
    private double qc_over_V, qc_over_Vdot;

    PitchDynamics(int frequency) {
        super(frequency);
    }

    void updateU(double delta_e){
        udot = xu * u + xa * alfa + xt * theta + xq * qc_over_V + xde * delta_e;
        u = integrate(u, udot);
    }

    public double getU(){
        return u;
    }

    void updateAlfa(double delta_e){
        alfadot = zu * u + za * alfa + zt * theta + zq * qc_over_V + zde * delta_e;
        alfa = integrate(alfa, alfadot);
    }

    public double getAlfa(){
        return alfa;
    }

    void updateTheta(){
        thetadot = (V/c)*qc_over_V;
        theta = integrate(theta, thetadot);
    }

    public double getTheta()
    {
        return theta;
    }

    void updateQC_over_V(double delta_e) {
        qc_over_Vdot = mu * u + ma * alfa + mt * theta + mq * qc_over_V + mde * delta_e;
        qc_over_V = integrate(qc_over_V, qc_over_Vdot);
    }

    public double getQC_over_V(){
        return qc_over_V;
    }

    double updateThetaOld(double delta_e){
        delta_edot = differentiate(delta_e_old, delta_e); // Differentiate elevator deflection
        delta_e_old = delta_e; // Store the previous value
        thetadotdotdot = -T2*thetadotdot - T3*thetadot + K*delta_edot + K*T1*delta_e; // Perform differential equation for pitch

        // Integrate (3 times)
        thetadotdot = integrate(thetadotdot, thetadotdotdot);
        thetadot = integrate(thetadot, thetadotdot);
        theta = integrate(theta, thetadot);
        return theta;
    }

    void reset() {
        u = 0;
        udot = 0;
        alfa = 0;
        alfadot = 0;
        theta = 0;
        thetadot = 0;
        thetadotdot = 0;
        thetadotdotdot = 0;
        qc_over_V = 0;
        qc_over_Vdot = 0;

        delta_edot = 0;
        delta_e_old = 0;
        V = 121.3;
        UpdateStateMatrix(V);
        System.out.println("Cessna pitch reset");
    }
}
