import java.util.ArrayList;
import java.util.HashMap;


public class AntlrVisitor extends MiniSysBaseVisitor {
    StringBuilder outputStringBuilder = new StringBuilder();
    String retType;
    int registerNum = 0;//first block = %0
    String nowFuncName;
    String nowBlockLabel = "%0";
    String operationNumber = null;
    boolean blockIncludeIf = false;
    HashMap<String, Variable> variableHashMap_local = new HashMap<>(); //is used to store information about variable
    HashMap<String, Variable> variableHashMap_global = new HashMap<>();
    HashMap<String,Function> functionHashMap = new HashMap<>();
    ArrayList<Block> blockArrayList = new ArrayList<>();
    String bType; // when define var, set bType = btype

    public StringBuilder getOutputStringBuilder() {
        return outputStringBuilder;
    }

    public void setOutputStringBuilder(StringBuilder outputStringBuilder) {
        this.outputStringBuilder = outputStringBuilder;
    }



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
        outputStringBuilder.append("declare i32 @getint()" + System.lineSeparator());
        outputStringBuilder.append("declare i32 @getch()" + System.lineSeparator());
        outputStringBuilder.append("declare i32 @getarray(i32*)" + System.lineSeparator());
        outputStringBuilder.append("declare void @putint(i32)" + System.lineSeparator());
        outputStringBuilder.append("declare void @putch(i32)" + System.lineSeparator());
        outputStringBuilder.append("declare void @putarray(i32,i32*)" + System.lineSeparator());
    }

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        outputStringBuilder.append("define dso_local ");
        if (ctx.funcType().getText().equals("int")){
            retType = "i32 ";
            outputStringBuilder.append("i32 ");
        }
        if (ctx.ident().getText().equals("main")){
            outputStringBuilder.append("@main");
            outputStringBuilder.append("(");
            //this should be a visit param
            outputStringBuilder.append(")");
            outputStringBuilder.append(" {" + System.lineSeparator());
            visit(ctx.block());
            outputStringBuilder.append("}");
        }

        return null;
    }

    @Override
    public Object visitCompUnit(MiniSysParser.CompUnitContext ctx) {
        System.out.println("now visit compunit");
        initFunctionMap();
        super.visitCompUnit(ctx);
        outputBrBlockLabel();
        System.out.println("now exit");
        return null;
    }

    public void outputBrBlockLabel(){
        int insertIndex = 0;
        for (Block block:blockArrayList){
            if (!block.isBrComplete()){
                System.out.println("some block had not benn completed : " + block.getOperationNumber());
//                System.exit(-1);
            }
            if (!block.isNeedInsert()){
                continue;
            }
            if (block.getBrLabelNum() == 2){
                String str = "br i1 " + block.getJudgeRegister() + ", label " + block.getBrLabel1() + ", label " + block.getBrLabel2() + System.lineSeparator();
                outputStringBuilder.insert(block.getInsertIndex() + insertIndex,str);
                insertIndex = insertIndex + str.length();
            }
            else if (block.getBrLabelNum() == 1){
                if (block.getBrLabel1() == null){
                    continue;
                }
                String str = "br label " + block.getBrLabel1() + System.lineSeparator();
                outputStringBuilder.insert(block.getInsertIndex() + insertIndex,str);
                insertIndex = insertIndex + str.length();
            }
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
        outputStringBuilder.append(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
        // define name and value,that is,  constDef : ident = constInitVal
        if (ctx.children.size() == 3){
            visit(ctx.constInitVal());
            outputStringBuilder.append("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());

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
        outputStringBuilder.append(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
        //define only name, that is,  ident
        if (ctx.children.size() == 1){
            //do nothing
//            super.visitVarDef(ctx);
        }
        // define name and value,that is,  varDef : ident = initVal
        else if (ctx.children.size() == 3){
            visit(ctx.initVal());
            outputStringBuilder.append("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());

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
            outputStringBuilder.append("ret " + retType + operationNumber + System.lineSeparator());
        }
        //exp;
        else if (ctx.lVal()==null && ctx.exp() != null ){
            if (ctx.getParent().getChild(0).getText().equals("if")){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;
                backFillBrBlockLabelList(nowBlockLabel,2);
                printBlockLabel(registerNum);

                Block block = new Block();
                block.setInsertIndex(outputStringBuilder.length());
                block.setBrLabelNum(1);
                block.setOperationNumber(nowBlockLabel);
                blockArrayList.add(block);
            }
            super.visitStmt(ctx);
        }
        //lval = exp;
        else if (ctx.lVal() != null){
            if (ctx.getParent().getChild(0).getText().equals("if")){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;
                backFillBrBlockLabelList(nowBlockLabel,2);
                printBlockLabel(registerNum);
            }

            String regName = ctx.lVal().getText();
            //judge whther regName had benn defined and isConst = false
            Variable variable = variableHashMap_local.get(regName);
            if (variable != null){
                //local
                visit(ctx.exp());
                if (!variable.isConst()){
                    outputStringBuilder.append("store i32 " + operationNumber + ", " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
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
            if (ctx.getParent().getChild(0).getText().equals("if")){
                Block block = new Block();
                block.setInsertIndex(outputStringBuilder.length());
                block.setBrLabelNum(1);
                block.setOperationNumber(nowBlockLabel);
                blockArrayList.add(block);
            }
        }
        // if ( cond ) stmt else stmt
        else if (child0.equals("if")){
            boolean justIf = ctx.children.size() == 5;
            //judge whether have || or &&
            boolean haveAndOrCond = ctx.cond().lOrExp().lOrExp() != null || ctx.cond().lOrExp().lAndExp().lAndExp() != null;
            // have || or && , that means cond has create block, don't need to create another
            if (blockArrayList.size() > 0 && !haveAndOrCond && !checkAllBlockComplete()){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;

                if (blockIncludeIf){
                    backFillBrBlockLabelList(nowBlockLabel,7);
                    blockIncludeIf = false;
                }
                else {
                    backFillBrBlockLabelList(nowBlockLabel,2);
                }
                printBlockLabel(registerNum);
            }
            visit(ctx.cond());
            // have || or && , that means cond has create block, don't need to create another
            if (!haveAndOrCond){
                String judgeRegister = operationNumber;
                Block block = new Block();
                block.setInsertIndex(outputStringBuilder.length());
                block.setJudgeRegister(judgeRegister);
                block.setBrLabelNum(2);
                block.setOperationNumber(nowBlockLabel);
                blockArrayList.add(block);
            }

            visit(ctx.stmt(0));
            //backfill into lor cond
            backFillBrBlockLabelList(nowBlockLabel,5);
            backFillBrBlockLabelList("%"+ (registerNum +1),6);
            //just if only can visit stmt0
            if (!justIf){
                visit(ctx.stmt(1));
            }
            //out of if-else , so get a new block , don't need to add to list

            registerNum++;
            nowBlockLabel = "%" + registerNum;
//            if (registerNum == 14)System.exit(-1);
            //just if need special insert
            if (justIf){
                backFillBrBlockLabelList(nowBlockLabel,3);
            }
            else {
                backFillBrBlockLabelList(nowBlockLabel,1);
            }
            printBlockLabel(registerNum);
            Block block1 = new Block();
            block1.setBrLabelNum(1);
            block1.setOperationNumber(nowBlockLabel);
            block1.setInsertIndex(outputStringBuilder.length());
            if (checkAllBlockComplete()){
//                if (registerNum == 13)System.exit(-1);
                block1.setBrComplete(true);
            }
            blockArrayList.add(block1);
        }
        //block
        else if (ctx.block() != null){
            registerNum ++;
            nowBlockLabel = "%" + registerNum;
            backFillBrBlockLabelList(nowBlockLabel,2);
            printBlockLabel(registerNum);
            Block block = new Block();
            block.setInsertIndex(outputStringBuilder.length());
            block.setBrLabelNum(1);
            block.setOperationNumber(nowBlockLabel);
            blockArrayList.add(block);
            blockIncludeIf = true;
            visit(ctx.block());
        }
        else {
            super.visitStmt(ctx);
        }
        return null;
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
            outputStringBuilder.append(System.lineSeparator() + regNum + ":" + System.lineSeparator());
        }
    }
    public void printBlockLabel(int regNum){
        outputStringBuilder.append(System.lineSeparator() + regNum + ":" + System.lineSeparator());
    }

    public void backFillBrBlockLabelList(String blockLabelName, int type){
        //insert into 1 brLabelNum = 2
        if (type == 2){
            for(Block block : blockArrayList){
                if(block.isBrComplete()){
                    continue;
                }
                if (block.getBrLabelNum() == 2){
                    if (block.getBrLabel1() == null){
                        block.setBrLabel1(blockLabelName);
                        if (block.getBrLabel2() != null){
                            block.setBrComplete(true);
                        }
                        break;
                    }
                    else if (block.getBrLabel2() == null){
                        block.setBrLabel2(blockLabelName);
                        if (block.getBrLabel1() != null){
                            block.setBrComplete(true);
                        }
                        break;
                    }
                    else {
                        System.out.println("block back fill wrong. block is not complete but brLabel 1 and 2 was filled");
                        System.exit(-1);
                    }
                }
            }
        }
        //insert into 2  brLabelNum = 1
        else if (type == 1){
            int num = 2 ;
            for(int i = blockArrayList.size()-1; i>=0;i--){
                Block block = blockArrayList.get(i);
                if(block.isBrComplete()){
                    continue;
                }
                if (block.getBrLabelNum() == 1){
                    if (block.getBrLabel1() == null){
                        block.setBrLabel1(blockLabelName);
                        block.setBrComplete(true);
                        num --;
                        if (num == 0){
                            break;
                        }
                    }
                    else {
                        System.out.println("block back fill wrong. block is not complete but brLabel 1 was filled");
                        System.exit(-1);
                    }
                }
            }
        }
        // at the end of only if
        else if (type == 3){
            int num = 2 ;
            for(int i = blockArrayList.size()-1; i>=0;i--){
                Block block = blockArrayList.get(i);
                if(block.isBrComplete()){
                    continue;
                }
                if (block.getBrLabelNum() == 1){
                    if (block.getBrLabel1() == null){
                        block.setBrLabel1(blockLabelName);
                        block.setBrComplete(true);
                        num --;
                        if (num == 0){
                            break;
                        }
                    }
                    else {
                        System.out.println("block back fill wrong. block is not complete but brLabel 1 was filled");
                        System.exit(-1);
                    }
                }
                else if (block.getBrLabelNum() == 2){
                    if (block.getBrLabel2() == null){
                        block.setBrLabel2(blockLabelName);
                        block.setBrComplete(true);
                        num --;
                        if (num == 0){
                            break;
                        }
                    }
                    else {
                        System.out.println("block back fill wrong. block is not complete but brLabel 2 was filled");
                        System.exit(-1);
                    }
                }
            }
        }
        //lor or land called : insert into lor's brLabel2
        //land called : insert into land's brLabel1
        else if (type == 4){
            for (Block block:blockArrayList){
                if (block.isBrComplete()){
                    continue;
                }
                if (block.isLor() && block.getBrLabel2() == null){
                    block.setBrLabel2(blockLabelName);
                    if (block.getBrLabel1() != null){
                        block.setBrComplete(true);
                    }
                    break;
                }
                if (block.isLAnd() && block.getBrLabel1() == null){
                    block.setBrLabel1(blockLabelName);
                    if (block.getBrLabel2() != null){
                        block.setBrComplete(true);
                    }
                    break;
                }
            }
        }
        //backfill lor cond
        else if (type == 5){
            for (Block block : blockArrayList){
                if (block.isBrComplete()){
                    continue;
                }
                if (block.isLor()){
                    if (block.getBrLabel1() == null){
                        block.setBrLabel1(blockLabelName);
                    }
                    if (block.getBrLabel2() != null){
                        block.setBrComplete(true);
                    }
                    continue;
                }
            }
        }
        //backfill land cond
        else if (type == 6){
            for (Block block : blockArrayList){
                if (block.isBrComplete()){
                    continue;
                }
                if (block.isLAnd()){
                    if (block.getBrLabel2() == null){
                        block.setBrLabel2(blockLabelName);
                    }
                    if (block.getBrLabel1() != null){
                        block.setBrComplete(true);
                    }
                    continue;
                }
            }
        }
        //backfill to last uncomplete label
        else if (type == 7){
            for (int i = blockArrayList.size()-1 ; i>=0 ;i--){
                Block block = blockArrayList.get(i);
                block.setBrLabel1(blockLabelName);
                block.setBrComplete(true);
                break;
            }
        }
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
                outputStringBuilder.append(operationNumber + " = load i32, " + variable.getiType() + "* " + variable.getOperationNumber() + System.lineSeparator());
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
                outputStringBuilder.append("%" + registerNum + " = sub " + "i32 " + "0" + ", "+operationNumber + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            //need update
            else if (ctx.unaryOp().getText().equals("!")){
                registerNum++;
                outputStringBuilder.append("%" + registerNum + " = icmp eq i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
                registerNum++;
                outputStringBuilder.append("%" + registerNum + " = zext i1 " + operationNumber + " to i32" + System.lineSeparator());
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
                        outputStringBuilder.append(operationNumber + " = call " + function.getRetType() + " @" + function.getFuncName() + "()" + System.lineSeparator());
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
                        outputStringBuilder.append("call " + function.getRetType() + " @" + function.getFuncName() + "(" + "i32 " + operationNumber +")" + System.lineSeparator());
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
                outputStringBuilder.append("%" + registerNum + " = mul " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
            }
            else if(op.equals("/")){
                outputStringBuilder.append("%" + registerNum + " = sdiv " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
            }
            else if(op.equals("%")){
                outputStringBuilder.append("%" + registerNum + " = srem " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
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
                outputStringBuilder.append("%" + registerNum + " = add " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
            }
            else if(op.equals("-")){
                outputStringBuilder.append("%" + registerNum + " = sub " + "i32 " +  register1 + ", "+ register2+ System.lineSeparator());
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
                outputStringBuilder.append(operationNumber + " = icmp slt " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals(">")){
                outputStringBuilder.append(operationNumber + " = icmp sgt " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals("<=")){
                outputStringBuilder.append(operationNumber + " = icmp sle " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
            }
            else if(op.equals(">=")){
                outputStringBuilder.append(operationNumber + " = icmp sge " + "i32 " +  register1 + ", "+ register2 + System.lineSeparator());
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
                outputStringBuilder.append(operationNumber + " = icmp eq " + "i32 " +  register1 + ", " + register2 + System.lineSeparator());
            }
            else if(op.equals("!=")){
                outputStringBuilder.append(operationNumber + " = icmp ne " + "i32 " +  register1 + ", " + register2 + System.lineSeparator());
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
            if (ctx.lAndExp().lAndExp() != null){
                visit(ctx.lAndExp());
            }
            if (blockArrayList.size() > 0 && !checkAllBlockComplete()){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;
                backFillBrBlockLabelList(nowBlockLabel,4);
                if (blockIncludeIf){
                    backFillBrBlockLabelList(nowBlockLabel,7);
                    blockIncludeIf = false;
                }
                printBlockLabel(registerNum);
            }

            visit(ctx.lAndExp());
            String judgeRegister = operationNumber;
            Block block = new Block();
            block.setJudgeRegister(judgeRegister);
            block.setInsertIndex(outputStringBuilder.length());
            block.setBrLabelNum(2);
            block.setOperationNumber(nowBlockLabel);

            block.setLAnd(true);
            blockArrayList.add(block);

            registerNum ++;
            nowBlockLabel = "%" + registerNum;
            backFillBrBlockLabelList(nowBlockLabel,4);
            printBlockLabel(registerNum);
            visit(ctx.eqExp());
            //when don't have == != >= <= > <
            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputStringBuilder.append("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
            String judgeRegister1 = operationNumber;
            Block block1 = new Block();
            block1.setBrLabelNum(2);
            block1.setNeedInsert(true);
            block1.setOperationNumber(nowBlockLabel);
            block1.setBrComplete(false);
            //it means eq is the last condition of cond
            if (ctx.getParent().getClass().getSimpleName().equals("LOrExpContext")){
                block1.setLor(true);
            }
            else {
                block1.setLAnd(true);
            }
            block1.setJudgeRegister(judgeRegister1);
            block1.setInsertIndex(outputStringBuilder.length());
            blockArrayList.add(block1);


        }
        else {
            super.visitLAndExp(ctx);
            //when don't have == != >= <= > <
            if (ctx.eqExp().children.size() == 1 && ctx.eqExp().relExp().children.size() == 1){
                registerNum ++;
                outputStringBuilder.append("%" + registerNum + " = icmp ne i32 " + operationNumber + ", 0" + System.lineSeparator());
                operationNumber = "%" + registerNum;
            }
        }
        return null;
    }

    @Override
    public Object visitLOrExp(MiniSysParser.LOrExpContext ctx) {
        // lor || land
        //
        if (ctx.lOrExp() != null){
            if (ctx.lOrExp().lOrExp() != null){
                visit(ctx.lOrExp());
            }
            if (blockArrayList.size() > 0 && !checkAllBlockComplete()){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;
                backFillBrBlockLabelList(nowBlockLabel,4);
                if (blockIncludeIf){
                    backFillBrBlockLabelList(nowBlockLabel,7);
                    blockIncludeIf = false;
                }
                printBlockLabel(registerNum);
            }

            visit(ctx.lOrExp());
            //judge if before have &&
            boolean havaAndCondBefore = ctx.lOrExp().lAndExp().children.size() == 3;
            //if have && the eq block had benn created,so don't need to create another
            if (!havaAndCondBefore){
                String judgeRegister = operationNumber;
                Block block = new Block();
                block.setJudgeRegister(judgeRegister);
                block.setInsertIndex(outputStringBuilder.length());
                block.setBrLabelNum(2);
                block.setOperationNumber(nowBlockLabel);
                block.setLor(true);
                blockArrayList.add(block);
            }

            //just ||
            if (ctx.lAndExp().lAndExp() == null){
                registerNum ++;
                nowBlockLabel = "%" + registerNum;
                backFillBrBlockLabelList(nowBlockLabel,4);
                printBlockLabel(registerNum);
                visit(ctx.lAndExp());
                String judgeRegister1 = operationNumber;
                Block block1 = new Block();
                block1.setBrLabelNum(2);
                block1.setOperationNumber(nowBlockLabel);
                block1.setLor(true);
                block1.setJudgeRegister(judgeRegister1);
                block1.setInsertIndex(outputStringBuilder.length());
                blockArrayList.add(block1);
            }
            else{
                visit(ctx.lAndExp());
            }
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
//            if (checkNeedNewBlock()){
//                registerNum ++;
//                nowBlockLabel = "%" + registerNum;
//                backFillBrBlockLabelList(nowBlockLabel,4);
//            }
//            printBlockLabel(registerNum);