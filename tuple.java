
public class tuple {

	String attribute;
	String value;
	public tuple(String att, String val){
		attribute = att;
		value = val;
	}
	
	public String getAttribute(){
		return attribute;
	}
	
	public String getValue(){
		return value;
	}
	
	public String toString(){
		return (attribute + " = " + value);
	}
}
