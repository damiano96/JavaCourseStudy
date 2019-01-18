public class Start {

    public static final int N = 4;
    public static final double X_FIRST = 2.5;
    public static final double X_LAST = -0.2;
    public static final int STEPS = 5;

    private int factorial(int n) {
        if(n<2) return n;
        return n*factorial(n-1);
    }

    private double approximationMethod(double x) {
        double result = x;
        for(int n=1; n<N; n++) {
            result += Math.pow(-1, n) * Math.pow(2, 2*n) * (Math.pow(x, (2*n)+1) / factorial((2*n)+1));
        }
        return result;
    }

    public static void main(String args[]) {
        Start s = new Start();

        double valueSinCos, approximation, difference;
        double x = X_FIRST;
        double nextStep = (X_LAST - X_FIRST) / (STEPS+1);

        for(int i=0; i<=STEPS+1; i++) {
            if(i>0) x+=nextStep;
            valueSinCos = Math.sin(x)*Math.cos(x);
            approximation = s.approximationMethod(x);
            difference = valueSinCos - approximation;

            System.out.println(String.format( "x=%7.4f sin(x)cos(x)=%8.6f aprox=%8.6f delta=%10.8f",
                  x,      valueSinCos,  approximation,  difference ));
        }
    }
}
