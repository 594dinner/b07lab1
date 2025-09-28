public class Driver {
    private static String readFirstLine(String path) throws java.io.IOException {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path))) {
            String s = br.readLine();
            return (s == null) ? "" : s;
        }
    }
    private static void writeLine(String path, String line) throws java.io.IOException {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(path))) {
            pw.println(line);
        }
    }
    public static void main(String[] args) {
        try {
            String f1, f2;
            if (args.length >= 2) {
                f1 = args[0];
                f2 = args[1];
            } else {
                f1 = "poly1.txt";
                f2 = "poly2.txt";
                writeLine(f1, "6-2x+5x3");
                writeLine(f2, "5-3x2+7x8");
                System.out.println("[Demo] Created sample input files: " + f1 + ", " + f2);
            }
            Polynomial p1 = new Polynomial(new java.io.File(f1));
            Polynomial p2 = new Polynomial(new java.io.File(f2));
            Polynomial sum = p1.add(p2);
            Polynomial prod = p1.multiply(p2);
            String sumFile = "sum.txt";
            String prodFile = "product.txt";
            sum.saveToFile(sumFile);
            prod.saveToFile(prodFile);
            System.out.println("p1:      " + readFirstLine(f1));
            System.out.println("p2:      " + readFirstLine(f2));
            System.out.println("p1+p2 => " + readFirstLine(sumFile) + "   (saved to " + sumFile + ")");
            System.out.println("p1*p2 => " + readFirstLine(prodFile) + "  (saved to " + prodFile + ")");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
