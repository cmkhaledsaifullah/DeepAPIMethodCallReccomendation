package encoderDecoder.Model;

public class OutputPercentage
{
    long noOfCorrectPrediction;
    long noOfInstance;

    public OutputPercentage(long noOfCorrectPrediction, long noOfInstance)
    {
        this.noOfCorrectPrediction = noOfCorrectPrediction;
        this.noOfInstance = noOfInstance;
    }

    public long getNoOfCorrectPrediction() {
        return noOfCorrectPrediction;
    }

    public void setNoOfCorrectPrediction(long noOfCorrectPrediction) {
        this.noOfCorrectPrediction = noOfCorrectPrediction;
    }

    public long getNoOfInstance() {
        return noOfInstance;
    }

    public void setNoOfInstance(long noOfInstance) {
        this.noOfInstance = noOfInstance;
    }
}
