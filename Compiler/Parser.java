package cop5556sp17;
import cop5556sp17.AST.*;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.List;

//import java.util.List;
//import java.util.ArrayList;
import cop5556sp17.Scanner.*;//Token;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program program;
		program = program();
		matchEOF();
		return program;
	}

	Expression expression() throws SyntaxException {
		
		Token firstToken, operator;
		Expression expression, rightExpression;
		
		int count =0;
		
		//Error: long place handling PARENS
//		if(t.isKind(LPAREN))
//		{
//			count++;
//			consume();
//		}
		firstToken = t;		
		//Author: Haitang Wang----Implemented
		try {
			expression=term();
			//System.out.println("after term: \t" + t.getText());	
			while(isRelOp() )
			{			
				operator = consume();
				rightExpression = term();
				expression = new BinaryExpression(firstToken, expression, operator, rightExpression);
			
				}	
			if(count>0){
				//match(RPAREN);
			}
			return expression;
			}
		 catch(SyntaxException e){
			   // System.out.println("Incorrect expression__Try");
				throw new SyntaxException("Incorrent expression");
		}
		
	}

	Expression term() throws SyntaxException {
		
		//Author: Haitang ----Implemented
		Expression expression, rightExpression;
		Token firstToken, operator;
		firstToken = t;		
		try{
			//factor();
			expression = elem();
			while(isWeakOp())
			{
				//System.out.println("WeakOp: " + t.getText());
				operator = consume();
				rightExpression = elem();
				expression = new BinaryExpression(firstToken, expression, operator, rightExpression);
				
			}
		}
		catch (SyntaxException e){
			//System.out.println("INcorrent term");
			throw new SyntaxException("Not correct term");
		}
		return expression;
	}

	Expression elem() throws SyntaxException {
		
		Expression expression, rightExpression;
		Token firstToken, operator;
		firstToken = t;
		try{
			expression = factor();
			//System.out.println(t.getText() + "This token");

			//boolean tem = t.isKind(TIMES)|| t.isKind(DIV)|| t.isKind(AND)|| t.isKind(MOD);
			//System.out.println(tem);

			while(isStrongOp())
			{
				//System.out.println("isStrongOp: " + t.getText());
				operator = consume();
				
				rightExpression = factor();
				expression = new BinaryExpression(firstToken, expression, operator, rightExpression);

			}
			return expression;

		}
		catch (SyntaxException e) {
			//System.out.println("Incorrent elem");
			throw new SyntaxException("Incorrent elem");
		}
		
	}

	Expression factor() throws SyntaxException {
		Token firstToken;
		Expression expression = null;
		Kind kind = t.kind;
		firstToken = t;
		switch (kind) {
		case IDENT: {
			match(t.kind);
			expression = new IdentExpression(firstToken);		}
			break;
		case INT_LIT: {
			//System.out.println(t.getText());
			match(t.kind);
			expression = new IntLitExpression(firstToken);//consume();

		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			match(t.kind);
			expression = new BooleanLitExpression(firstToken);
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			match(t.kind);
			expression = new ConstantExpression(firstToken);
		}
			break;
		case LPAREN: {
			match(t.kind);
			expression = expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor");
		}
		return expression;
	}

	Block block() throws SyntaxException {
		//
		//Author: Haitang Wang  ---Implemented
		Token firstToken;
		firstToken = t;
		Block block;
		ArrayList<Dec> decs = new ArrayList<Dec>();
		ArrayList<Statement> statements = new ArrayList<Statement>();
//		try{
			if(t.isKind(LBRACE))
			{
				//System.out.println("Inside block");

				match(LBRACE);

				while(isDec() || isStatement())
				{
					if(isDec())
					{
						decs.add(dec());
					}
					else
					{
						//System.out.println("Statement section: " + t.getText());

						statements.add(statement());
					}				
				} //end While	
				//System.out.println("Expecting }: " + t.getText());
				match(RBRACE);
				//return;
				//System.out.println("first token in block inside block(): " + t.getText());
				block = new Block(firstToken, decs, statements);
				return block;
			}
			else{
				throw new SyntaxException("Not correct block");
			}
			// can be empty in the block
			
							
//		}
//		catch(SyntaxException e)
//		{
//			throw new SyntaxException("Not correct block");
//		}
	}

	Program program() throws SyntaxException {
		
		Program program;
		Token firstToken;
		firstToken = t;
		ArrayList<ParamDec> paramList = new ArrayList<ParamDec>();
		Block b;
//		try{
			if(t.isKind(IDENT))
			{
				//System.out.println("IDENT token:" +t.getText());
 
				match(IDENT);
/*				Token temp;
				temp = scanner.peek();*/
				//System.out.println("token after IDENT:" +t.getText());
				if(t.isKind(LBRACE))
				{
					//System.out.println("we are here:" );
					b = block();
				}
				else
				{
					//System.out.println("first totekn in paramDec: " +t.getText());

					paramList.add(paramDec());
					
/*					while(t.isKind(LPAREN)){
						match(LPAREN);
						paramDec();
					}*/
					//System.out.println("first totekn after paramDec: " +t.getText());

					while(t.isKind(COMMA)){
						match(COMMA);
						//System.out.println("first totekn in paramDec: " +t.getText());

						paramList.add(paramDec());
					}
					//System.out.println("before block: " +t.getText());

					b = block();
				}
				
				//System.out.println("CUrrent token:" +t.getText());

				program = new Program(firstToken, paramList, b);
				return program;
			}
			
			else{
				throw new SyntaxException("Incorrent program1");
			}
//		}
//		catch (SyntaxException e)
//		{
//			throw new SyntaxException("Incorrent program2");
//		}
	}

	ParamDec paramDec() throws SyntaxException {
		
		//Author: Haitang Wang ----implemented
		//System.out.println("Keyword" + t.getText());
		ParamDec paramDec;
		Token firstToken, ident;
		firstToken = t;
			if(t.isKind(KW_URL)|| t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN))
			{
				consume();
				if(t.isKind(IDENT))
				{
					ident = consume();
					paramDec = new ParamDec(firstToken, ident);
					//System.out.println("ParamDec String:" + paramDec.toString());
					return paramDec;
				}
				else{
					throw new SyntaxException("Incorrent paramDec");
				}
			}
			else
			{
				throw new SyntaxException("Incorrent paramDec");

			}

	}

	Dec dec() throws SyntaxException {
		
		//Author: Haitang Wang -----Implemented
		Token firstToken, identToken;
		firstToken = t;
		Dec dec;		
		if (isDec())
		{
			firstToken =consume();
			if (t.isKind(IDENT))
			{
				identToken = consume();
				//System.out.println("inside dec(): firstToken: " + firstToken.getText() + " ident toekn: "+identToken.getText());
				dec = new Dec(firstToken,identToken);
				
				//System.out.println("Exist dec()");
				return dec;

			}
			
			else
				throw new SyntaxException("Incorrect dec in IDENT");
		}
		else
		{
			throw new SyntaxException("Incorrect dec");
		}
	}

	Statement statement() throws SyntaxException {
		
		Statement statement;
		//Author: Haitang Wang --Implemented
		Expression expression;
		Block block;
		Token firstToken;
		Chain chain;
		firstToken = t;
		
		//System.out.println("inside statement: " + firstToken.getText());
//		try{
			switch(t.kind)
			{
			case OP_SLEEP:
			{
				consume();
			//	System.out.println("before expression: " + t.getText());
//				if(t.isKind(LPAREN))
//					consume();
				expression = expression();
//				if(t.isKind(RPAREN))
//					consume();

				match(SEMI);
				//System.out.println("FIrst token: " + firstToken.getText());
				statement = new SleepStatement(firstToken, expression);
			} break;
			
			case KW_WHILE:
			{
				//while statement
/*				consume();
				match(LPAREN);
				expression();
				match(RPAREN);	
				block();*/			
				
				try{
					//System.out.println("Print current key world: \t" + t.getText());
					if(t.isKind(KW_WHILE)){
						consume();
						//System.out.println("Print current key world: \t" + t.getText());
						match(LPAREN);
						//System.out.println("Print current key world: \t" + t.getText());

						expression = expression();
						//System.out.println("Print current key world: \t" + t.getText());

						match(RPAREN);
						block = block();
						statement = new WhileStatement(firstToken, expression,block);
					}
					else{
						throw new SyntaxException("Incorrect while statement");
					}
				}
				catch(SyntaxException e){
					throw new SyntaxException("Incorrect while statement");
				}
				//whileStatement();
				
			} break;
			
			case KW_IF:
			{
				//if statement
				/*consume();
				match(LPAREN);
				expression();
				match(RPAREN);
				block();*/
				if(t.isKind(KW_IF)){
					consume();
					match(LPAREN);
					expression = expression();
					match(RPAREN);
					block=block();
					statement = new IfStatement(firstToken, expression, block);
				}
				else{
					throw new SyntaxException("Incorrect while statement");
				}
				//ifStatement();
			} break;
			
			//partial Chain
			case OP_BLUR: //Chain chainElem FilterOp
			case OP_GRAY:
			case OP_CONVOLVE://FilterOP
			case KW_SHOW: //frameOP
			case KW_HIDE:
			case KW_MOVE:
			case KW_XLOC:
			case KW_YLOC:   //end frameOP
			case OP_WIDTH:   //Start ImageOp
			case OP_HEIGHT:  //end ImageOP
			case KW_SCALE:
			{
				chain = chain();
				match(SEMI);
				statement = chain;
			} break;		
			case IDENT:  // This can either be ASSIGN or chain
			{
			//	System.out.println("In IDENT case: " );
				Token tem;
				tem = scanner.peek();//scanner.tokens.get(scanner.tokenNum+1);
				if (tem.isKind(ASSIGN))
				{
					statement = assign();	
					match(SEMI);
				}
				else
				{
					chain = chain();
					statement = chain;
					match(SEMI);
				}				
			} break;
			
			default: 
				throw new SyntaxException("Incorrent statements really?" );}
			
			//System.out.println("Statement return: ");
			return statement;

//		}
//		catch (SyntaxException e){
//			throw new SyntaxException("Incorrent statements");
//		}

		
	}

	Chain chain() throws SyntaxException {
		Token firstToken, operator;
		ChainElem chainElem;
		Chain chain;

		firstToken = t;
		// Author: Haitang Wang  ---Implemented
		try {
			chain = chainElem();
			arrowOp();
			operator = consume();
			chainElem = chainElem();
		} catch (SyntaxException e) {
			throw new SyntaxException("Incorrect statement starts with...");
		}
		chain = new BinaryChain(firstToken, chain, operator, chainElem);
		while (isArrowOp()) {
			arrowOp();
			operator = consume();
			chainElem = chainElem();
			chain = new BinaryChain(firstToken, chain, operator, chainElem);
		}
		return chain;
		
		
	}

	ChainElem chainElem() throws SyntaxException {

		Token firstToken;
		Tuple tuple;
		ChainElem chainElem=null;
		
		//Author: Haitang Wang-------Implemented
		//consume();
		//System.out.println("Enter chainElem: " + t.getText());
		firstToken = t; //Keyworld 
		if (t.isKind(IDENT) || isFilterOp() || isFrameOp() || isImageOp())
		{
			if(t.isKind(IDENT))
			{
			match(IDENT);
			chainElem = new IdentChain(firstToken);
			}

			else
			{
				if(isFilterOp()){
					//System.out.println("Filter KEY: " + t.getText());
					firstToken=consume();
					//System.out.println("After KeY: " + t.getText());
					tuple = arg();
					//System.out.println("After arg(): " + t.getText());

					chainElem = new FilterOpChain(firstToken, tuple);
					
				}
				
				if(isFrameOp()){
					firstToken = consume();
					tuple = arg();
					chainElem = new FrameOpChain(firstToken, tuple);
				}
				
				if(isImageOp()){
					firstToken = consume();
					tuple = arg();
					chainElem = new ImageOpChain(firstToken, tuple);
				}
			}
			return chainElem;

		}
		else 
			throw new SyntaxException("incorrect chainElem");
	}

	Tuple arg() throws SyntaxException {
		Token firstToken;
		Expression expression;
		ArrayList<Expression> expressionList = new ArrayList<Expression>();
		Tuple tuple;
		
		firstToken = t;
		if (t.isKind(LPAREN)) {
			match(LPAREN);
			expression = expression();
			expressionList.add(expression);
			while (t.isKind(COMMA)) {
				match(COMMA);
				expression = expression();
				expressionList.add(expression);
			}
			match(RPAREN);
		} 
		
		tuple = new Tuple(firstToken, expressionList);
		return tuple;
		
		/*
		
		Tuple tuple=null;
		List<Expression>  expressionList = new ArrayList<Expression>();
		Token firstToken = t;
		try{
			//System.out.println("First token in Tuple: "+ t.getText());

			if(t.isKind(LPAREN))
			{

				match(LPAREN);

				expressionList.add(expression());
			
				while (t.isKind(COMMA))
				{
					//System.out.println("Expect ,"+ t.getText());
					match(COMMA);
				
				//	System.out.println(scanner.tokenNum + " Token size");

				    int countLparen =0;
				    if(t.isKind(LPAREN))
				    {
				    	match(LPAREN);

				    	countLparen++;
				    }
					expressionList.add(expression());
					if(countLparen>0)
						match(RPAREN);
				}
				//System.out.println("Expect ): "+t.getText());
				match(RPAREN);
			   //	System.out.println("After ): "+t.getText());
				
			//	System.out.println("tuple size of Expression list: " +expressionList.size());
				return tuple;
				

			}
			else
			{
				//consume();
				tuple = new Tuple(firstToken, expressionList);
			    //System.out.println("EMpty TUple");
				return tuple;	
			}		
		}		
		catch(SyntaxException e){
			//System.out.println("Incorrent arg() How come?");
			throw new SyntaxException("Incorrent arg()");
		}

*/
	}
	
	AssignmentStatement assign() throws SyntaxException {
		//Author: Haitang Wang --IMpelmented
		AssignmentStatement statement;
		Token firstToken;
		firstToken = t;
		IdentLValue var;
		Expression expression;	
		//System.out.println("Inside Assign: " + t.getText());
		try{
			var = identLValue();
			consume();
			//System.out.println("Expected Assign: " + t.getText());

			match(ASSIGN);
			expression = expression();
			statement = new AssignmentStatement(firstToken, var, expression);
			return statement;
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Incorrect assign");
		}
	}
	
	WhileStatement whileStatement() throws SyntaxException{
		Expression expression;
		WhileStatement whileStatement;
		Block block;
		Token firstToken;
		firstToken =t;

		try{
			//System.out.println("Print current key world: \t" + t.getText());
			if(t.isKind(KW_WHILE)){
				consume();
				//System.out.println("Print current key world: \t" + t.getText());
				match(LPAREN);
				//System.out.println("Print current key world: \t" + t.getText());

				expression = expression();
				//System.out.println("Print current key world: \t" + t.getText());

				match(RPAREN);
				block = block();
			}
			else{
				throw new SyntaxException("Incorrect while statement");
			}
		}
		catch(SyntaxException e){
			throw new SyntaxException("Incorrect while statement");
		}
		whileStatement = new WhileStatement(firstToken,expression,block);
		return whileStatement;

	}
	
	
	IfStatement ifStatement() throws SyntaxException{
		
		Expression expression;
		IfStatement ifStatement;
		Block block;
		Token firstToken;
		firstToken =t;
			if(t.isKind(KW_IF)){
				consume();
				match(LPAREN);
				expression = expression();
				match(RPAREN);
				block=block();
			}
			else{
				throw new SyntaxException("Incorrect while statement");
			}
			ifStatement = new IfStatement(firstToken,expression,block);
			return ifStatement;
		}
	
	IdentLValue identLValue(){
		IdentLValue identLValue;
		Token firstToken;
		firstToken = t;
		identLValue = new IdentLValue(firstToken);
		return identLValue;
	}
	
    boolean isFilterOp()  {
    	if(t.isKind(OP_BLUR)|| t.isKind(OP_GRAY) ||t.isKind(OP_CONVOLVE) )
    		return true;
    	else 
    		return false;
    }
    
    boolean isFrameOp()   {
    	if(t.isKind(KW_SHOW)|| t.isKind(KW_HIDE) ||t.isKind(KW_MOVE ) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC) )
    		return true;
    	else 
    		return false;
    }
    
    boolean isImageOp() {
    	if(t.isKind(OP_WIDTH)|| t.isKind(OP_HEIGHT) ||t.isKind(KW_SCALE)  )
    		return true;
    	else 
    		return false;
    }
    
    boolean isWeakOp(){
    	if(t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR))
    		return true;
    	else
    		return false;
    }
    boolean isArrowOp()  {
    	if(t.isKind(ARROW)|| t.isKind(BARARROW)  )
    		return true;
    	else 
    		return false;
    }
    
    boolean isStatement()
    {
    	if(t.isKind(OP_SLEEP)|| t.isKind(KW_WHILE)||t.isKind(KW_IF) || t.isKind(IDENT) || isFilterOp() ||isFrameOp() || isImageOp() )
    	{
        	return true;    	
    	}
    	else
    		return false;
   	
    }
    
    boolean isDec() {
    	if(t.isKind(KW_FRAME)|| t.isKind(KW_IMAGE)|| t.isKind(KW_INTEGER)|| t.isKind(KW_BOOLEAN))
    		return true;
    	else
    		return false;
    }
    
    boolean isStrongOp()
    {
    	if (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD))
    		return true;
    	else
    		return false;
    }
    
    boolean isRelOp(){
    	if(t.isKind(LT) || t.isKind(LE)|| t.isKind(GE) ||t.isKind(GT) || t.isKind(EQUAL) || t.isKind(NOTEQUAL))
    		return true;
    	else
    		return false;
    }
    
    void relOp() throws SyntaxException{
    	if(t.isKind(LT) || t.isKind(LE)|| t.isKind(GE) ||t.isKind(GT) || t.isKind(EQUAL) || t.isKind(NOTEQUAL))
    	{
    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    
    void strongOp() throws SyntaxException{
    	if (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD))
    	{
    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    
    void arrowOp() throws SyntaxException{
    	if(t.isKind(ARROW)|| t.isKind(BARARROW)  )
    	{
    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    void frameOp() throws SyntaxException{
    	if(t.isKind(KW_SHOW)|| t.isKind(KW_HIDE) ||t.isKind(KW_MOVE ) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC) )
    	{ 		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    
    void imageOp() throws SyntaxException{
    	if(t.isKind(OP_WIDTH)|| t.isKind(OP_HEIGHT) ||t.isKind(KW_SCALE)  )
    	{    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    void weakOp() throws SyntaxException{
        if(t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR))
    	{
    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
    void filterOp() throws SyntaxException{
    	if(t.isKind(OP_BLUR)|| t.isKind(OP_GRAY) ||t.isKind(OP_CONVOLVE) )
    	{
    		
    	}
    	else
    	{
			throw new SyntaxException("Incorrect while statement");

    	}
	}
	

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			
			//System.out.println(t.getText()+"before consume");

			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		//System.out.println(scanner.tokenNum+"After Consume" + tmp.getText() + "Consumed token");
		//System.out.println(t.getText()+" Next token");

		return tmp;
	}
	
	
	
	/*//Added new code section
	public class RuleElement{
		private String text;
		
		//construct a new ruleElement with a string identifier
		public RuleElement(String text){
			this.text = text;
		}
		
		public String getText(){
			return text;
		}
		
		@Override
		public String toString(){
			return text;
		}
		
		@Override 
		public boolean equals(Object o)
		{
			if(o instanceof RuleElement){
				RuleElement other = (RuleElement) o;
				return other.text.equals(text);
			}
			return false;
		}
	}
	
	
	// Terminal 
	public class Terminal extends RuleElement{
		
		*//**
		 * construct a new terminal with a string identifier
		 *//*
		
		public Terminal(String text){
			super(text);
		}
		
		//DO I need to modify this code?
		public Kind Kind(){
			for(Kind kind : Kind.values())
			{
				if(kind.getText().equals(getText()))
				{
					return kind;
				}
			}
			return T_UNKNOWN;
		}
		
		public boolean isEmptyString(){
			return Kind()==Kind.T_EMPTY_STRING;
		}
		
	}
	
	
	//TerminalPair 
	
	 * Paring of a terminal and a list of RuleElements
	 
	public class TerminalPair{
		private Terminal terminal;
		private List<RuleElement> ruleElements;
		
		//construct a new terminalPair with the given terminal and rulements
		public TerminalPair(Terminal terminal, List<RuleElement> ruleElements){
			this.terminal = terminal;
			this.ruleElements = ruleElements;
		}
		
		//get a terminal
		public Terminal getTerminal(){
			return terminal;
		}
		// get a ruelment
		public List<RuleElement> getRuleElements(){
			return ruleElements;
		}
		
		//Determine if a list of terminal contains a specific terminal
		public boolean terminalPairListContainsTerminal(List<TerminalPair> terminalPairs, Terminal terminal) {
			for (TerminalPair p : terminalPairs) {
				if (p.getTerminal().equals(terminal)) {
					return true;
				}
			}
			
			return false;
	 	}
		
		@Override
		public String toString(){
			return terminal +","+ruleElements;
		}	
	}
	
	// Variable represtned by a String
	public class Variable extends RuleElement{
		//construct a new variable with a string text
		public Variable(String text){
			super(text);
		}
	}
	
	
	 * A rule consists of a variable and a right side which consisits of RuleElements
	 * 
	 
	public class Rule{
		private Variable leftSide;
		private List<RuleElement> rightSide;
		
		//Construct a new Rule with a variable for the left side and list of ruleElements
		// for the right side
		public Rule(Variable leftSide, List<RuleElement> tailRuleRightSide)
		{
			this.leftSide = leftSide;
			this.rightSide = tailRuleRightSide;
		}
		
		//Construct a new rule with a variable for the left side and a single RuleELement for the right side
		public Rule(Variable leftSide, RuleElement rightSide){
			this.leftSide = leftSide;
			this.rightSide = new ArrayList<RuleElement>();
			this.rightSide.add(rightSide);			
		}
		
		//Determine whether or not this Rule has left recursion
		public boolean hasLeftRecursion(){
			return leftSide == rightSide.get(0);
		}
		
		//return leftside of Rule
		public Variable getLeftSide()
		{
			return leftSide;
		}
		
		public List<RuleElement> getRightSide(){
			return rightSide;
		}
		
		//Add a variable to the right side of the Rule
		public void addToRightSide(Variable ruleElement){
			rightSide.add(ruleElement);
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append(leftSide + ":");
			
			for (RuleElement re: rightSide){
				sb.append(re + " ");
			}
			
			return sb.toString();
		}
	}
	
	//Terminal that represents emptyString
	public class EmptyString extends Terminal{
		
		public EmptyString(){
			super("{epsilon}");
		}
	}*/
	
	//Important LL1 Grammar: lists of terminals, list of variables, list of rules, and a start variable
}
