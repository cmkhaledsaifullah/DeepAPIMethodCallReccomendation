package encoderDecoder.Utils;


import encoderDecoder.Model.OutputPercentage;

import java.util.ArrayList;
import java.util.List;

public class Evaluator
{
    List<String> labels;
    List<List<String>> predictions;

    public Evaluator(List<String> labels, List<List<String>> predictions)
    {
        this.labels = labels;
        this.predictions = predictions;
    }
    public void topNAccuracy(int topN)
    {
        int correctCount = 0;
        for(int i=0;i<labels.size();i++)
        {
            List<String> predicted = predictions.get(i);
            String[] token = labels.get(i).trim().split(" ");
            boolean flag = getAccuracyValue(topN,i,token.length);
            if(flag == true)
                correctCount++;
        }
        Double accuracy = (double)correctCount/(labels.size());
        System.out.println("===============Top "+topN+" Reccomendation=================");
        System.out.println("Number of Evaluation Cases: "+labels.size());
        System.out.println("Overall Accuracy: "+ accuracy);
    }

    public void topNBlueScore(int topN)
    {
        long labelToken = 0;
        long predictedtoken = 0;
        for(int i=0;i<labels.size();i++)
        {
            List<String> predicted = predictions.get(i);
            List<List<Integer>> countclip = new ArrayList<>();
            String [] token = labels.get(i).trim().split(" ");
            for(int j = 0; j < topN; j++)
            {
                List<Integer> countClipJth = new ArrayList<>();
                String[] eachpred = predicted.get(j).split(" ");
                for(int k = 0; k< token.length;k++)
                {
                    countClipJth.add(0);
                }
                for(int m = 0; m < token.length; m++)
                {
                    for(int z =0; z< eachpred.length;z++)
                    {
                        if(eachpred[z].equals(token[m]))
                        {
                            countClipJth.set(m,countClipJth.get(m)+1);
                        }
                    }
                }
                countclip.add(countClipJth);
            }
            long tempPredictedTOken = maxCount(countclip);
            labelToken += token.length;
            predictedtoken += tempPredictedTOken;
        }

        Double blueScore = (double)predictedtoken/labelToken;
        System.out.println("Overall Blue Score: "+ blueScore);
        System.out.println();
    }

    public void topNAccuracy(int topN, int noOfArguments)
    {
        int correctCount = 0;
        int numberOfInstance = 0;
        for(int i=0;i<labels.size();i++)
        {
            String[] token = labels.get(i).trim().split(" ");
            if(token.length < (noOfArguments+1))
                continue;

            boolean flag = getAccuracyValue(topN,i,token.length);
            if(flag == true)
                correctCount++;
            numberOfInstance++;
        }
        Double accuracy = (double)correctCount/(numberOfInstance);
        System.out.println("Number of Argument: "+ noOfArguments);
        System.out.println("Number of Evaluation Cases: "+ numberOfInstance);
        System.out.println("Accuracy: "+ accuracy);
    }


    public void topNBlueScore(int topN, int noOfArguments)
    {
        long labelToken = 0;
        long predictedtoken = 0;
        for(int i=0;i<labels.size();i++)
        {
            String [] token = labels.get(i).trim().split(" ");
            if(token.length < (noOfArguments+1))
                continue;

            List<String> predicted = predictions.get(i);
            List<List<Integer>> countclip = new ArrayList<>();
            for(int j = 0; j < topN; j++)
            {
                List<Integer> countClipJth = new ArrayList<>();
                String[] eachpred = predicted.get(j).split(" ");
                for(int k = 0; k< token.length;k++)
                {
                    countClipJth.add(0);
                }
                for(int m = 0; m < token.length; m++)
                {
                    for(int z =0; z< eachpred.length;z++)
                    {
                        if(eachpred[z].equals(token[m]))
                        {
                            countClipJth.set(m,countClipJth.get(m)+1);
                        }
                    }
                }
                countclip.add(countClipJth);
            }
            long tempPredictedTOken = maxCount(countclip);
            labelToken += token.length;
            predictedtoken += tempPredictedTOken;
        }

        Double blueScore = (double)predictedtoken/labelToken;
        System.out.println("Blue Score: "+ blueScore);
        System.out.println();
    }

