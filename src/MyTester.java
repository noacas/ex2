import java.util.ArrayList;
import java.util.Collections;

public class MyTester {
    public static void main(String[] args) {
        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        for (int i = 20; i > 10; i--) {
            fibonacciHeap.insert(i);
        }

        fibonacciHeap.insert(4);
        FibonacciHeap.HeapNode node5 = fibonacciHeap.insert(5);
        FibonacciHeap.HeapNode node6 = fibonacciHeap.insert(6);
        fibonacciHeap.deleteMin();

        fibonacciHeap.insert(1);
        fibonacciHeap.insert(2);
        fibonacciHeap.insert(3);
        fibonacciHeap.deleteMin();

        fibonacciHeap.insert(1);
        for (int i = 30; i > 20; i--) {
            fibonacciHeap.insert(i);
        }

        while (fibonacciHeap.potential() != 1) {
            fibonacciHeap.deleteMin();
        }
        for (int i = 20; i > 10; i--) {
            fibonacciHeap.insert(i);
        }
        while (fibonacciHeap.potential() != 1) {
            fibonacciHeap.deleteMin();
        }
        int[] res = fibonacciHeap.kMin(fibonacciHeap, fibonacciHeap.size() - 3);
        for (int i=0; i< res.length; i++) {
            System.out.println(res[i]);
        }
        for (int i=0; i< res.length; i++) {
            System.out.println(fibonacciHeap.findMin().getKey());
            fibonacciHeap.deleteMin();
        }
    }
}
