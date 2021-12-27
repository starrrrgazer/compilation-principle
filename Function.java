public class Function {
    String retType; // i32 or ?
    String funcName;//

    public Function(String retType,String funcName){
        this.retType = retType;
        this.funcName = funcName;
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }
}
