/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isvm.rce;



/**
 *
 * @author helen
 */
public class Trace {

    int numberCluster;
    int numberGene;
    double precision;

    public Trace(int numberCluster, int numberGene, double precision) {
        this.numberCluster = numberCluster;
        this.numberGene = numberGene;
        this.precision = precision;
    }

    public int getNumberCluster() {
        return numberCluster;
    }

    public void setNumberCluster(int numberCluster) {
        this.numberCluster = numberCluster;
    }

    public int getNumberGene() {
        return numberGene;
    }

    public void setNumberGene(int numberGene) {
        this.numberGene = numberGene;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }


}
