import java.math.BigInteger;
import java.util.ArrayList;
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

        if (x.compareTo(BigInteger.valueOf(0))==0) {
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
}