    public void percentageAccuracyTopN(int topN)
    {
        OutputPercentage _0to25 = new OutputPercentage(0,0);
        OutputPercentage _26to50 = new OutputPercentage(0,0);
        OutputPercentage _51to75 = new OutputPercentage(0,0);
        OutputPercentage _76to100 = new OutputPercentage(0,0);
        for(int i=0;i<labels.size();i++)
        {
            String[] token = labels.get(i).trim().split(" ");

            if(token.length == 1)
            {
                boolean flag = getAccuracyValue(topN,i,1);
                if (flag == true)
                {
                    _76to100.setNoOfCorrectPrediction(_76to100.getNoOfCorrectPrediction()+1);
                }
                _76to100.setNoOfInstance(_76to100.getNoOfInstance()+1);
            }
            else if(token.length == 2)
            {
                boolean flag = getAccuracyValue(topN,i,1);
                if (flag == true)
                {
                    _26to50.setNoOfCorrectPrediction(_26to50.getNoOfCorrectPrediction()+1);
                }
                _26to50.setNoOfInstance(_26to50.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,2);
                if (flag == true)
                {
                    _76to100.setNoOfCorrectPrediction(_76to100.getNoOfCorrectPrediction()+1);
                }
                _76to100.setNoOfInstance(_76to100.getNoOfInstance()+1);

            }
            else if(token.length == 3)
            {
                boolean flag = getAccuracyValue(topN,i,1);
                if (flag == true)
                {
                    _26to50.setNoOfCorrectPrediction(_26to50.getNoOfCorrectPrediction()+1);
                }
                _26to50.setNoOfInstance(_26to50.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,2);
                if (flag == true)
                {
                    _51to75.setNoOfCorrectPrediction(_51to75.getNoOfCorrectPrediction()+1);
                }
                _51to75.setNoOfInstance(_51to75.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,3);
                if (flag == true)
                {
                    _76to100.setNoOfCorrectPrediction(_76to100.getNoOfCorrectPrediction()+1);
                }
                _76to100.setNoOfInstance(_76to100.getNoOfInstance()+1);

            }
            else if(token.length > 3)
            {
                boolean flag = getAccuracyValue(topN,i,1);
                if (flag == true)
                {
                    _0to25.setNoOfCorrectPrediction(_0to25.getNoOfCorrectPrediction()+1);
                }
                _0to25.setNoOfInstance(_0to25.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,2);
                if (flag == true)
                {
                    _26to50.setNoOfCorrectPrediction(_26to50.getNoOfCorrectPrediction()+1);
                }
                _26to50.setNoOfInstance(_26to50.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,3);
                if (flag == true)
                {
                    _51to75.setNoOfCorrectPrediction(_51to75.getNoOfCorrectPrediction()+1);
                }
                _51to75.setNoOfInstance(_51to75.getNoOfInstance()+1);

                flag = getAccuracyValue(topN,i,4);
                if (flag == true)
                {
                    _76to100.setNoOfCorrectPrediction(_76to100.getNoOfCorrectPrediction()+1);
                }
                _76to100.setNoOfInstance(_76to100.getNoOfInstance()+1);

            }
        }
        Double accuracy = (double) _0to25.getNoOfCorrectPrediction() / _0to25.getNoOfInstance();
        System.out.println("Accuracy [0-25]%: "+ accuracy);
        accuracy = (double) _26to50.getNoOfCorrectPrediction() / _26to50.getNoOfInstance();
        System.out.println("Accuracy [26-50]%: "+ accuracy);
        accuracy = (double) _51to75.getNoOfCorrectPrediction() / _51to75.getNoOfInstance();
        System.out.println("Accuracy [51-75]%: "+ accuracy);
        accuracy = (double) _76to100.getNoOfCorrectPrediction() / _76to100.getNoOfInstance();
        System.out.println("Accuracy [76-100]%: "+ accuracy);

    }

    private long maxCount(List<List<Integer>> countclip)
    {
        long returnedVal = 0;
        int dimension = countclip.get(0).size();
         for(int i = 0; i < dimension; i++)
         {
             int max = 0;
             for (int j = 0; j < countclip.size();j++)
             {
                 List<Integer> jth = countclip.get(j);
                 if(jth.get(i)>max)
                 {
                     max = jth.get(i);
                 }

             }
            returnedVal += max;
         }
         return returnedVal;
    }

    private boolean getAccuracyValue(int topN, int index, int noOfTerm)
    {
        String[] token = labels.get(index).trim().split(" ");
        String labelString = "";
        for(int i=0;i<noOfTerm;i++)
        {
            labelString += token[i];
        }
        Boolean flag = false;
        List<String> predicted = predictions.get(index);
        try {
            for (int j = 0; j < topN; j++)
            {
                if (predicted.get(j) != null && labelString != null)
                {
                    if (predicted.get(j).trim().contains(labelString.trim()))
                    {
                        flag = true;
                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            //System.err.println(predicted);
        }
        return flag;
    }

    private long getBlueScoreValue(int topN, int index, int noOfTerm)
    {
        List<String> predicted = predictions.get(index);
        List<List<Integer>> countclip = new ArrayList<>();
        String [] token = labels.get(index).trim().split(" ");
        for(int j = 0; j < topN; j++)
        {
            List<Integer> countClipJth = new ArrayList<>();
            String[] eachpred = predicted.get(j).split(" ");
            for(int k = 0; k< noOfTerm;k++)
            {
                countClipJth.add(0);
            }
            for(int m = 0; m < noOfTerm; m++)
            {
                for(int z =0; z< eachpred.length;z++)
                {
                    if(eachpred[z].contains(token[m]))
                    {
                        countClipJth.set(m,countClipJth.get(m)+1);
                    }
                }
            }
            countclip.add(countClipJth);
        }
        long tempPredictedTOken = maxCount(countclip);
        return  tempPredictedTOken;
    }
}
