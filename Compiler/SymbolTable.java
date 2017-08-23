package cop5556sp17;



import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import cop5556sp17.AST.Dec;


public class SymbolTable {

	//  add fields
	int currentScope;
	int nextScope;
	
	class Attributes{
		int scope;
		Dec dec;
		public Attributes(int scope, Dec dec){
			super();
			this.scope = scope;
			this.dec = dec;
		}
	}
	
	Map<String, List<Attributes>> symbolTable = new HashMap<>();
	Stack<Integer> scopeStack = new Stack<>();
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//  IMPLEMENT THIS
		currentScope = nextScope++;
		scopeStack.push(currentScope);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//  IMPLEMENT THIS
		currentScope = scopeStack.pop();
	}
	
	public boolean insert(String ident, Dec dec){
		//  IMPLEMENT THIS
		boolean status;
		status = symbolTable.containsKey(ident);
		if(status)
		{
			//if it exists then check its scope value
			List<Attributes> scopes=symbolTable.get(ident);
			for(Attributes cTuple: scopes){
				if(cTuple.scope == currentScope){
					return false;
				}
			}
			
			scopes.add(new Attributes(currentScope,dec));
			symbolTable.put(ident,  scopes);
			return true;
		}
		else{
			List<Attributes> attributes = new ArrayList<>();
			attributes.add(new Attributes(currentScope,dec));
			symbolTable.put(ident, attributes);
			return true;			
		}
	}
	
	public Dec lookup(String ident){
		//  IMPLEMENT THIS
		boolean status = symbolTable.containsKey(ident);
		if(!status){
			return null;
		}
		else{
			List<Attributes> decs = symbolTable.get(ident);
			for(int i = decs.size()-1; i>=0; i--){
				if(scopeStack.contains(decs.get(i).scope)){
					return decs.get(i).dec;
				}
			}
			return null;
		}
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
	


}
