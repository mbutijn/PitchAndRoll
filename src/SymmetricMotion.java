class SymmetricMotion extends Dynamics {

    private final double c; // chord length
    private double xu, xa, xt, xq, xde, xdt; // horizontal force derivatives
    private double zu, za, zt, zq, zde, zdt; // vertical force derivatives
    private double mu, ma, mt, mq, mde, mdt; // moment derivatives
    private double u, udot, alfa, alfadot, theta, thetadot, qc_over_V, qc_over_Vdot; // variables
    private double airspeed, climbrate, altitude = 3048; // m

    SymmetricMotion(int frequency) {
        super(frequency);
        c = 2.022;
        double twmuc = 2 * 102.7;
        double KY2 = 0.980;

        double CX0 = 0;
        double CZ0 = -1.1360;
        double Cm0 = 0;
        double CXu = -0.2199;
        double CZu = -2.2720;
        double Cmu = 0;
        double CXa = 0.4653;
        double CZa = -5.1600;
        double Cma = -0.4300;
        double CXfa = 0;
        double CZfa = -1.4300;
        double Cmfa = -3.7000;
        double CXq = 0;
        double CZq = -3.8600;
        double Cmq = -7.0400;
        double CXde = 0;
        double CZde = -0.6238;
        double Cmde = -1.5530;
        double CXdt = 0.5;
        double CZdt = 0.1;
        double Cmdt = 0.1;

        xu  = (V/c)*(CXu/twmuc);
        xa  = (V/c)*(CXa/twmuc);
        xt  = (V/c)*(CZ0/twmuc);
        xq  = 0;
        xde = (V/c)*(CXde /twmuc);
        xdt = (V/c)*(CXdt /twmuc);

        zu  = (V/c)*( CZu/(twmuc-CZfa));
        za  = (V/c)*( CZa/(twmuc-CZfa));
        zt  = (V/c)*(-CX0/(twmuc-CZfa));
        zq  = (V/c)*((CZq+twmuc)/(twmuc-CZfa));
        zde = (V/c)*(CZde /(twmuc-CZfa));
        zdt = (V/c)*(CZdt /(twmuc-CZfa));

        mu  = (V/c)*((Cmu+CZu*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        ma  = (V/c)*((Cma+CZa*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        mt  = (V/c)*((-CX0*Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        mq  = (V/c)*(Cmq+Cmfa*(twmuc+CZq)/(twmuc-CZfa))/(twmuc*KY2);
        mde = (V/c)*((Cmde + CZde *Cmfa/(twmuc-CZfa))/(twmuc*KY2));
        mdt = (V/c)*((Cmdt + CZdt *Cmfa/(twmuc-CZfa))/(twmuc*KY2));
    }

    void updateU(double delta_e, double delta_t){
        udot = xu * u + xa * alfa + xt * theta + xq * qc_over_V + xde * delta_e + xdt * delta_t;
        u = integrate(u, udot);
    }

    public double getU(){
        return u;
    }

    void updateAlfa(double delta_e, double delta_t){
        alfadot = zu * u + za * alfa + zt * theta + zq * qc_over_V + zde * delta_e + zdt * delta_t;
        alfa = integrate(alfa, alfadot);
    }

    public double getAlfa(){
        return alfa;
    }

    public boolean updateTheta(){
        thetadot = (V/c)*qc_over_V;
        theta = integrate(theta, thetadot);

        if (theta > 90){
            return true;
        } else if (theta < -90){
            return true;
        } else {
            return false;
        }
    }

    public double getTheta()
    {
        return theta;
    }

    void updateQC_over_V(double delta_e, double delta_t, boolean toggleRoll) {
        qc_over_Vdot = mu * u + ma * alfa + mt * theta + mq * qc_over_V + mde * delta_e + mdt * delta_t;
        qc_over_V = integrate(qc_over_V, qc_over_Vdot);

        if (toggleRoll){
            qc_over_V *= -1;
        }
    }

    public double getQC_over_V(){
        return qc_over_V;
    }

    public double getAirspeed() {
        airspeed = V + u;
        return airspeed;
    }

    public double getClimbRate() {
        climbrate = Math.sin(Math.toRadians(theta - alfa)) * airspeed;
        return climbrate;
    }

    public double getAltitude(){
        altitude = integrate(altitude, climbrate);
        return altitude;
    }

    void reset() {
        u = 0;
        udot = 0;
        alfa = 0;
        alfadot = 0;
        theta = 0;
        thetadot = 0;
        qc_over_V = 0;
        qc_over_Vdot = 0;

        System.out.println("Cessna pitch reset");
    }
}
