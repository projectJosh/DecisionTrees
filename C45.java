import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class C45 {
	static ArrayList<String> attributes;
	static boolean display = false;
	static HashMap<String, ArrayList<String>> posValues;
	
	public C45(ArrayList<Instance> inst, ArrayList<Instance> train, ArrayList<Instance> test, ArrayList<String> a){
		//instances = inst;
		//trainingSet = train;
		//testSet = test;
		attributes = a;
		posValues = new HashMap<String, ArrayList<String>>();
	}
	
	
	
	/*
	//Handling both continuous and discrete attributes - In order to handle continuous attributes, C4.5 creates a threshold and 
	 * then splits the list into those whose attribute value is above the threshold and those that are less than or equal to it.[4]
	//Handling training data with missing attribute values - C4.5 allows attribute values to be marked as ? for missing. 
	 * Missing attribute values are simply not used in gain and entropy calculations.
	//Handling attributes with differing costs.
	//Pruning trees after creation - C4.5 goes back through the tree once it's been created and attempts to remove branches that 
	 * do not help by replacing them with leaf nodes.
	 * Must handle continuous within tree as building. 
	 * 
	*/
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
	 * Given an arraylist of instances, an attribute, and a threshold, returns the gain of the values corresponding to the attribute categorized by the threshold.
	 * @param inst
	 * @param att
	 * @param threshold
	 * @return
	 */
	public static double gain(ArrayList<Instance> inst, String att, Double threshold){
		double sum = 0;
		int pos = position(att);
		ArrayList<Instance> lowSet = new ArrayList<Instance>();
		ArrayList<Instance> highSet = new ArrayList<Instance>();
		for(int i = 0; i < inst.size(); i++){
			double current = Double.parseDouble((inst.get(i).getAtt().get(pos)));
			if(current<=threshold){
				lowSet.add(inst.get(i));
			}
			else{
				highSet.add(inst.get(i));
			}
		}
		sum = sum + (1.0*lowSet.size()/inst.size()*entropy(lowSet));
		sum = sum + (1.0*highSet.size()/inst.size()*entropy(highSet));
		return entropy(inst) - sum;	
	}
	/**
	  * calculates gain of discrete attributes
	  * @param inst
	  * @param att
	  * @return
	  */
	public static double gain(ArrayList<Instance> inst, String att){
		//I think i need to have two different gain methods, one for continuous attributes and one for discrete attributes.
		//The continuous ones need to take in a threshold. 
		//I think the discrete gain method doesn't change at all from ID3's.
		//The continuous one should be mostly identical, but will have an extra step in which it categorizes the value of the instance based on threshold.
		double sum = 0;
		//for each possible value for the attribute att:
		Map<String, ArrayList<Instance>> subset;
		subset = split(inst, att);
		Iterator<String> iter = subset.keySet().iterator();
		while(iter.hasNext()){
			String n = iter.next(); // current value of att being looked at.
			sum = sum + (1.0*subset.get(n).size()) / inst.size() * entropy(subset.get(n));
		}
		return entropy(inst) - sum;
	}
	
	/*
	 * Six steps:
	 * Change gain to be gain over SplitInfo. (splitinfo(S,A)). In contrast to entropy, it is the proportion of subsetV to S. 
	 */
	/**
	 * Given a set of instances and an attribute, calculates the bias in gain
	 * @param S
	 * @param attribute
	 * @return
	 */
	public static double splitInfo(ArrayList<Instance> S, String attribute){
		//I need to calculate, for each possible value of attribute,
		//what the total weight is of all instances with that value.
		//Then do the math and sum up shit.
		//First step: get all possible Values of attribute.
		ArrayList<String> posV = posValues(attribute, S);
		double sum = 0;
		double sheavy = 0;
		for(int c = 0; c < S.size(); c++){
			sheavy = sheavy + S.get(c).getWeight();
		}
		//Second step: for each possible value of attribute, build the subsetV.
		for(int i = 0; i < posV.size(); i++){
			ArrayList<Instance> Sv = new ArrayList<Instance>();
			int index = attributes.indexOf(attribute);
			double vheavy = 0.0;
			for(int t = 0; t < S.size(); t++){
				//For each instance in Set S, if it's value for attribute is posV.get(i):
				if(S.get(t).getAtt().get(index).equals(posV.get(i))){
					Sv.add(S.get(t));
					vheavy = vheavy + S.get(t).getWeight();
				}
			}
			//Math stuff!
			//We need the sum of the weights in Sv: that' what vheavy is.
			//We also need the sum of the weights in S: that's sheavy.
			sum = sum + vheavy/sheavy*Math.log10(vheavy/sheavy)/Math.log10(2);
		}
		if(sum*-1 < 0.01){
			return 0.01;
		}
		return sum*-1;
	}
	/** Then handle continuous: Before calculating gain/splitinfo, check
	 *  to see if it is continuous variable, if so find appropriate 
	 *  thresholds, then calc gain. Will return a double which can be used
	 *  to split values into two categories.
	 *  
	 */
	public static double thresholds(ArrayList<Instance> S, String attribute){ 
		//Use 2 lists, one for double one for instances, sort the doubles, then mimic the changes into the instances list.
		double t = 0;
		int index = position(attribute);
		//possible candidates for t are the means of each range
		// between values with differing labels.
		//So, sort S by the (numeric) value of attribute,
		ArrayList<Instance> sortedS = new ArrayList<Instance>();
		ArrayList<Double> sortedIndices = new ArrayList<Double>();
		sortedS.addAll(S);
		for(int i = 0; i < S.size(); i++){
			double keyd = Double.parseDouble(S.get(i).getAtt().get(index));
			sortedIndices.add(keyd);
		}
		boolean go = true;
		while(go){
			for(int n = 0; n < sortedIndices.size(); n++){
				if(n==0){
				
				}
				else if(sortedIndices.get(n)<sortedIndices.get(n-1)){
					double storage = sortedIndices.get(n);
					sortedIndices.set(n, sortedIndices.get(n-1));
					sortedIndices.set(n-1, storage);
					go = true;
				}
				else{
					go = false;
				}
			}
		}
		//Then, loop through, keeping track of the previous label and value.
		String oldLabel = "";
		double oldValue = 0;
		ArrayList<Double> candidates = new ArrayList<Double>();	
		for(int x = 0; x < S.size(); x++){
			double newValue = Double.parseDouble(S.get(x).getAtt().get(index));
			String newLabel = S.get(x).getAtt().get(0);
			if(!newLabel.equals(oldLabel) & x > 0 & newValue!=oldValue){ //Because we don't want to consider the very first instance, with the very lowest value.
				//we also don't want to have a threshold where there are two adjacent doubles which are the same, but with different labels.
				candidates.add((oldValue+newValue)/2);
			}
		}
		//If newlabel!=oldlabel, add (oldvalue+newvalue)/2 into list of candidates.
		//Then, for each candidate, find gain(S,attribute) for each
		double bestGain = 0;
		for(int y = 0; y < candidates.size(); y++){
			double g = gain(S, attribute, candidates.get(y));
			if(g > bestGain){
				t = candidates.get(y);
				bestGain = g;
			}
		}
		//candidate. Then return the t which results in highest gain.
		return t;
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
	 * Returns a set with all of the complete instances from Set, plus the reduced weight instances derived from Set's missingVal instances.
	 * @param inst
	 * @return
	 */
	public static ArrayList<Instance> createPartials(ArrayList<Instance> Set, String attribute){
		//First step: Create subsets, one for complete instances in set, 
		//And then one further for each possible value of attribute found amongst Set.
		//Then return the arrayList of partial instances, to be used in the calling program.
		ArrayList<Instance> comSet = new ArrayList<Instance>();
		ArrayList<Instance> misSet = new ArrayList<Instance>();
		int index = position(attribute);
		for(int a = 0; a < Set.size(); a++){
			if(!Set.get(a).getAtt().get(index).equals("?")){
				comSet.add(Set.get(a));
			}
			else{
				misSet.add(Set.get(a));
			}
		}
		ArrayList<String> values = posValues(attribute, Set);
		ArrayList<Instance> partials = new ArrayList<Instance>();
		//Next: Within the set of complete instances, find the total "weight" of each possible value for attribute.
		//Then for each instance in Set but not in comSet, copy that instance, giving it a reduced weight and that value.
		for(int b = 0; b < values.size(); b++){
			String val = values.get(b);
			double totalWeight = 0.0;
			for(int c = 0; c < comSet.size(); c++){
				if(comSet.get(c).getAtt().get(index).equals(val)){
					totalWeight = totalWeight + comSet.get(c).getWeight();
				}
			}
			for(int d = 0; d < misSet.size(); d++){
				Instance inst = new Instance(misSet.get(d).getAtt());
				inst.getAtt().set(index, val);
				inst.setWeight(totalWeight/Set.size());
				partials.add(inst);
			}
		}
		partials.addAll(comSet);
		return partials;
	}
	
	
	 /* 
	  * Add Pruning
	  * 
	 * When testing this additions, just remove all instances that have missing values.
	 * Then, go back and add in functionality that will handle missing values.
	 * Instances need a new field, weight. Full instances have 1 weight, partials get their substitutions
	 * Gain/splitinfo have to incorporate weight instead of how many things are in them.
	 *
	Should I first build the decision tree separately, then go through and find the rules? I think yes.
	go through rules in sorted order, assuming nothing is missing. 
	*/
	
	/**
	 * Given an instance, and the rules, I need to find out what label the rules predict for that instance.
	 * @param instanceMap
	 * @param tree
	 * @return
	 */
	public static String prediction(ArrayList<ArrayList<tuple>> rules, Instance inst){ 
		/*
		 * Given a list of rules, and an instance, recurse through rules.
		 * iterate through the rules, and if that rule(ArrayList of strings)'s values are all in inst, return the label
		 * associated with that rule.
		 * TODO: This seems to only ever return a 'true', and it also failed to find a match for something.
		 */
		boolean match = false;
		for(int i = 0; i < rules.size(); i++){
			ArrayList<tuple> rule = rules.get(i);
			for(int v = 0; v < rule.size(); v++){
				tuple pair = rule.get(v);
				if(inst.getAtt().get(position(pair.getAttribute())).equals(pair.getValue()) | inst.getAtt().get(position(pair.getAttribute())).equals("?")){
					match = true;
				}
				else{
					match = false;
					break;
				}
			}
			if(match){
				return rule.get(rule.size()-1).getValue();
			}
		}
		//We shouldn't ever reach this point. TODO make sure of that? Are we certain that there will always be
		//a rule that "matches" each instance?
		//System.out.println(inst);
		//System.out.println(position("head_shape"));
		//System.out.println(inst.getAtt().get(1));
		//TODO based on the output found, there are no rules for 
		//head_shape = octagon??? Is this an issue with the decision tree?
		//for(int c = 0; c < rules.size();c++){
			//System.out.println(rules.get(c));
		//}
		//System.out.println(inst);
		System.out.println("prediction failed");
		return "???";
	}
	
	/**
	 * Creates the rules in C4.5. Perhaps should have another method that simply checks an instance against the rules to find label and call that prediction....
	 * @param instanceMap
	 * @param tree
	 * @return
	 */
	public static void buildRules(MyNode tree, ArrayList<ArrayList<tuple>> rules, ArrayList<tuple> path){
		/* After the tree is built, recursive function (build rules), will take a tree and a list of rules and a path(which will be a list).
		  * 	if(T is a leaf):
		  * 		rule from path
		  * 		save rule in rules
		  * 	else:
		  * 		for each child node of tree:
		  * 			newPath = copyPath
		  * 			add branch to newPath
		  * 			buildRules(child, newPath,rules)
		  * where the children are values. However, these are attributes. SO: for each child node of tree:
		  * 
		  */ 
		if(tree.isLeaf()){
			tuple leaf = new tuple("label", tree.getValue());
			path.add(leaf);
			rules.add(path);
		}
		else{
			Iterator<String> it = tree.getEdges().keySet().iterator();
			while(it.hasNext()){
				String n = it.next();
				ArrayList<tuple> newPath = new ArrayList<tuple>();
				newPath.addAll(path);
				tuple t = new tuple(tree.getValue(), n);
				//System.out.println(t);
				//if(tree.getValue().equals("head_shape") & n.equals("\"octagon\"")){
					//System.out.println("buildRules found an octahead");
				//}
				newPath.add(t);
				buildRules(tree.getEdges().get(n), rules, newPath);
			}

		}
		//Don't need to return anything, since it's just adding rules to the initial ArrayList<ArrayList<String>> passed to the first call to this program.
	}
	
	/**
	 * runs C4.5 on an arraylist of attributes and arraylist of instances, returns the overall decision tree as a node.
	 * @param Att
	 * @param S
	 * @return
	 */
	public static MyNode C45alg(ArrayList<String> Att, ArrayList<Instance> S){
		//TODO what happens when there's a missing value; how does that affect the attribute of the node?
		//If there are missing values, how does this change? 
		//If there are continuous values, how does this change? 
		//Anything else that'll change this algorithm?
		MyNode N = new MyNode();
		//Need to collect all label values from S.
		ArrayList<String> labels = posValues("label",S); 
		if(Att.size()==1){ //If Att.size()==1, then the only thing left should be the label.
			N.setValue(commonLabel(S));
			N.setLeaf(true);
			if(display){System.out.println("leaf node: " + N.getValue());}
			return N;
			//N <- leaf node predicting most common label l in S
		}
		else if(labels.size()==1){
			N.setValue(S.get(0).getAtt().get(0));
			N.setLeaf(true);
			if(display){System.out.println("One label: " + N.getValue());}
			return N;
		}
		else{
			//Finding argmax of Gain(set, att) TODO This is really where shit hits the fan.
			String star = null;
			//star = attribute that maximizes Gain/SplitInformation
			
			double bestScore = -1;
			for(int z = 1; z < Att.size(); z++){
				//I need to check to see if the attribute is continuous. If so, I need to find threshold and use that.
				//I also need to see if there are any missing values.
				//How to check if Att.get(z) is continuous or discrete? use a try/catch statement
				boolean isDiscrete = true;
				double current = 0.0;
				try{
					Double.parseDouble(S.get(0).getAtt().get(z));
					isDiscrete = false;
				}catch(NumberFormatException e){
					isDiscrete = true;
				}
				if(posValues(Att.get(z),S).contains("?") & !isDiscrete){	//If the attribute is both continuous and has missing values:
					ArrayList<Instance> filledI = createPartials(S, Att.get(z));
					double thresh = thresholds(filledI,Att.get(z));
					current = gain(filledI, Att.get(z), thresh)/splitInfo(filledI, Att.get(z));
					if(current > bestScore){
						bestScore = current;
						star = Att.get(z);
					}
				}
				else if(posValues(Att.get(z),S).contains("?")){			//Else if the attribute has missing values:
					//If the attribute contains missing values:
					ArrayList<Instance> filledI = createPartials(S, Att.get(z));
					current = gain(filledI, Att.get(z)) / splitInfo(filledI, Att.get(z));
					if(current > bestScore){
						bestScore = current;
						star = Att.get(z);
					}
				}
				else if(!isDiscrete){						//Else if the attribute is continuous:
					double thresh = thresholds(S,Att.get(z));
					current = gain(S, Att.get(z), thresh)/splitInfo(S, Att.get(z));
					if(current > bestScore){
						bestScore = current;
						star = Att.get(z);
					}
				}
				else{					//Else, it's complete and discrete and i'm happy.
					current = gain(S, Att.get(z))/splitInfo(S, Att.get(z));
					if( current > bestScore){
						bestScore = current;
						star = Att.get(z);
					}
				}
			}
			N.setValue(star);
			ArrayList<String> possibleV = posValues(star, S);
			
			
			int position = position(star);
			if(display){System.out.println(star);}
			for(int x = 0; x < possibleV.size(); x++){ //for each possible value of a* 	
				ArrayList<Instance> setV = new ArrayList<Instance>(); //create a subset of instances with that value
				for(int s = 0; s < S.size(); s++){ // for each instance in set S. 
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
					child = C45alg(atta, setV);
					N.getEdges().put(possibleV.get(x), child);
					N.addChild(child);
				}
				
			}
			if(display){System.out.println(N.getEdges());}
		}
		return N;
	} 
	
	
}
