import java.util.ArrayList;

public class Instance {

	double weight;
	ArrayList<String> att;
	boolean full;
	
	public Instance(ArrayList<String> vars){
		att = vars;
		full = true; //TODO maybe this shouldn't be initialized as full=true?
		weight = 1;
	}
	
	public Integer size(){
		return att.size();
	}
	
	public ArrayList<String> getAtt(){
		return att;
	}
	
	public String toString(){
		return att.toString();
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setWeight(double w){
		weight = w;
	}
	
	
}
