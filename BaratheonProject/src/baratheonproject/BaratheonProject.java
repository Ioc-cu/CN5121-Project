package baratheonProject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Scanner;

public class BaratheonProject {
    
    public static LinkedList<String> namesList= new LinkedList<>();
    public static LinkedList<String> malesList= new LinkedList<>();
    public static LinkedList<String> femalesList= new LinkedList<>();
    public static LinkedList<String> relatedList= new LinkedList<>();
    public static LinkedList<String> container = new LinkedList<String>();
    public static String[] resultArray =  {"father,mother", "son,daugher", "brother,sister", "cousin","husband,wife", "grandfather,grandmother", "grandson,granddaugther", "nephew,niece", "uncle,aunt"};
    public static String[] listArray;
    public static String[] namesArray;
    public static String[] query = new String[2];
    public static int related = 0;
    public static int openedFile = 0;
    public static int pos = 0;
    
    public static class Input{
        
        //Reads file contents and adds them to a LinkedList
        public static void read(String file) throws IOException{
            BufferedReader in = new BufferedReader(new FileReader (file));
            String str;
            LinkedList<String> list = new LinkedList<>();
            while ((str = in.readLine()) != null) {
                list.add(str);
            }
            listArray = list.toArray(new String[0]);
            //System.out.println(list + "\n" + listArray[0]); Arrayprinting for debugging purposes
        }
        
       //Scans for name queries in order to do comparisons (See Comparison Class)
       public static void getName(){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the first name: ");
            query[0]= scanner.nextLine();
            for (; checkNames(query[0]) == false;) {
                System.out.println("Name not found. Please try again!");
                query[0]= scanner.nextLine();
            }
            System.out.println("Enter the second name: ");
            query[1]= scanner.nextLine();
            for (; checkNames(query[1]) == false;) {
                System.out.println("Name not found. Please try again!");
                query[1]= scanner.nextLine();
            }
        }

