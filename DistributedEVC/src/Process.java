import java.math.BigInteger;
import java.util.Hashtable;
import java.util.concurrent.PriorityBlockingQueue;

public class Process extends Thread{

    private int processId;
    private int primeNo;
    private BigInteger evcValue;
    private int totalEventCount;

    private Hashtable<Integer, Event> internalEventMap = new Hashtable<>();
    private Hashtable<Integer, Event> sendEventMap = new Hashtable<>();
    private Hashtable<Integer, Event> receiveEventMap = new Hashtable<>();
    private PriorityBlockingQueue<Event> eventQueue = new PriorityBlockingQueue<>();

    static boolean stop = false;

    public Process() {
    }

    public Process(int processId, int primeNo) {
        this.processId = processId;
        this.primeNo = primeNo;
        this.evcValue = new BigInteger("1");
        this.totalEventCount = 0;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int id) {
        this.processId = id;
    }

    public int getPrimeNo() {
        return primeNo;
    }

    public void setPrimeNo(int primeNo) {
        this.primeNo = primeNo;
    }

    public BigInteger getEvcValue() {
        return evcValue;
    }

    public void setEvcValue(BigInteger evcValue) {
        this.evcValue = evcValue;
    }

    public Hashtable<Integer, Event> getInternalEventMap() {
        return internalEventMap;
    }

    public void setInternalEventMap(Hashtable<Integer, Event> internalEventMap) {
        this.internalEventMap = internalEventMap;
    }

    public Hashtable<Integer, Event> getSendEventMap() {
        return sendEventMap;
    }

    public void setSendEventMap(Hashtable<Integer, Event> sendEventMap) {
        this.sendEventMap = sendEventMap;
    }

    public Hashtable<Integer, Event> getReceiveEventMap() {
        return receiveEventMap;
    }

    public void setReceiveEventMap(Hashtable<Integer, Event> receiveEventMap) {
        this.receiveEventMap = receiveEventMap;
    }

    public int getTotalEventCount() {
        return totalEventCount;
    }

    public void setTotalEventCount(int totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public PriorityBlockingQueue<Event> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(PriorityBlockingQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {

        while(!stop){
            if(getEvcValue().compareTo(EVC.maxEVCValue) == 1){
                stop = true;
            }else{
                if(!eventQueue.isEmpty()){
                    deQueueExecute();
                }else{
                    try {
                        sleep(EVC.frequency);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stop();
    }

    private void deQueueExecute(){

        Event event = eventQueue.poll();
        if(event.getType().equals(EventType.INTERNAL)){
            internalEventMap.put(event.getId(), event);
        }else if(event.getType().equals(EventType.SEND)){
            sendEventMap.put(event.getId(), event);
        }else{
            receiveEventMap.put(event.getId(),event);
        }
        totalEventCount++;
        event.execute();
    }
}
