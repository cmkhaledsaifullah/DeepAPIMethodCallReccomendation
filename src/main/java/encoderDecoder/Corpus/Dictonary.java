package encoderDecoder.Corpus;

import encoderDecoder.Model.Input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Dictonary
{
    private Map<String, Double> dictonary;
    private Map<Double, String> revdictonary;
    private List<List<Double>> corpus;
    private List<List<Double>> label;

    public Dictonary()
    {
        this.dictonary = new HashMap<String, Double>();
        this.revdictonary = new HashMap<Double,String>();
        this.corpus = new ArrayList<List<Double>>();
        this.label = new ArrayList<List<Double>>();
    }

    public void createDictionary(String CHARS, String CORPUS_FILENAME, int ROW_SIZE, int MAX_dictonary) throws IOException, FileNotFoundException
    {
        double idx = processSpecials(CHARS);

        //Start to gather token.
        System.out.println("Building the dictionary...");
        CorpusProcessor corpusProcessor = gatherToken(CORPUS_FILENAME,ROW_SIZE);

        Map<Double, Set<String>> freqMap = sortTokenBasedOnFrequency(corpusProcessor);

        // the tokens order is preserved for TreeSet
        Set<String> dictonarySet = storeOrderedToken(freqMap,MAX_dictonary);

        // all of the above means that the dictionary with the same MAX_dictonary constraint and made from the same source file will always be
        // the same, the tokens always correspond to the same number so we don't need to save/restore the dictionary
        System.out.println("Dictionary is ready, size is " + dictonarySet.size());

        storeInDictonary(idx,dictonarySet);

        System.out.println("Total dictionary size is " + dictonary.size() + ". Processing the dataset...");

        //creating numerical corpus for each row in the training dataset.
        convertDatsetBasedOnDictonary(corpusProcessor,CORPUS_FILENAME,ROW_SIZE);
        System.out.println("Done. Corpus size is " + corpus.size());
    }

    private double processSpecials(String CHARS)
    {
        double idx = 3.0;
        dictonary.put("<unk>", 0.0);
        revdictonary.put(0.0, "<unk>");
        dictonary.put("<eos>", 1.0);
        revdictonary.put(1.0, "<eos>");
        dictonary.put("<go>", 2.0);
        revdictonary.put(2.0, "<go>");

        //Adding all special symbols in the dictonary
        for (char c : CHARS.toCharArray())
        {
            if (!dictonary.containsKey(c)) {
                dictonary.put(String.valueOf(c), idx);
                revdictonary.put(idx, String.valueOf(c));
                ++idx;
            }
        }
        return idx;
    }

    private CorpusProcessor gatherToken(String CORPUS_FILENAME, int ROW_SIZE) throws IOException {
        //Start to gather token.
        CorpusProcessor corpusProcessor = new CorpusProcessor(CORPUS_FILENAME, ROW_SIZE, true);
        corpusProcessor.start();
        return corpusProcessor;
    }

    private Map<Double, Set<String>> sortTokenBasedOnFrequency(CorpusProcessor corpusProcessor)
    {
        //get the frequency of each token
        Map<String, Double> freqs = corpusProcessor.getFreq();

        Map<Double, Set<String>> freqMap = new TreeMap<>(new Comparator<Double>() {

            @Override
            public int compare(Double o1, Double o2) {
                return (int) (o2 - o1);
            }
        });
        // tokens of the same frequency fall under the same key, the order is reversed so the most frequent tokens go first
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            Set<String> set = freqMap.get(entry.getValue());
            if (set == null) {
                set = new TreeSet<>(); // tokens of the same frequency would be sorted alphabetically
                freqMap.put(entry.getValue(), set);
            }
            set.add(entry.getKey());
        }
        return freqMap;
    }
    private Set<String> storeOrderedToken(Map<Double, Set<String>> freqMap,int MAX_dictonary)
    {
        Set<String> dictonarySet = new TreeSet<>();
        int cnt = 0;
        //add all special symbol in the dictonarySet
        dictonarySet.addAll(dictonary.keySet());
        // get most frequent tokens and put them to dictonarySet
        for (Map.Entry<Double, Set<String>> entry : freqMap.entrySet()) {
            for (String val : entry.getValue()) {
                if (dictonarySet.add(val) && ++cnt >= MAX_dictonary) {
                    break;
                }
            }
            if (cnt >= MAX_dictonary) {
                break;
            }
        }
        return dictonarySet;
    }
    private void storeInDictonary(double idx, Set<String> dictonarySet)
    {
        // index the dictionary and build the reverse dictionary for lookups
        for (String word : dictonarySet) {
            if (!dictonary.containsKey(word.trim())) {
                dictonary.put(word.trim(), idx);
                revdictonary.put(idx, word.trim());
                ++idx;
            }
        }
    }

    private void convertDatsetBasedOnDictonary(CorpusProcessor corpusProcessor, String CORPUS_FILENAME, int ROW_SIZE) throws IOException
    {
        corpusProcessor = new CorpusProcessor(CORPUS_FILENAME, ROW_SIZE, false)
        {
            @Override
            protected void processLine(Input input)
            {
                List<String> words = new ArrayList<>();
                String labelString = input.getLabel();
                tokenizeLine(labelString.trim(), words, true);
                //System.out.println(words+","+label.size());
                label.add(wordsToIndexes(words));

                words = new ArrayList<String>();
                String contextString = input.getRecievervariable()+" "+input.getContext();
                tokenizeLine(contextString.trim(), words, true);
                //System.out.println(words+","+ corpus.size());
                corpus.add(wordsToIndexes(words));
            }
        };
        corpusProcessor.setdictonary(dictonary);
        corpusProcessor.start();
    }



    public Map<String, Double> getdictonary() {
        return dictonary;
    }

    public Map<Double, String> getRevdictonary() {
        return revdictonary;
    }

    public List<List<Double>> getCorpus() {
        return corpus;
    }

    public List<List<Double>> getLabel() {
        return label;
    }
}
