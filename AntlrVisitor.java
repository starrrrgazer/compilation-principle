import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


public class AntlrVisitor extends MiniSysBaseVisitor {
    ArrayList<String> outputList = new ArrayList<>();
    String outputListString;
    String retType;
    int registerNum = 0;//first block = %0
    String nowFuncName;
    String nowBlockLabel = "%0";
    String operationNumber = null;
    HashMap<String, Variable> variableHashMap_local = new HashMap<>(); //is used to store information about variable
    HashMap<String, Variable> variableHashMap_global = new HashMap<>();
    HashMap<String,Function> functionHashMap = new HashMap<>();
    String bType; // when define var, set bType = btype
    boolean isReturn = false ; // judge whether if return
    Stack<StackElem> callbackStack = new Stack<StackElem>(); // if block callback
    Stack<Integer> retStack = new Stack<Integer>();// ret index
//    ListStack<StackElem> callbackStack = new ListStack<StackElem>(); // if block callback
//    ListStack<Integer> retStack = new ListStack<Integer>();// ret index
    ArrayList<Block> blockArrayList = new ArrayList<>();
    ListStack<ArrayList<Block>> blockStack = new ListStack<>();


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

    @Override
    public Object visitCompUnit(MiniSysParser.CompUnitContext ctx) {
        System.out.println("now visit compunit");
        initFunctionMap();
        super.visitCompUnit(ctx);
        outputListToString();
        System.out.println("now exit");
        return null;
    }

