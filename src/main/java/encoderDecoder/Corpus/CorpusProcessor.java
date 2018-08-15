package encoderDecoder.Corpus;

import encoderDecoder.Model.Input;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CorpusProcessor {
    public static final String SPECIALS = "";
    private Set<String> dictonarySet = new HashSet<>();
    private Map<String, Double> freq = new HashMap<>();
    private Map<String, Double> dictonary = new HashMap<>();
    private boolean countFreq;
    private InputStream is;
    private int rowSize;

    public CorpusProcessor(String filename, int rowSize, boolean countFreq) throws FileNotFoundException {
        this(new FileInputStream(filename), rowSize, countFreq);
    }

    public CorpusProcessor(InputStream is, int rowSize, boolean countFreq) {
        this.is = is;
        this.rowSize = rowSize;
        this.countFreq = countFreq;
    }

    public void start() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            String wholeLine = "";
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(" \\+\\+\\+\\$\\+\\+\\+ ");
                Input input = new Input();
                input.setLabel(lineSplit[0].trim());
                input.setRecievervariable(lineSplit[1].trim());
                input.setContext(lineSplit[2].trim());
                processLine(input);


            }
        }
    }

    protected void processLine(Input input)
    {
        String line = input.getLabel()+" "+input.getRecievervariable()+" "+input.getContext();
        tokenizeLine(line, dictonarySet, false);
    }

    // here we not only split the words but also store punctuation marks
    protected void tokenizeLine(String token, Collection<String> resultCollection, boolean addSpecials)
    {
        String[] words = token.split(" ");
        for (String word : words)
        {
            word = word.trim();
            if (!word.isEmpty())
            {
                boolean specialFound = true;
                while (specialFound && !word.isEmpty())
                {
                    for (int i = 0; i < word.length(); ++i)
                    {
                        int idx = SPECIALS.indexOf(word.charAt(i));
                        specialFound = false;
                        if (idx >= 0)
                        {
                            String word1 = word.substring(0, i);
                            if (!word1.isEmpty()) {
                                addWord(resultCollection, word1.trim());
                            }
                            if (addSpecials) {
                                addWord(resultCollection, String.valueOf(word.charAt(i)));
                            }
                            word = word.substring(i + 1).trim();
                            specialFound = true;
                            break;
                        }
                    }
                }
                if (!word.isEmpty())
                {
                    addWord(resultCollection, word.trim());
                }
            }
        }
    }

    private void addWord(Collection<String> coll, String word) {
        if (coll != null)
        {
            coll.add(word);
        }
        if (countFreq)
        {
            Double count = freq.get(word);
            if (count == null)
            {
                freq.put(word, 1.0);
            }
            else {
                freq.put(word, count + 1);
            }
        }
    }

    public Set<String> getdictonarySet() {
        return dictonarySet;
    }

    public Map<String, Double> getFreq() {
        return freq;
    }

    public void setdictonary(Map<String, Double> dictonary) {
        this.dictonary = dictonary;
    }

    /**
     * Converts an iterable sequence of words to a list of indices. This will
     * never return {@code null} but may return an empty {@link java.util.List}.
     *
     * @param words
     *            sequence of words
     * @return list of indices.
     */
    protected final List<Double> wordsToIndexes(final Iterable<String> words) {
        int i = rowSize;
        final List<Double> wordIdxs = new LinkedList<>();
        for (final String word : words) {
            if (--i == 0) {
                break;
            }
            final Double wordIdx = dictonary.get(word.trim());
            if (wordIdx != null) {
                wordIdxs.add(wordIdx);
            } else {
                wordIdxs.add(0.0);
            }
        }
        //System.out.println(wordIdxs);
        return wordIdxs;
    }

}
