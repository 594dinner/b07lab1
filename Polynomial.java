public class Polynomial {
    private final double[] n;
    public Polynomial() {
        this.n = new double[]{0.0};
    }
    public Polynomial(double[] c) {
        if (c == null || c.length == 0) {
            this.n = new double[]{0.0};
        } else {
            this.n = new double[c.length];
            System.arraycopy(c, 0, this.n, 0, c.length);
        }
    }
    public Polynomial add(Polynomial other) {
        int n = Math.max(this.n.length, other.n.length);
        double[] sum = new double[n];
        for (int i = 0; i < n; i++) {
            double a = (i < this.n.length) ? this.n[i] : 0.0;
            double b = (i < other.n.length) ? other.n[i] : 0.0;
            sum[i] = a + b;
        }
        return new Polynomial(sum);
    }
    public double evaluate(double x) {
        double val = 0.0, pow = 1.0;
        for (double a : n) {
            val += a * pow;
            pow *= x;
        }
        return val;
    }
    public boolean hasRoot(double x) {
        return Math.abs(evaluate(x)) < 1e-9;
    }
}