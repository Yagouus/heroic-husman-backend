package domainLogic.workflow;

import config.constants.NameConstants;
import domainLogic.exceptions.*;
import domainLogic.workflow.Task.Task;
import domainLogic.workflow.logReader.LogReaderInterface;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Conxunto de entradas dun log dun workflow
 *
 * @author Vanesa G.P.
 * @version 0.1
 */
public class Log {
    /* ---------------------- Atributes ------------------------------*/
    //Se son moitas entradas igual non poden estar todas en memoria
    //pode que sexa necesario un sistema de E/S
    //ou que vaia lendo as entradas a medida qeu as precisa non como se esta a facer agora

    /**
     * HashMap de tarefas no que a clave � o identificador da tarefa e o valor a
     * propia tarefa. Non hai claves repetidas.
     */
    private HashMap<String, Task> tasks;
    private HashMap<Integer, Task> intToTask;
    /**
     * HashMap de instancias no que a clave � o identificador da instancia e o
     * valor a propia instancia. Non hai claves repetidas.
     */
    private ConcurrentHashMap<String, CaseInstance> caseInstances;
    /**
     * Obxecto que se encarga de leer o log e obter as entradas de este.
     */
    private LogReaderInterface reader;
    /**
     * Obxecto que se encarga de procesar as entradas do log para obter as
     * propiedades que se precisen. Por defecto � de tipo EntryProcessorImpl
     */
    private EntryProcessorInterface entryProcessor;
    private int numOfActivities;
    private int numOfCases;
    private final String name;
    private final String path;
    private int indexStartTask = Task.INVALID_TASK;
    private int indexEndTask = Task.INVALID_TASK;

    /* -------------------- Constructors -----------------------------*/
    protected Log(String name, String path) {
        super();
        Task.restartcount();
        tasks = new HashMap<>();
        intToTask = new HashMap<>();
        caseInstances = new ConcurrentHashMap<>();
        entryProcessor = new EntryProcessorImpl();
        this.name = name;
        this.path = path;
    }

    public Log(String name, String path, LogEntryInterface... entries)
            throws EmptyLogException, WrongLogEntryException {
        this(name, path);
        if (entries.length == 0) {
            throw new EmptyLogException();
        }
        addEntries(entries);
    }

    public Log(String name, String path, ArrayList<LogEntryInterface> entries)
            throws EmptyLogException, WrongLogEntryException {
        this(name, path);
        if (entries.isEmpty()) {
            throw new EmptyLogException();
        }
        addEntries(entries);
    }

    public Log(String name, String path, LogReaderInterface reader, File logFile, String workflowId)
            throws EmptyLogException, WrongLogEntryException, NonFinishedWorkflowException,
            InvalidFileExtensionException, MalformedFileException {
        this(name, path);
        this.reader = reader;
        readLog(logFile, workflowId);
    }

    public Log(String name, String path, LogReaderInterface reader, EntryProcessorInterface entryProcessor, File logFile, String workflowId)
            throws EmptyLogException, WrongLogEntryException, NonFinishedWorkflowException,
            InvalidFileExtensionException, MalformedFileException {
        this(name, path);
        this.reader = reader;
        if (entryProcessor != null) {
            this.entryProcessor = entryProcessor;
        }
        readLog(logFile, workflowId);
    }

    /* ------------------ Getters and setters-------------------------*/
    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Task> getIntToTasks() {
        return intToTask;
    }

    public String getPath() {
        return path;
    }

    public Task getTask(Integer elem) {
        return intToTask.get(elem);
    }
    public Task getTask(String elem) {
        return tasks.get(elem);
    }

    public String getName() {
        return name;
    }

    public int getNumOfTasks() {
        return this.tasks.size();
    }

    public int getNumOfCases() {
        return this.numOfCases;
    }

    public ConcurrentHashMap<String, CaseInstance> getCaseInstances() {
        return caseInstances;
    }

    public Integer getNumOfActivities() {
        return numOfActivities;
    }

    public int getIndexStartTask() {
        return indexStartTask;
    }

    public int getIndexEndTask() {
        return indexEndTask;
    }

    public void setCaseInstances(ArrayList<CaseInstance> cases) {
        this.caseInstances.clear();
        for (int i = 0; i < cases.size(); i++) {
            this.caseInstances.put(cases.get(i).getId(), cases.get(i));
        }
    }