        //check if a name exists in the list...
        public static boolean checkNames(String name){
            for (String str : namesArray) {
                if (str.equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    //Adds male or female names on a list, and namesList is later used for part 3.
    public static class Names {
        public static void namesAlphabetically(){ // Alphabetical Name Sorting
            int i =0;
            for (String str : listArray) {
                if (str.trim().contains(",male")) {
                    String arr[] = str.split(",",2);
                    namesList.add(arr[0]);
                    malesList.add(arr[0]);
                } else if (str.trim().contains(",female")) {
                    String arr[] = str.split(",",2);
                    namesList.add(arr[0]);
                    femalesList.add(arr[0]);
                    //System.out.println(femalesList);
                }
                i++;
            }
            java.util.Collections.sort(namesList);
            namesArray = namesList.toArray(new String[0]);
        }
    }
    
    public static class Output{
        
        //Part 3: Create a file named namesSorted.txt
        public static void writeNames() throws FileNotFoundException, UnsupportedEncodingException{
            PrintWriter writer = new PrintWriter("namesSorted.txt","UTF-8");
            for (String str : namesArray) {
                writer.println(str);
            }
            writer.close();
        }
        
        //Part 5:Initialization and writing of LinkledList contents on a .dot file
        public static void writeResult(String firstName, String secondName, String relation) throws IOException{
            
            //Initializing object variable before assigning it an object refference
            PrintWriter writer = null;
            if (openedFile == 0) {
                writer = new PrintWriter("result.dot", "UTF-8");
                writer.println("digraph G {");
                writer.println("  rankdir=TB");
            }else if(openedFile>0){
                writer = new PrintWriter(new FileWriter("result.dot", true));
            }
            
            //Link Creation for 2 diffferent relation groups (marriage,parents)
            //Marriage first along with children is a subgraph
            if (relation.equals("husband")) {
                writer.println("\nsubgraph node"+openedFile+"{");
                conditionalWriter(firstName,secondName,writer); //Need to write Linking nodes here?
                writer.println("  {\n  rank=same;\n \""+firstName+"\";\n \""+secondName+"\";\n  }\n");
                writer.println("  \""+firstName+"\" -> \""+secondName+"\" [dir=none, color=\"black:black\", label=\""+"married"+"\", weight=8]");
                writer.println("}\n");
                writer.close();
                //Need to finalize subgraph here
                pos++;
            }
            
            //Printing children relations
            if (relation.equals("father") || relation.equals("mother")){
                conditionalWriter(firstName,secondName,writer); //Need to write Linking nodes here?
                if(malesList.contains(firstName)){ //Linking nodes
                    writer.println("  \""+firstName+"\" -> \""+secondName+"\"[label=\""+relation+"\"]");   
                }else writer.println("  \""+firstName+"\" -> \""+secondName+"\"[label=\""+relation+"\"]");
                writer.close();
            }
            
            openedFile++;
        }
        
        //module of writeResult: conditional check for node creation (to avoid code clogging)
        public static void conditionalWriter(String firstName, String secondName, PrintWriter writer) throws IOException {
                //Unique Node Creation (Without Duplication)
                if(!container.contains(firstName)) { // Creating firstName's Node
                    writer.println("\n  \""+firstName+"\" [label=\"" + firstName + "\", shape=box]");
                    container.add(firstName);
                }
                if(!container.contains(secondName)){ // Creating secondName's Node
                    writer.println("  \""+secondName+"\" [label=\"" + secondName + "\", shape=box]\n");   
                    container.add(secondName);
                }
        }
        
        //close the result.dot file
        public static void closeGraphVizFile()throws IOException {
            PrintWriter writer = new PrintWriter (new FileWriter("result.dot",true));
            writer.println("}");
            writer.close();
        }
        
        //Creates the .png file through batch execution
        public static void createPNG() throws IOException{
            String command="dot -Tpng result.dot -o result.png";
            Runtime.getRuntime().exec("cmd start cmd.exe /c " + command);
        }
        
        //Core function of Part 5,uses writeResult and closeGraphVizFile in order to create the .dot file
        public static void createDOT() throws IOException{
            String[] segments;
            for (String str : listArray) {
                //Making sure that the string elements of the list have at least two comma seperators (represents relations)
                if (str.chars().filter(ch -> ch == ',').count() == 2) { 
                    segments = str.split(",",3); //Seperating elements in a String array
                    
                    //Printing seperated elements on screen
                    //System.out.println("firstName: " + segments[0] + "    relation: " + segments[1] + "    secondName: " + segments[2]); 
                    
                    writeResult(segments[0],segments[2],segments[1]); //Parsing elements to writeResult Function
                }
            }
            closeGraphVizFile(); //Finalizing .dot file
            createPNG(); //Visualizing .dot file
        }

        //Part 4: Prints the query results according to relations
        public static void printResults(String firstName,String secondName,int functionNumber) throws IOException{
            String array[] = resultArray[functionNumber].split(",",2);
            if (malesList.contains(firstName)) {
                System.out.println(firstName + " is the " + array[0] + " of " + secondName);
            } else if (femalesList.contains(firstName)){
                System.out.println(firstName + " is the " + array[1] + " of " + secondName);
            }
        }
    }    
    
    public static class Comparison{
        
        public static void parents() throws IOException{ //Relation Exists
            int i = 0;
            for (String str : listArray) {
                if (str.trim().contains(query[0]) && (str.trim().contains(",father,") || str.trim().contains(",mother,")) && str.trim().contains(query[1])){
                    String arr[] = str.split(",",3);
                    Output.printResults(arr[0], arr[2], 0);
                    related++;
                    break;
                } else if (i == listArray.length-1){
                    System.out.println("Neither is a parent to another");
                }
                i++;
            }
        }
        
        public static void offsprings() throws IOException{ // Needs work,relation doesn't exist,is derivative
            int i = 0;
            for (String str : listArray) {
                if (str.trim().contains(query[0]) && (str.trim().contains(",father,") || str.trim().contains(",mother,")) && str.trim().contains(query[1])){
                    String arr[] = str.split(",",3);
                    Output.printResults(arr[2], arr[0], 1);
                    related++;
                    break;
                } else if (i == listArray.length-1){
                    System.out.println("Neither is a son to the other");
                }
                i++;
            }
        }
        public static void siblings() throws IOException{ //Derivative,Needs work.
            int i = 0;
            //In order to find if the queries are siblings, they must have the same parent,so we make a parent table for one queryName
            String[] parents = new String[2];
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parents[i] = arr[0];
                }
            }
            i=0;
            for (String str : listArray) {
                if ( 
                str.contains(parents[0] + ",father," + query[1]) || str.contains(parents[0] + ",mother," + query[1])
                || str.contains(parents[1] + ",father," + query[1]) || str.contains(parents[1] + ",mother," + query[1])
                ) {
                    //String arr[] = str.split(",",3);
                    Output.printResults(query[0],query[1],2);
                    related++;
                    break;
                } else if (i == listArray.length-1){
                    System.out.println("They are not siblings");
                }
                i++;
            }
        }
        
        public static void cousins() throws IOException{ //Derivative,creates 3 tree levels
            int i = 0;
            int k = 0;
            String[] parentsFirst = new String[2];
            String[] parentsSecond = new String[2];
            String[] grandParentsFirst = new String[4];
            String[] grandParentsSecond = new String[4];
            String[] siblings = new String[8];
            
            //First tree level:parents of queries
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parentsFirst[i] = arr[0];
                    //System.out.println("parentsFirst:" + i + parentsFirst[i]);
                    i++;
                } else if (str.contains("father," + query[1]) || str.contains("mother," + query[1])) {
                    String arr[] = str.split(",",3);
                    parentsSecond[k] = arr[0];
                    //System.out.println("parentsSecond:" + k + parentsSecond[k]);
                    k++;
                }
            }

            //the root of the tree doesn't have any parents,ensuring that
            if (!(parentsFirst[0] == null || parentsSecond[0] == null)) {
                //Second Tree Level:Grandparents (in order to prove that parents of both queries are siblings
                i=0;
                k=0;
                for (String str : listArray) {
                    if (
                    str.contains("father," + parentsFirst[0]) || str.contains("mother," + parentsFirst[0])
                    || str.contains("father," + parentsFirst[1]) || str.contains("mother," + parentsFirst[1])
                    ) {
                        String arr[] = str.split(",",3);
                        grandParentsFirst[i] = arr[0];
                        //System.out.println("grandParentsFirst:" + i + grandParentsFirst[i]);
                        i++;
                    }
                    else if(
                    str.contains("father," + parentsSecond[0]) || str.contains("mother," + parentsSecond[0])
                    || str.contains("father," + parentsSecond[1]) || str.contains("mother," + parentsSecond[1])
                    ){
                        String arr[] = str.split(",",3);
                        grandParentsSecond[k] = arr[0];
                        //System.out.println("grandParentsSecond:" + k + grandParentsSecond[i]);
                        k++;
                    }
                }
            } else{
                System.out.println("They are not cousins");
                return;
            }
            
            //System.out.println(grandParentsFirst[0] + grandParentsSecond[0]);
            
            //Making sure they have the same grandparents,while also making sure that the values checked aren't null
            if (!(grandParentsFirst[0] == null || grandParentsSecond[0] == null)) {
                if (grandParentsFirst[0].equals(grandParentsSecond[0])) {
                    i = 0;
                    for (String str : listArray) {
                        if (
                            str.contains(grandParentsFirst[0] + ",father") || str.contains(grandParentsFirst[0] + ",mother")
                            || str.contains(grandParentsFirst[1] + ",father") || str.contains(grandParentsFirst[1] + ",mother")
                            || str.contains(grandParentsFirst[2] + ",father") || str.contains(grandParentsFirst[2] + ",mother")
                            || str.contains(grandParentsFirst[3] + ",father") || str.contains(grandParentsFirst[3] + ",mother")
                            ) {
                            String arr[] = str.split(",",3);
                            siblings[i] = arr[2];
                            i++;
                        }
                    }
                } else{
                    System.out.println("They are not cousins");
                    return;
                }
                
            }else{
                System.out.println("They are not cousins");
                return;
            }

            //Going a level down to find all possible siblings
            i = 0;
            for (String str : listArray) {
                if (
                    str.contains(siblings[0] + ",father," + query[0]) || str.contains(siblings[0] + ",mother," + query[0])
                    || str.contains(siblings[1] + ",father," + query[0]) || str.contains(siblings[1] + ",mother," + query[0])
                    || str.contains(siblings[0] + ",father," + query[1]) || str.contains(siblings[0] + ",mother," + query[1])
                    || str.contains(siblings[1] + ",father," + query[1]) || str.contains(siblings[1] + ",mother," + query[1])
                    ) {
                    String arr[] = str.split(",",3);
                    if (arr[2].equals(query[0])) {
                        Output.printResults(arr[2],query[1],3);
                    } else if (arr[2].equals(query[1])) {
                        Output.printResults(arr[2],query[0],3);
                    }
                    related++;
                    break;
                } else if (i == listArray.length-1) {
                    System.out.println("They are not cousins");
                }
                i++;
            }
        }
        
