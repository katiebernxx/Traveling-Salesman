/* Katie Bernard
 * 12/12/22
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.*;


public class GraphAlgorithms{


    /**
     * @param filename
     * @return a graph of the data it read from the file
     * @throws IOException
     */
    public static Graph<String, Object> readData(String filename) throws IOException{
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        Graph<String, Object> newGraph = new Graph<>();
        //Hashmap that maps strings to graph vertices
        HashMap<String, Graph.Vertex<String, Object>> cities = new HashMap<>();

        br.readLine();
        String line = br.readLine();
        while(line != null){
            String[] contents = line.split(",");
            String state1 = contents[1];
            String state2 = contents[3];
            
            if(!cities.containsKey(state1)){
                cities.put(state1, newGraph.addVertex(state1));
            }
            // if(!cities.containsKey(state1)){
            //    // System.out.println("unsuccessful1");
            // }

            if(!cities.containsKey(state2)){
                cities.put(state2, newGraph.addVertex(state2));
              //  System.out.println("putted");
            }
            // if(!cities.containsKey(state2)){
            // //    System.out.println("unsuccessful2");
            // }

            double miles = Double.parseDouble(contents[4]);
            int intMiles = (int) miles;
            newGraph.addEdge(cities.get(state1), cities.get(state2), intMiles);
            line = br.readLine();
        }

        return newGraph;
    }

    /**
     * @param <V>
     * @param <E>
     * @param g
     * @param source
     * @return a hashmap of each vertex mapped to the distance of the shortest path to get to it
     */
    public static <V, E> HashMap<Graph.Vertex<V, E>, Double> shortestPaths(Graph<V, E> g, Graph.Vertex<V, E> source){
        HashMap<Graph.Vertex<V, E>, Double> distances = new HashMap<>();
        //setting the hashmap with all the verticies as keys and their values to all be infinity
        for(Graph.Vertex<V, E> vertex : g.vertices){
            distances.put(vertex, Double.POSITIVE_INFINITY);
        } distances.put(source, 0.0); //except source's value is 0

        PriorityQueue<Graph.Vertex<V, E>> queue = new PriorityQueue<>(new Comparator<Graph.Vertex<V, E>>(){
            @Override
            //new queue of vertices prioritizing by shorter distances
            public int compare(Graph.Vertex<V, E> o1, Graph.Vertex<V, E> o2){
                return distances.get(o1).compareTo(distances.get(o2));
            }
        });

        for(Graph.Vertex<V, E> vertex : g.vertices){
            queue.offer(vertex);
        }

        while(!queue.isEmpty()){ //O(V) where V is the number of vertices
            //Poll fron the queue. For each edge attached to that vertex, reassign the distance to be
            Graph.Vertex<V, E> cur = queue.poll(); //O(log V)

            for(Graph.Edge<V,E> edgeOut : cur.edgesOut()){ //O(V)
                Graph.Vertex<V, E> next = edgeOut.other(cur);

                double curDistToNext = distances.get(next);
                double newDist = distances.get(cur) + ((Graph.WeightedEdge<V, E>) edgeOut).weight;
                if(newDist < curDistToNext){
                    distances.put(next, newDist);
                    queue.remove(next); //O(log V) for java, but could be O(1) if implemented better
                    queue.offer(next);
                }
            }
        } return distances;
    }

    /**
     * @param <V>
     * @param <E>
     * @param g
     * @param start
     * @return a collection of Ham Cycle solutions, which are lists of edges of visiting all the vertices and returning to the same point
     */
    public static <V, E> Collection<List<Graph.Edge<V, E>>> allHamCycles(Graph<V, E> g, Graph.Vertex<V, E> start){
        Collection<List<Graph.Edge<V, E>>> output = new ArrayList<>();
        List<Graph.Vertex<V, E>> curPath = new ArrayList<Graph.Vertex<V, E>>();
        curPath.add(start);
        allHamCycles(g, output, curPath);
        return output;
    }

    //Helper method
    public static <V, E> Collection<List<Graph.Edge<V, E>>> allHamCycles(Graph<V, E> g, Collection<List<Graph.Edge<V, E>>> output, List<Graph.Vertex<V, E>> curPath){
        if(curPath.size() == g.vertices.size()){
            List<Graph.Edge<V, E>> edges = new ArrayList<Graph.Edge<V, E>>();
            for(Graph.Vertex<V, E> vertex : curPath){
                Graph.Edge<V, E> curEdge = vertex.getEdgeTo(curPath.get((curPath.indexOf(vertex)+1) % curPath.size()));
                edges.add(curEdge);
            }
            output.add(edges);
            return output;
        }
        else{
            Graph.Vertex<V, E> last = curPath.get(curPath.size()-1);
            for(Graph.Vertex<V, E> neighbor : last.neighborsOut()){
                if (curPath.contains(neighbor)) continue;
                curPath.add(neighbor);
                allHamCycles(g, output, curPath);
                curPath.remove(neighbor);
            }
        }
        return output;
    
    }

    /**
     * @param <V>
     * @param <E>
     * @param g
     * @param source
     * @return the smallest/cheapest Ham Cycle in the form of an ordered list of edges
     */
    public static <V,E> List<Graph.Edge<V, E>> minTSP(Graph<V, E> g, Graph.Vertex<V, E> source){
        double shortestSum = Double.POSITIVE_INFINITY;  
        List<Graph.Edge<V, E>> shortestCycle = null;      
        Collection<List<Graph.Edge<V, E>>> h = allHamCycles(g, g.getVertex(0));
        for(List<Graph.Edge<V, E>> list : h){
            double curSum = 0.0;
            for (Graph.Edge<V, E> edge : list){
                curSum += ((Graph.WeightedEdge<V, E>) edge).weight;
            }
            if(curSum<shortestSum){
                shortestSum =curSum;
                shortestCycle = list;
            }
        }
        return shortestCycle;
    }

    /**
     * @param <V>
     * @param <E>
     * @param g
     * @return the minimal spanning tree of the graph where each vertex is connected in the shortest way possible
     *         in the form of a collection of edges
     */
    public static <V, E> Collection<Graph.Edge<V, E>> mst(Graph<V, E> g){
        class Data {
            double curDistance;
            Graph.Vertex<V, E> prev;
            Graph.Edge<V, E> prevEdge;
            public Data(double dist, Graph.Vertex<V,E> vert){
                curDistance = dist;
                prev = vert;
            }            
        }

        HashMap<Graph.Vertex<V,E>, Data> vertices = new HashMap<>();
        Collection<Graph.Vertex<V, E>> used = new ArrayList<Graph.Vertex<V, E>>();
        Collection<Graph.Edge<V, E>> mst = new ArrayList<Graph.Edge<V, E>>();

        PriorityQueue<Graph.Vertex<V,E>> unused = new PriorityQueue<Graph.Vertex<V,E>>(new Comparator<Graph.Vertex<V,E>>(){
            @Override
            public int compare(Graph.Vertex<V,E> v1, Graph.Vertex<V,E> v2) {
                double v1Dist = vertices.get(v1).curDistance;
                double v2Dist = vertices.get(v2).curDistance;
                if(v1Dist -v2Dist<0) return -1;
                else if(v1Dist - v2Dist > 0) return 1;
                else return 0;
            }
            });

        for (Graph.Vertex<V, E> vertex : g.verticesOrdered){
            Data dataPoint = new Data(Double.POSITIVE_INFINITY, null);
            vertices.put(vertex, dataPoint);
            unused.offer(vertex);
        } 

        Graph.Vertex<V, E> cur = unused.poll();
        while(unused.size() !=0){
            //System.out.println(cur);
            used.add(cur);
            if (vertices.get(cur).prevEdge != null) mst.add(vertices.get(cur).prevEdge);
            for(Graph.Edge<V, E> edge : cur.edgesOut()){

                double weight = ((Graph.WeightedEdge<V, E>) edge).weight;
                if(!used.contains(edge.other(cur)) && weight < vertices.get(edge.other(cur)).curDistance){
                    vertices.get(edge.other(cur)).curDistance = weight;
                    vertices.get(edge.other(cur)).prev = cur;
                    vertices.get(edge.other(cur)).prevEdge = edge;

                    unused.remove(edge.other(cur));
                    unused.offer(edge.other(cur));

                }
            }
            cur=unused.poll();
        }
        return mst;

    }

    /**
     * @param <V>
     * @param <E>
     * @param g
     * @return the approximate solution for the shortest way to visit all the vertices and return to the same one
     *         in the form of a collection of edges in order.
     */
    public static <V, E> Collection<Graph.Edge<V, E>> tspApprox(Graph<V, E> g){
        Collection<Graph.Edge<V, E>> mst = mst(g);
        Stack<Graph.Vertex<V, E>> stack = new Stack<Graph.Vertex<V,E>>();
        ArrayList<Graph.Vertex<V,E>> visited = new ArrayList<Graph.Vertex<V, E>>();
        List<Graph.Vertex<V, E>> path = new ArrayList<Graph.Vertex<V,E>>();
        List<Graph.Edge<V, E>> toReturn = new ArrayList<Graph.Edge<V,E>>();

        stack.push(g.getVertex(18));
        visited.add(g.getVertex(18));
        Graph.Vertex<V,E> cur = null;
        while(!stack.isEmpty()){
            //check if this is the final city
            if(path.size() == 46){
                cur = stack.pop();
                path.add(cur);
                stack.push(g.getVertex(18));
                visited.remove(18);
                
            }

            if(path.size() == 47){
                
                cur = stack.pop();
                path.add(cur);

                for(Graph.Vertex<V, E> vertex : path){
                    if(toReturn.size()<47){ 
                        Graph.Edge<V, E> curEdge = vertex.getEdgeTo(path.get((path.indexOf(vertex)+1) % path.size()));
                        toReturn.add(curEdge);
                    }
                }
                return toReturn;
            }
            cur = stack.pop();

            Collection<Graph.Edge<V,E>> edgesOut = cur.edgesOut();
            ArrayList<Graph.Edge<V,E>> edges = new ArrayList<Graph.Edge<V,E>>();

            for(Graph.Edge<V,E> edge : edgesOut){
                edges.add(edge);
            }
            Collections.shuffle(edges);

            for(Graph.Edge<V,E> edge : edges){
                if(mst.contains(edge) && !visited.contains(edge.other(cur))){
                    stack.push(edge.other(cur));
                    visited.add(edge.other(cur));
                }
            }
            
            path.add(cur);

        }
        
        return toReturn;
    }

    public static void main(String[] args) throws IOException{
        Graph<String, Object> g = new Graph<>();
        g = readData("airportDatacut.csv");
    //     System.out.println(g);
    //     System.out.println(shortestPaths(g, g.getVertex(0)));
    //     System.out.println(allHamCycles(g, g.getVertex(0)));
        // System.out.println(minTSP(g, g.verticesOrdered.get(0)));
          System.out.println(tspApprox(g));
    //    for(int i = 0; i<g.verticesOrdered.size(); i++){
    //     System.out.println(""+ i + " " + g.getVertex(i));
    //    }
    //    System.out.println();
    //    System.out.println(g.verticesOrdered);

    }
}