    /**
     * Devolve un array de casos (Case) que a s�a vez se compo�en de tarefas
     * (Task). Previamente tiveron que lerse as entradas do blog
     *
     * @return - arraylist de CaseInstance das entradas que ten o log
     */
    public ArrayList<CaseInstance> getArrayCaseInstances() {
        return new ArrayList<>(caseInstances.values());
    }

    public ArrayList<Task> getArrayTasks() {
        return new ArrayList<>(tasks.values());
    }

    public LinkedHashSet<CaseInstance> getArrayCaseInstancesSimlpified() {
        return new LinkedHashSet<>(caseInstances.values());
    }

    /* --------------------- Other methods ---------------------------*/

    /**
     * Lee o log co reader do atributo
     *
     * @param logFileName - Nome do arquivo que cont�n o log. Pode ser nulo se o
     *                    reader non o necesita.
     * @param workflowId  - identificador que permite seleccionar un subconxunto
     *                    de entradas no log. Pode ser nulo se o reader non o precisa.
     * @throws EmptyLogException
     * @throws WrongLogEntryException
     */
    public void readLog(String logFileName, String workflowId)
            throws EmptyLogException, WrongLogEntryException,
            NonFinishedWorkflowException, InvalidFileExtensionException, MalformedFileException {
        File logFile = new File(logFileName);
        readLog(logFile, workflowId);
    }

    /**
     * Lee o log co reader do atributo
     *
     * @param logFile - Arquivo que cont�n o log. Pode ser nulo se o reader non
     *                o necesita.
     * @workflowId - identificador que permite seleccionar un subconxunto de
     * entradas no log. Pode ser nulo se o reader non o precisa.
     */
    private void readLog(File logFile, String workflowId)
            throws EmptyLogException, WrongLogEntryException,
            NonFinishedWorkflowException, InvalidFileExtensionException, MalformedFileException {
        addEntries(reader.read(null, workflowId, logFile));
    }

    /**
     * Hai que engadir as entradas do log no orde na que aparecen
     *
     * @param entries
     */
    private void addEntries(LogEntryInterface... entries)
            throws WrongLogEntryException {
        if (entries != null) {
            final int entriesSize = entries.length;
            if (entriesSize > 0) {
                this.numOfActivities = entriesSize;
                for (int i = 0; i < entriesSize; i++) {
                    processEntry(entries[i]);
                }
            }
        }
    }

    /**
     * Hai que engadir as entradas do log no orde na que aparecen
     *
     * @param entries
     */
    private void addEntries(ArrayList<LogEntryInterface> entries)
            throws WrongLogEntryException {
        if (entries != null) {
            final int entriesSize = entries.size();
            if (entriesSize > 0) {
                numOfActivities = entriesSize;
                for (int i = 0; i < entriesSize; i++) {
                    processEntry(entries.get(i));
                }
            }
        }
    }

    /**
     * @param entry
     */
    private void processEntry(LogEntryInterface entry)
            throws WrongLogEntryException {
        Task t = tasks.get(entry.getTaskIdentifier());
        t = addTask(entryProcessor.createUpdateTask(t, entry));
        CaseInstance ci = caseInstances.get(entry.getCaseIdentifier());
        ci = addCase(entryProcessor.createUpdateCase(ci, entry));
        ci.addToTaskSequence(t.getMatrixID());
    }

    private Task addTask(Task t) {
        //Comprobamos se existe a tarefa
        if (!tasks.containsKey(t.getId())) {
            tasks.put(t.getId(), t);
            intToTask.put(t.getMatrixID(), t);
        }
        return t;
    }

    private CaseInstance addCase(CaseInstance ci) {
        //Comprobamos se existe a instancia ou caso
        if (!caseInstances.containsKey(ci.getId())) {
            caseInstances.put(ci.getId(), ci);
            numOfCases++;
        }
        return ci;
    }

