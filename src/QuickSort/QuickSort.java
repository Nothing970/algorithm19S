package QuickSort;

import javax.print.attribute.standard.PrinterMakeAndModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class QuickSort {

    Random random = new Random();
    int total = 1000000;
    int[] a = new int[total];
    int[] b = new int[total];
    int[] c = new int[total];

    public void swap(int[] arr, int i, int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public int partition(int[] arr, int start, int end){
//        int randNumber = random.nextInt(end - start + 1) + start;
        int randNumber = start;
        swap(arr, end, randNumber);
        int cur = arr[end];
        int i = start - 1;
        for(int j = start; j <= end - 1; j++){
            if(arr[j] <= cur){
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, ++i, end);
        return i;
    }

    public void quickSort(int[] arr, int start, int end){
        if(start < end){
            int index = partition(arr, start, end);
            quickSort(arr, start, index - 1);
            quickSort(arr, index + 1, end);
        }
    }


    public void dualPivotQuickSort(int[] items, int start, int end) {
        if (start < end) {
            if (items[start] > items[end]) {
                swap(items, start, end);
            }
            int pivot1 = items[start], pivot2 = items[end];
            int i = start, j = end, k = start + 1;
            OUT_LOOP:
            while (k < j) {
                if (items[k] < pivot1) {
                    swap(items, ++i, k++);
                } else if (items[k] <= pivot2) {
                    k++;
                } else {
                    while (items[--j] > pivot2) {
                        if (j <= k) {
                            // 扫描终止
                            break OUT_LOOP;
                        }
                    }

                    if (items[j] < pivot1) {
                        swap(items, j, k);
                        swap(items, ++i, k);
                    } else {
                        swap(items, j, k);
                    }
                    k++;
                }
            }
            swap(items, start, i);
            swap(items, end, j);

            dualPivotQuickSort(items, start, i - 1);
            dualPivotQuickSort(items, i + 1, j - 1);
            dualPivotQuickSort(items, j + 1, end);
        }
    }

    public void generateNums(int i){
        if(i == 10){
            Arrays.fill(a,1);
            Arrays.fill(b,1);
            Arrays.fill(c,1);
            return;
        }

        int maxIndex = total - 100000 * i;
        boolean[] used = new boolean[total];

        for(int j = 0; j < maxIndex; j++){
            int tmp = random.nextInt(total);
            if(used[tmp]){
                j--;
            }else{
                used[tmp] = true;
                a[j] = tmp;
                b[j] = tmp;
                c[j] = tmp;
            }
        }
        for(int j = maxIndex; j < total; j++){
            int tmp = random.nextInt(j);
            a[j] = a[tmp];
            b[j] = b[tmp];
            c[j] = c[tmp];
        }
    }


    public static void main(String[] args) {
        int[] arr = {15,1,14,2,13,3,12,4,11,5,10,6,9,7,8};
        QuickSort q = new QuickSort();
        q.quickSort(arr, 0, arr.length - 1);
        for(int num : arr){
            System.out.println(num);
        }


        for(int i = 1; i <= 9; i++){
            System.out.println("----------i=" + i + "----------");
            q.generateNums(i);
            long startTime = System.currentTimeMillis();
            Arrays.sort(q.a);
            long endTime = System.currentTimeMillis();
            long t1 = endTime - startTime;
            System.out.println("自带的快排时间: " + t1 + "ms");

            startTime = System.currentTimeMillis();
            q.dualPivotQuickSort(q.b, 0, q.total - 1);
            endTime = System.currentTimeMillis();
            long t2 = endTime - startTime;
            System.out.println("双轴的快排时间: " + t2 + "ms");

            startTime = System.currentTimeMillis();
            q.quickSort(q.c, 0, q.total - 1);
            endTime = System.currentTimeMillis();
            long t3 = endTime - startTime;
            System.out.println("实现的快排时间: " + t3 + "ms");
        }


    }

}
