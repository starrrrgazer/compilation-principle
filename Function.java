import java.util.ArrayList;
import java.util.HashMap;

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
    private ArrayList<String> paramsType; // store params type i32 or i32*
    private String declareString;
    private HashMap<String,Variable> functionParams; // funcParams
    private ArrayList<String> paramsName; // store params name

    public boolean isDeclare() {
        return isDeclare;
    }

    public void setDeclare(boolean declare) {
        isDeclare = declare;
    }



    public Function(){
        this.paramsType = new ArrayList<>();
        this.paramsName = new ArrayList<>();
        this.functionParams = new HashMap<>();
    }
    public Function(String retType,String funcName,boolean isDeclear,ArrayList<String> params,String declareString){
        this.retType = retType;
        this.funcName = funcName;
        this.isDeclare = isDeclear;
        this.paramsType = params;
        this.declareString = declareString;
    }

    public String defineFuncString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local " + retType + " @" + funcName + "(");
        int num = 0;
        int paramNum = paramsType.size();
        for (int i = 0;i < paramNum-1 ;i ++){
            stringBuilder.append(paramsType.get(i) + " %" + i + ", ");
        }
        stringBuilder.append(paramsType.get(paramNum-1) + " %" + (paramNum-1) + ")");
        return stringBuilder.toString();
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

    public HashMap<String, Variable> getFunctionParams() {
        return functionParams;
    }

    public void setFunctionParams(HashMap<String, Variable> functionParams) {
        this.functionParams = functionParams;
    }

    public ArrayList<String> getParamsType() {
        return paramsType;
    }

    public void setParamsType(ArrayList<String> paramsType) {
        this.paramsType = paramsType;
    }

    public ArrayList<String> getParamsName() {
        return paramsName;
    }

    public void setParamsName(ArrayList<String> paramsName) {
        this.paramsName = paramsName;
    }
}
