package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class Dec extends ASTNode {
	
	final Token ident;
	TypeName typeName;
	Dec dec;
	int slotNumber;

	public Dec(Token firstToken, Token ident) {
		super(firstToken);

		this.ident = ident;
	}

	public Token getType() {
		return firstToken;
	}

	public Token getIdent() {
		return ident;
	}
	
	public TypeName getTypeName(){
		return typeName;
	}

	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

	public void setTypeName(TypeName typeName2) {
		//  Auto-generated method stub
		this.typeName = typeName2;
	}

	public void setSlot(int slotNumber) {
		//  Auto-generated method stub
		this.slotNumber = slotNumber;
	}

	public Object getDecType() {
		//  Auto-generated method stub
		return typeName;
	}

	public int getSlot() {
		//  Auto-generated method stub
		return slotNumber;
	}
	
	public Dec getDec(){
		Dec dec;
		dec = new Dec(firstToken, ident); 
		return dec;
		
	}

}
