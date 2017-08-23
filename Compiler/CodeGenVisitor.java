package cop5556sp17;
 
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		int argIndex =0;
		for (ParamDec dec : params)
			dec.visit(this, argIndex++);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, 1);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//  visit the local variables
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		//CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		
		//left =1 , right = 0
		binaryChain.getE0().visit(this, 1);
		binaryChain.getE1().visit(this, 0);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //  Implement this
		Expression expr0=binaryExpression.getE0();
		Expression expr1=binaryExpression.getE1();
		Label l0, l1;
		Token operator;
		operator = binaryExpression.getOp();
	
		
		expr0.visit(this,arg);
		expr1.visit(this, arg);
		
		//divide cases based on operators
		switch(operator.kind){
		case PLUS:
			if(expr0.getTypeName() == TypeName.IMAGE){
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "add", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			}
			else{
				mv.visitInsn(IADD);
			}
			break;
		case MINUS:
			
			if(expr0.getTypeName() == TypeName.IMAGE){
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "sub", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			}
			else{
				mv.visitInsn(ISUB);
			}
			break;
		case DIV:
			if(expr0.getTypeName() == TypeName.IMAGE){
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "div", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			}
			else{
				mv.visitInsn(IDIV);
			}
			break;
		case TIMES:
			if(expr0.getTypeName()==TypeName.IMAGE || expr1.getTypeName()==TypeName.IMAGE){
				if(expr0.getTypeName()==TypeName.IMAGE){
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
				}
				else{//image on the right side
					
					mv.visitInsn(SWAP); 
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
				}
			} else {
			mv.visitInsn(IMUL);
			}
			break;
		case LT:
			l0 = new Label();
			l1 = new Label();
			mv.visitJumpInsn(IF_ICMPGE,  l0);
			mv.visitInsn(ICONST_1);			
		   
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l1);
			break;
		case GT:
			l0 = new Label();
			l1 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l0);
			mv.visitInsn(ICONST_1);
			
			mv.visitJumpInsn(GOTO,l1);
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l1);
			break;
			
		case EQUAL:
			l0 = new Label();
			l1 = new Label();
			if (expr0.getTypeName() == TypeName.INTEGER ||expr0.getTypeName() == TypeName.BOOLEAN )
			{
				mv.visitJumpInsn(IF_ICMPNE, l0);
				mv.visitInsn(ICONST_1);
				
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l1);
			}
			else{
				mv.visitJumpInsn(IF_ACMPNE, l0);
				mv.visitInsn(ICONST_1);
				
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l1);
			}

			break;
			
		case NOTEQUAL:
			
			l0 = new Label();
			l1 = new Label();
			if (expr0.getTypeName() == TypeName.INTEGER ||expr0.getTypeName() == TypeName.BOOLEAN )
			{
				mv.visitJumpInsn(IF_ICMPEQ, l0);
				mv.visitInsn(ICONST_1);
				
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l1);
			}
			else{//if it's image
				mv.visitJumpInsn(IF_ACMPEQ, l0);
				mv.visitInsn(ICONST_1);
				
				mv.visitJumpInsn(GOTO, l1);
				mv.visitLabel(l0);
				mv.visitInsn(ICONST_0);
				mv.visitLabel(l1);
			}

			break;
			
		case GE:
			l0 = new Label();
			l1 = new Label();
			mv.visitJumpInsn(IF_ICMPLT,l0);
			mv.visitInsn(ICONST_1);
			
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l1);
			break;
			
		case LE:
			l0 = new Label();
			l1 = new Label();
			mv.visitJumpInsn(IF_ICMPGT, l0);
			mv.visitInsn(ICONST_1);
			
			mv.visitJumpInsn(GOTO,l1);
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(l1);
			break;
		
		case AND:
			mv.visitInsn(IAND);
			break;
		case OR:
			mv.visitInsn(IOR);
			break;
		case MOD:
			if (expr0.getTypeName() == TypeName.IMAGE)
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			else
				mv.visitInsn(IREM);
			
			break;
		default:
				break;
		}
		
		
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//  Implement this
		Label blockStart;
		Label blockEnd;
		String varName;
		String varTypeDes;
		ArrayList<Dec> decList;
		ArrayList<Statement> statementList;
		int slotNumber = (Integer)arg;
		decList = block.getDecs();
		
		statementList = block.getStatements();
		
		//visit each dec to build the slotNumber array
		for(Dec dec: decList){
			dec.visit(this, slotNumber++);
		}
		
		blockStart = new Label();
		mv.visitLabel(blockStart);
		
		//visit statement with slotNumbers
		for(Statement statement: statementList){
			statement.visit(this, slotNumber);
			if (statement instanceof BinaryChain){
				mv.visitInsn(POP);
			}
		}
		
		blockEnd = new Label();
		mv.visitLabel(blockEnd);
		
		for(Dec dec:decList){
			
			varTypeDes = dec.getTypeName().getJVMTypeDesc();
			varName = dec.getIdent().getText();
			slotNumber = dec.getSlot();
			mv.visitLocalVariable(varName, varTypeDes, null, blockStart, blockEnd, slotNumber);
			
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// Implement this
		
		int status;
		status = booleanLitExpression.getValue() == true? 1:0;
		mv.visitLdcInsn(status);
		
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		switch(constantExpression.getFirstToken().kind){
		case KW_SCREENHEIGHT:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
			break;
		case KW_SCREENWIDTH:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
			break;
		default:
			break;
		}
		return null;

	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// Implement this
		int slotNumber;
		slotNumber = (Integer)arg;
		declaration.setSlot(slotNumber);
		switch(declaration.getTypeName()){
		case IMAGE:
		case FRAME:
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slotNumber);
			default:
				break;
		}
		
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		
		//filterOpChain.getArg().visit(this, arg);
		switch(filterOpChain.getFirstToken().kind){
		case OP_BLUR:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "blurOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		case OP_GRAY:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "grayOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		case OP_CONVOLVE:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFilterOps", "convolveOp", "(Ljava/awt/image/BufferedImage;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		
		frameOpChain.getArg().visit(this, arg);
		
		switch(frameOpChain.getFirstToken().kind){
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "showImage", "()Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "hideImage", "()Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "moveFrame", "(II)Lcop5556sp17/PLPRuntimeFrame;", false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getXVal", "()I", false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, "cop5556sp17/PLPRuntimeFrame", "getYVal", "()I", false);
			break;
			default:
				break;
		}
		
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// 1 = left, 0 = right
		if(identChain.getDec() instanceof ParamDec){//detDec
			if((int)arg ==1){ // left 
				switch(identChain.getDec().getTypeName()){
				case URL:
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), "Ljava/net/URL;");
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "readFromURL", "(Ljava/net/URL;)Ljava/awt/image/BufferedImage;", false);
					break;
				case FILE:
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), "Ljava/io/File;");
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "readFromFile", "(Ljava/io/File;)Ljava/awt/image/BufferedImage;", false);
					break;
				case INTEGER:
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), "I");
					break;
				default:
					break;
				}
			}
			else{
				//right
				switch(identChain.getDec().getTypeName()){
					case FILE:
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className, identChain.getDec().getIdent().getText(), "Ljava/io/File;");
						mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "write", "(Ljava/awt/image/BufferedImage;Ljava/io/File;)Ljava/awt/image/BufferedImage;", false);
						break;
					case INTEGER:
						mv.visitInsn(DUP);
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(PUTFIELD, className, identChain.getDec().getIdent().getText(), "I");
						break;
					default:
						break;
				}
			}
			
		}
		else{ //local variable 
			if((int)arg ==1) {//left
				switch(identChain.getDec().getTypeName()){
				case INTEGER:
					mv.visitVarInsn(ILOAD, identChain.getDec().getSlot());
					break;
				case IMAGE:
				case FRAME:
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					break;
				default:
					break;
				}
			}
			else{ // right side
				switch(identChain.getDec().getTypeName()){
				case INTEGER:
					mv.visitInsn(DUP);
					mv.visitVarInsn(ISTORE, identChain.getDec().getSlot());
					break;
				case IMAGE:	
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
					break;
				case FRAME:
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "createOrSetFrame", "(Ljava/awt/image/BufferedImage;Lcop5556sp17/PLPRuntimeFrame;)Lcop5556sp17/PLPRuntimeFrame;", false);
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
					break;
				default:
					break;
				}
			}
		}
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// Implement this
		String varName, varTypeDes;
		int slotNumber;
		Dec dec;
		TypeName decType;
		
		dec = identExpression.getDec();
		if(dec instanceof ParamDec){
			varName = dec.getIdent().getText();
			varTypeDes = dec.getTypeName().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD,0);
			mv.visitFieldInsn(GETFIELD,  className, varName, varTypeDes);
		}
		else{
			slotNumber = dec.getSlot();
			decType = dec.getTypeName();
			switch(decType){
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ILOAD, slotNumber);
				break;
				
			case FILE:
			case FRAME:
			case IMAGE:
			case URL:
				mv.visitVarInsn(ALOAD, slotNumber);
				break;
			case NONE:
				break;
		    default:
				break;
			}
		}
		
		
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// Implement this
		String varName, varTypeDes;
		Dec dec = identX.getDec();
		int slotNumber;
		TypeName decType;
		
		if(dec instanceof ParamDec){
			varName = dec.getIdent().getText();
			varTypeDes = dec.getTypeName().getJVMTypeDesc();
			mv.visitVarInsn(ALOAD,0);
			//swap two values in stack 
			mv.visitInsn(SWAP); 
			mv.visitFieldInsn(PUTFIELD, className,varName, varTypeDes);
			
		}
		else{
			
			slotNumber = dec.getSlot();
			decType = dec.getTypeName();
			
			switch(decType){
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ISTORE, slotNumber);
				break;
			case NONE:
				break;
			case IMAGE:
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
				mv.visitVarInsn(ASTORE, slotNumber);
				break;
			case FILE:
			case URL:
			case FRAME:
				mv.visitVarInsn(ASTORE, slotNumber);
				break;
			default:
				break;
			}
		}

		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// Implement this
		Expression expression = ifStatement.getE();
		expression.visit(this, arg);
		Block block = ifStatement.getB();
		Label label = new Label();
		mv.visitJumpInsn(IFEQ, label);
		block.visit(this, arg);
		mv.visitLabel(label);
		
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		imageOpChain.getArg().visit(this, arg);
		switch(imageOpChain.getFirstToken().kind){
		case OP_WIDTH:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", "()I", false);
			break;
		case OP_HEIGHT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", "()I", false);
			break;
		case KW_SCALE:
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "scale", "(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;", false);
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// Implement this
		int value = intLitExpression.getValue();
		mv.visitLdcInsn(value);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// Implement this
		//For assignment 5, only needs to handle integers and booleans
		FieldVisitor fieldVisitor;
		TypeName typeName;
		String varName, varTypeDes;
		int index;
		
		typeName = paramDec.getTypeName();
		varName = paramDec.getIdent().getText();
		varTypeDes =  typeName.getJVMTypeDesc();
		
		
		fieldVisitor = cw.visitField(0, varName, varTypeDes, null, null);
		fieldVisitor.visitEnd();
		
		index = (Integer)arg;

		
		switch(typeName){
		case INTEGER: //string to integer
			//load "this"
			mv.visitVarInsn(ALOAD, 0);
			//load "args"
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(index);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I",false);
			break;
		case BOOLEAN: // string to boolean
			//load "this"
			mv.visitVarInsn(ALOAD, 0);
			//load "args"
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(index);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",false);
			break;
		case URL:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);	//in case professor changes the signature of the getURL()
			break;
		case FILE:
			mv.visitVarInsn(ALOAD, 0);	//load "this"
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);	//load "args"
			mv.visitLdcInsn(index);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			break;
		case NONE:
			break;
			default:
				break;
		}
		mv.visitFieldInsn(PUTFIELD, className, varName, varTypeDes);
		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression e1;
		e1 = sleepStatement.getE();
		e1.visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for (Expression e: tuple.getExprList()){
			e.visit(this, arg);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// Implement this
		Expression expression = whileStatement.getE();
		Block block = whileStatement.getB();
		Label l0,l1;
		
		l0 = new Label();
		l1 = new Label();

		mv.visitJumpInsn(GOTO, l0);
		

		mv.visitLabel(l1);
		
		block.visit(this, arg);
		mv.visitLabel(l0);
		expression.visit(this, arg);
		
		mv.visitJumpInsn(IFNE, l1);
		
		return null;
	}

}
