public class question1 {

    public static void main(String[] args) {
        for (int j=1; j<=4; j++) {
        //for (int j=10; j<=25; j=j+5) {
            FibonacciHeap f = new FibonacciHeap();
            int m = (int) Math.pow(2, j);
            System.out.println("m is 2**"+j+ " = " + m);
            int[] toDecValues = new int[j+1];
            for (int i=j; i>=1; i--) {
                toDecValues[i] = m - (int) Math.pow(2,i) + 1;
                //toDecValues[i] = m - (int) Math.pow(2,i) ; //1.4
            }
            int i = 1;
            FibonacciHeap.HeapNode[] toDec = new FibonacciHeap.HeapNode[j+1];
            int linksBefore = f.linksCounter;
            int cutsBefore = f.cutsCounter;
            //start
            long startTime = System.currentTimeMillis();
            FibonacciHeap.HeapNode toDec6 = null; //1.6
            for (int k = m-1; k>=-1; k--) {
                FibonacciHeap.HeapNode ele = f.insert(k);
                /**1.6*/
                if (k == m-2) {
                    toDec6 = ele;
                }
                // */
                if ((i < toDecValues.length) && (toDecValues[i] == k)) {
                    toDec[i] = ele;
                    i++;
                }
            }
            f.deleteMin();
            //HeapPrinter.print(f, false);
            for (int t=j; t>=1; t--) {
               // System.out.print(toDec[t].getKey()+", ");
                f.decreaseKey(toDec[t], m+1);
            }
            HeapPrinter.print(f, true);
            f.decreaseKey(toDec6, m+1); //1.6
            //System.out.println();
            HeapPrinter.print(f, false);
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("run time " + totalTime);
            System.out.println("marked " + f.getNumMark());
            System.out.println("trees " + f.getTrees());
            System.out.println("links " + (f.linksCounter - linksBefore));
            System.out.println("cuts " + (f.cutsCounter - cutsBefore));
            System.out.println("potential " + f.potential());
        }
    }
}
