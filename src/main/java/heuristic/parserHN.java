package heuristic;

import domainLogic.workflow.algorithms.geneticMining.individual.CMIndividual;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

/**
 * Created by yagouus on 12/07/17.
 */
public class parserHN {


    private final static String HEAD = "digraph G {\n"
            + " nodesep=\".1\"; ranksep=\".1\";  fontsize=\"9\"; remincross=true;  fontname=\"Arial\";\n"
            + " node [ height=\".3\", width=\".3\", fontname=\"Arial\",fontsize=\"9\"];\n  		"
            + " edge [arrowsize=\"0.7\",fontsize=\"9\",fontname=\"Arial\",arrowhead=\"vee\"];\n";
    private final static String CLOSER = "];\n";
    private final static String ARC = " [label=\"  \"];\n";
    private final static String NODE = "[shape=\"box\",label=";

    public static String translate(CMIndividual ind) {
        String graphDot;

        graphDot = "";

        graphDot += HEAD;
        graphDot += "\n";
        graphDot += "\n";

        graphDot += writeNodes(ind);
        graphDot += "\n";

        graphDot += writeArcs(ind);
        graphDot += "\n";
        graphDot += "}";

        return graphDot;
    }

    private static String writeNodes(CMIndividual ind) {
        String nodesString, realName;
        final int numOfTasks = ind.getNumOfTasks();

        nodesString = "";

        for (int indexOfTask = 0; indexOfTask < numOfTasks; indexOfTask++) {
            realName = ind.getTask(indexOfTask).getTask().getId();
            nodesString += checkIsDummyNode(simplifyName(realName));
        }

        return nodesString;
    }

    private static String writeArcs(CMIndividual ind) {
        TIntHashSet outputs;
        final int numOfTasks = ind.getNumOfTasks();
        String nameActualTask, nodesString;

        nodesString = "";

        for (int indexOfTask = 0; indexOfTask < numOfTasks; indexOfTask++) {
            outputs = ind.getTask(indexOfTask).getOutputs().getUnionSubsets();
            nameActualTask = ind.getTask(indexOfTask).getTask().getId();
            TIntIterator tasks = outputs.iterator();
            while (tasks.hasNext()) {
                int id = tasks.next();
                nodesString += simplifyName(nameActualTask) + " -> " + simplifyName(ind.getTask(id).getTask().getId()) + ARC;
            }
        }

        return nodesString;
    }

    private static String checkIsDummyNode(String name) {
        return name.replaceAll("\\s", "") + NODE + "\"" + name + "\"" + CLOSER;
    }

    private static String simplifyName(String name) {
        return name.replaceAll(":complete", "");
    }
}

