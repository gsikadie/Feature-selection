/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;

import isvm.rce.Trace;
import java.util.ArrayList;
import java.util.List;
import weka.core.Utils;

/**
 *
 * @author helen
 */
public class Cluster {
    
    private List<Point> points;
    private Point centroid;
    private int id;

    //Creates a new Cluster
    public Cluster(int id) {
        this.id = id;
        this.points = new ArrayList();
        this.centroid = null;
    }
    
    public List<Point> getPoints() {
        return points;
    }
    
    public void addPoint(Point point) {
        points.add(point);
    }
    
    public void setPoints(List points) {
        this.points = points;
    }
    
    public Point getCentroid() {
        return centroid;
    }
    
    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }
    
    public int getId() {
        return id;
    }
    
    public void clear() {
        points.clear();
    }
    
    public void plotCluster() {
        System.out.println("[Cluster: " + id + "]");
        System.out.println("[Centroid: " + centroid + "]");
        System.out.println("[Points: \n");
        for (Point p : points) {
            System.out.println(p);
        }
        System.out.println("]");
    }
    
    public void plotClusterGene() {
        System.out.println("[Cluster Genes: " + id + "]");
        System.out.println("[Centroid Genes: " + centroid + "]");
        System.out.println("[Genes: \n");
        for (Point p : points) {
            System.out.println("Gene index: " + p.getIndexGene() + ", Gene name: " + p.getName());
        }
        System.out.println("]");
    }
    

    
}
