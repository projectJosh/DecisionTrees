import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ID3 {

	//static ArrayList<Instance> instances;// = new ArrayList<Instance>();
	//static ArrayList<Instance> trainingSet;// = new ArrayList<Instance>();
	//static ArrayList<Instance> testSet;// = new ArrayList<Instance>();
	static ArrayList<String> attributes;// = new ArrayList<String>();
	static HashMap<String, ArrayList<String>> posValues;// = new HashMap<String, ArrayList<String>>();
	public static boolean display = false;
	
	public ID3(ArrayList<Instance> inst, ArrayList<Instance> train, ArrayList<Instance> test, ArrayList<String> a){
		//instances = inst;
		//trainingSet = train;
		//testSet = test;
		attributes = a;
		posValues = new HashMap<String, ArrayList<String>>();
	}
	
	/**
	 * caclulates entropy
	 * @param S
	 * @return
	 */
	public static double entropy(ArrayList<Instance> S){
		/*
		 * I need to calculate how many types of labels there are,
		 * find the proportion of instances in S with label value L
		 */
		//To calculate how many values for label there are:
		ArrayList<String> labels = new ArrayList<String>();
		Iterator<Instance> it = S.iterator();
		while(it.hasNext()){
			Instance t = it.next();
			String v = t.getAtt().get(0);
			if(labels.contains(v)){
			}
			else{
				labels.add(v);
			}
		}
		ArrayList<Integer> counts = new ArrayList<Integer>();
		while(counts.size()<labels.size()){
			counts.add(0);
		}
		for(int i = 0; i < labels.size(); i++){
			Iterator<Instance> um = S.iterator();
			String j = labels.get(i);
			while(um.hasNext()){
				Instance a = um.next();
				if(a.getAtt().get(0).equals(j)){
					counts.set(i, counts.get(i)+1);
				}
			}
		}
		//Calculate now: p(S, l) = counts(i)/S.size()
		// for each value in labels, find p(S,l)
		double sum = 0;
		for(int x = 0; x < labels.size(); x++){
			double p = counts.get(x) / (double) S.size();
			if(p!=1){
				sum = (float) (sum + p*Math.log10(p)/Math.log10(2));
				
			}
			
		}
		return sum*-1;
	}
	
	/**
	 * This should split set S into an arrayList of subsets, each subset
	 * having a different value of a given attribute.
	 * @param S
	 * @param a
	 */
	public static Map<String, ArrayList<Instance>> split(ArrayList<Instance> S, String a){
		//First step, find out what column a is in:
		int index = position(a);
		Map<String, ArrayList<Instance>> subsets = new HashMap<String, ArrayList<Instance>>();
		//For each instance in S, check it's i'th element. If that 
		//element is in subsets as a key already, add that instance to the value
		//If not, put it in, and create a new ArrayList for it's value, containing that instance
		for(int x = 0; x < S.size(); x++){
			String key = S.get(x).getAtt().get(index);
			if (subsets.containsKey(key)){ 
				subsets.get(key).add(S.get(x));
			}
			else{
				ArrayList<Instance> list = new ArrayList<Instance>();
				list.add(S.get(x));
				subsets.put(key, list);
			}
		}
		return subsets;
	}
	 /**
	  * calculates gain
	  * @param inst
	  * @param att
	  * @return
	  */
	public static double gain(ArrayList<Instance> inst, String att){
		/*
		 * To calculate gain, we need entropy(check), and then the set of all
		 * attributes (check), and then  
		 */
		double sum = 0;
		//for each possible value for the attribute att:
		Map<String, ArrayList<Instance>> subset;
		subset = split(inst, att);
		Iterator<String> iter = subset.keySet().iterator();
		while(iter.hasNext()){
			String n = iter.next(); // current value of att being looked at.
			sum = sum + (1.0*subset.get(n).size()) / inst.size() * entropy(subset.get(n));
			//System.out.println(n + " " + subset.get(n).size());
		}
		return entropy(inst) - sum;
	}
	
	/**
	 * takes in a set of instances, and returns the most common label value.
	 * @param set
	 * @return
	 */
	public static String commonLabel(ArrayList<Instance> set){	
		Map<String, Integer> labels = new HashMap<String, Integer>();
		String bestLabel = "";
		int highest = -1;
	
		for(int u = 0; u < set.size(); u++){
			String L = set.get(u).getAtt().get(0);
		
			if(labels.containsKey(L)){
					labels.put(L, labels.get(L)+1);
					if(labels.get(L)>highest){
						highest = labels.get(L);
						bestLabel = L;
					}
			}
			else{
				labels.put(L, 1);
				if(labels.get(L)>highest){
					highest = 1;
					bestLabel = L;
				}
			}
		}
		return bestLabel;
	}
	
	/**
	 * A method which intakes an attribute (or the label), and then returns what index
	 * it has in the attributes list.
	 */
	public static int position(String attribute){
		for(int i = 0; i < attributes.size(); i++){
			if(attribute.equals(attributes.get(i))){
				return i;
			}
		}
		System.out.println("Uh oh spaghetti Oh! position returned -1");
		return -1;
	}
	
	/**
	 * Given an attribute and a list of Instances, returns a list of all
	 * of the different values of that attribute occurring in that list of instances.
	 * @param attribute
	 * @param S
	 * @return
	 */
	public static ArrayList<String> posValues(String attribute, ArrayList<Instance> S){ 
		int pos = position(attribute);
		ArrayList<String> values = new ArrayList<String>();
		for(int i = 0; i < S.size(); i++){
			String v = S.get(i).getAtt().get(pos);
			if(!values.contains(v)){
				values.add(v);
			}
		}
		return values;
	}
	
	/**
	 * runs ID3 on an arraylist of attributes and arraylist of instances, returns the overal decision tree as a node.
	 * @param Att
	 * @param S
	 * @return
	 */
	public static MyNode ID3alg(ArrayList<String> Att, ArrayList<Instance> S){
		MyNode N = new MyNode();
		//Need to collect all label values from S.
		ArrayList<String> labels = posValues("label",S); 
		if(Att.size()==1){ //If Att.size()==1, then the only thing left should be the label.
			N.setValue(commonLabel(S));
			N.setLeaf(true);
			if(display){System.out.println("leaf: " + N.getValue());}
			return N;
			//N <- leaf node predicting most common label l in S
		}
		else if(labels.size()==1){
			N.setValue(S.get(0).getAtt().get(0));
			N.setLeaf(true);
			if(display){System.out.println("One label: " + N.getValue() + " and Att is: " + Att);}
			return N;
		}
		else{
			//Finding argmax of Gain(set, att)
			String star = null;
			double bestScore = -1;
			for(int z = 1; z < Att.size(); z++){
				double current = gain(S, Att.get(z));
				if(Att.size()==7){
					//System.out.println(Att.get(z) + " " + current);
				}
				if( current > bestScore){
					bestScore = current;
					star = Att.get(z);
				}
			}
			N.setValue(star);
			ArrayList<String> possibleV = posValues.get(star);
			//What's the index in attributes of star?
			
			int position = position(star);
			if(display){System.out.println("Attribute: " + star);}
			for(int x = 0; x < possibleV.size(); x++){ //for each possible value of a* 	
				ArrayList<Instance> setV = new ArrayList<Instance>(); //create a subset of instances with that value
				for(int s = 0; s < S.size(); s++){ // for each instance in set S. //TODO Need to distinguish category of v, multiple attributes can have v.
					//Does new if statement fix it?    it shooooould.                
					if(S.get(s).getAtt().get(position).equals(possibleV.get(x))){ //If it contains the value v of a*,
						setV.add(S.get(s)); //add it to the subset setV
					}
				}
				if(setV.isEmpty()){ //If Sv is empty:
					MyNode ch = new MyNode();
					ch.setValue(commonLabel(S)); 
					N.getEdges().put(possibleV.get(x), ch);
					N.addChild(ch);
					ch.setLeaf(true);
					// make child, give it value of commmonLabel, save as child
					
				}
				else{
					ArrayList<String> atta = new ArrayList<String>();
					atta.addAll(Att);
					atta.remove(star);
					MyNode child = new MyNode();
					if(display){System.out.println("prior to ID3alg recursion, value: " + possibleV.get(x) + " and attribute is: " + star);}
					child = ID3alg(atta, setV);
					N.getEdges().put(possibleV.get(x), child);
					N.addChild(child);
				}
				
			}
			//if(display){System.out.println("Edges: " + N.getEdges());}
		}
		return N;
	}
	
	/**
	 * Given an instance and a tree, returns the label predicted for that instance by the tree.
	 * @param instance
	 * @param tree
	 * @return
	 */
	public static String prediction(HashMap<String, String> instanceMap, MyNode tree){ 
		//	TODO instead, pass in a hashmap of <attribute, instance's value for that attribute>, and iterate through the hashmap. Also don't need to remove values.
		// UPDATE: ^ i think that's been accomplished.
		//base case:
		//if(arrayList.size()==1){
		if(tree.isLeaf()){
			return tree.getValue();
		}
		else{
			//When jumping to a child node, the edge that pointed to it should be of the attribute which current node is.
			Iterator<String> iterator = tree.getEdges().keySet().iterator();
			//Now go through each each edge and associated child node, check to see if that pairing matches any pairing from instanceMap.
			while(iterator.hasNext()){
				String val = iterator.next();
				//Need to check two comparisons in if statement:
				//Take child node associated with current edge, find it's associated attribute. Then, if instanceMap.get(that).equals(val):
				//String attri = tree.getEdges().get(val).getValue(); //This is the attribute of the node associated with the edge val.
				//If the attribute of the edge is the same as the attribute of the current node,
				if(instanceMap.get(tree.getValue()).equals(val)){
					return prediction(instanceMap, tree.getEdges().get(val));
				}
			}
		}
		System.out.println("prediction failed");
		System.exit(1);
		return "nothing"; //I don't want the 'fail case' returned object to override the actual one. How do i make sure that doesn't happen?
	}
}
