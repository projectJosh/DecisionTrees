import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyNode {
	ArrayList<MyNode> children;
	MyNode parent;
	String v;
	boolean isLeaf;
	String edge;
	HashMap<String, MyNode> edges;
	
	public MyNode(String a){
		children = new ArrayList<MyNode>();
		v = a;
		isLeaf = false;
		edge = v;
		edges = new HashMap<String, MyNode>();
	}
	
	public HashMap<String, MyNode> getEdges(){
		return edges;
	}
	
	public MyNode(){
		children = new ArrayList<MyNode>();
		isLeaf = false;
		edges = new HashMap<String, MyNode>();
	}
	
	public ArrayList<MyNode> getChildren(){
		return children;
	}
	
	public MyNode getParent(){
		return parent;
	}
	
	public boolean setParent(MyNode N){
		parent=N;
		return true;
	}
	
	public boolean addChild(MyNode N){
		children.add(N);
		return true;
	}
	
	public void setValue(String a){
		v = a;
	}
	
	public String getValue(){
		return v;
	}
	public void setLeaf(boolean b){
		isLeaf = b;
	}
	
	public String toString(){
		return v.toString();
	}
	
	public boolean isLeaf(){
		return isLeaf;
	}
	
	/**
	 * Prints out the decision tree in a more accessible format.
	 * @param tree
	 */
	public void family(MyNode tree){
		if(tree.isLeaf()){
			System.out.println(v);
		}
		else{
			//System.out.println(tree.getValue());
			Iterator<String> iter = tree.getEdges().keySet().iterator();
			while(iter.hasNext()){
				String n = iter.next();
				System.out.println(tree.getValue() + " with value " + n + " links to " + tree.getEdges().get(n));
				family(tree.getEdges().get(n));
			}
		}
	}
}
