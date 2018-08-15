package encoderDecoder.Corpus;

import encoderDecoder.Model.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataPreprocessing
{

    public void getAllFIles()
    {
        String folderpath = "dataset/Models/";
        File datasetFolder = new File(folderpath);
        File[] allfiles = datasetFolder.listFiles();
        long count = 1;
        for(File file: allfiles)
        {
            count = getData(file.getAbsolutePath(),count);

        }
    }

    private long getData(String filepath, long count)
    {
        List<Context> contexts = new ArrayList<>();
        try {
            BufferedReader br =new BufferedReader(new FileReader(new File(filepath)));
            String sCurrentLine = "";
            while((sCurrentLine = br.readLine())!=null)
            {
                Context context = new Context();

                //Method Name: Reciever Type
                String[] methodtoken = sCurrentLine.split(">");
                context.setMethodName(methodtoken[1].trim());

                //Reciever Type
                sCurrentLine = br.readLine();
                String[] recievertoken = sCurrentLine.split(">");
                context.setRecieverVariable(recievertoken[1].trim());

                //Parameter Lists
                sCurrentLine = br.readLine();
                String[] parametertoken = sCurrentLine.split(">");
                context.setParameters(parametertoken[1].trim());

                //Surrounding Contexts
                sCurrentLine = br.readLine();
                String[] surroundingtoken = sCurrentLine.split(">");
                if(surroundingtoken.length<1)
                {
                    System.err.println(sCurrentLine);
                }
                context.setSurroundingContext(surroundingtoken[1].trim());

                //Line Contexts
                sCurrentLine = br.readLine();
                String[] linetoken = sCurrentLine.split(">");
                context.setLineContext(linetoken[1].trim());

                contexts.add(context);
            }
            br.close();
            System.out.println("Number of instances at "+filepath+" is: "+contexts.size());

            count = creteDatset(contexts, count);


        } catch (Exception e) {
            System.err.println(filepath);
            e.printStackTrace();
        }
        return count;
    }

    private long creteDatset(List<Context> contexts, long count)
    {
        File fullDatasetFile = new File("dataset/dataset.txt");
        File trainDatasetFile = new File("dataset/train_dataset.txt");
        File testDatasetFile = new File("dataset/test_dataset.txt");

        BufferedWriter bw_full = null;
        BufferedWriter bw_train = null;
        BufferedWriter bw_test = null;
        try {

            if (fullDatasetFile.exists())
                bw_full = new BufferedWriter(new FileWriter(fullDatasetFile, true));
            else
                bw_full = new BufferedWriter(new FileWriter(fullDatasetFile));


            if (trainDatasetFile.exists())
                bw_train = new BufferedWriter(new FileWriter(trainDatasetFile, true));
            else
                bw_train = new BufferedWriter(new FileWriter(trainDatasetFile));

            if (testDatasetFile.exists())
                bw_test = new BufferedWriter(new FileWriter(testDatasetFile, true));
            else
                bw_test = new BufferedWriter(new FileWriter(testDatasetFile));

            Random rand = new Random();

            for (Context context : contexts) {
                String line = "ID:"+count+" +++$+++ "+context.getMethodName() + " " + context.getParameters() + " +++$+++ " + context.getRecieverVariable() + " +++$+++ " + context.getSurroundingContext() + " " + context.getLineContext();
                bw_full.write(line);
                bw_full.newLine();

                if (rand.nextDouble() >= 0.9) {
                    bw_test.write(line);
                    bw_test.newLine();
                } else {
                    bw_train.write(line);
                    bw_train.newLine();
                }
                count++;
            }

            bw_full.close();
            bw_test.close();
            bw_train.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
