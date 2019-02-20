import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Event implements Comparable{

    private int id;
    private long timeStamp;
    private EventType type;
    private BigInteger evcValue;
    private int processId;
    private Apfloat evcValueLog;

    public Event() {
    }

    public Event(int id, long timeStamp, EventType type, BigInteger evcValue, int processId, Apfloat evcValueLog) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.type = type;
        this.evcValue = evcValue;
        this.processId = processId;
        this.evcValueLog = evcValueLog;
    }


    //Getters and Setters for the class variables - Start
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

    public Apfloat getEvcValueLog() {
        return evcValueLog;
    }

    public void setEvcValueLog(Apfloat evcValueLog) {
        this.evcValueLog = evcValueLog;
    }

    //Getters and Setters for the class variables - End

    //Method to execute the events from the process queue
    public void execute(){
        if(type.equals(EventType.INTERNAL)){
            localExecute();
        }else if(type.equals(EventType.SEND)){
            sendExecute();
        }else{
            receiveExecute();
        }
    }

    //Method to executing the Receive event
    private void receiveExecute() {

        //Calculate LCM value
        Process process = EVC.processMap.get(getProcessId());
        BigInteger evcValue = Utility.findLcm(process.getEvcValue(), getEvcValue());

        Apfloat evcValueLog = Utility.logMerge(process.getEvcValueLog(), getEvcValueLog());

        //local clock tick
        process.setEvcValue(evcValue.multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(evcValue);

        process.setEvcValueLog(evcValueLog.add(process.getPrimeNoLog()));
        setEvcValueLog(process.getEvcValueLog());

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }

        synchronized (EVC.eventMap){
            this.setId(EVC.eventMap.size());
            EVC.eventMap.put(EVC.eventMap.size(), this);
        }

        if(EVC.eventMap.size()%10 == 0){
            System.out.println("No. of Events : "+EVC.eventMap.size()+ " & EVC bit size : "+ Utility.bitCount(EVC.evcValueReached));
        }
    }

    //Method to executing the Send event
    private void sendExecute() {

        //local clock tick
        Process process = EVC.processMap.get(getProcessId());
        process.setEvcValue(process.getEvcValue().
                multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(process.getEvcValue());

        process.setEvcValueLog(process.getEvcValueLog().add(process.getPrimeNoLog()));
        setEvcValueLog(process.getEvcValueLog());

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }

        synchronized (EVC.eventMap){
            this.setId(EVC.eventMap.size());
            EVC.eventMap.put(EVC.eventMap.size(), this);
        }

        //Enqueue receive event at the receiving process
        Event event = new Event();
        event.setType(EventType.RECEIVE);
        event.setTimeStamp(getTimeStamp() + 10);
        event.setProcessId(Utility.selectRandomReceivingProcess(getProcessId()));
        event.setEvcValue(process.getEvcValue());
        event.setEvcValueLog(process.getEvcValueLog());

        Process selectedProcess = EVC.processMap.get(event.getProcessId());
        selectedProcess.getEventQueue().put(event);
        selectedProcess.getReceiveEventMap().put(event.getId(), event);
        EVC.receiveEventCount++;

        if(EVC.eventMap.size()%10 == 0){
            System.out.println("No. of Events : "+EVC.eventMap.size()+ " & EVC bit size : "+ Utility.bitCount(EVC.evcValueReached));
        }

    }

    //Method to executing the Internal event
    private void localExecute() {

        //local clock tick
        Process process = EVC.processMap.get(getProcessId());
        process.setEvcValue(process.getEvcValue().
                multiply(new BigInteger(String.valueOf(process.getPrimeNo()))));
        setEvcValue(process.getEvcValue());

        process.setEvcValueLog(process.getEvcValueLog().add(process.getPrimeNoLog()));
        setEvcValueLog(process.getEvcValueLog());

        synchronized (EVC.evcValueReached ){
            if(getEvcValue().compareTo(EVC.evcValueReached) == 1){
                EVC.evcValueReached = getEvcValue();
            }
        }

        synchronized (EVC.eventMap){
            this.setId(EVC.eventMap.size());
            EVC.eventMap.put(EVC.eventMap.size(), this);
        }

        if(EVC.eventMap.size()%10 == 0){
            System.out.println("No. of Events : "+EVC.eventMap.size()+ " & EVC bit size : "+ Utility.bitCount(EVC.evcValueReached));
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

    public int compare(Event e2){

        BigInteger zero = new BigInteger("0");
        if(getEvcValue().compareTo(e2.getEvcValue()) > 0){
            if(getEvcValue().mod(e2.getEvcValue()).equals(zero)){
                return 1;
            }
        }else if(getEvcValue().compareTo(e2.getEvcValue()) < 0){
            if(e2.getEvcValue().mod(getEvcValue()).equals(zero)){
                return -1;
            }
        }

        return 0;
    }

    public int compareLog(Event e2){

        BigDecimal ten = new BigDecimal(10);
        Apfloat two = new Apfloat(2, EVC.mantissa);
        BigDecimal temp;

        BigDecimal f1 = new BigDecimal(Math.pow(10,5));

        if(getEvcValueLog().compareTo(e2.getEvcValueLog()) > 0){
            temp = new BigDecimal(getEvcValueLog().subtract(e2.getEvcValueLog()).doubleValue());
            temp = temp.divide(f1);
            //temp = new BigDecimal(Math.pow(2.0,temp.doubleValue()));
            long longValue = temp.longValue();
            if(temp.doubleValue()==longValue){
                return 1;
            }
        }else if(e2.getEvcValueLog().compareTo(getEvcValueLog()) > 0){
            temp = new BigDecimal(e2.getEvcValueLog().subtract(getEvcValueLog()).doubleValue());
            temp = temp.divide(f1);
            //temp = new BigDecimal(Math.pow(2.0,temp.doubleValue()));
            long longValue = temp.longValue();
            if(temp.doubleValue()==longValue){
                return -1;
            }
        }

        return 0;
    }

    @Override
    public String toString(){
        return "Event {id:" +id+", Evc:"+evcValue+", log:"+evcValueLog+", process:"+processId+"}";
    }
}

