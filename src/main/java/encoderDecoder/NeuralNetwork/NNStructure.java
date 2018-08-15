package encoderDecoder.NeuralNetwork;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Map;

public class NNStructure
{
    /**
     * Configure and initialize the computation graph. This is done once in the
     * beginning to prepare the {network} for training.
     */
    public ComputationGraph createComputationGraph(double LEARNING_RATE, int TBPTT_SIZE,Map<String, Double> dictonary, int EMBEDDING_WIDTH, int HIDDEN_LAYER_WIDTH) {
        ComputationGraph network;
        final NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .updater(new RmsProp(LEARNING_RATE))
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer);

        final ComputationGraphConfiguration.GraphBuilder graphBuilder = builder.graphBuilder()
                .pretrain(false)
                .backprop(true)
                .backpropType(BackpropType.Standard)
                .tBPTTBackwardLength(TBPTT_SIZE)
                .tBPTTForwardLength(TBPTT_SIZE)
                .addInputs("inputLine", "decoderInput")
                .setInputTypes(InputType.recurrent(dictonary.size()), InputType.recurrent(dictonary.size()))
                .addLayer("embeddingEncoder",
                        new EmbeddingLayer.Builder()
                                .nIn(dictonary.size())
                                .nOut(EMBEDDING_WIDTH)
                                .build(),
                        "inputLine")
                .addLayer("encoder",
                        new LSTM.Builder()
                                .nIn(EMBEDDING_WIDTH)
                                .nOut(HIDDEN_LAYER_WIDTH)
                                .activation(Activation.TANH)
                                .build(),
                        "embeddingEncoder")
                .addVertex("thoughtVector", new LastTimeStepVertex("inputLine"), "encoder")
                .addVertex("dup", new DuplicateToTimeSeriesVertex("decoderInput"), "thoughtVector")
                .addVertex("merge", new MergeVertex(), "decoderInput", "dup")
                .addLayer("decoder",
                        new LSTM.Builder()
                                .nIn(dictonary.size() + HIDDEN_LAYER_WIDTH)
                                .nOut(HIDDEN_LAYER_WIDTH)
                                .activation(Activation.TANH)
                                .build(),
                        "merge")
                .addLayer("output",
                        new RnnOutputLayer.Builder()
                                .nIn(HIDDEN_LAYER_WIDTH)
                                .nOut(dictonary.size())
                                .activation(Activation.SOFTMAX)
                                .lossFunction(LossFunctions.LossFunction.MCXENT)
                                .build(),
                        "decoder")
                .setOutputs("output");

        network = new ComputationGraph(graphBuilder.build());
        return network;
    }
}
