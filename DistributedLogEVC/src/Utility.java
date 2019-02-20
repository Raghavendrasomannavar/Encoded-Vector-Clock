import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.LossOfPrecisionException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utility {

    public static ArrayList<Integer> generatePrimeList(int n) {

        ArrayList<Integer> primeList = new ArrayList<>();
        int status = 1, num = 3, count, j;

        if (n >= 1)
        {
            primeList.add(2);
        }

        for (count = 1; count <n;)
        {
            for (j = 2; j <= Math.sqrt(num); j++)
            {
                if (num%j == 0)
                {
                    status = 0;
                    break;
                }
            }
            if (status != 0)
            {
                primeList.add(num);
                count++;
            }
            status = 1;
            num++;
        }
        return primeList;
    }


    public static EventType selectEventType() {

        Random rand = new Random();
        int randomNo1 = rand.nextInt(100);
        if ( randomNo1 > EVC.probability )
            return EventType.INTERNAL;
        else
            return EventType.SEND;
    }

    public static int selectRandomProcess() {
        Random rand = new Random();
        return rand.nextInt(EVC.noOfProcesses);
    }

    public static int selectRandomReceivingProcess(int id) {

        boolean result = false;
        int processId = 0;
        while(!result){
            processId = selectRandomProcess();
            if(processId != id){
                result = true;
            }
        }
        return processId;
    }

    public static BigInteger findLcm(BigInteger x, BigInteger y){

        return (x.multiply(y)).divide(finGcd(x,y));
    }

    private static BigInteger finGcd(BigInteger x, BigInteger y) {

        if(x.compareTo(y) >= 1){
            BigInteger temp = x;
            x = y;
            y = temp;
        }

        if (x.compareTo(BigInteger.valueOf(0))==0) {
            return y;
        }
        return finGcd(y.mod(x), x);
    }

    private static Apfloat finGcd(Apfloat x, Apfloat y) {

        Apfloat zero = new Apfloat("0", EVC.mantissa);

        if(x.compareTo(y)>0){
            Apfloat temp = x;
            x = y;
            y = temp;
        }


        if (x.compareTo(zero)==0) {
            return y;
        }
        return finGcd(y.mod(x), x);
    }

    static long bitCount(BigInteger n){
        long count;

        //count = (int)logBigInteger(n) + 1;
        count = (long)(Math.log(n.doubleValue())/Math.log(2))+1;
        return  count;
    }

    private static double logBigInteger(BigInteger val) {
        if (val.signum() < 1)
            return val.signum() < 0 ? Double.NaN : Double.NEGATIVE_INFINITY;
        int blex = val.bitLength() - 977; // any value in 60..1023 works ok here
        if (blex > 0)
            val = val.shiftRight(blex);
        double res = Math.log(val.doubleValue());
        return blex > 0 ? res + blex * Math.log(2.0) : res;
    }

    public static Apfloat logMerge(Apfloat evcValueX, Apfloat evcValueY) {

        Apfloat finalEvc;
        Apfloat temp = new Apfloat("0", EVC.mantissa);
        Apfloat two = new Apfloat(2, EVC.mantissa);
        try {

            temp = ApfloatMath.log(finGcd(ApfloatMath.pow(two, evcValueX), ApfloatMath.pow(two, evcValueY)), two);

        }catch(LossOfPrecisionException ex){

        }
        finalEvc = evcValueX.add(evcValueY).subtract(temp);
        return finalEvc;
    }

    public static void compareEvents(){

        List<Event> eventList = new ArrayList<Event>(EVC.eventMap.values());

        Event ei, ej;
        for(int i=0; i<eventList.size()-1; i++){
            ei = eventList.get(i);
            for(int j=i+1; j<eventList.size(); j++){
                ej = eventList.get(j);

                if(ei.compare(ej) == ei.compareLog(ej)){
                    if((ei.compare(ej))==1 || (ei.compare(ej))==-1){
                        EVC.truePositive++;
                    }else {
                        EVC.trueNegative++;
                    }
                }else{
                    if((ei.compare(ej))==1){
                        if(ei.compareLog(ej) == -1){
                            EVC.falsePositive++;
                            printEvents("FP", ei, ej);
                        }else{
                            EVC.falseNegative++;
                            printEvents("FN", ei, ej);
                        }
                    }else if((ei.compare(ej))== -1){
                        if(ei.compareLog(ej) == 1){
                            EVC.falsePositive++;
                            printEvents("FP", ei, ej);
                        }else{
                            EVC.falseNegative++;
                            printEvents("FN", ei, ej);
                        }
                    }else{
                        if(ei.compareLog(ej) != 0){
                            EVC.falsePositive++;
                            printEvents("FP", ei, ej);
                        }
                    }
                }
            }

        }
    }

    public static void printEvents(String countType, Event ei, Event ej){
        System.out.println(countType+" : ");
        System.out.println("E1 - "+ei);
        System.out.println("E2 - "+ej);
    }
}
