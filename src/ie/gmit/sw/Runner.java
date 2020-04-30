package ie.gmit.sw;

public class Runner {
	public static void main(String[] args){
		
		/*			
			Each of the languages in the enum Language can be represented as a number between 0 and 234. You can 
			map the output of the neural network and the training data label to / from the language using the
			following. Eg. index 0 maps to Achinese, i.e. langs[0].  
		*/
		Language[] langs = Language.values(); //Only call this once...		
		for (int i = 0; i < langs.length; i++){
			System.out.println(i + "-->" + langs[i]);
		}
	}
}