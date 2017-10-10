/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isvm.rce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kmeans.Point;

/**
 *
 * @author helen
 */
public class Weight {

    private double[] w;
    private int n;

   public  double[] getWeight(String filename, int n) {
        try {
            this.n = n;
            w = new double[n];
            File f = new File(filename);
            BufferedReader b = new BufferedReader(new FileReader(f));
            b = new BufferedReader(new FileReader(f));
            String readLine = "";
            try {
                //skipping the heading
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
                b.readLine();
               // System.out.println("Reading file using Buffered Reader");
                
                while ((readLine = b.readLine()) != null) {
                    String[] tabVecteur = readLine.split(" ");
                    double poids = 0;
                    for (int i = 0; i < tabVecteur.length; i++) {

                        if (i == 0) {
                            poids = Double.parseDouble(tabVecteur[0]);
                        } else {
                            w[Integer.parseInt(tabVecteur[i].split(":")[0]) - 1] += poids * Double.parseDouble(tabVecteur[i].split(":")[1]);
                        }

                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(ISVMRCE.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.
                    getLogger(ISVMRCE.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;

    }

}
