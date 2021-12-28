public class Function {
    /*
    * System function include
    * int getint();
    * int getch();
    * int getarray(int []);
    * void putint(int);
    * void putch(int);
    * void putarray(int, int[]);
    * */
    private String retType; // i32 or ?
    private String funcName;//ident(name), don't include @
    private boolean isDeclare;
    private String[] paramsType; // store params type i32 or i32* ?
    private String declareString;

    public boolean isDeclare() {
        return isDeclare;
    }

    public void setDeclare(boolean declare) {
        isDeclare = declare;
    }

    public String[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(String[] paramsType) {
        this.paramsType = paramsType;
    }

    public Function(){

    }
    public Function(String retType,String funcName,boolean isDeclear,String[] params,String declareString){
        this.retType = retType;
        this.funcName = funcName;
        this.isDeclare = isDeclear;
        this.paramsType = params;
        this.declareString = declareString;
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

    public String getDeclareString() {
        return declareString;
    }

    public void setDeclareString(String declareString) {
        this.declareString = declareString;
    }

    public boolean checkParamsType(String params){
        for (String param : this.paramsType){
            if (param.equals(params)){
                return true;
            }
        }
        return false;
    }
}
