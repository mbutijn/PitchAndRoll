/**
 * Created by martin on 6-7-2017.
 */
class CessnaDynamics {

    private double samplePeriod;
    private static final double Ka = 10.6189, T1 = 0.9906, T2 = 2.7565, T3 = 7.6122, Ks = 0.29, K = Ka * Ks; // Pitch parameters
    private double theta, thetadot, thetadotdot, thetadotdotdot, delta_edot, delta_e_old; // Pitch dynamics
    private static final double K_delta_a = 4.627, T_1 = 2.038, T_2 = 4.646, T_3 = 7.937; // Roll parameters
    private double phi, phidot, phidotdot, phidotdotdot, delta_a_old, delta_adot; // Roll dynamics

    CessnaDynamics(int frequency) {
        samplePeriod = (double) 1/frequency;
    }

    double performPitchCalculation(double delta_e){
        delta_edot = differentiate(delta_e_old, delta_e);
        delta_e_old = delta_e; // Store the old pilot's control signal
        thetadotdotdot = -T2*thetadotdot - T3*thetadot + K*delta_edot + K*T1*delta_e;

        // Integrate
        thetadotdot = integrate(thetadotdot, thetadotdotdot);
        thetadot = integrate(thetadot, thetadotdot);
        theta = integrate(theta, thetadot);
        return theta;
    }

    double performRollCalculation(double delta_a){
        delta_adot = differentiate(delta_a_old, delta_a);
        delta_a_old = delta_a; // Store the old pilot's control signal
        phidotdotdot = -T_2 *phidotdot - T_3 *phidot + K_delta_a*delta_adot + T_1 *delta_a;// Perform roll angle calculation
        phidotdot = integrate(phidotdot, phidotdotdot);
        phidot = integrate(phidot, phidotdot); // Integrate
        phi = integrate(phi, phidot); // Integrate
        return phi;
    }

    void reset(){
        theta = 0;
        thetadot = 0;
        thetadotdot = 0;
        thetadotdotdot = 0;
        delta_edot = 0;
        delta_e_old = 0;
        System.out.println("Cessna pitch reset");

        phi = 0;
        phidot = 0;
        phidotdot = 0;
        phidotdotdot = 0;
        delta_adot = 0;
        delta_a_old = 0;

        System.out.println("Cessna roll reset");

    }

    void resetControls(){
        ControlSignal cs = Simulator.controlSignal;
        cs.lastAileron = 0;
        cs.lastElevator = 0;
    }

    private double integrate(double output, double integrand){
        return output + integrand * samplePeriod;
    }

    private double differentiate(double previous, double current){
        return (current - previous) / samplePeriod;
    }

}