    public void outputListToString(){
        outputListString = new String();
        for (String s : outputList){
            if (s == null)
                continue;
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

        Variable variable = variableHashMap_local.get(ctx.ident().getText());
        if (variable != null && variable.getBlockLabel().equals(nowBlockLabel)){
            System.out.println("variable " + variable.getVarName() + " had benn defined befor");
            System.exit(-1);
        }
        variable = new Variable();
        registerNum++;
        operationNumber = "%" + registerNum;

        //bType : int
        variable.setiType(bType);
        //set regName = ident
        variable.setVarName(ctx.getChild(0).getText());
        variable.setOperationNumber(operationNumber);
        variable.setConst(true);
        variable.setGlobal(false);
        variable.setBlockLabel(nowBlockLabel);
        outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
        // define name and value,that is,  constDef : ident = constInitVal
        if (ctx.children.size() == 3){
            visit(ctx.constInitVal());
            outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());

        }
        //need to store local hashmap
        variableHashMap_local.put(variable.getVarName(), variable);
//        super.visitVarDef(ctx);
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

        //judge whether variable had been defined
        Variable variable = variableHashMap_local.get(ctx.ident().getText());
        if (variable != null && variable.getBlockLabel().equals(nowBlockLabel)){
            System.out.println("variable " + variable.getVarName() + " had benn defined befor");
            System.exit(-1);
        }
        variable = new Variable();
        registerNum++;
        operationNumber = "%" + registerNum;

        //bType : int
        variable.setiType(bType);
        //set regName = ident
        variable.setVarName(ctx.getChild(0).getText());
        variable.setOperationNumber(operationNumber);
        variable.setConst(false);
        variable.setGlobal(false);
        variable.setBlockLabel(nowBlockLabel);
        outputList.add(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
        //define only name, that is,  ident
        if (ctx.children.size() == 1){
            //do nothing
//            super.visitVarDef(ctx);
        }
        // define name and value,that is,  varDef : ident = initVal
        else if (ctx.children.size() == 3){
            visit(ctx.initVal());
            outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());

        }
        //need to store local hashmap
        variableHashMap_local.put(variable.getVarName(), variable);
//        super.visitVarDef(ctx);
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
        System.out.println("now visit block" + ". block.text is " + ctx.getText());

        super.visitBlock(ctx);

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
            Variable variable = variableHashMap_local.get(regName);
            if (variable != null){
                //local
                visit(ctx.exp());
                if (!variable.isConst()){
                    outputList.add("store i32 " + operationNumber + ", " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
                }
                else {
                    System.out.println("stmt : lval is const");
                    System.exit(-1);
                }
            }
            else {
                variable = variableHashMap_global.get(regName);
                if (variable != null){
                    //global
                }
                else {
                    System.out.println("stmt : lval had not been defined");
                    System.exit(-1);
                }
            }
        }
        // if ( cond ) stmt else stmt
        else if (child0.equals("if")){
            boolean justIf = ctx.children.size() == 5;
            StackElem stackElem1 = new StackElem();
            callbackStack.push(stackElem1);
            retStack.push(callbackStack.size()-1);
            visit(ctx.cond());
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            StackElem stackElemTmp = callbackStack.pop();
            for (Integer index : stackElemTmp.getLocation()){
                if (outputList.get(index).contains("true")){
                    outputList.set(index,outputList.get(index).replaceFirst("true","%" + registerNum));
                }
                else {
                    outputList.set(index,outputList.get(index).substring(0,outputList.get(index).length())+ ", label %" + registerNum);
                }
            }
            isReturn = false;
            visit(ctx.stmt(0));
            //don't have "ret"
            if (!isReturn){
                outputList.add("br label %");
                isReturn = false;
                int retIndex = retStack.pop();
                StackElem stackElem2 = callbackStack.get(retIndex);
                stackElem2.getLocation().add(outputList.size()-1);
                callbackStack.set(retIndex,stackElem2);
            }
            //just if go out
            registerNum++;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            stackElemTmp = callbackStack.pop();
            if (justIf){
                for (Integer index : stackElemTmp.getLocation()){
                    outputList.set(index, outputList.get(index) + ", label %" + registerNum + System.lineSeparator());
                }
                registerNum++;
                operationNumber = "%" + registerNum;
                outputList.add("br label %" + registerNum + System.lineSeparator());
                stackElemTmp = callbackStack.pop();
                for (Integer index : stackElemTmp.getLocation()){
                    outputList.set(index, outputList.get(index).substring(0 , outputList.get(index).length() ) + registerNum + System.lineSeparator());
                }
                outputList.add(System.lineSeparator() + registerNum + ":"+ System.lineSeparator());
            }
            // if else need to visit else
            else {
                for (Integer index : stackElemTmp.getLocation()){
                    outputList.set(index, outputList.get(index) + ", label %" + registerNum + System.lineSeparator());
                }

                isReturn = false;
                visit(ctx.stmt(1));

                registerNum++;
                operationNumber = "%" + registerNum;
                outputList.add("br label %" + registerNum + System.lineSeparator());

                System.out.println(callbackStack.size());

                stackElemTmp = callbackStack.pop();
                for (Integer index : stackElemTmp.getLocation()){
                    outputList.set(index, outputList.get(index).substring(0 , outputList.get(index).length() ) + registerNum + System.lineSeparator());
                }
                outputList.add(System.lineSeparator() + registerNum + ":"+ System.lineSeparator());
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
        else {
            super.visitStmt(ctx);
        }
        return null;
    }

    public void popAndBackFillLabel1(String brLabel){
        ArrayList<Block> blocks = blockStack.pop();
        for (Block block:blocks){
            block.setBrLabel1(brLabel);
        }
    }
    public void popAndBackFillLabel2(String brLabel){
        ArrayList<Block> blocks = blockStack.pop();
        for (Block block:blocks){
            block.setBrLabel2(brLabel);
        }
    }
    public Block getNewBlock(int brLabelNum){
        registerNum ++ ;
        nowBlockLabel = "%" + registerNum;
        printBlockLabel(registerNum);
        Block block = new Block();
        block.setBrLabelNum(brLabelNum);
        block.setOperationNumber(nowBlockLabel);
        blockArrayList.add(block);
        return block;
    }
    //if now don't have block or all block is complete, return false
    public boolean checkAllBlockComplete(){
        for (Block block : blockArrayList){
            if (!block.isBrComplete()){
                return false;
            }
        }
        return true;
    }

    public boolean checkNeedNewBlock(){
        if (blockArrayList.size() == 0){
            return false;
        }
        for (Block block : blockArrayList){
            if (!block.isBrComplete()){
                return true;
            }
        }
        return false;
    }
    public void printBlockLabel2(int regNum){
        if (blockArrayList.size()>0 || checkNeedNewBlock()){
            outputList.add(System.lineSeparator() + regNum + ":" + System.lineSeparator());
        }
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
            Variable variable = variableHashMap_local.get(ctx.lVal().getText());
            if(variable != null){
                registerNum ++;
                operationNumber = "%" + registerNum;
                outputList.add(operationNumber + " = load i32, " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
            }
            else {
                variable = variableHashMap_global.get(ctx.lVal().getText());
                if (variable != null){
                    //need to write global register
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
        if(ctx.children.size() == 1){
            visit(ctx.mulExp());
        }
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
        //relExp <><=>= addExp
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

    @Override
    public Object visitLAndExp(MiniSysParser.LAndExpContext ctx) {
        //land && eq
        if (ctx.lAndExp() != null){
            visit(ctx.lAndExp());
            registerNum ++ ;
            operationNumber = "%" + registerNum;
            outputList.add(System.lineSeparator() + registerNum + ":" + System.lineSeparator());
            StackElem stackElemTmp = callbackStack.pop();
            for (Integer index : stackElemTmp.getLocation()){
                if (outputList.get(index).contains("true")){
                    outputList.set(index, outputList.get(index).replaceFirst("true", "%" + registerNum));
                }
                else {
                    outputList.set(index,outputList.get(index).substring(0,outputList.get(index).length()) + ", label %" + registerNum);
                }
            }
            visit(ctx.eqExp());
            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputList.add("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            outputList.add("br i1 " + operationNumber);
            int lastIndex = outputList.size() -1 ;
            stackElemTmp = callbackStack.pop();
            stackElemTmp.getLocation().add(lastIndex);
            callbackStack.push(stackElemTmp);
            stackElemTmp = new StackElem();
            stackElemTmp.getLocation().add(lastIndex);
            callbackStack.push(stackElemTmp);
        }
        else {
            visit(ctx.eqExp());
            //when don't have == != >= <= > <
            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputList.add("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            StackElem stackElemTmp = new StackElem();
            outputList.add("br i1 " + operationNumber);
            int lastIndex = outputList.size() -1 ;
            stackElemTmp = new StackElem();
            stackElemTmp.getLocation().add(lastIndex);

            callbackStack.push(stackElemTmp);
            System.out.println(callbackStack.size());
            stackElemTmp = new StackElem();
            stackElemTmp.getLocation().add(lastIndex);
            callbackStack.push(stackElemTmp);
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
            StackElem stackElemTmp = callbackStack.pop();
            StackElem stackElemTmp2 = callbackStack.pop();
            for (Integer index : stackElemTmp2.getLocation()){
                if (outputList.get(index).length() <= 16){
                    outputList.set(index, outputList.get(index).substring(0,outputList.get(index).length()) + ", label true, label %" + registerNum);
                }
                else {
                    outputList.set(index,outputList.get(index) + ",label %" + registerNum);
                }
            }

            visit(ctx.lAndExp());
            StackElem stackElemTmp3 = callbackStack.pop();
            stackElemTmp3.getLocation().addAll(stackElemTmp.getLocation());
            callbackStack.push(stackElemTmp3);
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