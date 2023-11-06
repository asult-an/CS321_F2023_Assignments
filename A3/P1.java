/**
 * Program Authors: Matthew Bertello, James Last, Adeel Sultan
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        Object[] input = readInput("./input1.txt");

        int turnPenalty = (int)input[0];

        ArrayList<ArrayList<Node>> graph;
        if(input[1] != null){
            graph = (ArrayList<ArrayList<Node>>)input[1];
        } 
        else{
            System.out.println("Input error");
            throw new RuntimeErrorException(null);
        }


        // Call method for getting the best path from first node to final node. Prints cost.
        long startTime = System.currentTimeMillis();
        int bestPath = findBestPath(turnPenalty, graph);
        System.out.println("Best path cost: " + bestPath);
        // Total time taken from beginning to end.
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Time taken finding the best path: " + totalTime + " milliseconds");

        //Call brute force method for getting the best path cost
        ArrayList<ArrayList<Node>> BFPaths = new ArrayList<ArrayList<Node>>();
        ArrayList<Integer> costs = new ArrayList<Integer>();
        BFBestPath(graph, 0, graph.size() + 1, new ArrayList<Node>(), turnPenalty, BFPaths, costs);
        int max = Integer.MIN_VALUE;
        for(Integer cost : costs){
            if(cost > max){
                max = cost;
            }
        }
        System.out.println("Brute Force solution: " + max);
    }
    /**
     * Reads an input file to generate and return an adjacency list graph and the turn penalty for the problem
     * @return Object[], with a list of lists of adjacent nodes and the turn penalty
     */
    public static Object[] readInput(String inputFilePath){
        Object[] data = new Object[2];
        ArrayList<ArrayList<Node>> graph = new ArrayList<>();

        try{
            File inputFile = new File(inputFilePath);
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
     * Generates all possible paths from the start node to the end node of a square grid graph using a brute force algorithm.
     * Then finds the cost associated with each path
     * @param graph The square grid graph
     * @param startNode The starting node
     * @param endNode The ending node
     * @param path A possible path
     * @param TP The turn penalty
     * @param paths A list of all possible paths
     * @param costs A list of costs associated with each possible path
     */
    public static void BFBestPath(ArrayList<ArrayList<Node>> graph, int startNode, int endNode, ArrayList<Node> path, int TP, ArrayList<ArrayList<Node>> paths, ArrayList<Integer> costs){
        //Recursively generate all possible paths from the start to the end node
        BFGeneratePathHelper(graph, 0, graph.size() + 1, new ArrayList<Node>(), TP, paths);

        int n = (int)Math.sqrt(graph.size() + 1);
        //for every possible path, calculate the cost of the graph traversal
        for(ArrayList<Node> racePath : paths){
            int raceCost = 0;
            int column = 1;
            boolean previousTurnUp = false;
            //calculate the cost in the expanding part of the graph
            for(int i = 0; i < n - 1; ++i){
                raceCost += racePath.get(i).weight;
                if(i != 0){
                    boolean currentTurnUp = racePath.get(i).id == (racePath.get(i - 1).id + column);
                    if(previousTurnUp == !currentTurnUp){
                        raceCost += TP;
                    }
                    previousTurnUp = currentTurnUp;
                }
                else if(racePath.get(i).id == 2){
                    previousTurnUp = true;
                }
                else{
                    previousTurnUp = false;
                }
                
                ++column;
            }
            //calculate the cost in the converging part of the graph
            for(int i = n - 1; i < 2 * n - 2; ++i){
                raceCost += racePath.get(i).weight;
                boolean currentTurnUp = racePath.get(i).id == (racePath.get(i - 1).id + (column - 1));
                if(previousTurnUp == !currentTurnUp){
                        raceCost += TP;
                }
                previousTurnUp = currentTurnUp;
                --column;
            }
            column = 0;

            //add the calculated cost to the list of costs
            costs.add(raceCost);
        }
    }
    /**
     * Generates all possible paths from the start node to the end node using a brute force approach.
     * @param graph The graph to traverse
     * @param startNode The starting node
     * @param endNode The ending node
     * @param path A possible path
     * @param TP The turn penalty
     * @param paths A list of all possible paths
     */
    public static void BFGeneratePathHelper(ArrayList<ArrayList<Node>> graph, int startNode, int endNode, ArrayList<Node> path, int TP, ArrayList<ArrayList<Node>> paths){
        //add TP logic
        for(Node neighbor : graph.get(startNode)){
            path.add(neighbor);
            if(neighbor.id == endNode){
                ArrayList<Node> newPath = new ArrayList<>(path);
                paths.add(newPath);
            }
            else{
                BFGeneratePathHelper(graph, neighbor.id - 1, endNode, path, TP, paths);
                
            }
            path.remove(path.size() - 1);
        }
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
        //System.out.println("Number of nodes: " + numNodes);
        int nodeSideLength = (int)Math.sqrt(numNodes);
        int numEdges = nodeSideLength * (nodeSideLength - 1) * 2;
        // TEST PRINT
        //System.out.println("Number of edges: " + numEdges);

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


        // Handle second half of graph's edges. edgesChecked does not need to be updated beyond this point!!!
        // "Column length" of nodes is used to determine when we should consider a node with one neighbor to be going
        // down, or up.
        int currentColumnLength = nodeSideLength;
        // TEST PRINT
        //System.out.println("Middle column's length (also the \"k\" in k by k): " + currentColumnLength);
        while (currentColumnLength > 1) {
            // Top node in column (only has one neighbor, going down)
            int bestPathDownFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight
                    + turnPenalty;
            int bestPathDownFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight;

            // Checks to see what paths to the currently checked node actually exist.
            if (bestPathCurrDirDown[nodesChecked + 1] == Integer.MIN_VALUE) {
                bestPathCurrDirDown[graph.get(nodesChecked).get(0).id] = bestPathDownFromUp;
            } else {
                bestPathCurrDirDown[graph.get(nodesChecked).get(0).id] =
                        Integer.max(bestPathDownFromUp, bestPathDownFromDown);
            }
            nodesChecked++;

            //Middle nodes (ones with two neighbors), of which there should be column length minus 2 (the top and bottom)
            for (int i = 0; i < currentColumnLength - 2; i++) {
                int bestPathUpFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight;
                int bestPathUpFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight
                        + turnPenalty;
                bestPathDownFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(1).weight
                        + turnPenalty;
                bestPathDownFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(1).weight;

                // No check is needed to be done here as all middle nodes have two paths leading to them.
                bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] =
                        Integer.max(bestPathUpFromUp, bestPathUpFromDown);
                bestPathCurrDirDown[graph.get(nodesChecked).get(1).id] =
                        Integer.max(bestPathDownFromUp, bestPathDownFromDown);
                nodesChecked++;
            }

            // Bottom node in column (only has one neighbor, going up)
            int bestPathUpFromUp = bestPathCurrDirUp[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight;
            int bestPathUpFromDown = bestPathCurrDirDown[nodesChecked + 1] + graph.get(nodesChecked).get(0).weight
                    + turnPenalty;

            // Checks to see what paths to the currently checked node actually exist.
            if (bestPathCurrDirUp[nodesChecked + 1] == Integer.MIN_VALUE) {
                bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] = bestPathUpFromDown;
            } else {
                bestPathCurrDirUp[graph.get(nodesChecked).get(0).id] =
                        Integer.max(bestPathUpFromUp, bestPathUpFromDown);
            }
            nodesChecked++;

            currentColumnLength--;
        }

        return Integer.max(bestPathCurrDirUp[numNodes], bestPathCurrDirDown[numNodes]);
    }
}