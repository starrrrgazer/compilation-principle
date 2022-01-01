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
    //don't need anymore, variables are stored into blockArrayList
//    HashMap<String, Variable> variableHashMap_local = new HashMap<>(); //is used to store information about variable
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
    boolean defineGlobalVariable = true;
    ArrayList<Integer> breakList = new ArrayList<>();
    ArrayList<Integer> continueList = new ArrayList<>();
    boolean isBreak = false;
    boolean isContinue = false;

    public void initFunctionMap(){
        Function function = new Function("i32","getint",false,null,"declare i32 @getint()" );
        functionHashMap.put("getint",function);
        Function function2 = new Function("i32","getch",false,null,"declare i32 @getch()");
        functionHashMap.put("getch",function2);
        Function function3 = new Function("i32","getarray",false, new String[]{"i32*"},"declare i32 @getarray(i32*)");
        functionHashMap.put("getarray",function3);
        Function function4 = new Function("void","putint",false,new String[]{"i32"},"declare void @putint(i32)");
        functionHashMap.put("putint",function4);
        Function function5= new Function("void","putch",false,new String[]{"i32"},"declare void @putch(i32)");
        functionHashMap.put("putch",function5);
        Function function6 = new Function("void","putarray",false,new String[]{"i32","i32*"},"declare void @putarray(i32,i32*)");
        functionHashMap.put("putarray",function6);
        outputList.add("declare i32 @getint()" + System.lineSeparator());
        outputList.add("declare i32 @getch()" + System.lineSeparator());
        outputList.add("declare i32 @getarray(i32*)" + System.lineSeparator());
        outputList.add("declare void @putint(i32)" + System.lineSeparator());
        outputList.add("declare void @putch(i32)" + System.lineSeparator());
        outputList.add("declare void @putarray(i32,i32*)" + System.lineSeparator());
    }

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        outputList.add("define dso_local ");
        if (ctx.funcType().getText().equals("int")){
            retType = "i32 ";
            outputList.add("i32 ");
        }
        if (ctx.ident().getText().equals("main")){
            outputList.add("@main");
            outputList.add("(");
            //this should be a visit param
            outputList.add(")");
            outputList.add(" {" + System.lineSeparator());
            visit(ctx.block());
            outputList.add("}");
        }

        return null;
    }

    public void initAllGlobalVariables(){
        System.out.println(outputList);
        outputList = new ArrayList<>();
        registerNum = 0;
        nowFuncName = new String();
        operationNumber = null;
        isReturn = false ; // judge whether if return
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
            defineGlobalVariable = true;
            if (ctx.compUnit() != null){
                visit(ctx.compUnit());
            }
            defineGlobalVariable = false;
            initAllGlobalVariables();
            initFunctionMap();
            if (ctx.funcDef() != null){
                visit(ctx.funcDef());
            }
            if (ctx.decl() != null){
                visit(ctx.decl());
            }

            outputListToString();
            System.out.println("now exit");
        }
        else {
            super.visitCompUnit(ctx);
        }
        //exit

        return null;
    }

    public void outputListToString(){
        outputListString = new String();
        for (String s : globalDeclareList){
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
        if (defineGlobalVariable){
            Variable variable = variableHashMap_global.get(ctx.ident().getText());
            if (variable != null){
                System.out.println("global variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            variable.setiType(bType);
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber(operationNumber);
            variable.setConst(true);
            variable.setGlobal(true);
            variable.setBlock(-1);
            if (ctx.constInitVal() != null){
                visit(ctx.constInitVal());
                if (operationNumber.charAt(0) == '%'){
                    variable.setValue(computeGlobalVariableValue());
                }
                else{
                    variable.setValue(Integer.parseInt(operationNumber));
                }
            }
            else {
                variable.setValue(0);
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
            outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
            // define name and value,that is,  constDef : ident = constInitVal
            if (ctx.children.size() == 3){
                visit(ctx.constInitVal());
                outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());
            }
            //need to store local hashmap
            blockArrayList.get(nowBlock).getVariableHashMap().put(variable.getVarName(), variable);
        }
        return null;
    }

    @Override
    public Object visitConstInitVal(MiniSysParser.ConstInitValContext ctx) {
        return super.visitConstInitVal(ctx);
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
        if (defineGlobalVariable){
            Variable variable = variableHashMap_global.get(ctx.ident().getText());
            if (variable != null){
                System.out.println("global variable " + variable.getVarName() + " had benn defined before");
                System.exit(-1);
            }
            variable = new Variable();
            variable.setiType(bType);
            variable.setVarName(ctx.ident().getText());
            variable.setOperationNumber(operationNumber);
            variable.setConst(false);
            variable.setGlobal(true);
            variable.setBlock(-1);
            if (ctx.initVal() != null){
                visit(ctx.initVal());
                if (operationNumber.charAt(0) == '%'){
                    variable.setValue(computeGlobalVariableValue());
                }
                else{
                    variable.setValue(Integer.parseInt(operationNumber));
                }
            }
            else {
                variable.setValue(0);
            }
            variableHashMap_global.put(variable.getVarName() , variable);
            globalDeclareList.add("@" + variable.getVarName() + " = dso_local global " + variable.getiType() + " " + variable.getValue() + System.lineSeparator());
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
            outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
            // define name and value,that is,  constDef : ident = constInitVal
            if (ctx.children.size() == 3){
                visit(ctx.initVal());
                outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());
            }
            //need to store local hashmap
            blockArrayList.get(nowBlock).getVariableHashMap().put(variable.getVarName(), variable);
        }
        return null;
    }

    @Override
    public Object visitInitVal(MiniSysParser.InitValContext ctx) {
        return super.visitInitVal(ctx);
    }

    @Override
    public Object visitFuncType(MiniSysParser.FuncTypeContext ctx) {
        System.out.println("now visit functype. "+ "functype.text is " + ctx.getText());
        return super.visitFuncType(ctx);
    }

    @Override
    public Object visitFuncFParams(MiniSysParser.FuncFParamsContext ctx) {
        return super.visitFuncFParams(ctx);
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
        if (nowBlock == 0){
            //copy global variables
            block.getVariableHashMap().putAll(variableHashMap_global);
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
            super.visitStmt(ctx);
            outputList.add("ret " + retType + operationNumber + System.lineSeparator());
            isReturn = true;
        }
        //exp;
        else if (ctx.lVal()==null && ctx.exp() != null ){
            super.visitStmt(ctx);
        }
        //lval = exp;
        else if (ctx.lVal() != null){
            String regName = ctx.lVal().getText();
            //judge whther regName had benn defined and isConst = false
            Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(regName);
            if (variable != null){
                //local
                visit(ctx.exp());
                if (!variable.isConst()){
                    if (variable.isGlobal()){
                        outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* @" + variable.getVarName() + System.lineSeparator());
                    }
                    else {
                        outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
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
            operationNumber = String.valueOf(number);
        }
        //primary : lval
        else if (ctx.lVal()!=null){
            // the lval was in exp
            //need to judge whether lval had been defined
            //lval : ident
            if (defineGlobalVariable){
                Variable variable = variableHashMap_global.get(ctx.lVal().getText());
                if (variable != null){
                    if (!variable.isConst()){
                        System.out.println("global var is not const");
                        System.exit(-1);
                    }
                    int value = variable.getValue();
                    operationNumber = String.valueOf(value);
                }
                else {
                    System.out.println("var has not been defined");
                    System.exit(-1);
                }
            }
            else {
                Variable variable = blockArrayList.get(nowBlock).getVariableHashMap().get(ctx.lVal().getText());
                if(variable != null){
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
                registerNum++;
                outputList.add("%" + registerNum + " = sub " + "i32 " + "0" + ", "+operationNumber + System.lineSeparator());
                operationNumber = "%" + registerNum;
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
                    if (function.getParamsType() == null){
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
                    visit(ctx.funcRParams());
                    boolean paramsLegal = function.checkParamsType("i32");
                    if (paramsLegal){
                        if(function.isDeclare() == false){
                            function.setDeclare(true);
                        }
                        outputList.add("call " + function.getRetType() + " @" + function.getFuncName() + "(" + "i32 " + operationNumber +")" + System.lineSeparator());
                    }
                    else {
                        System.out.println("function params is not legal");
                        System.exit(-1);
                    }
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