/*
I3S-CNRS\IFI 
 */
package isvm.rce;

import java.io.File;
import static java.lang.Math.ceil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import kmeans.Cluster;
import kmeans.KMeans;
import kmeans.Point;
import org.apache.commons.lang3.ArrayUtils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.LibSVM;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 *
 * @author Sikadie
 */
public class ISvmRce {

    /**
     * @param args the command line arguments
     */
    //the dataset files names
    private String datasetName = "data/colon/colon.arff";

    private double percentTraining = 66f;
    private static DataSource source;
    private Instances data;
    //the training dataset
    private Instances Xtrain;
    //the evaluation dataset
    private Instances Xeval;
    //All the genes list or the top n_g genes by t-test
    private ArrayList<Point> SS;
    //Initial number of clusters
    private int n = 100;
    //The parameter for changing the rule of removing a cluster of genes, we will have maximum p clusters at the end
    private int p = 4;
    //Final number of clusters, au moins etre égal à 1
    private int m = 1;
    //the reduction parameter for gene cluster
    private double d1 = 0.1d;
    //the reduction parameter for gene within the clusters
    private double d2 = 0.5d;
    //the parameter whether the genes in a cluster can be eliminated
    private int d3 = 2;

    //Calculate the w vector of svm
    private Weight weight;
    //Keep the trace of execution
    private List<Trace> traceExecution;
    //filter size
    private int filterSize = 300;

//method, return index of elements in array with smallest value
    public ISvmRce(String datasetName, double percentTrain, int n, int p, int m, double d1, double d2, int d3, int filterSize) {
        this.datasetName = datasetName;
        this.percentTraining = percentTrain;
        this.p = p;
        this.n = n;
        this.m = m;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.filterSize = filterSize;

    }

    public ISvmRce() {
    }

    public void init() {
        try {
            // initialization of training set and  genes list
            // we considered that the training set is 66% and the testing set is the rest
            weight = new Weight();
// Load data
            source = readDataFile(datasetName);
            data = source.getDataSet();

            int nbreAttrib = data.numAttributes() - 2;
            //permet de mettre tous lles gènes dans la recherche
//filterSize=nbreAttrib;
// Set class to last attribute
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
            data.randomize(new Random(0));
            data.setClassIndex(data.numAttributes() - 1);
        } catch (Exception ex) {
            Logger.getLogger(ISvmRce.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Filter the features with t-stat
        //target variable
        double[] y = new double[data.numInstances()];
        // t-stat results
        double[] resultStat = new double[data.numAttributes() - 1];
        for (int i = 0; i < data.numInstances(); i++) {
            y[i] = data.get(i).classValue();
        }
        double[] x = new double[data.numInstances()];
        //walk throw the columns 
        for (int t = 0; t < data.numAttributes() - 1; t++) {
            for (int i = 0; i < data.numInstances(); i++) {
                x[i] = data.get(i).value(t);
            }
            resultStat[t] = Point.tstat(x, y);
        }

        int[] bestFeatures = maxKIndexMoi(resultStat, filterSize);

//remove the indexes not contained in the top selected ones
        int size = data.numAttributes() - 2;
        for (int t = size; t >= 0; t--) {
            if (!ArrayUtils.contains(bestFeatures, t)) {
                data.deleteAttributeAt(t);
            }
        }
        traceExecution = new ArrayList<>();
// double classe = data.get(0).classValue();
        int trainSize = (int) Math.round(data.numInstances() * percentTraining
                / 100);
        int testSize = data.numInstances() - trainSize;
        Xtrain = new Instances(data, 0, trainSize);
        Xeval = new Instances(data, trainSize, testSize);
        System.out.println("training size: " + Xtrain.size());
        System.out.println("testing size: " + Xeval.size());

    }

    public static int[] bottomN(final double[] input, final int n) {
        return IntStream.range(0, input.length)
                .boxed()
                .sorted(comparing(i -> input[i]))
                .mapToInt(i -> i)
                .limit(n)
                .toArray();
    }

    public static int[] maxKIndex(double[] array, int top_k) {
        double[] max = new double[top_k];
        int[] maxIndex = new int[top_k];

        Arrays.fill(max, Double.NEGATIVE_INFINITY);
        Arrays.fill(maxIndex, -1);

        top:
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < top_k; j++) {
                if (array[i] > max[j]) {
                    for (int x = top_k - 1; x > j; x--) {
                        maxIndex[x] = maxIndex[x - 1];
                        max[x] = max[x - 1];
                    }
                    maxIndex[j] = i;
                    max[j] = array[i];
                    continue top;
                }
            }
        }
        return maxIndex;
    }

