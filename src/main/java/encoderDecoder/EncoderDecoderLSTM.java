package encoderDecoder;


import encoderDecoder.Corpus.CorpusProcessor;
import encoderDecoder.Corpus.DataPreprocessing;
import encoderDecoder.Corpus.Dictonary;
import encoderDecoder.NeuralNetwork.DMSREvaluation;
import encoderDecoder.NeuralNetwork.DMSRTraining;
import encoderDecoder.NeuralNetwork.NNStructure;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class EncoderDecoderLSTM {

    /**
     * Dictionary that maps words into numbers.
     */
    private static Map<String, Double> dictonary = new HashMap<>();

    /**
     * Reverse map of {@link #dictonary}.
     */
    private static Map<Double, String> revdictonary = new HashMap<>();

    private final String CHARS = CorpusProcessor.SPECIALS;

    /**
     * The contents of the corpus. This is a list of sentences (each word of the
     * sentence is denoted by a {@link java.lang.Double}).
     */
    private static List<List<Double>> training_context = new ArrayList<>();
    private static List<List<Double>> training_label = new ArrayList<>();

    private static final int HIDDEN_LAYER_WIDTH = 512; // this is purely empirical, affects performance and VRAM requirement
    private static final int EMBEDDING_WIDTH = 128; // one-hot vectors will be embedded to more dense vectors with this width
    private static final String CORPUS_FILENAME = "dataset/train_dataset.txt"; // filename of data corpus to learn
    private static final String Test_FILENAME = "dataset/test_dataset.txt"; // filename to test statistically
    private static final String MODEL_FILENAME = "dmsr_train.zip"; // filename of the model
    private static final String BACKUP_MODEL_FILENAME = "dmsr_train.bak.zip"; // filename of the previous version of the model (backup)
    private static final int MINIBATCH_SIZE = 32;
    private static final long SAVE_EACH_MS = TimeUnit.MINUTES.toMillis(5); // save the model with this period
    private static final long TEST_EACH_MS = TimeUnit.MINUTES.toMillis(1); // test the model with this period
    private static final int MAX_dictonary = 5000; // this number of most frequent words will be used, unknown words (that are not in the
    // dictonary) are replaced with <unk> token
    private static final int TBPTT_SIZE = 25;
    private static final double LEARNING_RATE = 1e-1;
    private static final int ROW_SIZE = 80; // maximum line length in tokens
    private static final int NO_OF_EPOCH = 10; // Number of epoch

    private static final double normalization_factor = 0.7; // Normalization factor for Beam search algorithm

    /**
     * The delay between invocations of {@link java.lang.System#gc()} in
     * milliseconds. If VRAM is being exhausted, reduce this value. Increase
     * this value to yield better performance.
     */
    private static final int GC_WINDOW = 2000;

    private static final int MACROBATCH_SIZE = 20; // see CorpusIterator

    /**
     * The computation graph model.
     */
    private ComputationGraph network;


    private List<String> test_labels = new ArrayList<String>();
    private List<String> test_predictions = new ArrayList<String>();

    private List<String> top20MethodName = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        //DataPreprocessing dataPreprocessing = new DataPreprocessing();
        //dataPreprocessing.getAllFIles();
        new EncoderDecoderLSTM().run(args);
    }

    private void run(String[] args) throws IOException {
        Nd4j.getMemoryManager().setAutoGcWindow(GC_WINDOW);

        //creating Dictonary and converting each string row in dataset into numerical vectors
        Dictonary dict = new Dictonary();
        dict.createDictionary(CHARS,CORPUS_FILENAME,ROW_SIZE,MAX_dictonary);

        //Retriving dictonary, corpus
        dictonary = dict.getdictonary();
        revdictonary = dict.getRevdictonary();
        training_context = dict.getCorpus();
        training_label = dict.getLabel();

        /*
        File networkFile = new File(MODEL_FILENAME);

        int offset = 0;

        if (networkFile.exists())
        {
            System.out.println("Loading the existing network...");
            network = ModelSerializer.restoreComputationGraph(networkFile);
            System.out.print("Enter r for run time testing or s for statistical testing from test file or a number to do trining from minibatch ");
            String input;
            try (Scanner scanner = new Scanner(System.in))
            {
                input = scanner.nextLine();
                if (input.toLowerCase().equals("r"))
                {
                    DMSREvaluation evaluation = new DMSREvaluation();
                    evaluation.runTimeEvaluation(scanner,network,dictonary,revdictonary,ROW_SIZE,normalization_factor);
                }
                else if(input.toLowerCase().equals("s"))
                {
                    DMSREvaluation evaluation = new DMSREvaluation();
                    evaluation.statisticalEvaluation(Test_FILENAME,network,dictonary,revdictonary,ROW_SIZE,normalization_factor);
                    System.exit(-1);
                }
                else {
                    offset = Integer.valueOf(input);
                    DMSRTraining training = new DMSRTraining();
                    training.test(network,training_context,dictonary,revdictonary,ROW_SIZE,normalization_factor);

                }
            }
        }
        else {
            System.out.println("Creating a new network...");
            NNStructure neuralnetwork = new NNStructure();
            network = neuralnetwork.createComputationGraph(LEARNING_RATE,TBPTT_SIZE, dictonary, EMBEDDING_WIDTH,HIDDEN_LAYER_WIDTH);
            network.init();
        }
        System.out.println("Number of parameters: " + network.numParams());
        network.setListeners(new ScoreIterationListener(1));
        DMSRTraining training = new DMSRTraining();
        training.train(networkFile, BACKUP_MODEL_FILENAME, offset, network, training_context, training_label, MINIBATCH_SIZE, MACROBATCH_SIZE, dictonary, revdictonary, ROW_SIZE, NO_OF_EPOCH, SAVE_EACH_MS, TEST_EACH_MS,normalization_factor);
        */
    }

}
