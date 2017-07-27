
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.Math;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/*
 * Each non-leaf node will have a hashmap<String, MyNode>, where String -> value, MyNode -> child node pointed towards by the value.
 * So, each Node that's not a leaf will have a key in its hashmap for each child node, and then .......................
 */
	
public class HW1one {
	static ArrayList<Instance> instances = new ArrayList<Instance>();
	static ArrayList<Instance> trainingSet = new ArrayList<Instance>();
	static ArrayList<Instance> testSet = new ArrayList<Instance>();
	static ArrayList<String> attributes = new ArrayList<String>();
	//static HashMap<String, ArrayList<String>> posValues = new HashMap<String, ArrayList<String>>();
	//public static boolean display = false;
	
	private static Scanner fl;
	private static Writer output;
	private static boolean isID3 = true;
	private static double propTraining = 0.5;
	
	public static void sendTrain(long seed){
		Random rng = new Random(seed);
		ArrayList<Instance> randomList = new ArrayList<Instance>();
		randomList.addAll(instances);
		Collections.shuffle(randomList, rng);
		
		int trainingSize = (int) Math.round(randomList.size()*propTraining);
		for(int i = 0; i < randomList.size(); i++){
			if(i<trainingSize){
				trainingSet.add(randomList.get(i));
			}
			else{
				testSet.add(randomList.get(i));
			}
		}
		
	}

	
	public static void main(String[] args) throws IOException{
		/*
		 * Take as input:
		 * The path to a file containing a data set
		 * The name of the algorithm to be used
		 * A random seed as an integer
		 */
		String path = args[0];
		String althorithm = args[1];
		String seed = args[2];
		Scanner sc = null;
		try {
			sc = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			System.out.print("404 File not found!");
			e.printStackTrace();
		}
		
		/* 
		 * Then:
		 * Parse the data set as a set of instances
		 */
		//Process the first line of the file as list of labels and attributes
		sc.useDelimiter(",");
		//I want to parse only the first line, using delim=",". How?
		String firstLine = sc.nextLine();
		fl = new Scanner(firstLine);
		fl.useDelimiter(",");
		while(fl.hasNext()){
			String quote = fl.next();
			quote = quote.replace("\"", "");
			attributes.add(quote);
		}
		
		//Now that the first line has been parsed into attributes and stored, i need to take each line,
		//ArrayList<Instance> instances = new ArrayList<Instance>();
		//Parse one line at a time, storing each line as an instance after removing ","
		ArrayList<String> variables = new ArrayList<String>();
		while(sc.hasNextLine()){
			ArrayList<String> tempInst = new ArrayList<String>();
			fl = new Scanner(sc.nextLine());
			fl.useDelimiter(",");
			while(fl.hasNext()){
				String word = fl.next();
				tempInst.add(word);
				if(!variables.contains(word)){
					variables.add(word);
				}
			}
			Instance inst = new Instance(tempInst);
			instances.add(inst);
		}
		
		
		/* Then:
		 * Split instances into training and test sets, using random seed
		 */
		Random rng = new Random();
		System.out.println(rng.nextInt());
		sendTrain(1234);
		//System.out.println(testSet.size());
		//System.out.println(trainingSet.size());
		if(isID3){ID3 alg = new ID3(instances, trainingSet, testSet, attributes);}
		else{C45 alg = new C45(instances, trainingSet, testSet, attributes); }
		ID3 alg = null;
		C45 alg2 = null;
		ArrayList<ArrayList<tuple>> rules = null;
		MyNode decisionTree;
		if(isID3){
			for(int n = 0; n < attributes.size(); n++){
				alg.posValues.put(attributes.get(n), alg.posValues(attributes.get(n), instances));
			}
			decisionTree = alg.ID3alg(attributes, trainingSet);
			//System.out.println(decisionTree);
		}
		else{
			for(int n = 0; n < attributes.size(); n++){
				alg2.posValues.put(attributes.get(n), alg2.posValues(attributes.get(n), instances));
			}
			decisionTree = alg2.C45alg(attributes, trainingSet); 
			rules = new ArrayList<ArrayList<tuple>>(); 
			ArrayList<tuple> trail = new ArrayList<tuple>(); 
			alg2.buildRules(decisionTree, rules, trail); 
		}
		//decisionTree.family(decisionTree); TODO useful for seeing the tree structure.
		System.out.println("top node is " + decisionTree.getValue());
		System.out.println(decisionTree.getEdges());
		//ArrayList<MyNode> image = new ArrayList<MyNode>();
		//After sending the training set through the algorithm, what happens next? how do we "evaluate" the decision tree using the testSet?
		//How do we construct the confusion matrix? Then we have to output?
		 /*
		 * Next:
		 * The decision tree should be evaluated during the test set
		 * 
		 * Create the confusion matrix (aka output)
		 */
		//First line of output:
		File file = new File("results.csv");
		output = new BufferedWriter(new FileWriter(file));
		ArrayList<String> labels = null;
		if(isID3){labels = alg.posValues("label", instances); }
		else{
			labels = alg2.posValues("label", instances);
		}
		for(int z = 0; z < labels.size(); z++){
			output.write(labels.get(z).toString());
			output.write(",");
		}
		output.write("\n");
		int totalHits = 0;
		//Next: For each possible label value, collect all of the instances with that label, and then counts how many of those instances were predicted to match each label.
		for(int b = 0; b < labels.size(); b++){
			ArrayList<Instance> subset = new ArrayList<Instance>();
			Map<String, Integer> scores = new HashMap<String, Integer>();
			
			for(int o = 0; o < labels.size(); o++){  //Initialize the "scores" for this subset of the testSet.
				scores.put(labels.get(o), 0);
			}
			
			for(int a = 0; a < testSet.size(); a++){	//Create the subset of the testSet, the instances which have the current label as their "true" label.
				if(testSet.get(a).getAtt().get(0).equals(labels.get(b))){	//if the instance's label is the label being labels.get(b)
					subset.add(testSet.get(a));
				}
			}
			
			for(int v = 0; v < subset.size(); v++){		//Go through each instance in the subset, get its predicted label, and put into the scores map.
				HashMap<String, String> instanceMap = new HashMap<String, String>();
				String label;
				for(int i = 0; i < attributes.size(); i++){
					instanceMap.put(attributes.get(i), subset.get(v).getAtt().get(i));
				}
				if(isID3){label = alg.prediction(instanceMap, decisionTree);}
				else{
					label = alg2.prediction(rules, subset.get(v)); 
				}
				//
				//TODO so, i currently have a 50% accuracy ratio, which is shit. This varies when I change how much of the data is used
				//for training. However, i never seem to see higher than 50% accuracy. Furthermore, C45's prediction method
				//is failing. It returns "", the case which should never occur.
				scores.put(label, scores.get(label)+1);
			}
			Iterator<Integer> iter = scores.values().iterator();
			int count = 0;
			while(iter.hasNext()){
				Integer ne = iter.next();
				//System.out.println(ne);
				output.write(ne.toString());
				output.write(",");
				if(count==b){
					totalHits = totalHits + ne;
				}
				count++;
			}
			output.write(labels.get(b).toString());
			output.write("\n");
			
		}
		System.out.println((double) totalHits/((1.0)*(testSet.size())));
		output.close();
		System.out.println("Output closed");
	}
}
