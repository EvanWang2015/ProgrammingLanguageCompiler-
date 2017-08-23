package cop5556sp17;

//import static cop5556sp17.Scanner.Kind.RPAREN;

import java.util.ArrayList;
import java.util.Objects;

//import cop5556sp17.Scanner.Kind;

//import com.sun.xml.internal.ws.util.xml.XMLReaderComposite.State;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),  // OP_SLEEP? 
		KW_SCALE("scale"), EOF("eof");// T_UNKNOWN("UNKNOWN"), EMPTY_STRING("epsilon");  // KW_SCALE? 

		Kind(String text) {
			this.text = text;
		}

		final String text;
		String getText() {
			return text;
		}
		
		//Added New string
		/*private String regex;
		private Kind(String  text, String regex)
		{
			this.text =text;
			this.regex = regex;
		}
		
		public String getRegex(){
			return regex;
		}
		
		public static Kind tokenWithIdentifier(String text)
		{
			for(Kind kind : Kind.values())
			{
				if(kind.getText().equals(text))
				{
					return kind;
				}
			}
			return T_UNKNOWN;
		}
		*/
	}
	
	public static enum State{
		
		START, IN_DIGIT, IN_IDENT, AFTER_EQ, AFTER_OR, AFTER_NOT, AFTER_GT, AFTER_LT, LEFT_COM;
		
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			
			//Author: Haitang Wang
			//if (length>0)
			{
				//System.out.println("Start Position"+pos + " Read length " +length);
				String tokenTemp = chars.substring(pos,  length+pos);
				//System.out.println("Text Return"+ tokenTemp);

				return tokenTemp;
			}

			//return null;
		}
		
		
		//Add isKind() finction required in the Parser.java
		public boolean isKind(Kind kind){

			if(this.kind == kind)
			return true;
			else
				return false;

	
		}
		
		
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			
			////Author:: Haitang Wang
			// Here we only consider '\n' as the sign of a new line, so '\r' is not considered
			int i = 0, line_number = 0; 
			int position = 0;
			char temp;
			while (i < pos){
				temp = chars.charAt(i);
				//////// 
				//////// since\n is just one char
				if (Objects.equals(temp, '\n'))
				{
					line_number++;
					position = 0; //reset;
				}
				else
					position++;
				i++;
			}
						
			LinePos TempLocation = new LinePos(line_number, position);
			return TempLocation;
			
			//return null;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			// IMPLEMENT THIS
            // Use try catch
	
			String tokenString = getText();
			int number;
			number = Integer.parseInt(tokenString);
			return number;
		}
		
	}

	
	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();


	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		
		///// Haitang Wang
		int length = chars.length();
		State state = State.START;
		
		int startPos = 0;
		int ch;
		
		while (pos<=length){
			//System.out.println("CUrrent Position NUmber: " +pos + "Char: " + " " +chars.charAt(pos) + "State: " + state + "CURRENT Size of tokens"+ tokens.size());
	        	
			ch = pos<length? chars.charAt(pos):-1;		
			
			switch(state){
			case START: {
				
				startPos = pos;
				//System.out.println("CUrrent Position NUmber: ");
				//Skip white space
				if (Character.isWhitespace(ch)){ // char ch, however the ch we have is INT
					pos++;
					break;
				}
				//System.out.println("First letter: " + chars.charAt(pos));
			
				switch(ch) {
				case -1: {tokens.add(new Token(Kind.EOF, pos, 0)); pos++;} break;
				case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1)); pos++;} break;
				case '-': {
					if(pos<chars.length()-1 && chars.charAt(pos+1) =='>' )
					{
						pos++;
						tokens.add(new Token(Kind.ARROW, startPos,2)); pos++;
					}
					else
					{
						tokens.add(new Token(Kind.MINUS, startPos,1)); pos++;
						state = State.START;
					}
					} break;
				case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1)); pos++;} break;
				//case '/': {tokens.add(new Token(Kind.DIV, startPos,1)); pos++;} break;
				case '/': {
					if (pos==chars.length())
					{
						state = State.START;
						tokens.add(new Token(Kind.DIV, startPos,1));
						pos++;
					}
					
					if (pos<chars.length() - 1 && chars.charAt(pos+1) =='*' ) 
					{
						state = State.LEFT_COM;
						pos++;
					}
						
					else
					{
						state = State.START;
						tokens.add(new Token(Kind.DIV, startPos,1));
						pos++;
					}
					 } break;

				case '%': {tokens.add(new Token(Kind.MOD, startPos,1)); pos++;} break;
				case ';': {tokens.add(new Token(Kind.SEMI, startPos,1)); pos++;} break;
				case ',': {tokens.add(new Token(Kind.COMMA, startPos,1)); pos++;} break;
				case '(': {tokens.add(new Token(Kind.LPAREN, startPos,1)); pos++;} break;
				case ')': {tokens.add(new Token(Kind.RPAREN,startPos,1)); pos++;} break;
				case '{': {tokens.add(new Token(Kind.LBRACE, startPos,1)); pos++;} break;
				case '}': {tokens.add(new Token(Kind.RBRACE, startPos,1)); pos++;} break;
				case '&': {tokens.add(new Token(Kind.AND, startPos,1)); pos++;} break;
				//The following need to consider arrows
				case '<': {state = State.AFTER_LT; pos++;
				if (pos==chars.length()){
					state = State.START;
					tokens.add(new Token(Kind.LT, startPos,1));}				
				} break;
				case '>': {state = State.AFTER_GT; pos++;
				if (pos==chars.length()){
					state = State.START;
					tokens.add(new Token(Kind.GT, startPos,1));}
				
				} break;
				case '|': {
					pos++;
/*					if (pos==chars.length()){
						state = State.START;
						tokens.add(new Token(Kind.OR, startPos,1));
					}*/
					if (pos < chars.length()-1 && chars.charAt(pos) =='-' &&  chars.charAt(pos+1)=='>' )
					{
						tokens.add(new Token(Kind.BARARROW, startPos, 3));
						pos = pos+2;
					}
					else 						
					{
						tokens.add(new Token(Kind.OR, startPos,1));
					}
					//state = State.AFTER_OR;
					} break;
				case '=': {state = State.AFTER_EQ; pos++; 					//In case the last element is = before EOF
				//System.out.println("Start Position" + startPos);
				if (pos==chars.length())
					throw new IllegalCharException("illegal char" + '=' + "In the end");} break;
				case '!':
				{
					if (pos<chars.length()-1)
					{state = State.AFTER_NOT; pos++;}
					else{
						tokens.add(new Token(Kind.NOT, startPos,1)); pos++; 
						//System.out.println("Pos Value: " + pos + "Char Lneth: " + chars.length());
					}			
				} break;
				
				case '\n': 
				{
					//state = State.AFTER_BS; 
				    state = State.START;
					pos++;
					} break;
				
				default: 
				{
					if (Character.isDigit(ch)) {
						if (ch=='0')
						{
							state = State.START;
							tokens.add(new Token(Kind.INT_LIT,startPos,1 )); pos++;
						}
						else {
							state = State.IN_DIGIT; pos++;
							if (pos==chars.length()){
								state = State.START;
								tokens.add(new Token(Kind.INT_LIT,startPos,1)); break;
							}
						}	
					}
					else if (Character.isJavaIdentifierStart(ch)){
						state = State.IN_IDENT; pos++;

					}
					
					else {
						throw new IllegalCharException("illegal char" +ch+ "at pos" + pos);
					}
					
				}
					
				} //switch (ch)
			} break; // case START
			
			case IN_DIGIT: {
				if (Character.isDigit(ch)){
					state = State.IN_DIGIT; 
					pos++;
					//Token tempToken = new Token(Kind.INT_LIT, startPos, pos-startPos);
				   // int value = tempToken.intVal();
				    //System.out.println(value);
				}
				//Where an IN_DIGIT ends
				else {
					state = State.START;
					
					// Should change this section to intVal() function? 
					
					Token tempToken = new Token(Kind.INT_LIT, startPos, pos-startPos);
				    
					//String tokenString = tempToken.getText();
					int number;
					
					String tokenString = tempToken.getText();					
					try {
						number = Integer.parseInt(tokenString);
					}
					catch (NumberFormatException e) {
							throw new IllegalNumberException("Number is too large");
							//return -1;
					}	
					//Now it's safe to add the Integer List. However, the pos doesn't increase as it's not
					// a legal char for the INT_LIT
					tokens.add(new Token(Kind.INT_LIT, startPos, pos-startPos));
				}				
			} break;
			
			case IN_IDENT: {
				if (Character.isJavaIdentifierPart(ch)){
					state = State.IN_IDENT;
					pos++;
				}
				else {
					state = State.START;
					
					//Find all the keywords
					String token = chars.substring(startPos,  pos);
					//System.out.println();
					//System.out.println(token);
					if (Objects.equals(token, new String("integer"))){
						tokens.add(new Token(Kind.KW_INTEGER,startPos, pos-startPos));
							}
					else if (Objects.equals(token, new String("boolean"))) {
						tokens.add(new Token(Kind.KW_BOOLEAN,startPos, pos-startPos));
					}
					else if (Objects.equals(token,  new String("image"))){
						tokens.add(new Token(Kind.KW_IMAGE,startPos, pos-startPos));
					}
					else if (Objects.equals(token, new String("url")))
						tokens.add(new Token(Kind.KW_URL,startPos,pos-startPos));
					else if (Objects.equals(token, new String("file")))
						tokens.add(new Token(Kind.KW_FILE,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("frame")))
						tokens.add(new Token(Kind.KW_FRAME,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("while")))
						tokens.add(new Token(Kind.KW_WHILE,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("if")))
						tokens.add(new Token(Kind.KW_IF,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("sleep")))
						tokens.add(new Token(Kind.OP_SLEEP,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("screenheight")))
						tokens.add(new Token(Kind.KW_SCREENHEIGHT,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("screenwidth")))
						tokens.add(new Token(Kind.KW_SCREENWIDTH,startPos, pos-startPos));
                    // identify filter_op_keyword
					else if (Objects.equals(token,  new String ("gray")))
						tokens.add(new Token(Kind.OP_GRAY,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("convolve")))
						tokens.add(new Token(Kind.OP_CONVOLVE,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("blur")))
						tokens.add(new Token(Kind.OP_BLUR,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("scale")))
						tokens.add(new Token(Kind.KW_SCALE,startPos, pos-startPos));
					
					//image_op_keyword
					else if (Objects.equals(token,  new String ("width")))
						tokens.add(new Token(Kind.OP_WIDTH,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("height")))
						tokens.add(new Token(Kind.OP_HEIGHT,startPos, pos-startPos));

                   //frame_op_keyword
					else if (Objects.equals(token,  new String ("xloc")))
						tokens.add(new Token(Kind.KW_XLOC,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("yloc")))
						tokens.add(new Token(Kind.KW_YLOC,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("hide")))
						tokens.add(new Token(Kind.KW_HIDE,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("show")))
						tokens.add(new Token(Kind.KW_SHOW,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("move")))
						tokens.add(new Token(Kind.KW_MOVE,startPos, pos-startPos));

					// boolean_literal
					else if (Objects.equals(token,  new String ("true")))
						tokens.add(new Token(Kind.KW_TRUE,startPos, pos-startPos));
					else if (Objects.equals(token,  new String ("false")))
						tokens.add(new Token(Kind.KW_FALSE,startPos, pos-startPos));

					else 
						tokens.add(new Token(Kind.IDENT, startPos, pos-startPos));
					//System.out.println(pos-startPos + "Token length");
				}
			} break;
			
			case AFTER_EQ: {
				if (ch == '='){
					//System.out.println("Start position Check: " + startPos);

					tokens.add(new Token(Kind.EQUAL, startPos, 2)); pos++;
					//System.out.println("TEsting" + startPos);
					state = State.START;

				}
				else {
					throw new IllegalCharException("After equal should be =");
				}
					
			} break;
			
			case AFTER_NOT: {
				if (ch == '=')
				{
					//System.out.println("Start position Check: " + startPos);
					state = State.START;
					tokens.add(new Token(Kind.NOTEQUAL, startPos,2)); pos++;
				}
				else {
					state = State.START;
					tokens.add(new Token(Kind.NOT, startPos,1));
				}
				
			} break;
			
			case AFTER_LT: {
				state = State.START;
				if (ch == '='){
					tokens.add(new Token(Kind.LE, startPos, 2)); pos++;
				}
				else if(ch == '-')
				{
					tokens.add(new Token(Kind.ASSIGN, startPos,2)); pos++;
				}
				else {
					tokens.add(new Token(Kind.LT, startPos,1));
				}	
			} break;
			
			case AFTER_GT: {
				state = State.START;
				if (ch == '='){
					tokens.add(new Token(Kind.GE, startPos, 2)); pos++;
				}
/*				else if (ch == '-')
				{
					tokens.add(new Token(Kind.ASSIGN, startPos,2)); pos++;
				}*/
				else {
					tokens.add(new Token(Kind.GT, startPos,1));
				}
			} break;
			
			case AFTER_OR: {
				if (ch == '-' || ch== '>'){
					state = State.AFTER_OR;
					pos++;
				}
				
				else {
					state = State.START;
					String token = chars.substring(startPos,  pos-startPos);
					if (token.length() ==1){
						tokens.add(new Token(Kind.OR, startPos,1));
					}
					else if (token.length() == 3 && Objects.equals(token, new String("|->")))
					{
						tokens.add(new Token(Kind.BARARROW, startPos, 3));
					}
					else{
						throw new IllegalCharException("Incorrent Bararrow");
					}
					
				}
			}break;
			
			case LEFT_COM: {
				
				if (ch =='*' && (pos<chars.length()-1) && chars.charAt(pos+1) =='/')  // Then we check if it is comment
				{
					pos = pos+2;
					state = State.START;					
				}
				else{
					if (pos >=chars.length()-1)
						throw new IllegalCharException("Unpaired Comment");
					pos++;
				}
				
			} break;
			default: assert false;
			} //switch(state)
		} //while
		
		/////	
		//tokens.add(new Token(Kind.EOF,pos,0));
		//System.out.println(tokens.size());
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum =0;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	
	
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}

	
	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		
		/** 
		 * Author: Haitang Wang 
		 *This method is similar to what we have in previous 
		 */					
		LinePos TempLocation = t.getLinePos();
		return TempLocation;

	}


}
