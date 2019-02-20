import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class EVC {

    static int noOfProcesses;
    private int bitSize;
    private int internalEventCount;
    private int sendEventCount;
    static int receiveEventCount;
    static int frequency;
    static int probability;
    static BigInteger evcValueReached;

    static Hashtable<Integer, Process> processMap = new Hashtable<>();
    static BigInteger maxEVCValue;
    static Hashtable<Integer, Event> eventMap = new Hashtable<>();


    public void startEVC() {

        //Initialize local variables
        internalEventCount = 0;
        sendEventCount = 0;
        receiveEventCount = 0;
        evcValueReached = new BigInteger("0");
        frequency = 10;
        probability = 0;

        Scanner in = new Scanner(System.in);

        //Input No of Processes
        System.out.println("Enter no. of processes : ");
        noOfProcesses = in.nextInt();

        //Input EVC bit size
        System.out.println("Enter the bit size (32 or 64) : ");
        bitSize = in.nextInt();

        //Calculate max EVC Value that can be accommodated
        maxEVCValue = new BigInteger("2").pow(((noOfProcesses*bitSize-1)-1));
        //maxEVCValue = new BigInteger("2").pow(((bitSize-1)-1));

        System.out.println("EVC bit size (n*bitSize): "+noOfProcesses*bitSize);
        System.out.println("Maximum EVC value : "+maxEVCValue);

        //Initialize processes
        initializeProcesses();

        //generate Events and add to process queue
        generateEvents();

        System.out.println("Number of events generated : "+eventMap.size());
        System.out.println("EVC value reached : "+evcValueReached);
        System.out.println("EVC bit size reached : "+Utility.bitCount(evcValueReached));
        System.out.println("Internal Event : "+internalEventCount);

    }


    private void initializeProcesses() {

        ArrayList<Integer> primeList = Utility.generatePrimeList(noOfProcesses);

        for(int i=0; i<noOfProcesses; i++){
            Process pi = new Process(i, primeList.get(i));
            processMap.put(pi.getProcessId(),pi);
            pi.start();
        }
    }


    private void generateEvents() {

        while(!Process.stop) {
            //Generate internal or communication event randomly
            Event event = new Event();
            event.setType(Utility.selectEventType());
            event.setTimeStamp(System.currentTimeMillis());

            //Assign generated event to random process
            event.setProcessId(Utility.selectRandomProcess());

            synchronized (eventMap){
                event.setId(eventMap.size());
                eventMap.put(eventMap.size(), event);
            }

            //Enqueue the generated event into the selected process queue
            Process selectedProcess = processMap.get(event.getProcessId());
            selectedProcess.getEventQueue().put(event);

            //Increment the respective event count
            if(event.getType().equals(EventType.INTERNAL)){
                internalEventCount++;
            }else{
                sendEventCount++;
            }

            if(eventMap.size()%10 == 0){
                System.out.println("No. of Events : "+eventMap.size()+ " & EVC bit size : "+Utility.bitCount(evcValueReached));
            }

            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
