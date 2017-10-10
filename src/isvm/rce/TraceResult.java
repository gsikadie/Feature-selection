/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isvm.rce;

import java.util.List;
import weka.core.Utils;

/**
 *
 * @author helen
 */
public class TraceResult extends Trace {

    private double std;

    public TraceResult(int numberCluster, int numberGene, double precision, double std) {
        super(numberCluster, numberGene, precision);
        this.std = std;
    }

    public double getStd() {
        return std;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public static TraceResult[] experiment(List<Trace[]> tabItteration) {
        TraceResult[] result = new TraceResult[tabItteration.size()];
        int size = tabItteration.get(0).length;
        for (int i = 0; i < tabItteration.size(); i++) {
            double[] nbreCluster = new double[size];
            double[] nbreGene = new double[size];
            double[] accuracy = new double[size];
            for (int j = 0; j < size; j++) {
                Trace t = tabItteration.get(i)[j];
                nbreCluster[j] = t.getNumberCluster();
                nbreGene[j] = t.getNumberGene();
                accuracy[j] = t.getPrecision();
            }
            result[i] = new TraceResult((int) Utils.mean(nbreCluster), (int) Utils.mean(nbreGene), Utils.mean(accuracy), Math.sqrt(Utils.variance(accuracy)));
        }
        return result;
    }
}
