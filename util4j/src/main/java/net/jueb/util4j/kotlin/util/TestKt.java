package net.jueb.util4j.kotlin.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Slf4j
public class TestKt {
    public static void main(String[] args) {
        List<Runnable> tasks=new ArrayList<>();
        for(int x=1;x<=14;x++){
            int player=x;
            tasks.add(()->{
                for(int i=0;i<10;i++){
                    log.info("X"+player+"-apple:"+i);
                    try {
//                        int k1= RandomUtils.nextInt(100)+1000;
//                        for(int k=0;k<k1;k++){
//                            UUID.randomUUID();
//                        }
                        Thread.sleep(RandomUtils.nextInt(10)+5);
                        Thread.yield();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        log.info("start");
        CoroutineUtilKt.execInCalcThread(tasks);
        log.info("end############");
//        log.info("start");
//        CoroutineUtilKt.execInCalcThread(tasks);
//        log.info("end############");
//
//        log.info("start");
//        CoroutineUtilKt.execInCalcThreadV2(tasks.iterator());
//        log.info("end############");
        new Scanner(System.in).nextLine();
    }
}
