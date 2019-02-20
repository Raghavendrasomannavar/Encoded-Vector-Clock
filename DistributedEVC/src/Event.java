import java.math.BigInteger;

public class Event implements Comparable{

    private int id;
    private long timeStamp;
    private EventType type;
    private BigInteger evcValue;
    private int processId;

    public Event() {
    }

    public Event(int id, long timeStamp, EventType type, BigInteger evcValue, int processId) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.type = type;
        this.evcValue = evcValue;
        this.processId = processId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public BigInteger getEvcValue() {
        return evcValue;
    }

    public void setEvcValue(BigInteger evcValue) {
        this.evcValue = evcValue;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public void execute(){
        if(type.equals(EventType.INTERNAL)){
            localExecute();
        }else if(type.equals(EventType.SEND)){
            sendExecute();
        }else{
            receiveExecute();
        }
    }

    private void receiveExecute() {

        //Calculate LCM value
        Process process = EVC.processMap.get(getProcessId());
        BigInteger evcValue = Utility.findLcm(process.getEvcValue(), getEvcValue());

        //local clock tick
        process.setEvcValue(evcValue.multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(evcValue);

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }
    }

    private void sendExecute() {

        //local clock tick
        Process process = EVC.processMap.get(getProcessId());
        process.setEvcValue(process.getEvcValue().
                multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(process.getEvcValue());

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }

        //Enqueue receive event at the receiving process
        Event event = new Event();
        event.setType(EventType.RECEIVE);
        event.setTimeStamp(getTimeStamp() + 10);
        event.setProcessId(Utility.selectRandomReceivingProcess(getProcessId()));
        event.setEvcValue(process.getEvcValue());

        synchronized (EVC.eventMap){
            event.setId(EVC.eventMap.size());
            EVC.eventMap.put(EVC.eventMap.size(), event);
        }

        Process selectedProcess = EVC.processMap.get(event.getProcessId());
        selectedProcess.getEventQueue().put(event);
        EVC.receiveEventCount++;

    }

    private void localExecute() {

        //local clock tick
        Process process = EVC.processMap.get(getProcessId());
        process.setEvcValue(process.getEvcValue().
                multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(process.getEvcValue());

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }

    }

    @Override
    public int compareTo(Object o) {
        if(this.equals(o)){
            return 0;
        }
        else if(getTimeStamp()>((Event)o).getTimeStamp())
        {
            return -1;
        }
        return 1;
    }
}

