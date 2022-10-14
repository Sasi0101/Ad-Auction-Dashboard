package com.example.seg;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Functions {

    /**
     * Constructor for Functions
     */
    public Functions(){
        //doesn't take in anything
    }

    /**
     * n choose r method
     * @param n the total number of elements
     * @param r the number to choose
     * @return the BigDecimal result
     */
    public BigDecimal NChooseR(int n, int r) {
        int nMinusX = n - r;
        BigDecimal nFactorial = factorial(n);
        BigDecimal rFactorial = factorial(r);
        BigDecimal nMinusRFactorial = factorial(nMinusX);
        BigDecimal result = new BigDecimal(0);
        try {
            result = nFactorial.divide(nMinusRFactorial.multiply(rFactorial), 100, RoundingMode.HALF_UP);
        } catch (Exception e) {
            System.out.println(e);
        }
        //Used for testing
        /*System.out.println(n);
        System.out.println(r);
        System.out.println(nMinusX);
        System.out.println("nFactorial: " + nFactorial.intValue());
        System.out.println("rFactorial: " + rFactorial.intValue());
        System.out.println("nMinusRFactorial: " + nMinusRFactorial.intValue());
        System.out.println(nMinusRFactorial.multiply(rFactorial));
        System.out.println("result: " + result.intValue());*/
        return result;
    }

    /**
     * a to the power of b method (no decimal numbers)
     * @param numberA base
     * @param numberB exponent
     * @return the BigDecimal value
     */
    public BigDecimal aToPowerOfb(BigDecimal numberA, BigDecimal numberB){
        //System.out.println(numberA);
        //System.out.println(numberB);
        BigDecimal ans = new BigDecimal(1);
        try {
            if (numberB.longValue() > 0) {
                for (int i = numberB.intValue(); i > 0; i--) {
                    ans = ans.multiply(numberA);
                }
            } else if (numberB.longValue() < 0) {
                for (int i = numberB.intValue(); i < 0; i++) {
                    ans = ans.divide(numberA);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ans;
    }

    /**
     * a to the power of B method (can handle decimal numbers)
     * @param numberA the base
     * @param numberB the exponent
     * @return the BigDecimal result
     */
    public BigDecimal aToPowerOfBPoisson(BigDecimal numberA, BigDecimal numberB){
        //System.out.println("first number: "+numberA);
        //System.out.println("second number: "+numberB);
        BigDecimal ans = new BigDecimal(1);
        String[] bSplit;
        if (numberB.doubleValue() < 0) {
            var bSplit1 = numberB.toString().split("-");
            bSplit = bSplit1[1].split("\\.");
        } else {
            bSplit = numberB.toString().split("\\.");
        }
        if (bSplit.length < 2) {
            ans = aToPowerOfb(numberA,numberB);
        } else {
            try {
                if (numberB.doubleValue() > 0) {
                    for (int i = Integer.parseInt(bSplit[0]); i > 0; i--) {
                        ans = ans.multiply(numberA);
                        //System.out.println(ans);
                    }
                    //System.out.println("0."+bSplit[1]);
                    ans = ans.multiply(nthRoot(numberA.doubleValue(),convertDecToFrac(Double.parseDouble("0."+bSplit[1]))));
                    //System.out.println(ans);
                } else if (numberB.doubleValue() < 0) {
                    for (int i = Integer.parseInt("-"+bSplit[0]); i < 0; i++) {
                        //System.out.println(i);
                        ans = ans.divide(numberA,100,RoundingMode.HALF_UP);
                        //System.out.println(ans);
                    }
                    //System.out.println("0."+bSplit[1]);
                    ans = ans.multiply(BigDecimal.valueOf(1 / nthRoot(numberA.doubleValue(),convertDecToFrac(Double.parseDouble("0."+bSplit[1]))).doubleValue()));
                    //System.out.println(ans);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //System.out.println(ans);
        return ans;
    }

    /**
     * method to calculate the factorial of a number
     * @param number to be calculated with
     * @return the BigDecimal result
     */
    public BigDecimal factorial(int number){
        BigDecimal result = new BigDecimal(1);
        for (int i=number;i > 1;i--) {
            result = result.multiply(BigDecimal.valueOf(i));
            //System.out.println(result);
        }
        return result;
    }

    /**
     * method to calculate the binomial PD value
     * @param x the number of successes
     * @param n the number of total trials
     * @param p the probability of each occurrence
     * @return the BigDecimal result
     */
    public BigDecimal binomialPdCalc(int x, int n, BigDecimal p){
        BigDecimal q = new BigDecimal(BigDecimal.valueOf(1).subtract(p).toString());
        //System.out.println(q);
        BigDecimal partOne = NChooseR(n,x);
        BigDecimal partTwo = aToPowerOfb(p,BigDecimal.valueOf(x));
        BigDecimal partThree = aToPowerOfb(q,BigDecimal.valueOf(n-x));

        //Used for testing
        //System.out.println("partOne: " + partOne);
        //System.out.println("partTwo: " + partTwo);
        //System.out.println("partThree: " + partThree);

        BigDecimal result = BigDecimal.valueOf(1);
        result = result.multiply(partOne);
        result = result.multiply(partTwo);
        result = result.multiply(partThree);

        //Checking for result too big
        String[] resultSplit = result.toString().split("-");
        if (resultSplit.length > 1) {
            int exp = Integer.parseInt(resultSplit[resultSplit.length - 1]);
            if (exp >= 100) {
                result = BigDecimal.valueOf(0);
            }
        }

        return result;
    }

    /**
     * method to calculate the Poisson PD value
     * @param x the number of successes
     * @param l the mean number of successes
     * @return a BigDecimal value to maintain accuracy
     */
    public BigDecimal poissonPdCalc(int x, BigDecimal l){
        //Calculating the different parts of the equation
        BigDecimal partOne = aToPowerOfBPoisson(l,BigDecimal.valueOf(x));
        BigDecimal partTwo = aToPowerOfBPoisson(BigDecimal.valueOf(Math.E),l.negate());
        BigDecimal partThree = factorial(x);

        //Used for testing
        System.out.println("partOne: " + partOne);
        System.out.println("partTwo: " + partTwo);
        System.out.println("partThree: " + partThree);

        //combining the full equation
        BigDecimal result = BigDecimal.valueOf(1);
        result = result.multiply(partOne);
        result = result.multiply(partTwo);
        result = result.divide(partThree,100,RoundingMode.HALF_UP);

        //Checking for result has too many figures
        String[] resultSplit = result.toString().split("-");
        if (resultSplit.length > 1) {
            int exp = Integer.parseInt(resultSplit[resultSplit.length - 1]);
            if (exp >= 100) {
                result = BigDecimal.valueOf(0);
            }
        }
        return result;
    }

    /**
     * method to calculate the nth root of a variable
     * @param numberIn the number to be rooted
     * @param fraction the fraction to be rooted by
     * @return the BigDecimal result
     */
    public BigDecimal nthRoot(Double numberIn,String fraction){
        String[] fractSplit = fraction.split("/");
        double result = 0;
        if (fractSplit.length > 1) {
            result = Math.pow(numberIn, Double.parseDouble(fractSplit[0]) / Double.parseDouble(fractSplit[1]));
        }
        return BigDecimal.valueOf(result);
    }

    /**
     * method to convert a decimal to a fraction
     * @param x the decimal
     * @return the String fraction
     */
    public String convertDecToFrac(double x){
        if (x < 0) {
            return "-" + convertDecToFrac(-x);
        }
        double tolerance = 1.0E-6;
        double h1=1; double h2=0;
        double k1=0; double k2=1;
        double b = x;
        do {
            double a = Math.floor(b);
            double aux = h1; h1=a*h1+h2; h2 = aux;
            aux = k1; k1=a*k1+k2; k2 = aux;
            b=1/(b-a);
        } while (Math.abs(x-h1/k1) > x*tolerance);

        return h1+"/"+k1;
    }

}

