// Generated from D:/2021compile/compilation-principle\MiniSys.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MiniSysParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MiniSysVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#compUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompUnit(MiniSysParser.CompUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(MiniSysParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#constDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDecl(MiniSysParser.ConstDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#bType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBType(MiniSysParser.BTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#constDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDef(MiniSysParser.ConstDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#constInitVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstInitVal(MiniSysParser.ConstInitValContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(MiniSysParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(MiniSysParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#initVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitVal(MiniSysParser.InitValContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(MiniSysParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#funcType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncType(MiniSysParser.FuncTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#funcFParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncFParams(MiniSysParser.FuncFParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#funcFParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncFParam(MiniSysParser.FuncFParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MiniSysParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#blockItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockItem(MiniSysParser.BlockItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(MiniSysParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(MiniSysParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(MiniSysParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#lVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLVal(MiniSysParser.LValContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#primaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExp(MiniSysParser.PrimaryExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#unaryExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExp(MiniSysParser.UnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#unaryOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOp(MiniSysParser.UnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#funcRParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncRParams(MiniSysParser.FuncRParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#mulExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExp(MiniSysParser.MulExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#addExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExp(MiniSysParser.AddExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#relExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExp(MiniSysParser.RelExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#eqExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExp(MiniSysParser.EqExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#lAndExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLAndExp(MiniSysParser.LAndExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#lOrExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLOrExp(MiniSysParser.LOrExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#constExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstExp(MiniSysParser.ConstExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent(MiniSysParser.IdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#nondigit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNondigit(MiniSysParser.NondigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniSysParser#digit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigit(MiniSysParser.DigitContext ctx);
}