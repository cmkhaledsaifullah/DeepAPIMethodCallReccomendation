package encoderDecoder.Model;

public class Context
{
    String methodName;
    String recieverVariable;
    String parameters;
    String surroundingContext;
    String lineContext;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getRecieverVariable() {
        return recieverVariable;
    }

    public void setRecieverVariable(String recieverVariable) {
        this.recieverVariable = recieverVariable;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getSurroundingContext() {
        return surroundingContext;
    }

    public void setSurroundingContext(String surroundingContext) {
        this.surroundingContext = surroundingContext;
    }

    public String getLineContext() {
        return lineContext;
    }

    public void setLineContext(String lineContext) {
        this.lineContext = lineContext;
    }
}
