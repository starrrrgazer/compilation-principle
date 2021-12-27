// Generated from D:/2021compile/compilation-principle\MiniSys.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MiniSysParser}.
 */
public interface MiniSysListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#compUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompUnit(MiniSysParser.CompUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#compUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompUnit(MiniSysParser.CompUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(MiniSysParser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(MiniSysParser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstDecl(MiniSysParser.ConstDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#constDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstDecl(MiniSysParser.ConstDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#bType}.
	 * @param ctx the parse tree
	 */
	void enterBType(MiniSysParser.BTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#bType}.
	 * @param ctx the parse tree
	 */
	void exitBType(MiniSysParser.BTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#constDef}.
	 * @param ctx the parse tree
	 */
	void enterConstDef(MiniSysParser.ConstDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#constDef}.
	 * @param ctx the parse tree
	 */
	void exitConstDef(MiniSysParser.ConstDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void enterConstInitVal(MiniSysParser.ConstInitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#constInitVal}.
	 * @param ctx the parse tree
	 */
	void exitConstInitVal(MiniSysParser.ConstInitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(MiniSysParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(MiniSysParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(MiniSysParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(MiniSysParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#initVal}.
	 * @param ctx the parse tree
	 */
	void enterInitVal(MiniSysParser.InitValContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#initVal}.
	 * @param ctx the parse tree
	 */
	void exitInitVal(MiniSysParser.InitValContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncDef(MiniSysParser.FuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncDef(MiniSysParser.FuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#funcType}.
	 * @param ctx the parse tree
	 */
	void enterFuncType(MiniSysParser.FuncTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#funcType}.
	 * @param ctx the parse tree
	 */
	void exitFuncType(MiniSysParser.FuncTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#funcFParams}.
	 * @param ctx the parse tree
	 */
	void enterFuncFParams(MiniSysParser.FuncFParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#funcFParams}.
	 * @param ctx the parse tree
	 */
	void exitFuncFParams(MiniSysParser.FuncFParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#funcFParam}.
	 * @param ctx the parse tree
	 */
	void enterFuncFParam(MiniSysParser.FuncFParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#funcFParam}.
	 * @param ctx the parse tree
	 */
	void exitFuncFParam(MiniSysParser.FuncFParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MiniSysParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MiniSysParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void enterBlockItem(MiniSysParser.BlockItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#blockItem}.
	 * @param ctx the parse tree
	 */
	void exitBlockItem(MiniSysParser.BlockItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(MiniSysParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(MiniSysParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(MiniSysParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(MiniSysParser.ExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#cond}.
	 * @param ctx the parse tree
	 */
	void enterCond(MiniSysParser.CondContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#cond}.
	 * @param ctx the parse tree
	 */
	void exitCond(MiniSysParser.CondContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#lVal}.
	 * @param ctx the parse tree
	 */
	void enterLVal(MiniSysParser.LValContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#lVal}.
	 * @param ctx the parse tree
	 */
	void exitLVal(MiniSysParser.LValContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#primaryExp}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExp(MiniSysParser.PrimaryExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#primaryExp}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExp(MiniSysParser.PrimaryExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExp(MiniSysParser.UnaryExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#unaryExp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExp(MiniSysParser.UnaryExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOp(MiniSysParser.UnaryOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#unaryOp}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOp(MiniSysParser.UnaryOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void enterFuncRParams(MiniSysParser.FuncRParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#funcRParams}.
	 * @param ctx the parse tree
	 */
	void exitFuncRParams(MiniSysParser.FuncRParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#mulExp}.
	 * @param ctx the parse tree
	 */
	void enterMulExp(MiniSysParser.MulExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#mulExp}.
	 * @param ctx the parse tree
	 */
	void exitMulExp(MiniSysParser.MulExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#addExp}.
	 * @param ctx the parse tree
	 */
	void enterAddExp(MiniSysParser.AddExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#addExp}.
	 * @param ctx the parse tree
	 */
	void exitAddExp(MiniSysParser.AddExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#relExp}.
	 * @param ctx the parse tree
	 */
	void enterRelExp(MiniSysParser.RelExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#relExp}.
	 * @param ctx the parse tree
	 */
	void exitRelExp(MiniSysParser.RelExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#eqExp}.
	 * @param ctx the parse tree
	 */
	void enterEqExp(MiniSysParser.EqExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#eqExp}.
	 * @param ctx the parse tree
	 */
	void exitEqExp(MiniSysParser.EqExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#lAndExp}.
	 * @param ctx the parse tree
	 */
	void enterLAndExp(MiniSysParser.LAndExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#lAndExp}.
	 * @param ctx the parse tree
	 */
	void exitLAndExp(MiniSysParser.LAndExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#lOrExp}.
	 * @param ctx the parse tree
	 */
	void enterLOrExp(MiniSysParser.LOrExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#lOrExp}.
	 * @param ctx the parse tree
	 */
	void exitLOrExp(MiniSysParser.LOrExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#constExp}.
	 * @param ctx the parse tree
	 */
	void enterConstExp(MiniSysParser.ConstExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#constExp}.
	 * @param ctx the parse tree
	 */
	void exitConstExp(MiniSysParser.ConstExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(MiniSysParser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(MiniSysParser.IdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#nondigit}.
	 * @param ctx the parse tree
	 */
	void enterNondigit(MiniSysParser.NondigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#nondigit}.
	 * @param ctx the parse tree
	 */
	void exitNondigit(MiniSysParser.NondigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link MiniSysParser#digit}.
	 * @param ctx the parse tree
	 */
	void enterDigit(MiniSysParser.DigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link MiniSysParser#digit}.
	 * @param ctx the parse tree
	 */
	void exitDigit(MiniSysParser.DigitContext ctx);
}