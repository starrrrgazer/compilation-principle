public class Variable {

    private String operationNumber; // register num

    private String varName;// register ident(name)

    private boolean isConst;// whether is const(can not be change)

    private boolean isGlobal;//

    private String iType;// i32 or i1 or i32* ?

    private String blockLabel; // the label name

    public Variable(){}

    public Variable(String number, String regName, boolean isConst, boolean isGlobal, String iType){
        this.operationNumber = number;
        this.varName = regName;
        this.isConst = isConst;
        this.isGlobal = isGlobal;
        this.iType = iType;
    }

    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        if(iType.equals("int")){
            this.iType = "i32";
        }
        else {
            this.iType = "i32";
        }
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public String getBlockLabel() {
        return blockLabel;
    }

    public void setBlockLabel(String blockLabel) {
        this.blockLabel = blockLabel;
    }
}
