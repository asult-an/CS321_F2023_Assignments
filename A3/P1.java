
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.management.RuntimeErrorException;

public class P1{

    public static class Node{
        int id;
        int weight;

        public Node(int inID, int inWeight){
            id = inID;
            weight = inWeight;
        }
    }
    public static void main(String[] args){
        Object input[] = readInput();
        int turnPenalty = (int)input[0];
        System.out.println(turnPenalty);

        ArrayList<ArrayList<Node>> graph;
        if(input[1] != null){
            graph = (ArrayList<ArrayList<Node>>)input[1];
        } 
        else{
            System.out.println("Input error");
            throw new RuntimeErrorException(null);
        }
        int count = 1;
        for(ArrayList<Node> neighbor : graph){
            for(Node node : neighbor){
                System.out.println("Node " + count + ": " + node.id + " " + node.weight);
            }
            ++count;
        }

    }
    /**
     * Reads an input file to generate and return an adjacency list graph and the turn penalty for the problem
     * @return Object[], with a list of lists of adjacent nodes and the turn penalty
     */
    public static Object[] readInput(){
        Object[] data = new Object[2];
        ArrayList<ArrayList<Node>> graph = new ArrayList<ArrayList<Node>>();

        try{
            File inputFile = new File("./InputFiles/input2.txt");
            Scanner fileIn = new Scanner(inputFile);
            fileIn.useDelimiter(" ");

            int turnPenalty = Integer.parseInt(fileIn.next());
            data[0] = turnPenalty;
            int numNodes = Integer.parseInt(fileIn.next());

            int expansionLimit = (int)(Math.sqrt(numNodes)*(Math.sqrt(numNodes)-1)/2);
            int column = 1;
            int count = 0;

            //adds neighbors/edge weights of each node while the graph is expanding from start
            for(int i = 1; i <= expansionLimit; ++i){
                ArrayList<Node> adjacentNodes = new ArrayList<Node>();
                adjacentNodes.add(new Node(i + column, Integer.parseInt(fileIn.next())));
                adjacentNodes.add(new Node(i + column + 1, Integer.parseInt(fileIn.next())));
                graph.add(adjacentNodes);
                ++count;
                if(count == column){
                    ++column;
                    count = 0;
                }
            }
            //adds neighbors/edge weights while graph is converging on finish
            for(int i = expansionLimit + 1; i < numNodes; ++i){
                boolean topEdge = count == 0;
                boolean bottomEdge = count == (column - 1);
                ArrayList<Node> adjacentNodes = new ArrayList<Node>();
                if(topEdge){
                    adjacentNodes.add(new Node(i + column, Integer.parseInt(fileIn.next())));
                }
                else if(bottomEdge){
                    adjacentNodes.add(new Node(i + column - 1, Integer.parseInt(fileIn.next())));
                }
                else{
                    adjacentNodes.add(new Node(i + column -1, Integer.parseInt(fileIn.next())));
                    adjacentNodes.add(new Node(i + column, Integer.parseInt(fileIn.next())));
                }

                graph.add(adjacentNodes);
                ++count;
                if(count == column){
                    --column;
                    count = 0;
                }
            }
            fileIn.close();
        }
        catch(FileNotFoundException e){
            
            System.out.println("File not found");
            throw new RuntimeException(e);
        }
        
        data[1] = graph;
        return data;
    }
}