        public static void marriage() throws IOException{ //Exists
            int i = 0;
            for (String str : listArray) {
                if (str.trim().contains(query[0]) && (str.trim().contains(",husband,") || str.trim().contains(",wife,")) && str.trim().contains(query[1])){
                    String arr[] = str.split(",",3);
                    Output.printResults(arr[0], arr[2], 4);
                    related++;
                    break;
                } else if (i == listArray.length-1){
                    System.out.println("They are not married");
                }
                i++;
            }
        }
        
        //Checks if any of the names are grandparents of each other
        public static void grandparents() throws IOException { //Derivative
            int i = 0;
            int k = 0;
            String[] parentsFirst = new String[2];
            String[] parentsSecond = new String[2];
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parentsFirst[i] = arr[0];
                    i++;
                } else if (str.contains("father," + query[1]) || str.contains("mother," + query[1])) {
                    String arr[] = str.split(",",3);
                    parentsSecond[k] = arr[0];
                    k++;
                }
            }
            
            i = 0;
            for (String str : listArray) {
                if (
                    str.contains(query[0] + ",father," + parentsSecond[0]) || str.contains(query[0] + ",mother," + parentsSecond[0])
                    || str.contains(query[0] + ",father," + parentsSecond[1]) || str.contains(query[0] + ",mother," + parentsSecond[1])
                    || str.contains(query[1] + ",father," + parentsSecond[0]) || str.contains(query[1] + ",mother," + parentsSecond[0])
                    || str.contains(query[1] + ",father," + parentsSecond[1]) || str.contains(query[1] + ",mother," + parentsSecond[1])
                    || str.contains(query[0] + ",father," + parentsSecond[0]) || str.contains(query[0] + ",mother," + parentsSecond[0])
                    || str.contains(query[0] + ",father," + parentsSecond[1]) || str.contains(query[0] + ",mother," + parentsSecond[1])
                    || str.contains(query[1] + ",father," + parentsSecond[0]) || str.contains(query[1] + ",mother," + parentsSecond[0])
                    || str.contains(query[1] + ",father," + parentsSecond[1]) || str.contains(query[1] + ",mother," + parentsSecond[1])
                    ) {
                    String arr[] = str.split(",",3);
                    if (arr[0].equals(query[0])) {
                        Output.printResults(arr[0],query[1],5);
                    } else if (arr[0].equals(query[1])) {
                        Output.printResults(arr[0],query[0],5);
                    }
                    related++;
                    break;
                } else if (i == listArray.length-1) {
                    System.out.println("Neither one is a grandparent to the other");
                }
                i++;
            }
        }
        
        //GrandChildren() has the same implementation with Grandparents()
        public static void grandChildren() throws IOException { //Derivative
            int i = 0;
            int k = 0;
            String[] parentsFirst = new String[2];
            String[] parentsSecond = new String[2];
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parentsFirst[i] = arr[0];
                    i++;
                } else if (str.contains("father," + query[1]) || str.contains("mother," + query[1])) {
                    String arr[] = str.split(",",3);
                    parentsSecond[k] = arr[0];
                    k++;
                }
            }
            
            i = 0;
            for (String str : listArray) {
                if (
                    str.contains(query[0] + ",father," + parentsFirst[0]) || str.contains(query[0] + ",mother," + parentsFirst[0])
                    || str.contains(query[0] + ",father," + parentsFirst[1]) || str.contains(query[0] + ",mother," + parentsFirst[1])
                    || str.contains(query[1] + ",father," + parentsFirst[0]) || str.contains(query[1] + ",mother," + parentsFirst[0])
                    || str.contains(query[1] + ",father," + parentsFirst[1]) || str.contains(query[1] + ",mother," + parentsFirst[1])
                    || str.contains(query[0] + ",father," + parentsSecond[0]) || str.contains(query[0] + ",mother," + parentsSecond[0])
                    || str.contains(query[0] + ",father," + parentsSecond[1]) || str.contains(query[0] + ",mother," + parentsSecond[1])
                    || str.contains(query[1] + ",father," + parentsSecond[0]) || str.contains(query[1] + ",mother," + parentsSecond[0])
                    || str.contains(query[1] + ",father," + parentsSecond[1]) || str.contains(query[1] + ",mother," + parentsSecond[1])
                    ) {
                    String arr[] = str.split(",",3);
                    if (arr[0].equals(query[0])) {
                        Output.printResults(query[1],arr[0],6);
                    } else if (arr[0].equals(query[1])) {
                        Output.printResults(query[0],arr[0],6);
                    }
                    related++;
                    break;
                } else if (i == listArray.length-1) {
                    System.out.println("Neither one is a grandchild to the other");
                }
                i++;
            }
        }
        
        public static void nieceNephew() throws IOException { //Derivative
            int i = 0;
            int k = 0;
            String[] parentsFirst = new String[2];
            String[] parentsSecond = new String[2];
            String[] grandParents = new String[4];
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parentsFirst[i] = arr[0];
                    i++;
                } else if (str.contains("father," + query[1]) || str.contains("mother," + query[1])) {
                    String arr[] = str.split(",",3);
                    parentsSecond[k] = arr[0];
                    k++;
                }
            }
            //FINDING THE PARENTS OF parents
            i=0;
            for (String str : listArray) {
                if (
                str.contains("father," + parentsFirst[0]) || str.contains("mother," + parentsFirst[0])
                || str.contains("father," + parentsFirst[1]) || str.contains("mother," + parentsFirst[1])
                || str.contains("father," + parentsSecond[0]) || str.contains("mother," + parentsSecond[0])
                || str.contains("father," + parentsSecond[1]) || str.contains("mother," + parentsSecond[1])
                ) {
                    String arr[] = str.split(",",3);
                    grandParents[i] = arr[0];
                    i++;
                }
            }
            
            i = 0;
            for (String str : listArray) {
                if (
                    str.contains(grandParents[0] + ",father," + query[0]) || str.contains(grandParents[0] + ",mother," + query[0])
                    || str.contains(grandParents[1] + ",father," + query[0]) || str.contains(grandParents[1] + ",mother," + query[0])
                    || str.contains(grandParents[0] + ",father," + query[1]) || str.contains(grandParents[0] + ",mother," + query[1])
                    || str.contains(grandParents[1] + ",father," + query[1]) || str.contains(grandParents[1] + ",mother," + query[1])
                    ) {
                    String arr[] = str.split(",",3);
                    if (arr[2].equals(query[0])) {
                        Output.printResults(query[1],arr[2],7);
                    } else if (arr[2].equals(query[1])) {
                        Output.printResults(query[0],arr[2],7);
                    }
                    related++;
                    break;
                } else if (i == listArray.length-1) {
                    System.out.println("Neither one is an niece/nephew to the other");
                }
                i++;
            }
        }
        
        public static void auntUncle() throws IOException { //Derivative
            
            int i = 0;
            int k = 0;
            String[] parentsFirst = new String[2];
            String[] parentsSecond = new String[2];
            String[] grandParents = new String[4];
            
            //Finding the parents of both queries
            for (String str : listArray) {
                if (str.contains("father," + query[0]) || str.contains("mother," + query[0])) {
                    String arr[] = str.split(",",3);
                    parentsFirst[i] = arr[0];
                    i++;
                } else if (str.contains("father," + query[1]) || str.contains("mother," + query[1])) {
                    String arr[] = str.split(",",3);
                    parentsSecond[k] = arr[0];
                    k++;
                }
            }
            
            //Going 1 level higher:Finding the parents of parents
            i=0;
            for (String str : listArray) {
                if (
                str.contains("father," + parentsFirst[0]) || str.contains("mother," + parentsFirst[0])
                || str.contains("father," + parentsFirst[1]) || str.contains("mother," + parentsFirst[1])
                || str.contains("father," + parentsSecond[0]) || str.contains("mother," + parentsSecond[0])
                || str.contains("father," + parentsSecond[1]) || str.contains("mother," + parentsSecond[1])
                ) {
                    String arr[] = str.split(",",3);
                    grandParents[i] = arr[0];
                    i++;
                }
            }
            
            //If the niece's grandparents are the same parents as the uncle's, 
            i = 0;
            for (String str : listArray) {
                if (
                    str.contains(grandParents[0] + ",father," + query[0]) || str.contains(grandParents[0] + ",mother," + query[0])
                    || str.contains(grandParents[1] + ",father," + query[0]) || str.contains(grandParents[1] + ",mother," + query[0])
                    || str.contains(grandParents[0] + ",father," + query[1]) || str.contains(grandParents[0] + ",mother," + query[1])
                    || str.contains(grandParents[1] + ",father," + query[1]) || str.contains(grandParents[1] + ",mother," + query[1])
                    ) {
                    String arr[] = str.split(",",3);
                    if (arr[2].equals(query[0])) {
                        Output.printResults(arr[2],query[1],8);
                    } else if (arr[2].equals(query[1])) {
                        Output.printResults(arr[2],query[0],8);
                    }
                    related++;
                    break;
                } else if (i == listArray.length-1) {
                    System.out.println("Neither one is an uncle/aunt to the other");
                }
                i++;
            }
        }
        
        public static void notRelated() throws IOException{
            if (related > 0) {
                System.out.println("Alas, They are related.");
            }else System.out.println("Alas, They are not related at all.");
        }
        
    }
    
    
    public static void main(String[] args) throws IOException {
        
        //Part 2:Reading the .csv file and aquiring query strings
        Input.read("family.csv");
        Names.namesAlphabetically();
        
        //In case the same name is entered twice,Ask for the query again
        do{
            Input.getName();
            if(query[0].equals(query[1])){
            System.out.println("A name cannot have a relation with itself, try again!");
            }
        }while(query[0].equals(query[1]));
        
        //Part 3 Sorting alphabetically on a file
        Output.writeNames();
        
        //Part 4: Comparison method calls (while each method does it's own relation check,they are all dependant on Comparison.compare(relation, output, index) for repetition elimination)
        Comparison.parents();
        Comparison.offsprings();
        Comparison.siblings();
        Comparison.cousins();
        Comparison.marriage();
        Comparison.grandparents();
        Comparison.grandChildren();
        Comparison.nieceNephew();
        Comparison.auntUncle();
        Comparison.notRelated();
        
        //Part 5:Creating the .dot file along with dot command execution
        Output.createDOT(); //Creates the output.dot file AND  Finalizes it.
    }
}
