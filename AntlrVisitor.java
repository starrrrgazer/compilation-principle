public class AntlrVisitor extends MiniSysBaseVisitor {
    StringBuilder stringBuilder = new StringBuilder();
    String retString;
    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public Object visitFuncDef(MiniSysParser.FuncDefContext ctx) {
        System.out.println("now visit funcdef");
        stringBuilder.append("define dso_local ");
        visit(ctx.funcType());
        visit(ctx.ident());
        stringBuilder.append("(");
        //this should be a visit param
        stringBuilder.append(")");
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
            retString = "i32 ";
            stringBuilder.append("i32 ");
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
        stringBuilder.append("{" + System.lineSeparator());
        super.visitBlock(ctx);
        stringBuilder.append(System.lineSeparator() + "}");
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
        String child0 = String.valueOf(ctx.getChild(0));
        if(child0.equals("return")){
            stringBuilder.append("ret " + retString);
        }
        return super.visitStmt(ctx);
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
        System.out.println("now visit primaryexp. primaryexp text is : " + ctx.Number());
        if(ctx.Number() != null){
            String number = String.valueOf(ctx.Number());
            if(number.charAt(0) == '0'){
                //hex
                if(number.charAt(1) == 'x' || number.charAt(1) == 'X'){
                    stringBuilder.append(Integer.parseInt(number.substring(2),16));
                }
                //oct
                else {
                    stringBuilder.append(Integer.parseInt(number.substring(1),8));
                }
            }
        }

        return super.visitPrimaryExp(ctx);
    }

    @Override
    public Object visitUnaryExp(MiniSysParser.UnaryExpContext ctx) {
        System.out.println("now visit unaryexp");
        return super.visitUnaryExp(ctx);
    }

    @Override
    public Object visitUnaryOp(MiniSysParser.UnaryOpContext ctx) {
        return super.visitUnaryOp(ctx);
    }

    @Override
    public Object visitFuncRParams(MiniSysParser.FuncRParamsContext ctx) {
        return super.visitFuncRParams(ctx);
    }

    @Override
    public Object visitMulExp(MiniSysParser.MulExpContext ctx) {
        System.out.println("now visit mulexp");
        return super.visitMulExp(ctx);
    }

    @Override
    public Object visitAddExp(MiniSysParser.AddExpContext ctx) {
        System.out.println("now visit addexp");
        return super.visitAddExp(ctx);
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
        stringBuilder.append("@" + ctx.getText());
        return super.visitIdent(ctx);
    }

    @Override
    public Object visitDigit(MiniSysParser.DigitContext ctx) {
        return super.visitDigit(ctx);
    }
}
