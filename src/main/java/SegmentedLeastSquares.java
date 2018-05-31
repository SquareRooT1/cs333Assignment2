import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Created by muhammedkanlidere on 29/03/2018.
 */

public class SegmentedLeastSquares {
    private static ArrayList<Point> points;
    private static double coefficient;
    private static double[] OPTcosts;
    private static int [] OPTsolution;
    private static ArrayList<Point> OPTsegment;
    private static double[][] errors, intercept, slope;

    private static void build (File file){
        System.out.println("Set the coefficient :");
        try{
            Scanner inputReader = new Scanner(System.in);
            coefficient = inputReader.nextDouble();
            points = new ArrayList<Point>();
            Scanner fileReader = new Scanner(file).useDelimiter("\\s+");;
            while(fileReader.hasNextLine()){
                Point point = new Point(fileReader.nextDouble(), fileReader.nextDouble());
                points.add(point);
            }
            int arrSize = points.size() + 1;
            OPTcosts = new double[arrSize];
            OPTsolution = new int[arrSize];
            Arrays.fill(OPTcosts, Integer.MAX_VALUE);
            Arrays.fill(OPTsolution, 0);
            errors = new double[arrSize][arrSize];
            intercept = new double[arrSize][arrSize];
            slope = new double[arrSize][arrSize];
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static void setErrors(int n){
        for(int j = 1; j <= n; j++){
            for(int i = 1; i <= j; i++){
                if(i == j)
                    errors[i][j] = Integer.MAX_VALUE;
                else
                    errors[i][j] = calculateError(i,j);
            }
        }
    }

    private static double calculateOPTCost(int size){
        OPTcosts[0] = 0;
        setErrors(size);
        for(int j = 1; j <= size; j++){
            double minCost = Integer.MAX_VALUE;
            int index = 0;
            for(int i = 1; i <= j; i++){
                double tempCost = errors[i][j] + OPTcosts[i-1] + coefficient;
                if(tempCost < minCost){
                    minCost = tempCost;
                    index = i;
                }
            }
            OPTcosts[j] = minCost;
            OPTsolution[j] = index;
        }
        return OPTcosts[size];
    }

    private static double calculateError(int index1, int index2){
        double  a = 0,
                b = 0,
                result = 0,
                totalX = 0,
                totalY = 0,
                totalXSqr = 0,
                totalXmultY = 0;

        int N = index2 - index1 + 1 ;
        for(int i = index1; i <= index2; i++){
            Point point = points.get(i - 1);
            totalX += point.x;
            totalY += point.y;
            totalXSqr += (point.x * point.x);
            totalXmultY += point.x * point.y;
        }

        double top = N * totalXmultY - (totalX * totalY);
        if(top == 0){
            a = 0;
        }
        else{
            double num = (N * totalXSqr - Math.pow(totalX, 2));
            if(num == 0)
                a = Integer.MAX_VALUE;
            else
                a = top / num;
        }
        b = ((totalY - a * totalX) / N);
        intercept[index1][index2] = a;
        slope[index1][index2] = b;
        for (int j = index1; j <= index2; j++) {
            Point point = points.get(j - 1);
            result += Math.pow((point.y - a * (point.x) - b), 2);
        }
        return result;
    }

    private static void setOPT(int N){
        OPTsegment = new ArrayList<Point>();
        for(int i = N, j = OPTsolution[N]; i > 0; i = j - 1, j = OPTsolution[i]) {
            Point segment = new Point(j,i);
            OPTsegment.add(segment);
        }
    }

    private static void printResult(double result){
        System.out.println("Solution Cost: " + (int)result + " Coefficient: " + coefficient);
        System.out.println("Optimal Solution is -> ");
        for(Point point : OPTsegment){
            System.out.println("Start Index: " + point.x + " End Index: " + point.y +
                    "a: " + intercept[(int)point.x][(int)point.y] + " b: " + slope[(int)point.x][(int)point.y]);
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("src/Points.txt");
        build(file);
        int size = points.size();
        double result = calculateOPTCost(size);
        setOPT(size);
        printResult(result);
    }

}
class Point{
    double x,y;
    Point(double x, double y){
        this.x = x;
        this.y = y;
    }
}