    public static int[] maxKIndexMoi(double[] array, int top_k) {
        int[] maxIndex = new int[top_k];
        double[] arrayCpy = ArrayUtils.clone(array);
        for (int i = 0; i < top_k; i++) {
            double maxV = 0.0d;
            int index = 0;
            for (int j = 0; j < arrayCpy.length; j++) {
                if (arrayCpy[j] >= maxV) {
                    index = j;
                    maxV = arrayCpy[j];
                }
            }
            arrayCpy[index] = -1;
            maxIndex[i] = index;
        }

        return maxIndex;
    }

    public static Evaluation classify(Classifier model,
            Instances trainingSet, Instances testingSet) throws Exception {
        Evaluation evaluation = new Evaluation(trainingSet);

        model.buildClassifier(trainingSet);
        evaluation.evaluateModel(model, testingSet);

        return evaluation;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public static ArrayList<Point> readAttributesFile(Instances instances) {
        int dimension = instances.numAttributes() - 1;

        ArrayList<Point> S1 = new ArrayList(dimension);

        for (int i = 0; i < dimension; i++) {
            S1.add(new Point(instances.numInstances()));
            int k = 0;
            S1.get(i).setName(instances.attribute(i).name());
            S1.get(i).setIndexGene(i);

            for (Instance intance : instances) {
                S1.get(i).getCoordonnees()[k] = intance.value(i);
                k++;

            }

        }

        return S1;
    }

    public static DataSource readDataFile(String filename) {
        //charger les données

        try {
            source = new ConverterUtils.DataSource(filename);
        } catch (Exception ex) {

        }

        return source;
    }

    public double evaluateModelWithPreselectedFeatures() {
        double accuracy = 0.0;
        try {
            LibSVM modelComp = new LibSVM();
            // System.out.println("GLobal info" + modelComp.globalInfo());
            // options for the model
            String options = ("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1");
            String[] optionsArray = options.split(" ");
            modelComp.setOptions(optionsArray);
            FastVector predictions = new FastVector();

            // For each training-testing split pair, train and test the classifier
            Evaluation validation = classify(modelComp, Xtrain, Xeval);

            predictions.appendElements(validation.predictions());

            // Calculate overall accuracy of current classifier on all splits
            accuracy = calculateAccuracy(predictions);
            System.out.println("Begining: " + n + " clusters - " + filterSize + " genes" + "Accuracy of model with bests features" + ": "
                    + String.format("%.2f%%", accuracy)
                    + "\n---------------------------------");

        } catch (Exception ex) {
            Logger.getLogger(ISvmRce.class.getName()).log(Level.SEVERE, null, ex);
        }
        return accuracy;
    }

    public double evaluateModelOnEachStepWithSelectedFeatures(String[] optionsArrayLite, Instances XtrainCpy, Instances XEvalCpy) {
        double accuracyLite = 0;
        try {
            LibSVM modelLite = new LibSVM();

            modelLite.setOptions(optionsArrayLite);
            FastVector predictionsLite = new FastVector();

            Evaluation validationLite = classify(modelLite, XtrainCpy, XEvalCpy);

            predictionsLite.appendElements(validationLite.predictions());

            accuracyLite = calculateAccuracy(predictionsLite);

        } catch (Exception ex) {
            Logger.getLogger(ISvmRce.class.getName()).log(Level.SEVERE, null, ex);
        }
        return accuracyLite;
    }

    public ArrayList<Point> lookupGeneVector(Instances data) {
        int nbreAttributs = data.numAttributes() - 1;
        SS = readAttributesFile(data);
        //step 0
        //step 0.0: look for genes vectors
        for (int i = 0; i < nbreAttributs; i++) {
            double[] vector = new double[Xtrain.size()];
            for (int j = 0; j < Xtrain.size(); j++) {
                vector[j] = (double) Xtrain.get(j).value(i);
            }
            SS.get(i).setCoordonnees(vector);
        }
        return SS;
    }

    /*do the recursive eleimination while given us back the trace of execution*/
    public void start() {

        try {
            SS = lookupGeneVector(data);
//this set keeps the track of genes that are selected atthe beginning, all is selected
            ArrayList<Point> S = new ArrayList<>();
            String optionslite = ("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1");
            String[] optionsArrayLite = optionslite.split(" ");

            traceExecution.add(new Trace(filterSize, S.size(), evaluateModelWithPreselectedFeatures()));
//take the genes vector

            for (Point p : SS) {
                S.add(p.clone());

            }
            List<Cluster> nclusters = null;
            List<Integer> deletedIndex = new ArrayList();
            Instances XtrainCpy = new Instances(Xtrain);
            Instances XEvalCpy = new Instances(Xeval);
            int numberClusterRemove = 0;
            //we want at most N clusters
            while ((n >= m) && (n > 1)) {

                // Step 1: Cluster the given genes S into n clusters S1,S2,...,Sn using K-means(cluster step)
                KMeans.launch(n, S.size(), Xtrain.size(), S);
                //1.1 get the clusters after kmeans
                nclusters = KMeans.getClusters();
                // Step 2. Train a linear SVM model SVM(X(S)) by dataset X(S), then for each cluster, calculate it score according to infinte norma scoring
                //2.1 Train a linear SVM model SVM(X(S)) by dataset X(S)

                //2.2 create the model 
                int k = 0;
                for (Point s : S) {

                    s.setSvm_order(k);
                    k++;

                }
                LibSVM m = new LibSVM();
                m.setOptions(optionsArrayLite);
                File weighFile = new File("weight.txt");
                m.setModelFile(weighFile);

                m.buildClassifier(XtrainCpy);

                double[] weights = weight.getWeight("weight.txt", S.size());

                //2.3 Evaluate the score of each cluster
//create the array that will contains the score of each cluster
                double[] scoreTab = new double[nclusters.size()];
//evaluate the score(infinite norm) for each cluster and store it
                int j = 0;
                for (Cluster cluster : nclusters) {

                    double max = 0.0d;
                    for (int i = 0; i < cluster.getPoints().size(); i++) {
                        Point p = (Point) cluster.getPoints().get(i);
                        if (max < Math.abs(weights[p.getSvm_order()])) {
                            max = Math.abs(weights[p.getSvm_order()]);
                        }
                    }
                    //affectation du score
                    scoreTab[j] = max;
                    j++;

                }

                //3 Remove the worst clusters and genes
                //3.1   Remove the d1% clusters with lowest score
                numberClusterRemove = (int) ceil(n * d1);
                if (n >= p) {

                    if (numberClusterRemove == 0) {
                        numberClusterRemove = 1;
                    }
                    //indexes of the clusters with smallest rate
                    int[] indexesSmallestCluster = bottomN(scoreTab, numberClusterRemove);
                    //trier par ordre décroissant
                    Arrays.sort(indexesSmallestCluster);
                    Collections.reverse(Arrays.asList(indexesSmallestCluster));
                    List<Cluster> trash = new ArrayList<>();
                    //walk into useless clusters and eliminate them
                    for (int i = 0; i < numberClusterRemove; i++) {
                        Cluster throwCluster = nclusters.get(indexesSmallestCluster[i]);
                        trash.add(throwCluster);
                        //Save the indexes of the concerned genes
                        for (int id = 0; id < throwCluster.getPoints().size(); id++) {
                            deletedIndex.add(throwCluster.getPoints().get(id).getIndexGene());
                        }
                    }

                    //Step 5
                    nclusters.removeAll((Collection<Cluster>) trash);

                } else {
                    //3.2 Remove the d2% non representative genes in the clusters where the number of genes is bigger than d3
                    ArrayList<Integer> retenusIndex = new ArrayList<>();
                    for (int i = 0; i < nclusters.size(); i++) {
                        if (nclusters.get(i).getPoints().size() > d3) {
                            retenusIndex.add(i);
                        }
                    }
                    //go into the remainings clusters and eliminate d2%
                    for (int i = 0; i < retenusIndex.size(); i++) {
                        int numberPointRemove = (int) (nclusters.get(retenusIndex.get(i)).getPoints().size() * d2);
                        double[] scoreTabCluster = new double[nclusters.get(retenusIndex.get(i)).getPoints().size()];
                        double max = 0.0d;
                        for (int c = 0; c < nclusters.get(retenusIndex.get(i)).getPoints().size(); c++) {
                            Point p = (Point) nclusters.get(retenusIndex.get(i)).getPoints().get(c);

                            scoreTabCluster[c] = Math.abs(weights[p.getSvm_order()]);
                        }
                        //indexes of the clusters with smallest rate
                        int[] indexesSmallestGeneInCluster = bottomN(scoreTabCluster, numberPointRemove);
                        // delete the genes selected from the current cluster
                        Arrays.sort(indexesSmallestGeneInCluster);
                        ArrayUtils.reverse(indexesSmallestGeneInCluster);
                        for (int t = 0; t < indexesSmallestGeneInCluster.length; t++) {
                            deletedIndex.add(nclusters.get(retenusIndex.get(i)).getPoints().get(indexesSmallestGeneInCluster[t]).getIndexGene());

                        }
                        for (int t = 0; t < indexesSmallestGeneInCluster.length; t++) {
                            nclusters.get(retenusIndex.get(i)).getPoints().remove(indexesSmallestGeneInCluster[t]);

                        }

                    }
                }
                //Step 4:merge surviving genes into one pool S

                XEvalCpy = new Instances(Xeval);
                XEvalCpy.clear();
                XtrainCpy = new Instances(Xtrain);
                XtrainCpy.clear();
                for (Instance instance : Xtrain) {
                    XtrainCpy.add(instance);
                }
                for (Instance instance : Xeval) {
                    XEvalCpy.add(instance);
                }
                S.clear();
                for (Point p : SS) {
                    S.add(p.clone());
                }
                //delete the columns contained in deletedIndex, corresponding to genes we deleted in clusters or individually
                //Step 5 here
                int sizeS = SS.size() - 1;

                Collections.sort(deletedIndex);
                Collections.reverse(deletedIndex);
                //int sizemouf=0;

                for (Integer d : deletedIndex) {
                    XtrainCpy.deleteAttributeAt(d);
                    //sizemouf = XtrainCpy.numAttributes()-1;
                    XEvalCpy.deleteAttributeAt(d);
                    S.remove(d.intValue());

                }

//step 5:decrease n by d1%
                n -= numberClusterRemove;
                //evaluate at each step the number of cluster and the number of remainings genes
                double precision = evaluateModelOnEachStepWithSelectedFeatures(optionsArrayLite, XtrainCpy, XEvalCpy);
                traceExecution.add(new Trace(n, S.size(), precision));
                System.out.println(n + " clusters - " + S.size() + " genes" + " Accuracy of Libsvm after feature selection" + ": "
                        + String.format("%.2f%%", precision)
                        + "\n---------------------------------");
            }
//Print the remaining genes by group
            for (Cluster cluster : nclusters) {
                cluster.plotClusterGene();
            }

        } catch (Exception ex) {
            Logger.getLogger(ISvmRce.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getPercentTraining() {
        return percentTraining;
    }

    public void setPercentTraining(double percentTraining) {
        this.percentTraining = percentTraining;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    public int getD3() {
        return d3;
    }

    public void setD3(int d3) {
        this.d3 = d3;
    }

    public List<Trace> getTraceExecution() {
        return traceExecution;
    }

    public int getFilterSize() {
        return filterSize;
    }

    public void setFilterSize(int filterSize) {
        this.filterSize = filterSize;
    }

}
