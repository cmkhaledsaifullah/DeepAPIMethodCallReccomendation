package encoderDecoder.NeuralNetwork;

import encoderDecoder.Corpus.CorpusIterator;
import encoderDecoder.Model.Output;
import encoderDecoder.Utils.MapSort;
import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.graph.vertex.GraphVertex;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DMSRTraining
{
    private List<String> outputString;
    public DMSRTraining()
    {
        this.outputString = new ArrayList<String>();
    }
    public void train(File networkFile,String BACKUP_MODEL_FILENAME, int offset, ComputationGraph network, List<List<Double>> context, List<List<Double>> label, int MINIBATCH_SIZE, int MACROBATCH_SIZE, Map<String, Double> dictonary,Map<Double, String> revdictonary, int ROW_SIZE,int NO_OF_EPOCH , long SAVE_EACH_MS, long TEST_EACH_MS, double normalization_factor) throws IOException {

        long lastSaveTime = System.currentTimeMillis();
        long lastTestTime = System.currentTimeMillis();

        //Passing context into CorpusIterator Class
        CorpusIterator logsIterator = new CorpusIterator(context,label, MINIBATCH_SIZE, MACROBATCH_SIZE, dictonary.size(), ROW_SIZE);

        for (int epoch = 1; epoch <= NO_OF_EPOCH; ++epoch)
        {
            System.out.println("Epoch " + epoch);
            if (epoch == 1) {
                logsIterator.setCurrentBatch(offset);
            } else {
                logsIterator.reset();
            }
            int lastPercentage = 0;


            while (logsIterator.hasNextMacrobatch())
            {
                // Fit the macro batch in the Neural Network
                network.fit(logsIterator);
                logsIterator.nextMacroBatch();

                System.out.println("Batch = " + logsIterator.batch());

                // Calculate the percentage of completion
                int newPercentage = (logsIterator.batch() * 100 / logsIterator.totalBatches());
                if (newPercentage != lastPercentage) {
                    System.out.println("Epoch complete: " + newPercentage + "%");
                    lastPercentage = newPercentage;
                }

                //Save model after SAVE_EACH_MS time.
                if (System.currentTimeMillis() - lastSaveTime > SAVE_EACH_MS) {
                    saveModel(networkFile,BACKUP_MODEL_FILENAME,network);
                    lastSaveTime = System.currentTimeMillis();
                }

                //test model after TEST_EACH_MS
                if (System.currentTimeMillis() - lastTestTime > TEST_EACH_MS) {
                    test(network,context,dictonary,revdictonary,ROW_SIZE, normalization_factor);
                    lastTestTime = System.currentTimeMillis();
                }
            }
        }
    }


    private void saveModel(File networkFile, String BACKUP_MODEL_FILENAME, ComputationGraph network) throws IOException {
        System.out.println("Saving the model...");
        File backup = new File(BACKUP_MODEL_FILENAME);
        if (networkFile.exists()) {
            if (backup.exists()) {
                backup.delete();
            }
            networkFile.renameTo(backup);
        }
        ModelSerializer.writeModel(network, networkFile, true);
        System.out.println("Done.");
    }

    public void test(ComputationGraph network, List<List<Double>> context, Map<String, Double> dictonary,Map<Double, String> revdictonary, int ROW_SIZE, double normalization_factor) {
        System.out.println("======================== TEST ========================");
        Random rnd = new Random();
        int selected = rnd.nextInt(context.size());
        List<Double> rowIn = new ArrayList<>(context.get(selected));
        System.out.print("In: ");
        for (Double idx : rowIn) {
            System.out.print(revdictonary.get(idx) + " ");
        }
        System.out.println();
        System.out.print("Out: ");
        output(network,dictonary,revdictonary,rowIn,ROW_SIZE, normalization_factor);
        System.out.println(outputString);
        System.out.println("====================== TEST END ======================");
    }

    public void output(ComputationGraph network, Map<String, Double> dictonary,Map<Double, String> revdictonary,List<Double> rowIn, int ROW_SIZE, double normalization_factor) {
        network.rnnClearPreviousState();
        Collections.reverse(rowIn);
        INDArray in = Nd4j.create(ArrayUtils.toPrimitive(rowIn.toArray(new Double[0])), new int[] { 1, 1, rowIn.size() });
        double[] decodeArray = new double[dictonary.size()];
        decodeArray[2] = 1;
        INDArray decode = Nd4j.create(decodeArray, new int[] { 1, dictonary.size(), 1 });
        network.feedForward(new INDArray[] { in, decode }, false, false);
        org.deeplearning4j.nn.layers.recurrent.LSTM decoder = (org.deeplearning4j.nn.layers.recurrent.LSTM) network
                .getLayer("decoder");
        Layer output = network.getLayer("output");
        GraphVertex mergeVertex = network.getVertex("merge");
        INDArray thoughtVector = mergeVertex.getInputs()[1];
        LayerWorkspaceMgr mgr = LayerWorkspaceMgr.noWorkspaces();

        //Get top N number method name
        List<Output> methods = getFirstTopN(mergeVertex,decode,thoughtVector,mgr,decoder,output);

        //preparing the method names at the first position of the reccomendation
        List<String> methodidx = new ArrayList<>();
        for (Output temp: methods)
        {
            methodidx.add(temp.getIdx()+" ");
        }

        //Implementing beam search on the method name
        BeamSearch beamSearch = new BeamSearch(methodidx);
        beamSearch.BSAlgorithm(methods,network,mgr,ROW_SIZE,dictonary,normalization_factor);
        List<String> top10Output = beamSearch.getResultLine();


        //converting Integer output into string Output
        for (String eachOutput: top10Output)
        {
            String[] token = eachOutput.split(" ");
            String tempString ="";
            for (int i=0;i<token.length;i++)
            {
                if(!token[i].trim().isEmpty())
                {
                    try
                    {
                        int eachIdx = Integer.parseInt(token[i]);
                        tempString+= revdictonary.get((double) eachIdx) + " ";

                    }
                    catch (Exception ex)
                    {
                        System.err.println(token);
                    }

                }
            }
            outputString.add(tempString);
        }
        /*for(Integer idx: methodName)
        {
            if(outputString.size()>=10)
                break;
            outputString.add(revdictonary.get((double) idx)+" ");
            network.rnnClearPreviousState();
            double[] newDecodeArray = new double[dictonary.size()];
            newDecodeArray[idx] = 1;
            decode = Nd4j.create(newDecodeArray, new int[] { 1, dictonary.size(), 1 });
            network.feedForward(new INDArray[] { in, decode }, false, false);
            decoder = (org.deeplearning4j.nn.layers.recurrent.LSTM) network
                    .getLayer("decoder");
            output = network.getLayer("output");
            mergeVertex = network.getVertex("merge");
            thoughtVector = mergeVertex.getInputs()[1];
            mgr = LayerWorkspaceMgr.noWorkspaces();
            getTopNReccomendation(ROW_SIZE,mergeVertex,decode,thoughtVector,mgr,decoder,output,dictonary,revdictonary,printUnknowns);
        }*/

    }

    private List<Output> getFirstTopN(GraphVertex mergeVertex,INDArray decode,  INDArray thoughtVector,LayerWorkspaceMgr mgr, org.deeplearning4j.nn.layers.recurrent.LSTM decoder, Layer output)
    {
        List<Output> returnedFirstTopN = new ArrayList<>();

        mergeVertex.setInputs(decode, thoughtVector);
        INDArray merged = mergeVertex.doForward(false, mgr);
        INDArray activateDec = decoder.rnnTimeStep(merged, mgr);
        INDArray out = output.activate(activateDec, false, mgr);

        //for top-3,5,10,20 we collect the method name and then based on the method name we generate the whole sequence.
        Map<Integer,Double> idxes = new HashMap<Integer,Double>();

        //collect the probability of each method name in a hash map.
        for (int s = 0; s < out.size(1); s++)
        {
            double temp = out.getDouble(0,s,0);
            idxes.put(s,temp);
        }

        //sort the map in the descending order.
        Map<Integer,Double> sorted = MapSort.sortByComparator(idxes);

        //collect top 20 method name and stored in the top20MethodName list
        int topn = 1;
        for(Map.Entry<Integer,Double> entry : sorted.entrySet())
        {
            Output eachOutput = new Output();
            eachOutput.setIndex(topn);
            eachOutput.setIdx(entry.getKey());
            eachOutput.setProbabilityValue(entry.getValue());
            returnedFirstTopN.add(eachOutput);

            if(topn >= 10)
                break;

            topn++;
        }
        return returnedFirstTopN;
    }

    public List<String> getOutputString()
    {
        return outputString;
    }

}
