package encoderDecoder.NeuralNetwork;

import encoderDecoder.Corpus.CorpusProcessor;
import encoderDecoder.Model.Input;
import encoderDecoder.Utils.Evaluator;
import org.deeplearning4j.nn.graph.ComputationGraph;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DMSREvaluation
{
    private List<String> test_labels;
    private List<List<String>> test_predictions;
    private List<String> test_predicted_Strings;

    public DMSREvaluation()
    {
        this.test_labels = new ArrayList<String>();
        this.test_predictions = new ArrayList<List<String>>();
        this.test_predicted_Strings = new ArrayList<>();
    }


    public void runTimeEvaluation(Scanner scanner, ComputationGraph network, Map<String, Double> dictonary, Map<Double, String> revdictonary, int ROW_SIZE, double normalizing_factor) throws IOException {
        System.out.println("Dialog started.");
        while (true) {
            System.out.print("In> ");
            // input line is appended to conform to the corpus format
            String line = scanner.nextLine() + "\n";
            CorpusProcessor runTimeProcessor = new CorpusProcessor(new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)), ROW_SIZE,
                    false) {
                @Override
                protected void processLine(Input input) {
                    List<String> words = new ArrayList<>();
                    String contextString = input.getRecievervariable()+" "+input.getContext();
                    tokenizeLine(contextString, words, true);
                    final List<Double> wordIdxs = wordsToIndexes(words);
                    if (!wordIdxs.isEmpty()) {
                        System.out.print("Got words: ");
                        for (Double idx : wordIdxs) {
                            System.out.print(revdictonary.get(idx) + " ");
                        }
                        System.out.println();
                        System.out.println("Output> ");
                        DMSRTraining training = new DMSRTraining();
                        training.output(network, dictonary,revdictonary,wordIdxs,ROW_SIZE, normalizing_factor);
                        List<String> outputSet = training.getOutputString();
                        for (String singleOutput: outputSet)
                            System.out.println(singleOutput);
                    }
                }
            };
            runTimeProcessor.setdictonary(dictonary);
            runTimeProcessor.start();
        }
    }

    public void statisticalEvaluation(String Test_FILENAME,ComputationGraph network, Map<String, Double> dictonary, Map<Double, String> revdictonary,int ROW_SIZE, double normalization_factor) throws IOException {
        System.out.println("Stattistical Evaluation Starts......");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("evaluation.txt")));
        BufferedReader br = new BufferedReader(new FileReader(new File(Test_FILENAME)));
        String sCurrentLine= "";
        org.deeplearning4j.eval.Evaluation evaluation= new org.deeplearning4j.eval.Evaluation(dictonary.size());
        int count =1;
        while((sCurrentLine=br.readLine())!=null)
        {
            bw.write("+++++++++++++++++++++++++++++++++++++++");
            bw.newLine();
            bw.write("Test Case: "+ count);
            bw.newLine();
            bw.write("Context: "+sCurrentLine);
            bw.newLine();
            System.out.println("Testing starts for Instance: "+ count++);
            String[] tokens = sCurrentLine.split(" \\+\\+\\+\\$\\+\\+\\+ ");
            String labelString = tokens[0].trim();
            bw.write("Actual Output: "+labelString);
            bw.newLine();
            test_labels.add(labelString.trim());
            CorpusProcessor statisticalProcessor = new CorpusProcessor(new ByteArrayInputStream(sCurrentLine.getBytes(StandardCharsets.UTF_8)), ROW_SIZE,
                    false) {
                @Override
                protected void processLine(Input input) {
                    List<String> words = new ArrayList<>();
                    String contextString = input.getRecievervariable()+" "+input.getContext();
                    tokenizeLine(contextString, words, true);
                    final List<Double> wordIdxs = wordsToIndexes(words);
                    if (!wordIdxs.isEmpty())
                    {
                        DMSRTraining training = new DMSRTraining();
                        training.output(network,dictonary,revdictonary,wordIdxs,ROW_SIZE, normalization_factor);
                        List<String> predictedStrings = training.getOutputString();
                        List<String> predictedmethod = new ArrayList<String>();
                        for(String predicted: predictedStrings)
                        {
                            String[] token = predicted.split(" ");
                            predictedmethod.add(token[0]);
                        }


                        test_predictions.add(predictedStrings);
                        test_predicted_Strings.addAll(predictedStrings);
                    }
                }
            };
            statisticalProcessor.setdictonary(dictonary);
            statisticalProcessor.start();
            bw.write("Output: ");
            bw.newLine();
            for(String line: test_predicted_Strings)
            {
                //System.out.println(line);
                if(line.contains("<eos>"))
                {
                    bw.write(line.substring(0,line.indexOf("<eos>")));
                    //System.out.println(line.substring(0,line.indexOf("<eos>")));
                }

                else if(line.contains("<unk>"))
                {
                    bw.write(line.substring(0,line.indexOf("<unk>")));
                    //System.out.println(line.substring(0,line.indexOf("<unk>")));
                }


                bw.newLine();
            }
            test_predicted_Strings = new ArrayList<>();

        }
        bw.close();
        Evaluator evaluator = new Evaluator(test_labels,test_predictions);

        //Top 1 reccomendation
        evaluator.topNAccuracy(1);
        evaluator.topNBlueScore(1);
        evaluator.topNAccuracy(1,0);
        evaluator.topNBlueScore(1,0);
        evaluator.topNAccuracy(1,1);
        evaluator.topNBlueScore(1,1);
        evaluator.topNAccuracy(1,2);
        evaluator.topNBlueScore(1,2);
        evaluator.topNAccuracy(1,3);
        evaluator.topNBlueScore(1,3);
        evaluator.percentageAccuracyTopN(1);


        //top 3 reccomendation
        evaluator.topNAccuracy(3);
        evaluator.topNBlueScore(3);
        evaluator.topNAccuracy(3,0);
        evaluator.topNBlueScore(3,0);
        evaluator.topNAccuracy(3,1);
        evaluator.topNBlueScore(3,1);
        evaluator.topNAccuracy(3,2);
        evaluator.topNBlueScore(3,2);
        evaluator.topNAccuracy(3,3);
        evaluator.topNBlueScore(3,3);
        evaluator.percentageAccuracyTopN(3);


        //top 5 reccomendation
        evaluator.topNAccuracy(5);
        evaluator.topNBlueScore(5);
        evaluator.topNAccuracy(5,0);
        evaluator.topNBlueScore(5,0);
        evaluator.topNAccuracy(5,1);
        evaluator.topNBlueScore(5,1);
        evaluator.topNAccuracy(5,2);
        evaluator.topNBlueScore(5,2);
        evaluator.topNAccuracy(5,3);
        evaluator.topNBlueScore(5,3);
        evaluator.percentageAccuracyTopN(5);



        //top 10 reccomendation
        evaluator.topNAccuracy(10);
        evaluator.topNBlueScore(10);
        evaluator.topNAccuracy(10,0);
        evaluator.topNBlueScore(10,0);
        evaluator.topNAccuracy(10,1);
        evaluator.topNBlueScore(10,1);
        evaluator.topNAccuracy(10,2);
        evaluator.topNBlueScore(10,2);
        evaluator.topNAccuracy(10,3);
        evaluator.topNBlueScore(10,3);
        evaluator.percentageAccuracyTopN(10);
    }

    public List<String> getTest_labels()
    {
        return test_labels;
    }
    public List<List<String>> getTest_predictions()
    {
        return test_predictions;
    }
}
