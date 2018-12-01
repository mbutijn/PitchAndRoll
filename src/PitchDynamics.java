class PitchDynamics extends Dynamics {

    private static final double Ka = 10.6189, T1 = 0.9906, T2 = 2.7565, T3 = 7.6122, Ks = 0.29, K = Ka * Ks; // Parameters
    private double theta, thetadot, thetadotdot, thetadotdotdot, delta_edot, delta_e_old; // Dynamics

    PitchDynamics(int frequency) {
        super(frequency);
    }

    double update(double delta_e){
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
        theta = 0;
        thetadot = 0;
        thetadotdot = 0;
        thetadotdotdot = 0;
        delta_edot = 0;
        delta_e_old = 0;
        System.out.println("Cessna pitch reset");
    }
}
