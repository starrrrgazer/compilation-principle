import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class AntlrVisitor extends MiniSysBaseVisitor {
    ArrayList<String> outputList = new ArrayList<>();
    String outputListString;
    String retType;
    int registerNum = 0;//first block = %0
    int blockNum = -1;//
    int nowBlock = -1;
    String nowFuncName;
    String operationNumber = null;
//    HashMap<String, Variable> variableHashMap_local = new HashMap<>(); //is used to store information about variable //don't need anymore, variables are stored into blockArrayList
    HashMap<String, Variable> variableHashMap_global = new HashMap<>(); // belong to block -1
    HashMap<String,Function> functionHashMap = new HashMap<>();
    String bType; // when define var, set bType = btype
    boolean isReturn = false ; // judge whether if return
    Stack<ArrayList<Integer>> callbackStack = new Stack<>(); // if block callback
    Stack<Integer> retStack = new Stack<Integer>();// ret index
//    ListStack<StackElem> callbackStack = new ListStack<StackElem>(); // if block callback
//    ListStack<Integer> retStack = new ListStack<Integer>();// ret index
//    ListStack<ArrayList<Block>> blockStack = new ListStack<>();
    ArrayList<Block> blockArrayList = new ArrayList<>();
    ArrayList<String> globalDeclareList = new ArrayList<>();
    boolean isDefineGlobalVariable = false;
    ArrayList<Integer> breakList = new ArrayList<>();
    ArrayList<Integer> continueList = new ArrayList<>();
    boolean isBreak = false;
    boolean isContinue = false;
    boolean isDefineArray = false; // when define array , need to cal the dimension
    int operationNumber_array = 0;// is used to cal the dimension
    int nowDimension = 0;
    Variable nowArrayVariable;
    ArrayList<Integer> offsets = new ArrayList<>();
    boolean isDefineFunc = false;
    Function nowFunction;
    ArrayList<String> functionOutputList = new ArrayList<>();
    boolean isFuncDefBlock = false;
    int operationNumber_global = 0;
    boolean isRegisterPtr = false; // judge  the register is i32 or i32*
    boolean hasReturn = false;

    public void initFunctionMap(){

        Function function = new Function("i32","getint",false,null,"declare i32 @getint()" );
        functionHashMap.put("getint",function);
        Function function2 = new Function("i32","getch",false,null,"declare i32 @getch()");
        functionHashMap.put("getch",function2);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("i32*");
        Function function3 = new Function("i32","getarray",false, strings,"declare i32 @getarray(i32*)");
        functionHashMap.put("getarray",function3);

        strings = new ArrayList<>();
        strings.add("i32");
        Function function4 = new Function("void","putint",false,strings,"declare void @putint(i32)");
        functionHashMap.put("putint",function4);

        strings = new ArrayList<>();
        strings.add("i32");
        Function function5= new Function("void","putch",false,strings,"declare void @putch(i32)");
        functionHashMap.put("putch",function5);

        strings = new ArrayList<>();
        strings.add("i32");
        strings.add("i32*");
        Function function6 = new Function("void","putarray",false,strings,"declare void @putarray(i32,i32*)");
        functionHashMap.put("putarray",function6);

        strings = new ArrayList<>();
        strings.add("i32*");
        strings.add("i32");
        strings.add("i32");
        Function function7 = new Function("void","memset",false,strings,"declare void @memset(i32*, i32, i32)");
        functionHashMap.put("memset",function7);
        functionOutputList.add("declare i32 @getint()" + System.lineSeparator());
        functionOutputList.add("declare i32 @getch()" + System.lineSeparator());
        functionOutputList.add("declare i32 @getarray(i32*)" + System.lineSeparator());
        functionOutputList.add("declare void @putint(i32)" + System.lineSeparator());
        functionOutputList.add("declare void @putch(i32)" + System.lineSeparator());
        functionOutputList.add("declare void @putarray(i32,i32*)" + System.lineSeparator());
        functionOutputList.add("declare void @memset(i32*, i32, i32)" + System.lineSeparator());
    }

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        String funcName = ctx.ident().getText();
        funcInit(funcName);
        //define other function
        if (isDefineFunc){
            Function function = functionHashMap.get(funcName);
            if (function != null){
                System.out.println("function had been defined");
                System.exit(-1);
            }
            else {
                function = new Function();
                functionHashMap.put(funcName,function);
                nowFunction = function;
                //retType
                if (ctx.funcType().getText().equals("int")){
                    function.setRetType("i32");
                }
                else if (ctx.funcType().getText().equals("void")){
                    function.setRetType("void");
                }
                else {
                    System.out.println("retType is not int or void");
                    System.exit(-1);
                }
                //ident
                function.setFuncName(funcName);
                //funcFParams
                if (ctx.funcFParams() != null){
                    visit(ctx.funcFParams());
                }
                //block
                isFuncDefBlock = true;
                outputList.add(nowFunction.defineFuncString() + " {" + System.lineSeparator());
                funcFParamsInit();
                visit(ctx.block());
                if (function.getRetType().equals("void") && !hasReturn){
                    outputList.add("ret void" + System.lineSeparator());
                }
                else if (function.getRetType().equals("int") && !hasReturn){
                    System.out.println("retType is int but had not return");
                    System.exit(-1);
                }
                outputList.add("}" + System.lineSeparator());
            }
        }
        //define main
        else {
            if (ctx.ident().getText().equals("main")){
                Function function = new Function();
                function.setFuncName("main");
                function.setRetType("i32");
                nowFunction = function;
                registerNum = 0;

                outputList.add("define dso_local i32 @main()");
                outputList.add(" {" + System.lineSeparator());
                isFuncDefBlock = true;
                visit(ctx.block());
                outputList.add("}");
                functionHashMap.put("main",function);
            }
            else {
                System.out.println("main is not the last function");
                System.exit(-1);
            }
        }
        return null;
    }

    public void funcFParamsInit(){
        System.out.println("funcFParamsInit");
        int paramsNum = nowFunction.getParamsName().size();
        for (int i = 0; i<paramsNum;i++){
            Variable variable = nowFunction.getFunctionParams().get(nowFunction.getParamsName().get(i));
            if (variable.getiType().equals("i32")){
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(operationNumber + " = alloca i32" + System.lineSeparator());
                outputList.add("store i32 " + variable.getOperationNumber() + ", i32* " + operationNumber + System.lineSeparator());
                variable.setOperationNumber(operationNumber);
            }
            else {
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(operationNumber + " = alloca i32*" + System.lineSeparator());
                outputList.add("store i32* " + variable.getOperationNumber() + ", i32* *" + operationNumber + System.lineSeparator());
                variable.setOperationNumber(operationNumber);
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(operationNumber + " = load i32* , i32* * " + variable.getOperationNumber() + System.lineSeparator());
                variable.setOperationNumber(operationNumber);
            }
        }
    }

    public void funcInit(String funcName){
        System.out.println("funcInit");
        registerNum = -1;
        operationNumber = null;
        nowFuncName = funcName;
        isReturn = false;
        callbackStack = new Stack<>(); // if block callback
        retStack = new Stack<Integer>();// ret index
    }

    public int computeGlobalVariableValue(){
        System.out.println(outputList);
        int[] value = new int[9999];
        int finalValue = 0;
        for (int i = 0;i<outputList.size();i++){
            String[] arr = outputList.get(i).split("\\s+");
            System.out.println(arr[5]);
            int regNum = Integer.parseInt(arr[0].substring(1));
            String op = arr[2];
            String number1 = arr[4];
            String number2 = arr[5];
            int num1 = 0;
            int num2 = 0;
            if (number1.charAt(0) == '%' && number2.charAt(0) == '%'){
                num1 = value[Integer.parseInt(number1.substring(1,number1.length()-1))];
                num2 = value[Integer.parseInt(number2.substring(1))];
            }
            else if (number1.charAt(0) == '%' && number2.charAt(0) != '%'){
                num1 = value[Integer.parseInt(number1.substring(1,number1.length()-1))];
                num2 = Integer.parseInt(number2);
            }
            else if (number1.charAt(0) != '%' && number2.charAt(0) == '%'){
                num1 = Integer.parseInt(number1.substring(0,number1.length()-1));
                num2 = value[Integer.parseInt(number2.substring(1))];
            }
            else {
                num1 = Integer.parseInt(number1.substring(0,number1.length()-1));
                num2 = Integer.parseInt(number2);
            }
            if (op.equals("add")){
                value[regNum] = num1 + num2;
            }
            else if (op.equals("mul")){
                value[regNum] = num1 * num2;
            }
            else if (op.equals("sub")){
                value[regNum] = num1 - num2;
            }
            else if (op.equals("sdiv")){
                value[regNum] = num1 / num2;
            }
            else if (op.equals("srem")){
                value[regNum] = num1 % num2;
            }
            if (i == outputList.size()-1){
                finalValue = value[regNum];
            }
        }
        outputList.clear();
        return finalValue;
    }
    @Override
    public Object visitCompUnit(MiniSysParser.CompUnitContext ctx) {
        System.out.println("now visit compunit");
        //first compunit
        if (ctx.getParent() == null){
            initFunctionMap();

            if (ctx.compUnit() != null){
                visit(ctx.compUnit());
            }

            if (ctx.funcDef() != null){
                visit(ctx.funcDef());
            }
            if (ctx.decl() != null){
                visit(ctx.decl());
            }

            if (functionHashMap.get("main") == null){
                System.out.println("main had not been defined");
                System.exit(-1);
            }
            outputListToString();
            System.out.println("now exit");
        }
        else {
            if (ctx.compUnit() != null){
                visit(ctx.compUnit());
            }
            if (ctx.funcDef() != null){
                isDefineFunc = true;
                visit(ctx.funcDef());
                isDefineFunc = false;
            }
            if (ctx.decl() != null){
                isDefineGlobalVariable = true;
                visit(ctx.decl());
                isDefineGlobalVariable = false;
            }
        }
        //exit

        return null;
    }

    public void outputListToString(){
        outputListString = new String();
        for (String s : globalDeclareList){
            outputListString += s;
        }
        for (String s : functionOutputList){
            outputListString += s;
        }
        for (String s : outputList){
            outputListString += s;
        }
    }

    @Override
    public Object visitDecl(MiniSysParser.DeclContext ctx) {
        System.out.println("now visit decl. decl.text is " + ctx.getText());
        //constDecl
        if(ctx.constDecl() != null){
            super.visitDecl(ctx);
        }
        // varDecl
        else if (ctx.varDecl() != null){
            super.visitDecl(ctx);
        }
        return null;
    }

    @Override
    public Object visitConstDecl(MiniSysParser.ConstDeclContext ctx) {
        System.out.println("now visit constDecl. constDecl.text is " + ctx.getText());
        bType = ctx.bType().getText();
        super.visitConstDecl(ctx); // visit varDef List
        return null;
    }

    @Override
    public Object visitBType(MiniSysParser.BTypeContext ctx) {
        return super.visitBType(ctx);
    }

    @Override
    public Object visitConstDef(MiniSysParser.ConstDefContext ctx) {
        if (isDefineGlobalVariable){
            Variable variable = variableHashMap_global.get(ctx.ident().getText());
            if (variable != null){
                System.out.println("global variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            variable.setiType(bType);
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber("@" + variable.getVarName());
            variable.setConst(true);
            variable.setGlobal(true);
            variable.setBlock(-1);
            //define global array
            if (ctx.children.size()>3){
                variable.setArray(true);
                isDefineArray = true;
                int dimensions = 0;
                int allLength = 1;
                for (int i = 0 ; i< ctx.constExp().size() ; i++){
                    dimensions ++;
                    visit(ctx.constExp(i));
                    int dimensionLength = operationNumber_array;
                    if (dimensionLength < 0){
                        System.out.println("array dimension length < 0");
                        System.exit(-1);
                    }
                    variable.getArrayDimensions().add(dimensionLength);
                    allLength = allLength * dimensionLength;
                }
                variable.setDimensions(dimensions);
                variable.setAllLength(allLength);
                variable.computeArrayDimensionWeight();
                variable.initArrayValue();
                isDefineArray = false;

                // then initval
                if (ctx.constInitVal() != null){
                    nowDimension = 0;
                    nowArrayVariable = variable;
                    offsets.add(0);
                    isDefineArray = true;
                    visit(ctx.constInitVal());
                    isDefineArray = false;
                    offsets.clear();
                    nowDimension = 0;
                    nowArrayVariable = null;
                    globalDeclareList.add("@" + variable.getVarName() + " = dso_local constant [" + allLength + " x i32] " + variable.getArrayValueString() + System.lineSeparator());
                }
                else {
                    //do nothing
                    globalDeclareList.add("@" + variable.getVarName() + " = dso_local constant [" + allLength + " x i32] zeroinitializer");
                }

            }

            else {
                if (ctx.constInitVal() != null){
                    visit(ctx.constInitVal());
//                    if (operationNumber.charAt(0) == '%'){
//                        variable.setValue(computeGlobalVariableValue());
//                    }
//                    else{
                        variable.setValue(operationNumber_global);
//                    }
                }
                else {
                    variable.setValue(0);
                }
            }

            variableHashMap_global.put(variable.getVarName() , variable);
        }
        else {
            Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(ctx.ident().getText());
            if (variable != null && variable.getBlock() == nowBlock){
                System.out.println("variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            registerNum++;
            operationNumber = "%" + registerNum;
            //bType : variable type
            variable.setiType(bType);
            //set regName = ident
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber(operationNumber);
            variable.setConst(true);
            variable.setGlobal(false);
            variable.setBlock(nowBlock);

            //define array , the num of constExp equal to the dimensions
            if (ctx.children.size() > 3){
                variable.setArray(true);
                isDefineArray = true;
                int dimensions = 0;
                int allLength = 1;
                for (int i = 0 ; i< ctx.constExp().size() ; i++){
                    dimensions ++;
                    visit(ctx.constExp(i));
                    int dimensionLength = operationNumber_array;
                    if (dimensionLength < 0){
                        System.out.println("array dimension < 0");
                        System.exit(-1);
                    }
                    variable.getArrayDimensions().add(dimensionLength);
                    allLength = allLength * dimensionLength;
                }
                variable.setDimensions(dimensions);
                variable.setAllLength(allLength);
                variable.computeArrayDimensionWeight();
                isDefineArray = false;
//                outputList.add(variable.allocaArrayString());
                outputList.add(operationNumber + " = alloca [" + allLength + " x i32]" + System.lineSeparator());

                //first, get ptr
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(variable.getArrayElementPtrByOffsets(operationNumber, new ArrayList<>()));
//                outputList.add(operationNumber + " = getelementptr " + variable.getArrayString() + ", " + variable.getArrayString() + "* " + variable.getOperationNumber() + ", i32 0, i32 " + "0" + System.lineSeparator());
                outputList.add("call void @memset(i32* " + operationNumber + ", i32 0, i32 " + (4*variable.getAllLength()) + ")" + System.lineSeparator());

                // then initval
                if (ctx.constInitVal() != null){
                    nowDimension = 0;
                    nowArrayVariable = variable;
                    offsets.add(0);
                    visit(ctx.constInitVal());
                    offsets.clear();
                    nowDimension = 0;
                    nowArrayVariable = null;
                }
                else {
                    //do nothing
                }

            }
            else {
                outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
                // define name and value,that is,  constDef : ident = constInitVal
                if (ctx.children.size() == 3){
                    visit(ctx.constInitVal());
                    outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());
                    variable.setValue(Integer.parseInt(operationNumber));
                }
            }
            //need to store local hashmap
            blockArrayList.get(nowBlock).getVariableHashMap().put(variable.getVarName(), variable);
        }
        return null;
    }

    @Override
    public Object visitConstInitVal(MiniSysParser.ConstInitValContext ctx) {
        //only exp
        if(ctx.constExp() != null){
            super.visitConstInitVal(ctx);
        }
        //define array
        else {
            int dimensionTmp = nowDimension;
            for (int i = 0 ; i<ctx.constInitVal().size() ; i++){
                if (ctx.constInitVal(i).constInitVal().size() > 0){
                    if (offsets.size() <= nowDimension+1){
                        offsets.add(0);
                    }
                    else {
                        offsets.set(nowDimension,i);
                    }
                    nowDimension ++;
                    visit(ctx.constInitVal(i));
                    nowDimension = dimensionTmp;
                }
                else {
                    offsets.set(nowDimension,i);
                    visit(ctx.constInitVal(i).constExp());
                    if (isDefineGlobalVariable){
//                        System.out.println("++++++++++++++++++++++++++++" + offsets + " dimension :" + nowDimension + " offset : " + i + " value :" + operationNumber_array);
                        int numTmp = operationNumber_array;
                        nowArrayVariable.setArrayValue(numTmp,offsets);
                    }
                    else {
                        String opNumTmp = operationNumber;
                        registerNum ++ ;
                        operationNumber = "%" + registerNum;
                        outputList.add(nowArrayVariable.getArrayElementPtrByOffsets(operationNumber,offsets));
                        outputList.add("store i32 " + opNumTmp + ", i32* " + operationNumber + System.lineSeparator());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object visitVarDecl(MiniSysParser.VarDeclContext ctx) {
        System.out.println("now visit varDecl. varDecl.text is " + ctx.getText());
        bType = ctx.bType().getText();
        super.visitVarDecl(ctx); // visit varDef List
        return null;
    }

    @Override
    public Object visitVarDef(MiniSysParser.VarDefContext ctx) {
        if (isDefineGlobalVariable){
            Variable variable = variableHashMap_global.get(ctx.ident().getText());
            if (variable != null){
                System.out.println("global variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            variable.setiType(bType);
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber("@" + variable.getVarName());
            variable.setConst(false);
            variable.setGlobal(true);
            variable.setBlock(-1);
            //define global array
            if (ctx.children.size()>3){
                variable.setArray(true);
                isDefineArray = true;
                int dimensions = 0;
                int allLength = 1;
                for (int i = 0 ; i< ctx.constExp().size() ; i++){
                    dimensions ++;
                    visit(ctx.constExp(i));
                    int dimensionLength = operationNumber_array;
                    if (dimensionLength < 0){
                        System.out.println("array dimension length < 0");
                        System.exit(-1);
                    }
                    variable.getArrayDimensions().add(dimensionLength);
                    allLength = allLength * dimensionLength;
                }
                variable.setDimensions(dimensions);
                variable.setAllLength(allLength);
                variable.computeArrayDimensionWeight();
                variable.initArrayValue();
                isDefineArray = false;

                // then initval
                if (ctx.initVal() != null){
                    nowDimension = 0;
                    nowArrayVariable = variable;
                    offsets.add(0);
                    isDefineArray = true;
                    visit(ctx.initVal());
                    isDefineArray = false;
                    offsets.clear();
                    nowDimension = 0;
                    nowArrayVariable = null;
                    globalDeclareList.add("@" + variable.getVarName() + " = dso_local global [" + allLength + " x i32] " + variable.getArrayValueString() + System.lineSeparator());
                }
                else {
                    //do nothing
                    globalDeclareList.add("@" + variable.getVarName() + " = dso_local global [" + allLength + " x i32] zeroinitializer" + System.lineSeparator());
                }

            }
            //define global var but not array
            else {
                if (ctx.initVal() != null){
                    visit(ctx.initVal());
//                    if (operationNumber.charAt(0) == '%'){
//                        variable.setValue(computeGlobalVariableValue());
//                    }
//                    else{
                        variable.setValue(operationNumber_global);
//                    }
                }
                else {
                    variable.setValue(0);
                }
                globalDeclareList.add("@" + variable.getVarName() + " = dso_local global " + variable.getiType() + " " + variable.getValue() + System.lineSeparator());
            }
            variableHashMap_global.put(variable.getVarName() , variable);
        }
        else {
            Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(ctx.ident().getText());
            if (variable != null && variable.getBlock() == nowBlock){
                System.out.println("variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            registerNum++;
            operationNumber = "%" + registerNum;
            //bType : variable type
            variable.setiType(bType);
            //set regName = ident
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber(operationNumber);
            variable.setConst(false);
            variable.setGlobal(false);
            variable.setBlock(nowBlock);
            System.out.println("now block is " + variable.getBlock() + " variable " + variable.getVarName() + " belong to block " + variable.getBlock());

            //define array , the num of constExp equal to the dimensions
            if (ctx.children.size() > 3){
                variable.setArray(true);
                isDefineArray = true;
                int dimensions = 0;
                int allLength = 1;
                for (int i = 0 ; i< ctx.constExp().size() ; i++){
                    dimensions ++;
                    visit(ctx.constExp(i));
                    int dimensionLength = operationNumber_array;
                    if (dimensionLength < 0){
                        System.out.println("array dimension < 0");
                        System.exit(-1);
                    }
                    variable.getArrayDimensions().add(dimensionLength);
                    allLength = allLength * dimensionLength;
                }
                variable.setDimensions(dimensions);
                variable.setAllLength(allLength);
                variable.computeArrayDimensionWeight();
                isDefineArray = false;

                outputList.add(operationNumber + " = alloca [" + allLength + " x i32]" + System.lineSeparator());

                //first, get ptr
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(variable.getArrayElementPtrByOffsets(operationNumber, new ArrayList<>()));
//                outputList.add(operationNumber + " = getelementptr " + variable.getArrayString() + ", " + variable.getArrayString() + "* " + variable.getOperationNumber() + ", i32 0, i32 " + "0" + System.lineSeparator());
                outputList.add("call void @memset(i32* " + operationNumber + ", i32 0, i32 " + (4*variable.getAllLength()) + ")" + System.lineSeparator());

                // then initval
                if (ctx.initVal() != null){
                    nowDimension = 0;
                    nowArrayVariable = variable;
                    offsets.add(0);
                    visit(ctx.initVal());
                    offsets.clear();
                    nowDimension = 0;
                    nowArrayVariable = null;
                }
                else {
                    //do nothing
                }

            }

            // don't define array
            else {
                outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
                // define name and value,that is,  constDef : ident = constInitVal
                if (ctx.children.size() == 3){
                    visit(ctx.initVal());
                    outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());
                }
            }
            //need to store local hashmap
            blockArrayList.get(nowBlock).getVariableHashMap().put(variable.getVarName(), variable);
        }
        return null;
    }

    @Override
    public Object visitInitVal(MiniSysParser.InitValContext ctx) {
        //only exp
        if(ctx.exp() != null){
            super.visitInitVal(ctx);
        }
        //define array
        else {
            int dimensionTmp = nowDimension;
            for (int i = 0 ; i<ctx.initVal().size() ; i++){
                if (ctx.initVal(i).initVal().size() > 0){
                    if (offsets.size() <= nowDimension+1){
                        offsets.add(0);
                    }
                    else {
                        offsets.set(nowDimension,i);
                    }
                    nowDimension ++;
                    visit(ctx.initVal(i));
                    nowDimension = dimensionTmp;
                }
                else {
//                    System.exit(-1);
                    offsets.set(nowDimension,i);
                    System.out.println("++++++++++++++++++++++++++++" + offsets + " dimension :" + nowDimension + " offset : " + i);
                    visit(ctx.initVal(i).exp());
                    if (isDefineGlobalVariable){
                        int numTmp = operationNumber_array;
                        nowArrayVariable.setArrayValue(numTmp,offsets);
                    }
                    else {
                        String opNumTmp = operationNumber;
                        registerNum ++ ;
                        operationNumber = "%" + registerNum;
                        outputList.add(nowArrayVariable.getArrayElementPtrByOffsets(operationNumber,offsets));
                        outputList.add("store i32 " + opNumTmp + ", i32* " + operationNumber + System.lineSeparator());
                    }

                }
            }
        }
        return null;
    }

    @Override
    public Object visitFuncType(MiniSysParser.FuncTypeContext ctx) {
        System.out.println("now visit functype. "+ "functype.text is " + ctx.getText());
        return super.visitFuncType(ctx);
    }

    @Override
    public Object visitFuncFParams(MiniSysParser.FuncFParamsContext ctx) {
        int size = ctx.funcFParam().size();
        for (int i = 0; i< size; i++){
            bType = ctx.funcFParam(i).bType().getText();
            String paramName = ctx.funcFParam(i).ident().getText();
            registerNum ++;
            operationNumber = "%" + registerNum;
            Variable variable = new Variable();
            variable.setFuncParam(true);
            variable.setVarName(paramName);
            variable.setOperationNumber(operationNumber);
            //array
            if (ctx.funcFParam(i).children.size() > 2){
                variable.setArray(true);
                variable.setiType("i32*");
                nowFunction.getParamsType().add("i32*");
                nowFunction.getParamsName().add(paramName);
                //exp num + 1 equal to dimensions
                int expNum = ctx.funcFParam(i).exp().size();
                variable.setDimensions(expNum+1);
                variable.getArrayDimensions().add(999);
                isDefineArray = true;
                for (int j = 0;j<expNum;j++){
                    visit(ctx.funcFParam(i).exp(j));
                    variable.getArrayDimensions().add(operationNumber_array);
                }
                isDefineArray = false;
                variable.computeArrayDimensionWeight();
            }
            //int
            else {
                variable.setiType("i32");
                nowFunction.getParamsType().add("i32");
                nowFunction.getParamsName().add(paramName);
            }
            nowFunction.getFunctionParams().put(paramName,variable);
        }
        return null;
    }

    @Override
    public Object visitFuncFParam(MiniSysParser.FuncFParamContext ctx) {
        return super.visitFuncFParam(ctx);
    }

    @Override
    public Object visitBlock(MiniSysParser.BlockContext ctx) {
        System.out.println("now visit block" + ". nowblock is  " + (blockNum+1) + " fatherblock is " + nowBlock);
        //the first block
        int fatherBlock = nowBlock;
        blockNum ++;
        nowBlock = blockNum;
        Block block = new Block(nowBlock,fatherBlock);
        if (isFuncDefBlock){
            isFuncDefBlock = false;
            //copy global variables
            block.getVariableHashMap().putAll(variableHashMap_global);
            if (isDefineFunc){
                block.getVariableHashMap().putAll(nowFunction.getFunctionParams());
            }
        }
        else {
            //copy father block 's variables
            block.getVariableHashMap().putAll(blockArrayList.get(fatherBlock).getVariableHashMap());
        }
        blockArrayList.add(block);
        super.visitBlock(ctx);
        nowBlock = fatherBlock;



        return null;
    }

    @Override
    public Object visitBlockItem(MiniSysParser.BlockItemContext ctx) {
        System.out.println("now visit blockitem. blockitem.text is " + ctx.getText());
        //decl
        if(ctx.decl() != null){
            super.visitBlockItem(ctx);
        }
        //stmt
        else if(ctx.stmt() != null){
            super.visitBlockItem(ctx);
        }
        return null;
    }

    @Override
    public Object visitStmt(MiniSysParser.StmtContext ctx) {
        System.out.println("now visit stmt. stmt text is : "+ ctx.getText());
        String child0 = ctx.getChild(0).getText();
        //return exp ;
        if(child0.equals("return")){
            if (nowFunction.getRetType().equals("void")){
                if (ctx.exp() != null){
                    System.out.println("function retType is void but has return exp");
                    System.exit(-1);
                }
                else {
                    hasReturn = true;
                    outputList.add("ret " + nowFunction.getRetType() + System.lineSeparator());
                }
            }
            else {
                visit(ctx.exp());
                outputList.add("ret " + nowFunction.getRetType() + " " + operationNumber + System.lineSeparator());
                hasReturn = true;
                isReturn = true;
            }
        }
        //exp;
        else if (ctx.lVal()==null && ctx.exp() != null ){
            super.visitStmt(ctx);
        }
        //lval = exp;
        else if (ctx.lVal() != null){
            String regName = ctx.lVal().ident().getText();
            //judge whther regName had benn defined and isConst = false
            Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(regName);
            if (variable != null){
                //local
                visit(ctx.exp());
                String opNumTmp = operationNumber;
                if (!variable.isConst()){
                    if (variable.isArray()){
                        if (ctx.lVal().exp().size() != variable.getDimensions()){
                            System.out.println("array [] is not equal to dimensions");
                            System.exit(-1);
                        }
                        //first, getelement ptr
                        getPtrFromArray(ctx.lVal(),variable);
                        //then store
                        if (variable.isArray()){
                            outputList.add("store i32 " + opNumTmp + ", " + "i32* " + operationNumber + System.lineSeparator());
                        }
                        else {
                            outputList.add("store i32 " + opNumTmp + ", " + "i32 " + operationNumber + System.lineSeparator());
                        }

                    }
                    else {

                        if (variable.isGlobal()){
                            outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* @" + variable.getVarName() + System.lineSeparator());
                        }
                        else {
                            outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
                        }
                    }


                }
                else {
                    System.out.println("stmt : lval is const");
                    System.exit(-1);
                }
            }
            else {
                System.out.println("stmt : lval had not been defined");
                System.exit(-1);
            }
        }
        // if ( cond ) stmt else stmt
        else if (child0.equals("if")){
            boolean justIf = ctx.children.size() == 5;
            StackElem stackElem1 = new StackElem();
            ArrayList<Integer> integers1 = new ArrayList<>();
            callbackStack.push(integers1);
            retStack.push(callbackStack.size()-1);
            visit(ctx.cond());
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            ArrayList<Integer> integersTmp = callbackStack.pop();
            replaceTrueLabel(outputList,integersTmp,registerNum);
            isReturn = false;
            isContinue = false;
            isBreak = false;
            visit(ctx.stmt(0));
            //don't have "ret"
            if (isBreak || isContinue){
                if (isBreak){
                    isBreak = false;
                }
                if (isContinue){
                    isContinue = false;
                }
            }
            else if (!isReturn){
                outputList.add("br label %");
                int retIndex = retStack.pop();
                ArrayList<Integer> integers2 = callbackStack.get(retIndex);
                integers2.add(outputList.size()-1);
                callbackStack.set(retIndex,integers2);
            }
            //just if go out
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            integersTmp = callbackStack.pop();
            if (justIf){
                for (Integer index : integersTmp){
                    outputList.set(index, outputList.get(index) + ", label %" + registerNum + System.lineSeparator());
                }
                registerNum++;
                operationNumber = "%" + registerNum;
                outputList.add("br label %" + registerNum + System.lineSeparator());
                integersTmp = callbackStack.pop();
                for (Integer index : integersTmp){
                    outputList.set(index, outputList.get(index) + registerNum + System.lineSeparator());
                }
                outputList.add(System.lineSeparator() + registerNum + ":"+ System.lineSeparator());
            }
            // if else need to visit else
            else {
                for (Integer index : integersTmp){
                    outputList.set(index, outputList.get(index) + ", label %" + registerNum + System.lineSeparator());
                }

                isReturn = false;
                isContinue = false;
                isBreak = false;

                visit(ctx.stmt(1));

                registerNum++;
                operationNumber = "%" + registerNum;
                if (isBreak || isContinue){
                    if (isBreak){
                        isBreak = false;
                    }
                    if (isContinue){
                        isContinue = false;
                    }
                }
                else {
//                    if (registerNum == 38)System.exit(-1);
                    outputList.add("br label %" + registerNum + System.lineSeparator());
                }
                outputList.add(System.lineSeparator() + registerNum + ":"+ System.lineSeparator());
//                registerNum++;
//                operationNumber = "%" + registerNum;
//                System.out.println(callbackStack.size());

                integersTmp = callbackStack.pop();
                for (Integer index : integersTmp){
                    outputList.set(index, outputList.get(index) + registerNum + System.lineSeparator());
                }

                if (isReturn){

                    outputList.add("br label %" + registerNum + System.lineSeparator());
                    isReturn = false;
                }
            }
        }
        //block
        else if (ctx.block() != null){
            visit(ctx.block());
        }
        //while ( cond ) stmt
        else if (child0.equals("while")){
            //store the break and while info
            ArrayList<Integer> newBreakList = new ArrayList<>(0);
            ArrayList<Integer> newContinueList = new ArrayList<>(0);
            newBreakList.addAll(breakList);
            newContinueList.addAll(continueList);
            breakList.clear();
            continueList.clear();

            //br while cond label block
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add("br label %" + registerNum + System.lineSeparator());

            int enterWhileBlockNum = registerNum; //store the regNum when enter while
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());

            visit(ctx.cond());

            //br while stmt label block
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            ArrayList<Integer> integersTmp = callbackStack.pop();
            replaceTrueLabel(outputList,integersTmp,registerNum);

            visit(ctx.stmt(0));

            registerNum++;
            operationNumber = "%" + registerNum;

            integersTmp = callbackStack.pop();
            for (Integer index : integersTmp){
                outputList.set(index, outputList.get(index) + ", label %" + registerNum + System.lineSeparator());
            }
            if (breakList.size() > 0){
                for (Integer index : breakList){
                    System.out.println("break : index is " + index + " " + outputList.get(index) + " " + registerNum);
                    System.out.println(" break list" + breakList);
                    outputList.set(index, outputList.get(index) + registerNum + System.lineSeparator());
                }
            }
            if (continueList.size()>0){
                for (Integer index : continueList){
                    System.out.println("continue : index is " + index + " " + outputList.get(index) + " " + registerNum);
                    System.out.println(" continue list" + continueList);
                    outputList.set(index, outputList.get(index) + enterWhileBlockNum + System.lineSeparator());
                }
            }
            //return while cond when no break or continue
//            else if (breakList.size() == 0 && continueList.size() == 0){
            if (!isContinue){
                outputList.add("br label %" + enterWhileBlockNum + System.lineSeparator());
            }
//            }
            //go out of while

            breakList.clear();
            continueList.clear();
            breakList.addAll(newBreakList);
            continueList.addAll(newContinueList);

            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
        }
        else if (child0.equals("continue")){
            outputList.add("br label %");
            continueList.add(outputList.size() - 1);
            isContinue = true;
        }
        else if (child0.equals("break")){
            outputList.add("br label %");
            breakList.add(outputList.size() - 1);
            isBreak = true;
        }
        else {
            super.visitStmt(ctx);
        }
        return null;
    }
    public void printBlockLabel(int regNum){
        outputList.add(System.lineSeparator() + regNum + ":" + System.lineSeparator());
    }
    @Override
    public Object visitExp(MiniSysParser.ExpContext ctx) {
        System.out.println("now visit exp");
        return super.visitExp(ctx);
    }

    @Override
    public Object visitCond(MiniSysParser.CondContext ctx) {
        return super.visitCond(ctx);
    }

    @Override
    public Object visitLVal(MiniSysParser.LValContext ctx) {
        return super.visitLVal(ctx);
    }

    @Override
    public Object visitPrimaryExp(MiniSysParser.PrimaryExpContext ctx) {
        System.out.println("now visit primaryexp. primaryexp text is : " + ctx.getText() );
        //primary : ( exp )
        if(ctx.exp() != null){
            visit(ctx.exp());
        }
        //primary : number
        else if(ctx.Number() != null){
            int number;
            String numberString = String.valueOf(ctx.Number());
            //hex or oct
            if(numberString.charAt(0) == '0'&& numberString.length()>1){
                //hex
                if(numberString.charAt(1) == 'x' || numberString.charAt(1) == 'X'){
                    number = Integer.parseInt(numberString.substring(2),16);
                }
                //oct
                else {
                    number = Integer.parseInt(numberString.substring(1),8);
                }
            }
            //dec
            else {
                number = Integer.parseInt(numberString);
            }
            if (isDefineArray){
                operationNumber_array = number;
            }
            else if (isDefineGlobalVariable){
                operationNumber_global = number;
            }
            else {
                operationNumber = String.valueOf(number);
            }

        }
        //primary : lval
        else if (ctx.lVal()!=null){
            // the lval was in exp
            //need to judge whether lval had been defined
            //lval : ident
            if (isDefineGlobalVariable){
                // TODO if variable is array
                Variable variable = variableHashMap_global.get(ctx.lVal().ident().getText());
                if (variable != null){
                    if (!variable.isConst()){
                        System.out.println("global var is not const");
                        System.exit(-1);
                    }
                    if (isDefineArray){
                        operationNumber_array = variable.getValue();
                    }
                    else {
                        operationNumber_global = variable.getValue();
                    }
                }
                else {
                    System.out.println("var has not been defined");
                    System.exit(-1);
                }
            }
            //  define local array
            else if (isDefineArray){
                Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(ctx.lVal().ident().getText());
                if (variable != null){
                    if (!variable.isConst()){
                        System.out.println(" var is not const");
                        System.exit(-1);
                    }
                    // TODO if variable is array
                    operationNumber_array = variable.getValue();
                }
                else {
                    System.out.println("var has not been defined");
                    System.exit(-1);
                }
            }
            // don't define anything
            else {
                Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(ctx.lVal().ident().getText());
                if(variable != null){
                    // if is array
                    if (variable.isArray()){
                        isRegisterPtr = false;
                        if (variable.isGlobal()){
                            loadNumFromArray(ctx.lVal(),variable);
                        }
                        else {
                            loadNumFromArray(ctx.lVal(),variable);
                        }
                    }
                    // is not array
                    else {
                        isRegisterPtr = false;
                        if (variable.isGlobal()){
                            if (variable.isConst()){
                                int value = variable.getValue();
                                operationNumber = String.valueOf(value);
                            }
                            else {
                                registerNum ++;
                                operationNumber = "%" + registerNum;
                                outputList.add(operationNumber + " = load i32, " + variable.getiType() + "* @" + variable.getVarName() + System.lineSeparator());
                            }
                        }
                        else {
                            registerNum ++;
                            operationNumber = "%" + registerNum;
                            outputList.add(operationNumber + " = load i32, " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
                        }
                    }


                }
                else {
                    System.out.println("primary : lval has not been defined");
                    System.exit(-1);
                }
            }
        }
        else {
            super.visitPrimaryExp(ctx);
        }

        return null;
    }

    public void getPtrFromArray(MiniSysParser.LValContext ctx , Variable variable){
        if (ctx.exp() != null){
            ArrayList<String> opNumTmpList = new ArrayList<>();
            for (int i = 0; i<ctx.exp().size() ; i++){
                visit(ctx.exp(i));
                opNumTmpList.add(operationNumber);
            }
            while (opNumTmpList.size() != variable.getDimensions()){
                opNumTmpList.add("0");
//                System.out.println("when lval is array, the [] num is not equal to dimensions");
//                System.exit(-1);
            }
            for (int i = 0; i<opNumTmpList.size() ; i++){
                registerNum ++ ;
                operationNumber = "%" + registerNum;
                if ( i == 0){
                    outputList.add(operationNumber + " = mul i32 " + opNumTmpList.get(i) + ", " + variable.getArrayDimensionsWeight().get(i) + System.lineSeparator());
                }
                else {
                    outputList.add(operationNumber + " = mul i32 " + opNumTmpList.get(i) + ", " + variable.getArrayDimensionsWeight().get(i) + System.lineSeparator());
                    registerNum ++;
                    operationNumber = "%" + registerNum;
                    outputList.add(operationNumber + " = add i32 %" + (registerNum-1) + ", %" + (registerNum-2) + System.lineSeparator());
                }
            }
            registerNum ++;
            operationNumber = "%" + registerNum;
            outputList.add(variable.getArrayElementPtrByRegister(operationNumber,"%" + (registerNum-1)));
        }
        else {
            registerNum ++ ;
            operationNumber = "%" + registerNum;
            outputList.add(variable.getArrayElementPtrByOffsets(operationNumber, new ArrayList<>()));
//            System.out.println("variable is array , but dont have a[]");
//            System.exit(-1);
        }
    }

    public void loadNumFromArray(MiniSysParser.LValContext ctx , Variable variable){
        if (ctx.exp() != null){
            ArrayList<String> opNumTmpList = new ArrayList<>();
            for (int i = 0; i<ctx.exp().size() ; i++){
                visit(ctx.exp(i));
                opNumTmpList.add(operationNumber);
            }
            while (opNumTmpList.size() != variable.getDimensions()){
                isRegisterPtr = true;
                opNumTmpList.add("0");
//                System.out.println("when lval is array, the [] num is not equal to dimensions");
//                System.exit(-1);
            }
            for (int i = 0; i<opNumTmpList.size() ; i++){
                registerNum ++ ;
                operationNumber = "%" + registerNum;
                if ( i == 0){
                    outputList.add(operationNumber + " = mul i32 " + opNumTmpList.get(i) + ", " + variable.getArrayDimensionsWeight().get(i) + System.lineSeparator());
                }
                else {
                    outputList.add(operationNumber + " = mul i32 " + opNumTmpList.get(i) + ", " + variable.getArrayDimensionsWeight().get(i) + System.lineSeparator());
                    registerNum ++;
                    operationNumber = "%" + registerNum;
                    outputList.add(operationNumber + " = add i32 %" + (registerNum-1) + ", %" + (registerNum-2) + System.lineSeparator());
                }
            }

            registerNum ++;
            operationNumber = "%" + registerNum;
            outputList.add(variable.getArrayElementPtrByRegister(operationNumber,"%" + (registerNum-1)));
            if (!variable.isFuncParam() && !isRegisterPtr){
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(operationNumber + " = load i32, i32* %" + (registerNum-1) + System.lineSeparator());
            }
        }
        else {
            isRegisterPtr = true;
            registerNum ++ ;
            operationNumber = "%" + registerNum;
            outputList.add(variable.getArrayElementPtrByOffsets(operationNumber, new ArrayList<>()));
//            System.out.println("variable is array , but dont have a[]");
//            System.exit(-1);
        }
    }

    @Override
    public Object visitUnaryExp(MiniSysParser.UnaryExpContext ctx) {
        System.out.println("now visit unaryexp");
        //primaryExp
        if(ctx.primaryExp() != null){
            super.visit(ctx.primaryExp());
        }
        //unaryOp unaryExp
        else if(ctx.unaryOp() != null){
            visit(ctx.unaryExp());
            if(ctx.unaryOp().getText().equals("-")){
                if (isDefineArray){
                    operationNumber_array = -operationNumber_array;
                }
                else if (isDefineGlobalVariable){
                    operationNumber_global = -operationNumber_global;
                }
                else {
                    registerNum++;
                    outputList.add("%" + registerNum + " = sub " + "i32 " + "0" + ", "+operationNumber + System.lineSeparator());
                    operationNumber = "%" + registerNum;
                }

            }
            //need update
            else if (ctx.unaryOp().getText().equals("!")){
                registerNum++;
                outputList.add("%" + registerNum + " = icmp eq i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
                registerNum++;
                outputList.add("%" + registerNum + " = zext i1 " + operationNumber + " to i32" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
        }
        // ident ( [funcRParams] )
        else if (ctx.ident() != null){
            String funcName = ctx.ident().getText();
            //judge whether function had been defined
            Function function = functionHashMap.get(funcName);
            if (function != null){
                //judge whether params is legal
                // no paramas: getint getch
                if(ctx.funcRParams() == null ){
                    if (function.getParamsType().size() == 0){
                        //if had not been declared before, declare it
                        if(function.isDeclare() == false){
                            function.setDeclare(true);
                        }
                        registerNum ++ ;
                        operationNumber = "%" + registerNum;
                        outputList.add(operationNumber + " = call " + function.getRetType() + " @" + function.getFuncName() + "()" + System.lineSeparator());
                    }
                    else {
                        System.out.println("function params is not legal");
                        System.exit(-1);
                    }
                }
                //here dont think about putarray and get array
                else {
                    int size = ctx.funcRParams().exp().size();
                    if (size != function.getParamsType().size()){
                        System.out.println("call function params num is not equal to need function");
                        System.exit(-1);
                    }
                    ArrayList<String> opNumTmpList = new ArrayList<>();
                    boolean paramsLegal;
                    for (int i = 0; i<size;i++){
                        isRegisterPtr = false;
                        visit(ctx.funcRParams().exp(i));
                        if (isRegisterPtr){
                            paramsLegal = function.checkParamsType("i32*",i);
                        }
                        else {
                            paramsLegal = function.checkParamsType("i32",i);
                        }
                        if (paramsLegal){
                             opNumTmpList.add(operationNumber);
                        }
                        else {
                            System.out.println("function params is not legal");
                            System.exit(-1);
                        }
                    }
                    outputList.add("call " + function.getRetType() + " @" + function.getFuncName() + "(");
                    for (int i = 0; i< size ;i++){
                        outputList.add(function.getParamsType().get(i) + " " + opNumTmpList.get(i));
                        if (i != size-1){
                            outputList.add(", ");
                        }
                    }
                    outputList.add(")" + System.lineSeparator());


                }
            }
            else {
                System.out.println("function had not benn defined");
                System.exit(-1);
            }
        }
        return null;
    }

    @Override
    public Object visitUnaryOp(MiniSysParser.UnaryOpContext ctx) {
        System.out.println("now visit unaryop. unaryop is : " + ctx.getText());
        return super.visitUnaryOp(ctx);
    }

    @Override
    public Object visitFuncRParams(MiniSysParser.FuncRParamsContext ctx) {
        return super.visitFuncRParams(ctx);
    }

    @Override
    public Object visitMulExp(MiniSysParser.MulExpContext ctx) {
        System.out.println("now visit mulexp");
        if(ctx.children.size() == 1){
            visit(ctx.unaryExp());
        }
        else if(ctx.children.size() == 3){
            if (isDefineArray){
                visit(ctx.mulExp());
                int num1 = operationNumber_array;
                visit(ctx.unaryExp());
                int num2 = operationNumber_array;
                String op = String.valueOf(ctx.getChild(1));
                if(op.equals("*")){
                    operationNumber_array = num1 * num2;
                }
                else if(op.equals("/")){
                    operationNumber_array = num1 / num2;
                }
                else if(op.equals("%")){
                    operationNumber_array = num1 % num2;
                }
            }
            else if (isDefineGlobalVariable){
                visit(ctx.mulExp());
                int num1 = operationNumber_global;
                visit(ctx.unaryExp());
                int num2 = operationNumber_global;
                String op = String.valueOf(ctx.getChild(1));
                if(op.equals("*")){
                    operationNumber_global = num1 * num2;
                }
                else if(op.equals("/")){
                    operationNumber_global = num1 / num2;
                }
                else if(op.equals("%")){
                    operationNumber_global = num1 % num2;
                }
            }
            else {
                String register1 = null;
                String register2 = null;
                visit(ctx.mulExp());
                register1 = operationNumber;
                visit(ctx.unaryExp());
                register2 = operationNumber;

                String op = String.valueOf(ctx.getChild(1));
                registerNum++;
                if(op.equals("*")){
                    outputList.add("%" + registerNum + " = mul " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
                }
                else if(op.equals("/")){
                    outputList.add("%" + registerNum + " = sdiv " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
                }
                else if(op.equals("%")){
                    outputList.add("%" + registerNum + " = srem " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
                }
                operationNumber = "%" + registerNum;
            }
        }
        else {
            super.visit(ctx);
        }
        return null;
    }

    @Override
    public Object visitAddExp(MiniSysParser.AddExpContext ctx) {
        System.out.println("now visit addexp");
        //mulexp
        if(ctx.children.size() == 1){
            visit(ctx.mulExp());
        }
        //addexp + - mulexp
        else if(ctx.children.size() == 3){
            if (isDefineArray){
                visit(ctx.addExp());
                int num1 = operationNumber_array;
                visit(ctx.mulExp());
                int num2 = operationNumber_array;
                String op = String.valueOf(ctx.getChild(1));
                if(op.equals("+")){
                   operationNumber_array = num1 + num2;
                }
                else if(op.equals("-")){
                    operationNumber_array = num1 - num2;
                }
            }
            else if (isDefineGlobalVariable){
                visit(ctx.addExp());
                int num1 = operationNumber_global;
                visit(ctx.mulExp());
                int num2 = operationNumber_global;
                String op = String.valueOf(ctx.getChild(1));
                if(op.equals("+")){
                    operationNumber_global = num1 + num2;
                }
                else if(op.equals("-")){
                    operationNumber_global = num1 - num2;
                }
            }
            else {
                String register1 = null;
                String register2 = null;
                visit(ctx.addExp());
                register1 = operationNumber;
                visit(ctx.mulExp());
                register2 = operationNumber;
                String op = String.valueOf(ctx.getChild(1));
                registerNum++;
                if(op.equals("+")){
                    outputList.add("%" + registerNum + " = add " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
                }
                else if(op.equals("-")){
                    outputList.add("%" + registerNum + " = sub " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
                }
                operationNumber = "%" + registerNum;
            }
        }
        else {
            super.visit(ctx);
        }
        return null;
    }

    @Override
    public Object visitRelExp(MiniSysParser.RelExpContext ctx) {
        System.out.println("now visit relExp");
        //addExp
        if(ctx.children.size() == 1){
            visit(ctx.addExp());
        }
        //relExp < > <= >= addExp
        else if(ctx.children.size() == 3){
            String register1 = null;
            String register2 = null;
            visit(ctx.relExp());
            register1 = operationNumber;
            visit(ctx.addExp());
            register2 = operationNumber;
            String op = String.valueOf(ctx.getChild(1));
            registerNum++;
            operationNumber = "%" + registerNum;
            if(op.equals("<")){
                outputList.add(operationNumber + " = icmp slt " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals(">")){
                outputList.add(operationNumber + " = icmp sgt " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals("<=")){
                outputList.add(operationNumber + " = icmp sle " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals(">=")){
                outputList.add(operationNumber + " = icmp sge " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
        }
        else {
            super.visit(ctx);
        }
        return null;
    }

    @Override
    public Object visitEqExp(MiniSysParser.EqExpContext ctx) {
        System.out.println("now visit eqExp");
        //relExp
        if(ctx.children.size() == 1){
            visit(ctx.relExp());
        }
        //eqExp == != relExp
        else if(ctx.children.size() == 3){
            String register1 = null;
            String register2 = null;
            visit(ctx.eqExp());
            register1 = operationNumber;
            visit(ctx.relExp());
            register2 = operationNumber;
            String op = String.valueOf(ctx.getChild(1));
            registerNum++;
            operationNumber = "%" + registerNum;
            if(op.equals("==")){
                outputList.add(operationNumber + " = icmp eq " + "i32 " +  register1 + ", " + register2 + System.lineSeparator());
            }
            else if(op.equals("!=")){
                outputList.add(operationNumber + " = icmp ne " + "i32 " +  register1 + ", " + register2 + System.lineSeparator());
            }
        }
        else {
            super.visit(ctx);
        }

        return null;
    }
    public void replaceTrueLabel(ArrayList<String> strings, ArrayList<Integer> indexs, int regNum){
        for (Integer index : indexs){
            if (strings.get(index).contains("trueLabel")){
                strings.set(index, strings.get(index).replace("trueLabel", "%" + regNum));
            }
            else {
                strings.set(index,strings.get(index) + ", label %" + regNum);
            }
        }
    }
    @Override
    public Object visitLAndExp(MiniSysParser.LAndExpContext ctx) {
        //land && eq
        if (ctx.lAndExp() != null){
            visit(ctx.lAndExp());
            registerNum ++ ;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            ArrayList<Integer> integersTmp = callbackStack.pop();
            replaceTrueLabel(outputList,integersTmp,registerNum);

            visit(ctx.eqExp());

            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputList.add("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            outputList.add("br i1 " + operationNumber);
            int lastIndex = outputList.size() -1 ;
            integersTmp = callbackStack.pop();
            integersTmp.add(lastIndex);
            callbackStack.push(integersTmp);
            integersTmp = new ArrayList<>();
            integersTmp.add(lastIndex);
            callbackStack.push(integersTmp);
        }
        else {
            visit(ctx.eqExp());
            //when don't have == != >= <= > <
            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputList.add("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            ArrayList<Integer> integersTmp = new ArrayList<>();

            outputList.add("br i1 " + operationNumber);
            int lastIndex = outputList.size() -1 ;
            integersTmp.add(lastIndex);

            callbackStack.push(integersTmp);
            System.out.println(callbackStack.size());
            integersTmp = new ArrayList<>();
            integersTmp.add(lastIndex);
            callbackStack.push(integersTmp);
            System.out.println(callbackStack.size());
        }
        return null;
    }

    @Override
    public Object visitLOrExp(MiniSysParser.LOrExpContext ctx) {
        // lor || land
        //
        if (ctx.lOrExp() != null){
            visit(ctx.lOrExp());
            registerNum ++;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            ArrayList<Integer> integersTmp = callbackStack.pop();
            ArrayList<Integer> integersTmp2 = callbackStack.pop();
            for (Integer index : integersTmp2){
                if (outputList.get(index).length() <= 16){
                    outputList.set(index, outputList.get(index) + ", label trueLabel, label %" + registerNum);
                }
                else {
                    outputList.set(index,outputList.get(index) + ",label %" + registerNum);
                }
            }

            visit(ctx.lAndExp());

            ArrayList<Integer> integersTmp3 = callbackStack.pop();
            integersTmp3.addAll(integersTmp);
            callbackStack.push(integersTmp3);
        }
        else {
            super.visitLOrExp(ctx);
        }
        return null;
    }

    @Override
    public Object visitConstExp(MiniSysParser.ConstExpContext ctx) {
        return super.visitConstExp(ctx);
    }

    @Override
    public Object visitIdent(MiniSysParser.IdentContext ctx) {
        System.out.println("now visit ident" + ". ident text is : " + ctx.getText());
        return super.visitIdent(ctx);
    }

    @Override
    public Object visitDigit(MiniSysParser.DigitContext ctx) {
        return super.visitDigit(ctx);
    }
}