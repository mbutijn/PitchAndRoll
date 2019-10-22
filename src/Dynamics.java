/**
 * Created by martin on 6-7-2017.
 */
class Dynamics {
    private double samplePeriod;
    protected double V;

    Dynamics(int frequency) {
        samplePeriod = (double) 1 / frequency;
        V = 59.9;
    }

    double integrate(double output, double integrand){
        return output + integrand * samplePeriod;
    }

    double differentiate(double previous, double current){
        return (current - previous) / samplePeriod;
    }

}