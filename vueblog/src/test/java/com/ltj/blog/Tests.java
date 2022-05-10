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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int [] arr = new int[2];
        for(int i = 0; i < 2; i++) {
            arr[i] = scanner.nextInt();
        }
        List<int[]> list = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            int [] temp = new int[2];
            temp[0] = scanner.nextInt();
            temp[1] = scanner.nextInt();
            list.add(temp);
        }
//        list.forEach(a -> {
//            System.out.println(a[0] + ", " + a[1]);
//        });
        Tests t = new Tests();
        Map map = t.find(list, arr[0], arr[1]);
        Map<Object, Object> result = new HashMap<>();
        for(int i = 1; i <= n; i++) {
            result.put(i, 0);
        }
        map.forEach((k,v)->{
            result.put(k, v);
        });
        result.forEach((k,v)-> System.out.println(k + " " + v));
    }


    private Map<Integer, Integer> result;
    private void backTracing(List<int[]> list, int xSum, int ySum, int xGoal, int yGoal, int startIndex, int num) {
        if (xSum == xGoal && ySum == yGoal) result.put(num, result.getOrDefault(num, 0) + 1);
        for (int i = startIndex; i < list.size(); i++) {
            backTracing(list, xSum + list.get(i)[0], ySum + list.get(i)[1], xGoal, yGoal, i + 1, num+1);
        }
    }

    public Map<Integer, Integer> find(List<int[]> list, int xGoal, int yGoal) {
        result = new HashMap<>();
        backTracing(list, 0, 0, xGoal, yGoal, 0, 0);
        return result;
    }



}
