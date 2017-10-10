package kmeans;

import java.util.Random;
import weka.core.Utils;

public class Point {

    private int svm_order;

    public int getSvm_order() {
        return svm_order;
    }

    public void setSvm_order(int svm_order) {
        this.svm_order = svm_order;
    }
    private int cluster_number = 0;
    private String name;
    double[] coordonnees;
    private int indexGene;
    int dimension;

    public int getCluster_number() {
        return cluster_number;
    }

    public int getIndexGene() {
        return indexGene;
    }

    public void setIndexGene(int indexGene) {
        this.indexGene = indexGene;
    }

    public void setCluster_number(int cluster_number) {
        this.cluster_number = cluster_number;
    }

    public double[] getCoordonnees() {
        return coordonnees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setCoordonnees(double[] coordonnees) {
        this.coordonnees = coordonnees;
        this.dimension = coordonnees.length;
    }

    public Point(int dimension) {
        this.coordonnees = new double[dimension];
    }

    public Point(double[] p, int indexGene) {
        this.coordonnees = p;
        this.dimension = p.length;
        this.indexGene = indexGene;
    }

    public Point(String name, int indexGene) {
        this.name = name;
        this.indexGene = indexGene;

    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }

    public int getCluster() {
        return this.cluster_number;
    }
    //Creates random point

    protected static Point createRandomPoint(int min, int max, int dimension) {
        Random r = new Random();
        Point p = new Point(dimension);
        p.coordonnees = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            p.coordonnees[i] = min + (max - min) * r.nextDouble();
        }
        return p;
    }

    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
        double sum = 0, sum1 = 0, sum2 = 0, sum3 = 0;
        double moyenneP = moyenne(p.getCoordonnees());
        double moyenneCentroid = moyenne(centroid.getCoordonnees());
        int longueur = p.getCoordonnees().length;
        for (int i = 0; i < longueur; i++) {
            sum2 = sum2 + Math.pow((p.getCoordonnees()[i] - moyenneP), 2);
            sum3 = sum3 + Math.pow((centroid.getCoordonnees()[i] - moyenneCentroid), 2);

        }
        sum2 /= longueur;
        sum3 /= longueur;
        sum2 = Math.sqrt(sum2);
        sum3 = Math.sqrt(sum3);
        for (int i = 0; i < longueur; i++) {
            sum1 = sum1 + (p.getCoordonnees()[i] - moyenneP) * (centroid.getCoordonnees()[i] - moyenneCentroid) / (sum2 * sum3);
        }

        sum = 1 - sum1 / longueur;
        return sum;
    }

    //Calculates the distance between two points.
    protected static double distanceEuclid(Point p, Point centroid) {
        double sum1 = 0, sum2 = 0;
        int longueur = p.getCoordonnees().length;
        for (int i = 0; i < longueur; i++) {
            sum1 = sum1 + Math.pow(p.getCoordonnees()[i] - centroid.getCoordonnees()[i], 2);

        }
        sum2 = Math.sqrt(sum1);

        return sum2;
    }

    protected static double distanceManathan(Point p, Point centroid) {
        double sum1 = 0, sum2 = 0;
        int longueur = p.getCoordonnees().length;
        for (int i = 0; i < longueur; i++) {
            sum1 = sum1 + Math.abs(p.getCoordonnees()[i] - centroid.getCoordonnees()[i]);

        }
        sum2 = Math.sqrt(sum1);

        return sum2;
    }

    public static double moyenne(double[] g) {
        double sum = 0;
        for (int i = 0; i < g.length; i++) {
            sum += g[i];
        }
        return sum / g.length;
    }

    double getVariance(double[] g) {
        double mean = moyenne(g);
        double temp = 0;
        for (int i = 0; i < g.length; i++) {
            temp += (g[i] - mean) * (g[i] - mean);
        }

        return temp / (g.length - 1);
    }

    public static double tstat(double[] x, double[] y) {
        double resultat = 0.0d;
//seperate x in 2 sub arrays, for 1.0 and for 0.0
        int nbreZero = 0, nbreOne = 0;
        for (int i = 0; i < y.length; i++) {
            if (y[i] == 0.0) {
                nbreZero++;

            } else {
                nbreOne++;
            }

        }
        double[] indiceZero = new double[nbreZero];
        double[] indiceOne = new double[nbreOne];
        int zero = 0, one = 0;
        for (int i = 0; i < y.length; i++) {
            if (y[i] == 0.0) {
                indiceZero[zero] = x[i];
                zero++;

            } else {
                indiceOne[one] = x[i];
                one++;
            }

        }

        resultat = Math.abs(Utils.mean(indiceZero) - Utils.mean(indiceOne)) / (Math.sqrt(Utils.variance(indiceZero) / indiceZero.length + Math.sqrt(Utils.variance(indiceOne) / indiceOne.length)));

        if (Double.isNaN(resultat)) {
            resultat = 0.0d;

        }

        return resultat;
    }

    public static double norme(double[] g) {
        double sum = 0;
        for (int i = 0; i < g.length; i++) {
            sum += g[i] * g[i];
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString() {
        String chaine = "(";
        for (int i = 0; i < coordonnees.length; i++) {
            chaine += "x" + i + ":" + coordonnees[i] + ",";
        }
        return chaine;
    }

    public Point clone() {
        Point p = new Point(dimension);
        p.svm_order = this.svm_order;
        p.coordonnees = this.coordonnees.clone();
        p.dimension = this.dimension;
        p.indexGene = this.indexGene;
        p.name = this.name;
        p.cluster_number = cluster_number;

        return p;
    }
}
