
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
        Object[] input = readInput();

        // Print out the turn penalty. Note: Maybe dummy this out for the version we turn in?
        int turnPenalty = (int)input[0];
        System.out.println("Turn Penalty: " + turnPenalty);

        ArrayList<ArrayList<Node>> graph;
        if(input[1] != null){
            graph = (ArrayList<ArrayList<Node>>)input[1];
        } 
        else{
            System.out.println("Input error");
            throw new RuntimeErrorException(null);
        }

        // Prints out list of paths. Note: Maybe dummy this out for the version we turn in?
        int count = 1;
        for(ArrayList<Node> node : graph){
            for(Node neighbor : node){
                System.out.println("Node " + count + ": " + neighbor.id + " " + neighbor.weight);
            }
            ++count;
        }

        // Call method for getting the best path from first node to final node. Prints result.
        int bestPath = findBestPath(turnPenalty, graph);
        System.out.println(bestPath);
    }
    /**
     * Reads an input file to generate and return an adjacency list graph and the turn penalty for the problem
     * @return Object[], with a list of lists of adjacent nodes and the turn penalty
     */
    public static Object[] readInput(){
        Object[] data = new Object[2];
        ArrayList<ArrayList<Node>> graph = new ArrayList<>();

        try{
            File inputFile = new File("./input1.txt");
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
                ArrayList<Node> adjacentNodes = new ArrayList<>();
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
                ArrayList<Node> adjacentNodes = new ArrayList<>();
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

    /**
     * Finds the best path through an inputted k by k graph using dynamic programming.
     * @param turnPenalty, the penalty for changing from taking nodes' upper neighbor paths to their lower neighbor
     *                     paths, or vice versa
     * @param graph, the k by k graph to be traversed
     * @return long containing the best path through the graph (the one with the highest value)
     */
    private static int findBestPath(int turnPenalty, ArrayList<ArrayList<Node>> graph) {
        // Last node isn't explicitly listed in the graph, only the nodes that point to it, so storing this for
        // convenience.
        int numNodes = graph.size() + 1;
        // TEST PRINT
        System.out.println("Number of nodes: " + numNodes);
        int numEdges = 0;
        for (ArrayList<Node> nodes : graph) {
            numEdges += nodes.size();
        }
        // TEST PRINT
        System.out.println("Number of edges: " + numEdges);

        // Arrays to store best path values in for each node.
        int[] bestPathCurrDirUp = new int[numNodes + 1];
        int[] bestPathCurrDirDown = new int[numNodes + 1];
        Arrays.fill(bestPathCurrDirUp, Integer.MIN_VALUE);
        Arrays.fill(bestPathCurrDirDown, Integer.MIN_VALUE);
        bestPathCurrDirUp[1] = 0;
        bestPathCurrDirDown[1] = 0;

        // Handle first half of graph's edges.
        int nodesChecked = 0;
        int edgesChecked = 0;
        while (edgesChecked < numEdges / 2) {
            // Handling beginning case where direction penalties cannot be given.
            if (nodesChecked == 0) {
                bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] = graph.get(nodesChecked).get(0).weight;
                bestPathCurrDirDown[graph.get(nodesChecked).get(1).id] = graph.get(nodesChecked).get(1).weight;
            } else {
                // Calculates possible best paths. Note that some may be underflowed, huge values, so we check for this.
                int bestPathUpFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight;
                int bestPathUpFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight
                                         + turnPenalty;
                int bestPathDownFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(1).weight
                                         + turnPenalty;
                int bestPathDownFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(1).weight;

                // Checks to see what paths to the currently checked node actually exist.
                // Non-existent paths are represented by Integer.MIN_VALUE, so if we don't make sure to check for this,
                // then we would always pick the non-existent paths due to integer underflow.
                if (bestPathCurrDirUp[nodesChecked + 1] == Integer.MIN_VALUE) {
                    bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] = bestPathUpFromDown;
                    bestPathCurrDirDown[graph.get(nodesChecked).get(1).id] = bestPathDownFromDown;
                } else if (bestPathCurrDirDown[nodesChecked + 1] == Integer.MIN_VALUE) {
                    bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] = bestPathUpFromUp;
                    bestPathCurrDirDown[graph.get(nodesChecked).get(1).id] = bestPathDownFromUp;
                } else {
                    bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] =
                            Integer.max(bestPathUpFromUp, bestPathUpFromDown);
                    bestPathCurrDirDown[graph.get(nodesChecked).get(1).id] =
                            Integer.max(bestPathDownFromUp, bestPathDownFromDown);
                }
            }
            nodesChecked++;
            edgesChecked += 2;
        }

        // TODO: Change dummy return number to something that matters once the method is complete
        return Integer.max(bestPathCurrDirUp[numNodes], bestPathCurrDirDown[numNodes]);
    }
}