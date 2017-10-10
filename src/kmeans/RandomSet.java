/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author helen
 */
public class RandomSet {
    
    HashSet<Integer> randomNumbers;
    
    public Integer[] random(int limit, int nbre) {
        Random rand = new Random();
        int e;
        int i;
        int g = nbre;
        
        randomNumbers = new HashSet<Integer>();
        
        for (i = 0; i < g; i++) {
            e = rand.nextInt(limit);
            randomNumbers.add(e);
            if (randomNumbers.size() <= nbre) {
                if (randomNumbers.size() == nbre) {
                    g = nbre;
                }
                g++;
                randomNumbers.add(e);
            }
        }
        System.out.println(" Unique random numbers from 1 to " + limit + " are  : " + randomNumbers);
        return randomNumbers.toArray(new Integer[randomNumbers.size()]);
    }
}
