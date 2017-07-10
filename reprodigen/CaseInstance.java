/**
 *
 */
package domainLogic.workflow;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Vane
 * @since 24/02/2011
 */
public final class CaseInstance {
    /* ----------------------- Atributes -----------------------------*/

    /**
     * Identificador da instancia
     */
    private String id;
    /**
     * Secuencia ordenada de tarefas que se levaron a cabo na instancia
     */
    private TIntArrayList taskSequence;
    private HashMap<Integer, List<String>> SPrelated;
    private int maxPatternSize;
    /**
     * Outras propiedades da instancia
     */
    private CaseProperties properties;
    private int numInstances = 1;

    /* --------------------- Constructors ----------------------------*/
    public CaseInstance() {
        taskSequence = new TIntArrayList();
        SPrelated = new HashMap<>();
    }

    public CaseInstance(String id) {
        this();
        this.id = id;
    }

    public CaseInstance(CaseInstance caseInstance) {
        this();
        this.id = caseInstance.id;
        this.properties=caseInstance.properties;
        final int sizeTaskSeq = caseInstance.taskSequence.size();
        for (int i = 0; i < sizeTaskSeq; i++) {
            this.taskSequence.add(caseInstance.taskSequence.get(i));
        }
    }
    /* ------------------ Getters and setters-------------------------*/

    public int getMaxPatternSize() {
        return maxPatternSize;
    }

    public void setMaxPatternSize(int maxPatternSize) {
        this.maxPatternSize = maxPatternSize;
    }

    
    public HashMap<Integer, List<String>> getSPrelated() {
        return SPrelated;
    }

    public void setSPrelated(HashMap<Integer, List<String>> SPrelated) {
        this.SPrelated = SPrelated;
    }

    public void setTaskSequence(TIntArrayList taskSecuency) {
        this.taskSequence = taskSecuency;
    }

    public TIntArrayList getTaskSequence() {
        return taskSequence;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setProperties(CaseProperties properties) {
        this.properties = properties;
    }

    public CaseProperties getProperties() {
        return properties;
    }

    public int getNumInstances() {
        return numInstances;
    }

    public void setNumInstances(int numRepetitions) {
        this.numInstances = numRepetitions;
    }

    public void increaseNumrepetitions() {
        this.numInstances++;
    }

    public void decreaseNumrepetitions() {
        this.numInstances--;
    }

    /* --------------------- Other methods ---------------------------*/
    /**
     * Metodo que engade unha tarefa a secuencia de tarefas de unha instancia
     *
     * @param task - Tarefa que se vai a engadir
     */
    public void addToTaskSequence(Integer task) {
        this.taskSequence.add(task);
    }

    public boolean addDummieTasks(int start, int end) {
        if (this.taskSequence.get(0) != start) {
            this.taskSequence.insert(0, start);
        } else {
            return false;
        }
        if (this.taskSequence.get(this.taskSequence.size() - 1)!= end) {
            this.taskSequence.add(end);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CaseInstance other = (CaseInstance) obj;
        final int taskSequenceSize = this.taskSequence.size();
        if (taskSequenceSize != other.taskSequence.size()) {
            return false;
        } else {
            for (int indexTask = 0; indexTask < taskSequenceSize; indexTask++) {
                if (other.taskSequence.get(indexTask) != this.taskSequence.get(indexTask)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.taskSequence);
        return hash;
    }
}
