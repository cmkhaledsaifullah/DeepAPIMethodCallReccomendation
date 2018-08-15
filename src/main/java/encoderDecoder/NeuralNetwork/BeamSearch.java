package encoderDecoder.NeuralNetwork;

import encoderDecoder.Model.Output;
import encoderDecoder.Utils.MapSort;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.graph.vertex.GraphVertex;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class BeamSearch
{
    private  List<String> resultLine;

    public BeamSearch(List<String> resultLine)
    {
        this.resultLine = resultLine;
    }


    public void BSAlgorithm(List<Output> BestNNode, ComputationGraph network, LayerWorkspaceMgr mgr, int ROW_SIZE, Map<String, Double> dictonary,  double normalization_factor)
    {
        List<Output> completedresult;

        org.deeplearning4j.nn.layers.recurrent.LSTM decoder = (org.deeplearning4j.nn.layers.recurrent.LSTM) network
                .getLayer("decoder");
        Layer output = network.getLayer("output");
        GraphVertex mergeVertex = network.getVertex("merge");
        INDArray thoughtVector = mergeVertex.getInputs()[1];
        List<Output> listOfAllOutcome;

        for (int row = 1; row <= ROW_SIZE; ++row)
        {
            completedresult = new ArrayList<>();
            listOfAllOutcome = new ArrayList<>();
            int index = 0;
            int flag =0;

            for (Output entry : BestNNode)
            {
                int idx = entry.getIdx();
                double probability = entry.getProbabilityValue();

                if(idx == 1)
                {
                    Output completeOutput = new Output();
                    completeOutput.setIndex(index);
                    completeOutput.setIdx(idx);
                    completeOutput.setProbabilityValue(probability);
                    completedresult.add(completeOutput);
                    index++;
                    continue;
                }

                flag = 1;

                double[] decodeArray = new double[dictonary.size()];
                decodeArray[idx] = 1;
                INDArray decode = Nd4j.create(decodeArray, new int[]{1, dictonary.size(), 1});
                mergeVertex.setInputs(decode, thoughtVector);
                INDArray merged = mergeVertex.doForward(false, mgr);
                INDArray activateDec = decoder.rnnTimeStep(merged, mgr);
                INDArray out = output.activate(activateDec, false, mgr);

                Map<Integer, Double> idxes = new HashMap<>();

                //collect the probability of each word in dictonary in a hash map.
                for (int s = 0; s < out.size(1); s++) {
                    double temp = out.getDouble(0, s, 0);
                    temp = temp * probability;
                    idxes.put(s, temp);
                }

                //sort the map in the descending order.
                Map<Integer, Double> sorted = MapSort.sortByComparator(idxes);

                for(Map.Entry<Integer,Double> sortedentry:sorted.entrySet())
                {
                    Output eOutput = new Output();
                    eOutput.setIndex(index);
                    eOutput.setIdx(sortedentry.getKey());
                    eOutput.setProbabilityValue(sortedentry.getValue());
                    listOfAllOutcome.add(eOutput);
                }

                index++;

            }
            if(flag==0)
                break;
            List<Output> top10Token = getTopNOutcome(listOfAllOutcome, completedresult);
            updateResultLine(top10Token);
            BestNNode = top10Token;
        }


        //for (Output token: top10Token)
        //{
          //  System.out.println(token.getIndex()+" "+token.getIdx()+" "+token.getProbabilityValue());
        //}

    }

    private List<Output> getTopNOutcome(List<Output> listOfAllOutcome, List<Output> completedResult)
    {
        // Last token index, current token idx and probability value will be stored at outputs
        List<Output> outputs = new ArrayList<Output>();

        //After sorting the sortedoutputs is returned
        List<Output> sortedoutputs = new ArrayList<Output>();

        //store each outcome in the list of outputs

        outputs.addAll(listOfAllOutcome);
        outputs.addAll(completedResult);

        //get best 10 token out of all token and store in the stored list
        for (int i =1;i<=10;i++)
        {
            double maxprob = 0;
            int index = 0;
            for(int j = 0; j < outputs.size();j++)
            {
                double probvalue = outputs.get(j).getProbabilityValue();
                if(probvalue > maxprob)
                {
                    maxprob = probvalue;
                    index = j;
                }
            }
            sortedoutputs.add(outputs.get(index));
            outputs.remove(index);
        }


        return sortedoutputs;

    }

    private void updateResultLine(List<Output> currentToken)
    {
        List<String> temp = new ArrayList<String>();
        for(String line: resultLine)
        {
            temp.add(line);
        }
        resultLine = new ArrayList<String>();
        for (Output token: currentToken)
        {
            try
            {
                int index = token.getIndex();
                String tempString = temp.get(index);
                tempString = tempString+ token.getIdx()+" ";
                resultLine.add(tempString);
            }
            catch (Exception ex)
            {
                System.err.println(token.getIndex()+" "+token.getIdx()+" "+token.getProbabilityValue());
            }

        }
    }


    public List<String> getResultLine()
    {
        return resultLine;
    }
}
