/* Katie Bernard
 * 12/12/22
 */
import java.io.IOException;

public class GraphAlgorithmsTests{

    public static void main(String[] args) throws IOException{
        //Case 1: readData
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("StateData.csv");

            //verify
            System.out.println(g);

            //assert
            assert g != null : "Problem in readData";
        }

        //Case 2: shortestPaths
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("miniState.csv");
            

            //verify
            System.out.println(GraphAlgorithms.shortestPaths(g, g.getVertex(0)));

            //assert
            assert GraphAlgorithms.shortestPaths(g, g.getVertex(0)) != null : "Problem in getShortestPaths";
        }

        //Case 3: allHamCycles
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("miniState.csv");
            

            //verify
            System.out.println(GraphAlgorithms.allHamCycles(g, g.getVertex(0)));

            //assert
            assert GraphAlgorithms.allHamCycles(g, g.getVertex(0)) != null : "Problem in allHamCycles";
        }

        //Case 4: minTSP
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("miniState.csv");
            

            //verify
            System.out.println(GraphAlgorithms.minTSP(g, g.getVertex(0)));

            //assert
            assert GraphAlgorithms.minTSP(g, g.getVertex(0)) != null : "Problem in minTSP";
        }

        //Case 4: mst
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("StateData.csv");
            

            //verify
            System.out.println(GraphAlgorithms.mst(g));

            //assert
            assert GraphAlgorithms.mst(g) != null : "Problem in mst";
        }

        //Case 5: tspApprox
        {
            //setup
            Graph<String, Object> g = new Graph<>();
            g = GraphAlgorithms.readData("StateData.csv");
            

            //verify
            System.out.println(GraphAlgorithms.tspApprox(g));

            //assert
            assert GraphAlgorithms.tspApprox(g) != null : "Problem in tspApprox";
        }

        System.out.println("***ALL TESTS PASSED SUCCESSFULLY***");

    }
}