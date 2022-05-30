package AOP;

public class AdvisedSupport {
    private TargetSource targetSource;

    public AdvisedSupport(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }
}
