import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

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

    static int noOfEventsV;
    static int mantissa;

    static int truePositive;
    static int trueNegative;
    static int falsePositive;
    static int falseNegative;


    public void startEVC() {

        //Initialize local variables
        internalEventCount = 0;
        sendEventCount = 0;
        receiveEventCount = 0;
        evcValueReached = new BigInteger("0");
        frequency = 10;
        probability = 50;

        truePositive = 0;
        trueNegative = 0;
        falsePositive = 0;
        falseNegative = 0;

        Scanner in = new Scanner(System.in);

        //Input No of Processes
        System.out.println("Enter no. of processes : ");
        noOfProcesses = in.nextInt();

        //Input EVC bit size
        System.out.println("Enter the EVC bit size (32 or 64) : ");
        bitSize = in.nextInt();

        //Calculate max EVC Value that can be accommodated
        maxEVCValue = new BigInteger("2").pow(((noOfProcesses*bitSize-1)-1));
        //maxEVCValue = new BigInteger("2").pow(((bitSize-1)-1));

        //Input the mantissa value
        System.out.print("Enter the mantissa value : ");
        mantissa = in.nextInt();

        //Number of events = n * V
        noOfEventsV = noOfProcesses * 50;

        //Initialize processes
        initializeProcesses();

        //generate Events and add to process queue
        generateEvents();


        System.out.println("Number of events generated : "+eventMap.size());

        Utility.compareEvents();

        System.out.println("True positives : "+truePositive);
        System.out.println("True Negative : "+trueNegative);
        System.out.println("False Positive : "+falsePositive);
        System.out.println("False Negative : "+falseNegative);

        double FPR = (double)falsePositive*100.00/(falsePositive + trueNegative);
        double FNR = (double)falseNegative*100.00/(falseNegative + truePositive);

        System.out.println("False Positive rate : "+FPR);
        System.out.println("False Negative rate : "+FNR);
    }


    private void initializeProcesses() {

        ArrayList<Integer> primeList = Utility.generatePrimeList(noOfProcesses);
        Apfloat two = new Apfloat(2, mantissa);

        for(int i=0; i<noOfProcesses; i++){
            Apfloat primeLog = ApfloatMath.log(new Apfloat(primeList.get(i), mantissa), two);
            Process pi = new Process(i, primeList.get(i), primeLog);
            processMap.put(pi.getProcessId(),pi);
            System.out.println(pi);
            pi.start();
        }
    }


    /*
    * Method to generate events and assign it to random process.
    * Add the generated event to the selected process queue.
    *
    * */
    private void generateEvents() {

        while(!Process.stop) {
            //Generate internal or communication event randomly
            Event event = new Event();
            event.setType(Utility.selectEventType());
            event.setTimeStamp(System.currentTimeMillis());

            //Assign generated event to random process
            event.setProcessId(Utility.selectRandomProcess());


            //Enqueue the generated event into the selected process queue
            Process selectedProcess = processMap.get(event.getProcessId());
            selectedProcess.getEventQueue().put(event);


            //Increment the respective event count
            if(event.getType().equals(EventType.INTERNAL)){
                internalEventCount++;
            }else{
                sendEventCount++;
            }

            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
