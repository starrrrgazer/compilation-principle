import java.util.HashMap;


public class AntlrVisitor extends MiniSysBaseVisitor {


    StringBuilder outputStringBuilder = new StringBuilder();
    String retType;
    int registerNum = 0;
    String nowFuncName;
    String operationNumber;
    HashMap<String, Variable> variableHashMap_local = new HashMap<>(); //is used to store information about variable
    HashMap<String, Variable> variableHashMap_global = new HashMap<>();
    HashMap<String,Function> functionHashMap = new HashMap<>();
    String bType; // when define var, set bType = btype

    public StringBuilder getOutputStringBuilder() {

        return outputStringBuilder;
    }

    public void setOutputStringBuilder(StringBuilder outputStringBuilder) {
        this.outputStringBuilder = outputStringBuilder;
    }

    public void initGlobalVariables(){
        registerNum = 0;
        operationNumber = null;
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
    }

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        outputStringBuilder.append("define dso_local ");
        if(ctx.funcType().getText().equals("int")){
            retType = "i32 ";
            outputStringBuilder.append("i32 ");
        }
//        visit(ctx.funcType());
//        visit(ctx.ident());
        if (ctx.ident().getText().equals("main")){
            outputStringBuilder.append("@main");
            outputStringBuilder.append("(");
            //this should be a visit param
            outputStringBuilder.append(")");
        }
        visit(ctx.block());
        return null;
    }

    @Override
    public Object visitCompUnit(MiniSysParser.CompUnitContext ctx) {
        System.out.println("now visit compunit");
        initFunctionMap();
        return super.visitCompUnit(ctx);
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
        Variable variable = new Variable();
        registerNum++;
        operationNumber = "%" + registerNum;

        //bType : int
        variable.setiType(bType);
        //set regName = ident
        variable.setRegName(ctx.getChild(0).getText());
        variable.setOperationNumber(operationNumber);
        variable.setConst(true);
        variable.setGlobal(false);
        outputStringBuilder.append(operationNumber + " = alloca " + variable.getiType() + System.lineSeparator());
        // define name and value,that is,  constDef : ident = constInitVal
        if (ctx.children.size() == 3){
            visit(ctx.constInitVal());
            outputStringBuilder.append("store i32 " + operationNumber + ", " + variable.getiType() + "* "+ variable.getOperationNumber() + System.lineSeparator());

        }
        //need to store local hashmap
        variableHashMap_local.put(variable.getRegName(), variable);
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
        Variable variable = new Variable();
        registerNum++;
        operationNumber = "%" + registerNum;

        //bType : int
        variable.setiType(bType);
        //set regName = ident
        variable.setRegName(ctx.getChild(0).getText());
        variable.setOperationNumber(operationNumber);
        variable.setConst(false);
        variable.setGlobal(false);
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
        variableHashMap_local.put(variable.getRegName(), variable);
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
        outputStringBuilder.append("{" + System.lineSeparator());
        initGlobalVariables();
        super.visitBlock(ctx);
        outputStringBuilder.append(System.lineSeparator() + "}");
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
            outputStringBuilder.append("ret " + retType + operationNumber);
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
        }
        else {
            super.visitStmt(ctx);
        }
        return null;
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
                            outputStringBuilder.insert(0,function.getDeclareString() + System.lineSeparator());
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
                            outputStringBuilder.insert(0,function.getDeclareString() + System.lineSeparator());
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
        return super.visitRelExp(ctx);
    }

    @Override
    public Object visitEqExp(MiniSysParser.EqExpContext ctx) {
        return super.visitEqExp(ctx);
    }

    @Override
    public Object visitLAndExp(MiniSysParser.LAndExpContext ctx) {
        return super.visitLAndExp(ctx);
    }

    @Override
    public Object visitLOrExp(MiniSysParser.LOrExpContext ctx) {
        return super.visitLOrExp(ctx);
    }

    @Override
    public Object visitConstExp(MiniSysParser.ConstExpContext ctx) {
        return super.visitConstExp(ctx);
    }

    @Override
    public Object visitIdent(MiniSysParser.IdentContext ctx) {
        System.out.println("now visit ident" + ". ident text is : " + ctx.getText());
//        outputStringBuilder.append("@" + ctx.getText());
        return super.visitIdent(ctx);
    }

    @Override
    public Object visitDigit(MiniSysParser.DigitContext ctx) {
        return super.visitDigit(ctx);
    }
}
