package com.example.overapp.Utils;

import java.util.Random;

public class NumberController {
    public static int getRandomNumber(int min, int max) {
        if (min != max) {
//            创建一个新的Random对象来生成随机数。
            Random random = new Random();
//            生成一个[0, max)范围内的随机整数。并确保生成的随机数在[0, max-min]范围内。
            return random.nextInt(max) % (max - min + 1) + min;
        } else return min;
    }



    public static int[] getRandomNumberList(int min, int max, int n) {
        //判断是否已经达到索要输出随机数的个数
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n]; //用于存放结果的数组
        int count = 0;
        while (count < n) {
            int num = getRandomNumber(min, max);
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }
    // 参数n必须大于0
    public static int[] getRandomExceptList(int min, int max, int n, int except) {
        //判断是否已经达到索要输出随机数的个数
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n]; //用于存放结果的数组
        int count = 0;
        while (count < n) {
            int num = getRandomNumber(min, max);
            while (num == except) {
                num = getRandomNumber(min, max);
            }
            boolean flag = true;
            for (int j = 0; j < count; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }
}




