import java.util.ArrayList;

public class Variable {

    private String operationNumber; // register num

    private String varName;// register ident(name)

    private boolean isConst;// whether is const(can not be change)

    private boolean isGlobal;//

    private String iType;// i32 or i1 or i32*(array)

    private int block; // belong to block

    private int value; //global variable value init 0

    private boolean isArray;// whether is array

    private int dimensions;// array dimension num

    private int allLength;// array all length =   every dimension length * every dimension length * ... , that is equal to arrayDimensions mul each other

    private ArrayList<Integer> arrayDimensions;//the length of every array dimension

    private ArrayList<Integer> arrayDimensionsWeight;//the weight of every dimension

    private ArrayList<Integer> arrayValue;

    private boolean isFuncParam;

    public Variable(){
        this.isArray = false;
        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensionsWeight = new ArrayList<>();
        this.arrayValue = new ArrayList<>();
        this.dimensions = 1;
        this.allLength = 0;
        this.isFuncParam = false;
    }

    public Variable(String number, String regName, boolean isConst, boolean isGlobal, String iType){
        this.operationNumber = number;
        this.varName = regName;
        this.isConst = isConst;
        this.isGlobal = isGlobal;
        this.iType = iType;
        this.isArray = false;
        this.arrayDimensions = new ArrayList<>();
        this.arrayDimensionsWeight = new ArrayList<>();
        this.arrayValue = new ArrayList<>();
        this.dimensions = 1;
        this.allLength = 0;
        this.isFuncParam = false;
    }

    public void initArrayValue(){
        for (int i = 0; i<allLength ;i++){
            arrayValue.add(0);
        }
    }

    public String getArrayValueString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0 ; i< allLength-1 ; i++){
            stringBuilder.append("i32 " + arrayValue.get(i) + ", ");
        }
        stringBuilder.append("i32 " + arrayValue.get(allLength-1) + "]");
        return stringBuilder.toString();
    }

    public void setArrayValue(int value, ArrayList<Integer> offsets){
        int len = 0;
        offsets = addZeroToIntArray(offsets);
        for (int i = 0; i<dimensions ; i++){
            len = len + offsets.get(i) * arrayDimensionsWeight.get(i);
        }
        arrayValue.set(len,value);
    }

    public void computeArrayDimensionWeight(){
        for (int i = 0; i<dimensions ; i++){
            int weight = 1;
            for (int j = i+1; j<dimensions ; j++){
                weight = weight * arrayDimensions.get(j);
            }
            this.arrayDimensionsWeight.add(weight);
        }
    }

    public String getArrayElementPtrByRegister(String regNum, String register){
        return regNum + " = getelementptr "
                + "[" + allLength + " x i32" + "]" + ", " + "[" + allLength + " x i32" + "]" + "* " + operationNumber
                + ", i32 0," + " i32 " + register + System.lineSeparator();
    }

    public String getArrayElementPtrByOffsets(String regNum, ArrayList<Integer> offsets){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(regNum + " = getelementptr "
                + "[" + allLength + " x i32" + "]" + ", " + "[" + allLength + " x i32" + "]" + "* " + operationNumber
                + ", i32 0," + " i32 "
        );
        int len = 0;
        offsets = addZeroToIntArray(offsets);
        for (int i = 0; i<dimensions ; i++){
            len = len + offsets.get(i) * arrayDimensionsWeight.get(i);
        }
        stringBuilder.append(len + System.lineSeparator());
        return stringBuilder.toString();
    }

    public ArrayList<Integer> addZeroToIntArray(ArrayList<Integer> integers){
        while (integers.size() < dimensions){
            integers.add(0);
        }
        return integers;
    }


    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        if(iType.equals("int")){
            this.iType = "i32";
        }
        else if (iType.equals("i32*")){
            this.iType = "i32*";
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

    public int getBlock() {
        return block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public ArrayList<Integer> getArrayDimensions() {
        return arrayDimensions;
    }

    public void setArrayDimensions(ArrayList<Integer> arrayDimensions) {
        this.arrayDimensions = arrayDimensions;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public int getAllLength() {
        return allLength;
    }

    public void setAllLength(int allLength) {
        this.allLength = allLength;
    }

    public ArrayList<Integer> getArrayDimensionsWeight() {
        return arrayDimensionsWeight;
    }

    public void setArrayDimensionsWeight(ArrayList<Integer> arrayDimensionsWeight) {
        this.arrayDimensionsWeight = arrayDimensionsWeight;
    }

    public ArrayList<Integer> getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(ArrayList<Integer> arrayValue) {
        this.arrayValue = arrayValue;
    }

    public boolean isFuncParam() {
        return isFuncParam;
    }

    public void setFuncParam(boolean funcParam) {
        isFuncParam = funcParam;
    }
}