    public void checkSQM() {
        LinkedList<CaseInstance> cases = new LinkedList<>(this.caseInstances.values());
        for (CaseInstance nCase : cases) {
            int subPatternSize = 0;
            TIntArrayList taskSeqOriginal = nCase.getTaskSequence();
            for (CaseInstance otherCase : cases) {
                if (!nCase.getId().equals(otherCase.getId())) {
                    TIntArrayList taskSeqOther = otherCase.getTaskSequence();
                    final int taskSeqOriginalSize = taskSeqOriginal.size();
                    final int taskSeqOtherSize = taskSeqOther.size();
                    int longestSeq;
                    if (taskSeqOriginalSize < taskSeqOtherSize) {
                        longestSeq = taskSeqOriginalSize;
                    } else {
                        longestSeq = taskSeqOtherSize;
                    }
                    ArrayList<Integer> pattern = new ArrayList<>();
                    for (int indexTask = 0; indexTask < longestSeq; indexTask++) {
                        if (taskSeqOriginal.get(indexTask) == taskSeqOther.get(indexTask)) {
                            pattern.add(taskSeqOther.get(indexTask));
                        } else {
                            if (!pattern.isEmpty()) {
                                if (pattern.size() > subPatternSize) {
                                    subPatternSize = pattern.size();
                                }
                            }
                            break;
                        }
                    }
                    if (!pattern.isEmpty()) {
                        List<String> relatedCases = nCase.getSPrelated().get(pattern.size());
                        if (relatedCases == null) {
                            relatedCases = new ArrayList<>();
                        }
                        relatedCases.add(otherCase.getId());
                        nCase.getSPrelated().put(pattern.size(), relatedCases);
                        if (pattern.size() > nCase.getMaxPatternSize()) {
                            nCase.setMaxPatternSize(pattern.size());
                        }
                    }
                }
            }
        }
    }
    /* cada marcado sé qué marcados tiene asociados, ejecuto, y guardo todos los relacionados, necesito crear un árbol 
     El marking un hashmap <casId, marking> que se vaya actualizando*/

    public void simplifyAndAddDummies(final boolean simplifyLog, boolean addDummies) {
        if (!simplifyLog && !addDummies) {
            return;
        }
        LinkedList<CaseInstance> cases = new LinkedList<>(this.caseInstances.values());
        AbstractCollection<CaseInstance> newCases;
        //if we want the log simplified, we can use a linkedhashset(to preserve the order) to
        // remove duplicates. On the other hand, if we want the raw log,
        // we have to allocate all the cases in a ArrayList.
        if (simplifyLog) {
            newCases = new LinkedHashSet<>(cases);
        } else {
            newCases = new ArrayList<>(cases);
        }
        this.caseInstances.clear();
        Task end = tasks.get(NameConstants.END_DUMMY_TASK + NameConstants.TASK_DELIMETER + NameConstants.TASK_COMPLETE);
        Task start = tasks.get(NameConstants.START_DUMMY_TASK + NameConstants.TASK_DELIMETER + NameConstants.TASK_COMPLETE);
        if (end == null && start == null && addDummies) {
            end = new Task(NameConstants.END_DUMMY_TASK + NameConstants.TASK_DELIMETER + NameConstants.TASK_COMPLETE);
            end.setType(Task.FINAL);
            start = new Task(NameConstants.START_DUMMY_TASK + NameConstants.TASK_DELIMETER + NameConstants.TASK_COMPLETE);
            start.setType(Task.INITIAL);
            this.tasks.put(start.getId(), start);
            this.indexStartTask = start.getMatrixID();
            this.tasks.put(end.getId(), end);
            this.indexEndTask = end.getMatrixID();
            this.numOfActivities += numOfCases * 2;
        } else {
            // the log already have dummies, so, no need to re-add 'em
            addDummies = false;
        }

        for (CaseInstance newCase : newCases) {
            final int casesSize = cases.size();
            for (int indexCases = casesSize - 1; indexCases >= 0; indexCases--) {
                if (simplifyLog) {
                    if (!cases.get(indexCases).getId().equals(newCase.getId()) && cases.get(indexCases).getTaskSequence().equals(newCase.getTaskSequence())) {
                        newCase.increaseNumrepetitions();
                        cases.remove(indexCases);
                    }
                }
            }
            // lets assume that if one case already have the dummies, all the cases have 'em.
            if (addDummies) {
                addDummies = newCase.addDummieTasks(start.getMatrixID(), end.getMatrixID());
            }
            caseInstances.put(newCase.getId(), newCase);
        }
    }

    public void addNumOfActivities(Integer numOfActivities) {
        this.numOfActivities += numOfActivities;
    }

    public void addNumOfCases(int numOfCases) {
        this.numOfCases += numOfCases;
    }
}
