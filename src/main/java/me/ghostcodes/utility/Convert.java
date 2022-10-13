package me.ghostcodes.utility;

import java.util.List;

public class Convert {
    public static int[] listToArr(List<Integer> list){
        int[] arr = new int[list.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = list.get(i);
        }
        return arr;
    }
}
