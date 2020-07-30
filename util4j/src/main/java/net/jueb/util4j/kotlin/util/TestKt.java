package net.jueb.util4j.kotlin.util;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class TestKt {
    public static void main(String[] args) {
        List<Runnable> tasks=new ArrayList<>();
        tasks.add(()->{
           for(int i=0;i<3;i++){
               log.info("A"+i);
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
        tasks.add(()->{
            for(int i=0;i<3;i++){
                log.info("B"+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        log.info("start");
        CoroutineUtilKt.execInCalcThread(tasks);
        log.info("end");
        log.info("start");
        CoroutineUtilKt.execInCalcThreadV2(tasks);
        log.info("end");
        log.info("start");
        CoroutineUtilKt.execInCalcThread(tasks);
        log.info("end");
        log.info("start");
        CoroutineUtilKt.execInCalcThreadV2(tasks);
        log.info("end");
        new Scanner(System.in).nextLine();
    }
}
