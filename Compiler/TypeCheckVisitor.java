package cop5556sp17;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.BOOLEAN;
import static cop5556sp17.AST.Type.TypeName.FILE;
import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.INTEGER;
import static cop5556sp17.AST.Type.TypeName.NONE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// generated method stub
		// Author: Haitang Wang
		
		Chain chain = binaryChain.getE0();
		ChainElem chainElem = binaryChain.getE1();
		chain.visit(this,  null);
		chainElem.visit(this,  null);
		Token arrow = binaryChain.getArrow();
		Token firstToken = chainElem.getFirstToken();
		
		//only case of BarArrow 
		if(arrow.isKind(Kind.BARARROW)){  
			if(chain.getTypeName().isType(IMAGE) && chainElem instanceof FilterOpChain && (firstToken.isKind(OP_GRAY) || firstToken.isKind(OP_BLUR) || firstToken.isKind(OP_CONVOLVE))){
				binaryChain.setTypeName(IMAGE);
			}
			else{
				throw new TypeCheckException("Invalid binary chain type 1");
		} 
		} else if (arrow.isKind(ARROW))  // the following cases with Arrow
		{
			if(chain.getTypeName().isType(IMAGE) && chainElem instanceof FilterOpChain && (firstToken.isKind(OP_GRAY) || firstToken.isKind(OP_BLUR) || firstToken.isKind(OP_CONVOLVE)))
			{
				binaryChain.setTypeName(IMAGE);
			}
			else if( (chain.getTypeName().isType(URL)|| chain.getTypeName().isType(FILE) ) && chainElem.getTypeName().isType(IMAGE))
			{
				binaryChain.setTypeName(IMAGE);
			}
			else if(chain.getTypeName().isType(FRAME) && chainElem instanceof FrameOpChain && (firstToken.isKind(KW_XLOC) || firstToken.isKind(KW_YLOC) ))
			{
				binaryChain.setTypeName(INTEGER);
			}
			else if(chain.getTypeName().isType(FRAME) && chainElem instanceof FrameOpChain && (firstToken.isKind(KW_SHOW) || firstToken.isKind(KW_HIDE) || firstToken.isKind(KW_MOVE))){
				binaryChain.setTypeName(FRAME);
			}
			else if(chain.getTypeName().isType(IMAGE) && chainElem instanceof ImageOpChain && (firstToken.isKind(OP_WIDTH) || firstToken.isKind(OP_HEIGHT))){
				binaryChain.setTypeName(INTEGER);
			}
			else if(chain.getTypeName().isType(IMAGE) && chainElem.getTypeName()==(FRAME)){
				binaryChain.setTypeName(FRAME);
			}
			else if(chain.getTypeName().isType(IMAGE) && chainElem.getTypeName()==(FILE)){
				binaryChain.setTypeName(NONE);
			}
			else if(chain.getTypeName().isType(IMAGE) && chainElem instanceof ImageOpChain && firstToken.isKind(KW_SCALE)){
				binaryChain.setTypeName(IMAGE);
			}
			else if(chain.getTypeName().isType(IMAGE) && chainElem instanceof IdentChain && chainElem.getTypeName() == IMAGE){
				binaryChain.setTypeName(IMAGE);
			}
			else if(chain.getTypeName().isType(INTEGER) && chainElem instanceof IdentChain && chainElem.getTypeName() == INTEGER){
				binaryChain.setTypeName(INTEGER);
			}
			else{
				throw new TypeCheckException("invalid binary chain type 2");
			}
		}
		else{
			throw new TypeCheckException("invalid binary chain type 3");
		}
		if(binaryChain.getTypeName() ==null){
			throw new TypeCheckException("invalid binary chain type 4");
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// Auto-generated method stub
		//author: Haitang Wang
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();
		Token operator = binaryExpression.getOp();
		e0.visit(this,  null);
		e1.visit(this, null);
		if((operator.isKind(EQUAL) || operator.isKind(NOTEQUAL)) && e0.getTypeName() == e1.getTypeName()){
			binaryExpression.setTypeName(BOOLEAN);
		} else if(e0.getTypeName()==INTEGER && (operator.isKind(PLUS)|| operator.isKind(MINUS)) && e1.getTypeName()==INTEGER){
			binaryExpression.setTypeName(INTEGER);
		}
		else if(e0.getTypeName() ==IMAGE && (operator.isKind(PLUS) || operator.isKind(MINUS) && e1.getTypeName() ==IMAGE)){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0.getTypeName() ==INTEGER && (operator.isKind(TIMES) || operator.isKind(DIV)) && e1.getTypeName() ==INTEGER){
			binaryExpression.setTypeName(INTEGER);
		}
		else if(e0.getTypeName() ==INTEGER && operator.isKind(TIMES) && e1.getTypeName()==IMAGE){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0.getTypeName() ==IMAGE && (operator.isKind(TIMES) || operator.isKind(DIV)) && e1.getTypeName() ==INTEGER){
			binaryExpression.setTypeName(IMAGE);
		}
		else if(e0.getTypeName() ==INTEGER && (operator.isKind(LT) || operator.isKind(GT) || operator.isKind(LE) || operator.isKind(GE)) && e1.getTypeName() ==INTEGER){
			binaryExpression.setTypeName(BOOLEAN);
		}
		else if(e0.getTypeName() ==BOOLEAN && (operator.isKind(LT) || operator.isKind(GT) || operator.isKind(LE) || operator.isKind(GE)) && e1.getTypeName() ==BOOLEAN)
		{
			binaryExpression.setTypeName(BOOLEAN);
		}
		else if((operator.isKind(EQUAL) || operator.isKind(NOTEQUAL)) && e0.getTypeName() == e1.getTypeName()){
			binaryExpression.setTypeName(BOOLEAN);
		} else if (e0.getTypeName() ==BOOLEAN && (operator.isKind(AND) || operator.isKind(OR)) && e1.getTypeName() ==BOOLEAN) {
			binaryExpression.setTypeName(BOOLEAN);
		} else if (e0.getTypeName() ==INTEGER && operator.isKind(MOD) && e1.getTypeName() ==INTEGER) {
			binaryExpression.setTypeName(INTEGER);
		} else if (e0.getTypeName() ==IMAGE && operator.isKind(MOD) && e1.getTypeName() ==INTEGER) {
			binaryExpression.setTypeName(IMAGE);
		}
		else{
			throw new TypeCheckException("invalid binary expression type");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//  Auto-generated method stub
		
		//Author: Haitang Wang
		symtab.enterScope();
		ArrayList<Dec> declist = block.getDecs();
		
		ArrayList<Statement> statementList = block.getStatements();
		
		for (Dec dec: declist){
			dec.visit(this, arg);
		}
		
		for(Statement s: statementList){
			s.visit(this, null);
		}
		
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// Auto-generated method stub
		booleanLitExpression.setTypeName(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//  Auto-generated method stub
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, null);
		if(tuple.getExprList().size() !=0){
			throw new TypeCheckException("FIlterOpChain error");
		}
		else{
			filterOpChain.setTypeName(IMAGE);
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//  Auto-generated method stub
		Token frameOp = frameOpChain.getFirstToken();
		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);
		if((frameOp.isKind(KW_SHOW) || frameOp.isKind(KW_HIDE)) && tuple.getExprList().size()==0){
			frameOpChain.setTypeName(NONE);
		}
		else if( (frameOp.isKind(KW_XLOC) || frameOp.isKind(KW_YLOC) ) && tuple.getExprList().size()==0)
		{
			frameOpChain.setTypeName(INTEGER);
		}
		else if( frameOp.isKind(KW_MOVE) && tuple.getExprList().size()==2){
			frameOpChain.setTypeName(NONE);
		}
		else 
		{
			throw new TypeCheckException("FrameOpChain error");
		}
		
		//tuple.visit(this,null);
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		//  Auto-generated method stub
		String ident = identChain.getFirstToken().getText();
		
		Dec dec =symtab.lookup(ident);
		
		if(dec == null){
			throw new TypeCheckException("Ident: " + ident + "has not been declared yet.");
		}
		identChain.setTypeName(Type.getTypeName(dec.getType()));
		identChain.setDec(dec);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//  Auto-generated method stub
		String ident = identExpression.getFirstToken().getText();
		Dec dec = symtab.lookup(ident);
		
		if(dec ==null){
			throw new TypeCheckException("identExpression: " + ident + "has not been declared yet.");
		}
		identExpression.setTypeName(Type.getTypeName(dec.getType()));
		identExpression.setDec(dec);
		return null;
	}
	
	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//  Auto-generated method stub
		Expression e = ifStatement.getE();
		Block b = ifStatement.getB();
		e.visit(this, null);
		b.visit(this,null);
		if(BOOLEAN != e.getTypeName()){
			throw new TypeCheckException("IfStatement needs to be boolean");
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//  Auto-generated method stub
		intLitExpression.setTypeName(INTEGER);;
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//  Auto-generated method stub
		Expression e = sleepStatement.getE();
		e.visit(this, null);
		if(e.getTypeName()!=(INTEGER)){
			throw new TypeCheckException("SleepStatement requires the type to be INTEGER");
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//  Auto-generated method stub
		Expression e = whileStatement.getE();
		Block b = whileStatement.getB();
		e.visit(this, null);
		b.visit(this, null);
		if(e.getTypeName() != (BOOLEAN)){
			throw new TypeCheckException("WhileStatement requires the type to be Boolean");
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//  Auto-generated method stub
		declaration.setTypeName(Type.getTypeName(declaration.getType()));
		boolean status = symtab.insert(declaration.getIdent().getText(),declaration);
		if(!status){
			throw new TypeCheckException("THe dec has been declared already");
		}
		return null;
	}
	
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		//  Auto-generated method stub
		
		ArrayList<ParamDec> paramDecList = program.getParams();
		Block b = program.getB();
		
		for(ParamDec paramdec: paramDecList){
			paramdec.visit(this,null);
		}
		
		b.visit(this,null);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		//  Auto-generated method stub
		IdentLValue var= assignStatement.getVar();
		Expression e=assignStatement.getE();
		
		e.visit(this, null);
		var.visit(this, null);
		TypeName t1 = var.getDec().getTypeName();
		TypeName t2 = e.getTypeName();
		if( t1 != t2){
			throw new TypeCheckException("AssignmentStatement error");
		}
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		String ident = identX.getFirstToken().getText();
		Dec dec = symtab.lookup(ident);
		if (dec == null) {
			throw new TypeCheckException("visitIdentChain: ident '" + ident
					+ "' has not been declared or is not visible in the current scope ");
		}
		dec.setTypeName(Type.getTypeName(dec.getType()));
		identX.setDec(dec);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		paramDec.setTypeName(Type.getTypeName(paramDec.getType()));
		boolean success = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if (!success) {
			throw new TypeCheckException(
					"The variable '" + paramDec.getIdent().getText() + "' is already declared in this scope");
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.setTypeName(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		Token imageOp = imageOpChain.getFirstToken();
		Tuple argTuple = imageOpChain.getArg();
		
		argTuple.visit(this, arg);
		if (imageOp.isKind(Kind.OP_WIDTH) || imageOp.isKind(Kind.OP_HEIGHT)) {
			if (argTuple.getExprList().size() != 0) {
				throw new TypeCheckException("visitImageOpChain: Tuple size is not 0.");
			}
			imageOpChain.setTypeName(TypeName.INTEGER);
		} else if (imageOp.isKind(Kind.KW_SCALE)) {
			if (argTuple.getExprList().size() != 1) {
				throw new TypeCheckException("visitImageOpChain: Tuple size is not 1.");
			}
			imageOpChain.setTypeName(TypeName.IMAGE);
			
			//argTuple.visit(this, null);

		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> exprList = tuple.getExprList();
		for (Expression expression : exprList) {
			expression.visit(this, arg);
			if (TypeName.INTEGER != expression.getTypeName()) {
				throw new TypeCheckException("visitTuple: Expression type is not an INTEGER.");
			}
		}
		return null;
	}

}
