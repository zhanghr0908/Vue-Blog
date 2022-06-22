package com.ltj.blog;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@SpringBootTest
class Tests {

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        int n = scanner.nextInt();
//        int [] arr = new int[2];
//        for(int i = 0; i < 2; i++) {
//            arr[i] = scanner.nextInt();
//        }
//        List<int[]> list = new ArrayList<>();
//
//        for(int i = 0; i < n; i++) {
//            int [] temp = new int[2];
//            temp[0] = scanner.nextInt();
//            temp[1] = scanner.nextInt();
//            list.add(temp);
//        }
//    }
    public static void main(String[] args) {
//        int n = 6;
//        int[] nums = {1, 1, 1, 0, 0, 0};
//        int n = 6;
//        int[] nums = {1, 0, 1, 0, 0, 1};
        int n = 6;
        int[] nums = {0, 1, 0, 0, 1, 1};
        System.out.println(getAnswer(nums));
    }

    public static int getAnswer(int[] nums) {
        int ans = 0;
        for(int second = 0; second < nums.length; second++) {
            int temp1 = Integer.MAX_VALUE;
            int temp2 = Integer.MAX_VALUE;
            if (nums[second] != 1) continue;
            else {
                for(int i = second; i < nums.length; i++) {
                    if (nums[i] == 0) {
                        temp1 = i;
                        break;
                    }
                }
                for(int j = second; j >= 0; j--) {
                    if (nums[j] == 0) {
                        temp2 = j;
                        break;
                    }
                }
                int min = Math.min(temp1, temp2);
                nums[min] = 2;
                ans += Math.abs(second - min);
            }
        }
        return ans;
    }

}
