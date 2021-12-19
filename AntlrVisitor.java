import java.util.ArrayList;
import java.util.HashMap;

public class AntlrVisitor extends MiniSysBaseVisitor {

    class Register{
        String registerName;
    }
    StringBuilder outputStringBuilder = new StringBuilder();
    String retType;
    int registerNum = 0;
    String operationNumber;



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

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        outputStringBuilder.append("define dso_local ");
        visit(ctx.funcType());
        visit(ctx.ident());
        outputStringBuilder.append("(");
        //this should be a visit param
        outputStringBuilder.append(")");
        visit(ctx.block());
        return null;
    }

    @Override
    public Object visitCompUnit(MiniSysParser.CompUnitContext ctx) {
        System.out.println("now visit compunit");
        return super.visitCompUnit(ctx);
    }

    @Override
    public Object visitDecl(MiniSysParser.DeclContext ctx) {
        return super.visitDecl(ctx);
    }

    @Override
    public Object visitConstDecl(MiniSysParser.ConstDeclContext ctx) {
        return super.visitConstDecl(ctx);
    }

    @Override
    public Object visitBType(MiniSysParser.BTypeContext ctx) {
        return super.visitBType(ctx);
    }

    @Override
    public Object visitConstDef(MiniSysParser.ConstDefContext ctx) {
        return super.visitConstDef(ctx);
    }

    @Override
    public Object visitConstInitVal(MiniSysParser.ConstInitValContext ctx) {
        return super.visitConstInitVal(ctx);
    }

    @Override
    public Object visitVarDecl(MiniSysParser.VarDeclContext ctx) {
        return super.visitVarDecl(ctx);
    }

    @Override
    public Object visitVarDef(MiniSysParser.VarDefContext ctx) {
        return super.visitVarDef(ctx);
    }

    @Override
    public Object visitInitVal(MiniSysParser.InitValContext ctx) {
        return super.visitInitVal(ctx);
    }

    @Override
    public Object visitFuncType(MiniSysParser.FuncTypeContext ctx) {
        System.out.println("now visit functype. "+ "functype.gettext is " + ctx.getText());
        if(ctx.getText().equals("int")){
            retType = "i32 ";
            outputStringBuilder.append("i32 ");
        }
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
        System.out.println("now visit block" + ". block.gettext is " + ctx.getText());
        outputStringBuilder.append("{" + System.lineSeparator());
        initGlobalVariables();
        super.visitBlock(ctx);
        outputStringBuilder.append(System.lineSeparator() + "}");
        return null;
    }

    @Override
    public Object visitBlockItem(MiniSysParser.BlockItemContext ctx) {
        System.out.println("now visit blockitem");
        return super.visitBlockItem(ctx);
    }

    @Override
    public Object visitStmt(MiniSysParser.StmtContext ctx) {
        System.out.println("now visit stmt. stmt text is : "+ ctx.getText());
        if(ctx.children.size() == 3){
            String child0 = String.valueOf(ctx.getChild(0));
            if(child0.equals("return")){
                super.visitStmt(ctx);
                outputStringBuilder.append("ret " + retType + "%"+registerNum);
            }
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
        if(ctx.exp() != null){
            visit(ctx.exp());
        }
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
        else {
            super.visitPrimaryExp(ctx);
        }

        return null;
    }

    @Override
    public Object visitUnaryExp(MiniSysParser.UnaryExpContext ctx) {
        System.out.println("now visit unaryexp");
        if(ctx.children.size() == 1){
            //primaryExp
            super.visit(ctx.primaryExp());
        }
        else if(ctx.children.size() == 2){
            //unaryOp unaryExp
            visit(ctx.unaryExp());
            if(ctx.unaryOp().getText().equals("-")){
                registerNum++;
                outputStringBuilder.append("%" + registerNum + " = sub " + "i32 " + "0" + ", "+operationNumber + System.lineSeparator());
                operationNumber = "%" + registerNum;
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
        outputStringBuilder.append("@" + ctx.getText());
        return super.visitIdent(ctx);
    }

    @Override
    public Object visitDigit(MiniSysParser.DigitContext ctx) {
        return super.visitDigit(ctx);
    }
}
