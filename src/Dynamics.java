/**
 * Created by martin on 6-7-2017.
 */
class Dynamics {
    private double samplePeriod;
    protected double V, m, twmuc, KY2, c, S, lh, CX0, CZ0, Cm0, CXu, CZu, Cmu, CXa, CZa, Cma, CXq, CZq, Cmq, CXde, CZde, Cmde, CXfa, CZfa, Cmfa;
    protected double xu, xa, xt, xq, xde;
    protected double zu, za, zt, zq, zde;
    protected double mu, ma, mt, mq, mde;

    Dynamics(int frequency) {
        samplePeriod = (double) 1 / frequency;

        V = 59.9;
        m = 53361;
        twmuc = 2 * 102.7;
        KY2 = 0.980;
        c = 2.022;
        S = 24.2;
        lh = 5.5;

        CX0 = 0;
        CZ0 = -1.1360;
        Cm0 = 0;
        CXu = -0.2199;
        CZu = -2.2720;
        Cmu = 0;
        CXa = 0.4653;
        CZa = -5.1600;
        Cma = -0.4300;
        CXfa = 0;
        CZfa = -1.4300;
        Cmfa = -3.7000;
        CXq = 0;
        CZq = -3.8600;
        Cmq = -7.0400;
        CXde = 0;
        //CXdt =
        CZde = -0.6238;
        Cmde = -1.5530;

        UpdateStateMatrix(V);
    }

    public void UpdateStateMatrix(double Velocity){
        V = Velocity;
        xu   = (Velocity/c)*(CXu/twmuc);
        xa   = (Velocity/c)*(CXa/twmuc);
        xt   = (Velocity/c)*(CZ0/twmuc);
        xq   = 0;
        xde  = (Velocity/c)*(CXde /twmuc);
        //xdt =

        zu   = (Velocity/c)*( CZu/(twmuc-CZfa));
        za   = (Velocity/c)*( CZa/(twmuc-CZfa));
        zt   = (Velocity/c)*(-CX0/(twmuc-CZfa));
        zq   = (Velocity/c)*((CZq+twmuc)/(twmuc-CZfa));
        zde  = (Velocity/c)*(CZde /(twmuc-CZfa));
        //zdt =

        mu   = (Velocity/c)*((Cmu+CZu*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        ma   = (Velocity/c)*((Cma+CZa*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        mt   = (Velocity/c)*((-CX0*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        mq   = (Velocity/c)*(Cmq+Cmfa*(twmuc+CZq)/(twmuc-CZfa))/(twmuc*KY2);
        mde  = (Velocity/c)*((Cmde + CZde *Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        //mdt =
    }

    double integrate(double output, double integrand){
        return output + integrand * samplePeriod;
    }

    double differentiate(double previous, double current){
        return (current - previous) / samplePeriod;
    }

}