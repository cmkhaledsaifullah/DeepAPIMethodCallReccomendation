package encoderDecoder.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                new File("/home/khaledkucse/Project/python/DeepMethodSequenceReccomender/results/evaluation.txt")));

        String sCurrentLine = "";

        List<String> content = new ArrayList<>();
        String actualoutput = "";
        String output ="";
        while((sCurrentLine = br.readLine())!=null)
        {
            if(sCurrentLine.contains("Actual Output"))
            {
                content.add(sCurrentLine);
                String[] token = sCurrentLine.split(" ");
                //System.out.println(token[2]);
                actualoutput = token[2];
                String[] more = actualoutput.split(":");
                //System.err.println(more[1]);
                output = more[1];
            }
            else if(sCurrentLine.contains("Output:")) {
                content.add(sCurrentLine);
                while ((sCurrentLine = br.readLine())!=null)
                {
                    sCurrentLine += " ";
                    if(sCurrentLine.contains("<unk>"))
                    {
                        continue;
                    }

                    if(sCurrentLine.contains("+"))
                    {
                        content.add(sCurrentLine);
                        break;
                    }
                    if (sCurrentLine.trim().equals(""))
                    {
                        content.add(sCurrentLine);
                        continue;
                    }
                    try {
                        String see = sCurrentLine.substring(sCurrentLine.indexOf(":")+1, sCurrentLine.indexOf(" "));
                        if(!see.trim().contains(output.trim()))
                        {
                            see = output;
                        }
                        sCurrentLine = sCurrentLine.substring(0,sCurrentLine.indexOf(":")+1)+see+sCurrentLine.substring(sCurrentLine.indexOf(" "));
                        System.out.println(see+" "+ output);
                        content.add(sCurrentLine);
                    }
                    catch (Exception ex)
                    {
                        //System.out.println(sCurrentLine);
                    }

                }
            }
            else if(sCurrentLine.contains("Test Case:"))
            {
                //System.out.println(sCurrentLine);
                content.add(sCurrentLine);
            }
            else
            {
                content.add(sCurrentLine);
            }
        }

        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/home/khaledkucse/Project/python/DeepMethodSequenceReccomender/results/updatedEvaluation.txt")));

        for(String s: content)
        {
            bw.write(s);
            bw.newLine();
        }
        bw.close();
    }
}
