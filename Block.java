public class Block {
    private String operationNumber;
    private boolean isBrComplete;
    private int brLabelNum; // br to one label or two label
    private int insertIndex;// the index to insert "br xxx label xxx " to output
    private String brLabel1;
    private String brLabel2;
    private String judgeRegister;
    private boolean isLor;
    private boolean isLAnd;
    private boolean needInsert;// whether need to insert into output

    public Block(){
        this.isBrComplete = false;
        this.needInsert = true;
        this.isLor = false;
        this.isLAnd = false;
    }
    //when use !
    public void changeBrLabel(){
        String tmp = this.brLabel1;
        this.brLabel1 = this.brLabel2;
        this.brLabel2 = tmp;
    }

    public boolean isLor() {
        return isLor;
    }

    public void setLor(boolean lor) {
        isLor = lor;
    }

    public boolean isLAnd() {
        return isLAnd;
    }

    public void setLAnd(boolean LAnd) {
        isLAnd = LAnd;
    }

    public boolean isNeedInsert() {
        return needInsert;
    }

    public void setNeedInsert(boolean needInsert) {
        this.needInsert = needInsert;
    }

    public String getJudgeRegister() {
        return judgeRegister;
    }

    public void setJudgeRegister(String judgeRegister) {
        this.judgeRegister = judgeRegister;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public boolean isBrComplete() {
        return isBrComplete;
    }

    public void setBrComplete(boolean brComplete) {
        isBrComplete = brComplete;
    }

    public int getBrLabelNum() {
        return brLabelNum;
    }

    public void setBrLabelNum(int brLabelNum) {
        this.brLabelNum = brLabelNum;
    }

    public int getInsertIndex() {
        return insertIndex;
    }

    public void setInsertIndex(int insertIndex) {
        this.insertIndex = insertIndex;
    }

    public String getBrLabel1() {
        return brLabel1;
    }

    public void setBrLabel1(String brLabel1) {
        this.brLabel1 = brLabel1;
    }

    public String getBrLabel2() {
        return brLabel2;
    }

    public void setBrLabel2(String brLabel2) {
        this.brLabel2 = brLabel2;
    }
}
