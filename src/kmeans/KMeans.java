package kmeans;

import java.util.ArrayList;
import java.util.List;

public class KMeans {

    public final static int ITT_MAX = 200;

    //Number of Clusters. This metric should be related to the number of points
    private static int num_cluster;
    //Number of Points
    private static int num_points;
    private static int dimension;

    private static List<Point> points;
    public static List<Cluster> clusters;

    public KMeans(int num_clusters, int num_pointss, int dimensions, List<Point> pointss) {
        points = pointss;
        num_cluster = num_clusters;
        num_points = num_pointss;

        points = pointss;

        dimension = dimensions;
    }

    public static List<Point> launch(int num_cluster, int num_points, int dimension, List<Point> points) {

        KMeans kmeans = new KMeans(num_cluster, num_points, dimension, points);
        // if (!isInit) {
        kmeans.init();
        //   isInit = true;
        //}
        kmeans.calculate();
        return points;
    }

    //Initializes the process
    public static void init() {
        clusters = new ArrayList();
        RandomSet random = new RandomSet();
        Integer[] aleatoire = random.random(num_points, num_cluster);        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < num_cluster; i++) {
            Cluster cluster = new Cluster(i);
            // Point centroid = points.get(aleatoire[i]);
            Point centroid = points.get(aleatoire[i]);
            //Point centroid=Point.createRandomPoint(min_coordinate, max_coordinate, dimension);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        //Print Initial state
        //  plotClusters();
    }

    private static void plotClusters() {
        for (int i = 0; i < num_cluster; i++) {
            Cluster c = (Cluster) clusters.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one. 
        while ((!finish) || (iteration < ITT_MAX)) {

            List<Point> lastCentroids = new ArrayList<Point>();
            lastCentroids = getCentroids();
            //lastCentroids = getCentroids();
            //Clear cluster state
            clearClusters();
            //Assign points to the closer cluster,take too much time
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();
            List<Point> currentCentroids = new ArrayList<Point>();
            currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0.0d;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += Point.distanceEuclid((Point) lastCentroids.get(i), (Point) currentCentroids.get(i));
            }
            /*  System.out.println("#################");
            System.out.println("Itteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();*/
            if (iteration != 0) {
                if (distance <= 0.00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001) {
                    finish = true;
                }
            }
            iteration++;
        }
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List<Point> getCentroids() {
        List<Point> centroids = new ArrayList(num_cluster);
        for (Cluster cluster : clusters) {
            Point aux = cluster.getCentroid();
            centroids.add(aux.clone());
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for (Point point : points) {
            int p = 0;
            min = max;
            for (int i = 0; i < num_cluster; i++) {
                Cluster c = clusters.get(i);
                distance = Point.distanceEuclid(point, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            points.get(p).setCluster(cluster);
            points.get(p).setCluster(cluster);
            clusters.get(cluster).addPoint(point);
            p++;
        }
    }

    public int getNum_cluster() {
        return num_cluster;
    }

    public void setNum_cluster(int num_cluster) {
        this.num_cluster = num_cluster;
    }

    public int getNum_points() {
        return num_points;
    }

    public void setNum_points(int num_points) {
        this.num_points = num_points;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public static List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }

    private void calculateCentroids() {
        int c = 0;
        for (Cluster cluster : clusters) {
            //euclidian way
            double[] sum = new double[dimension];
            List<Point> list = cluster.getPoints();
            int n_points = list.size();

            for (Point point : list) {
                for (int i = 0; i < dimension; i++) {
                    sum[i] += point.coordonnees[i];
                }

            }

            Point centroid = cluster.getCentroid();
            //if (n_points < 0) {
            // for (Point point : list) {
            for (int i = 0; i < dimension; i++) {
                if (n_points != 0) {
                    cluster.getCentroid().coordonnees[i] = sum[i] / n_points;
                }

            }

            //}
            clusters.set(c, cluster);
            c++;
            //}

//euclidian way
/*
            double[] sum = new double[dimension];
            List<Point> list = cluster.getPoints();
            int n_points = list.size();
            Point zero = new Point(dimension);
            for (Point point : list) {
                for (int i = 0; i < dimension; i++) {
                    sum[i] += point.coordonnees[i] / Point.distance(point, zero);
                }

            }

            for (int i = 0; i < dimension; i++) {
                if (n_points != 0) {
                    cluster.getCentroid().coordonnees[i] = sum[i];
                }

            }

            //}
            clusters.set(c, cluster);
            c++;*/
        }
    }
}
