public class Variable {

    private String operationNumber; // register num

    private String regName;// register ident(name)

    private boolean isConst;// whether is const(can not be change)

    private boolean isGlobal;//

    private String iType;// i32 or i1 or i32* ?

    public Variable(){}

    public Variable(String number, String regName, boolean isConst, boolean isGlobal, String iType){
        this.operationNumber = number;
        this.regName = regName;
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

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }
}
