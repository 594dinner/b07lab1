public class Polynomial {
    private final double[] coeffs;
    private final int[] exps;
    private static final double EPS = 1e-12;
    public Polynomial() {
        this.coeffs = new double[0];
        this.exps   = new int[0];
    }
    public Polynomial(java.io.File file) throws java.io.IOException {
        String line;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            line = br.readLine();
        }
        if (line == null) line = "";
        line = line.replace(" ", "");
        Parsed p = parse(line);
        Canon can = canonicalize(p.cs, p.es, p.len);
        this.coeffs = can.cs;
        this.exps   = can.es;
    }
    public Polynomial add(Polynomial other) {
        int i = 0, j = 0;
        double[] tcs = new double[this.coeffs.length + other.coeffs.length];
        int[] tes    = new int[this.exps.length + other.exps.length];
        int k = 0;
        while (i < this.exps.length || j < other.exps.length) {
            int ei = (i < this.exps.length) ? this.exps[i] : Integer.MAX_VALUE;
            int ej = (j < other.exps.length) ? other.exps[j] : Integer.MAX_VALUE;
            if (ei == ej) {
                double v = this.coeffs[i] + other.coeffs[j];
                if (!isZero(v)) { tcs[k] = v; tes[k] = ei; k++; }
                i++; j++;
            } else if (ei < ej) {
                tcs[k] = this.coeffs[i]; tes[k] = this.exps[i]; k++; i++;
            } else {
                tcs[k] = other.coeffs[j]; tes[k] = other.exps[j]; k++; j++;
            }
        }
        return trim(tcs, tes, k);
    }
    public Polynomial multiply(Polynomial other) {
        int m = this.coeffs.length, n = other.coeffs.length;
        if (m == 0 || n == 0) return new Polynomial();
        double[] tcs = new double[m * n];
        int[]    tes = new int[m * n];
        int k = 0;
        for (int i = 0; i < m; i++) {
            if (isZero(this.coeffs[i])) continue;
            for (int j = 0; j < n; j++) {
                if (isZero(other.coeffs[j])) continue;
                tcs[k] = this.coeffs[i] * other.coeffs[j];
                tes[k] = this.exps[i] + other.exps[j];
                k++;
            }
        }
        Canon can = canonicalize(tcs, tes, k);
        return new Polynomial(can);
    }
    public void saveToFile(String fileName) throws java.io.IOException {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(fileName))) {
            pw.println(toText());
        }
    }
    private Polynomial(Canon can) {
        this.coeffs = can.cs;
        this.exps   = can.es;
    }
    private Polynomial(double[] cs, int[] es) {
        this.coeffs = cs;
        this.exps   = es;
    }
    private static Polynomial trim(double[] cs, int[] es, int len) {
        double[] ncs = new double[len];
        int[]    nes = new int[len];
        for (int i = 0; i < len; i++) { ncs[i] = cs[i]; nes[i] = es[i]; }
        return new Polynomial(ncs, nes);
    }
    private static boolean isZero(double v) { return Math.abs(v) < EPS; }
    private String toText() {
        if (coeffs.length == 0) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double c = coeffs[i];
            int e = exps[i];
            boolean first = i == 0;
            appendTerm(sb, c, e, first);
        }
        return sb.toString();
    }
    private static void appendTerm(StringBuilder sb, double c, int e, boolean first) {
        if (isZero(c)) return;
        boolean neg = c < 0;
        double a = Math.abs(c);

        if (first) { if (neg) sb.append('-'); }
        else       { sb.append(neg ? '-' : '+'); }

        if (e == 0) { sb.append(fmt(a)); return; }
        if (Math.abs(a - 1.0) > 1e-12) sb.append(fmt(a));
        sb.append('x');
        if (e != 1) sb.append(e);
    }
    private static String fmt(double v) {
        long r = Math.round(v);
        if (Math.abs(v - r) < 1e-10) return Long.toString(r);
        String s = Double.toString(v);
        if (s.indexOf('.') >= 0) {
            int i = s.length() - 1;
            while (i > 0 && s.charAt(i) == '0') i--;
            if (s.charAt(i) == '.') i--;
            s = s.substring(0, i + 1);
        }
        return s;
    }
    private static final class Parsed { double[] cs; int[] es; int len; }
    private static Parsed parse(String s) {
        Parsed p = new Parsed();
        if (s == null || s.length() == 0) { p.cs = new double[0]; p.es = new int[0]; p.len = 0; return p; }
        if (s.charAt(0) != '+' && s.charAt(0) != '-') s = "+" + s;
        double[] cs = new double[8];
        int[] es = new int[8];
        int len = 0;

        int i = 0, n = s.length();
        while (i < n) {
            char sign = s.charAt(i++);
            int st = i;
            while (i < n && (Character.isDigit(s.charAt(i)) || s.charAt(i)=='.')) i++;
            String num = s.substring(st, i);
            boolean hasX = false;
            int exp = 0;
            if (i < n && s.charAt(i) == 'x') {
                hasX = true;
                i++;
                int est = i;
                while (i < n && Character.isDigit(s.charAt(i))) i++;
                String en = s.substring(est, i);
                exp = en.isEmpty() ? 1 : Integer.parseInt(en);
            } else {
                exp = 0;
            }
            double coef = num.isEmpty() ? (hasX ? 1.0 : 0.0) : Double.parseDouble(num);
            if (sign == '-') coef = -coef;
            if (len == cs.length) {
                double[] ncs = new double[cs.length << 1];
                int[] nes = new int[es.length << 1];
                for (int t = 0; t < len; t++) { ncs[t] = cs[t]; nes[t] = es[t]; }
                cs = ncs; es = nes;
            }
            cs[len] = coef; es[len] = exp; len++;
        }
        p.cs = cs; p.es = es; p.len = len;
        return p;
    }
    private static final class Canon { final double[] cs; final int[] es; Canon(double[] a, int[] b){ cs=a; es=b; } }
    private static Canon canonicalize(double[] cs, int[] es, int len) {
        if (len == 0) return new Canon(new double[0], new int[0]);
        for (int i = 1; i < len; i++) {
            int   e = es[i];
            double c = cs[i];
            int j = i - 1;
            while (j >= 0 && es[j] > e) {
                es[j + 1] = es[j];
                cs[j + 1] = cs[j];
                j--;
            }
            es[j + 1] = e;
            cs[j + 1] = c;
        }
        double[] mcs = new double[len];
        int[]    mes = new int[len];
        int k = 0;
        int i = 0;
        while (i < len) {
            int e = es[i];
            double sum = 0.0;
            while (i < len && es[i] == e) { sum += cs[i]; i++; }
            if (!isZero(sum)) { mcs[k] = sum; mes[k] = e; k++; }
        }
        double[] fcs = new double[k];
        int[]    fes = new int[k];
        for (int t = 0; t < k; t++) { fcs[t] = mcs[t]; fes[t] = mes[t]; }
        return new Canon(fcs, fes);
    }
}