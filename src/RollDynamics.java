class RollDynamics extends Dynamics {
    private static final double K_delta_a = 4.627, T_1 = 2.038, T_2 = 4.646, T_3 = 7.937; // Roll parameters
    private double phi, phidot, phidotdot, phidotdotdot, delta_a_old, delta_adot; // Roll dynamics

    RollDynamics(int frequency) {
        super(frequency);
    }

    double update(double delta_a){
        delta_adot = differentiate(delta_a_old, delta_a); // Differentiate aileron deflection
        delta_a_old = delta_a; // Store the previous value
        phidotdotdot = -T_2 *phidotdot - T_3 *phidot + K_delta_a*delta_adot + T_1 *delta_a; // Perform differential equation for roll

        // Integrate (3 times)
        phidotdot = integrate(phidotdot, phidotdotdot);
        phidot = integrate(phidot, phidotdot);
        phi = integrate(phi, phidot);
        return phi;
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
