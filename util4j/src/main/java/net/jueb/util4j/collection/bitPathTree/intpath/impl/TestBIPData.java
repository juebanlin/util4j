package net.jueb.util4j.collection.bitPathTree.intpath.impl;

import net.jueb.util4j.collection.bitPathTree.intpath.BitIntPathData;
import net.jueb.util4j.collection.bitPathTree.intpath.BitMaskEnum;

import java.util.HashMap;
import java.util.Map;

public class TestBIPData {

    public static void testW(int count,int num){
        for (int i = 0; i < count; i++) {
            long aw;
            long ar;
            long af;
            long t=System.currentTimeMillis();
            BIPData<String> b=new BIPData<>(BitMaskEnum.MASK_1111_1111_1111_1111);
            if(i==0){
                System.out.println(b.getConfig().toString());
            }
            for(int x=0;x<num;x++)
            {
                b.write(x,"i="+x);
            }
            aw=System.currentTimeMillis()-t;
            t=System.currentTimeMillis();
            for(int x=0;x<num;x++)
            {
                b.read(x);
            }
            ar=System.currentTimeMillis()-t;
            t=System.currentTimeMillis();
            for (String s : b) {
                
            }
            af=System.currentTimeMillis()-t;

            Map<Integer,String> map=new HashMap<>();
            t=System.currentTimeMillis();
            for(int x=0;x<num;x++)
            {
                map.put(x,"i="+x);
            }
            System.out.println(aw+":"+ar+":"+af);
        }
        System.out.println("#########");
        for (int i = 0; i < count; i++) {
            long bw;
            long br;
            long bf;
            Map<Integer,String> map=new HashMap<>();
            long t=System.currentTimeMillis();
            for(int x=0;x<num;x++)
            {
                map.put(x,"i="+x);
            }
            bw=System.currentTimeMillis()-t;
            t=System.currentTimeMillis();
            for(int x=0;x<num;x++)
            {
                map.get(x);
            }
            br=System.currentTimeMillis()-t;

            t=System.currentTimeMillis();
            for (String value : map.values()) {

            }
            bf=System.currentTimeMillis()-t;
            System.out.println(bw+":"+br+":"+bf);
        }
    }

    public static void main(String[] args) {
        testW(10,1000000);
//		b.forEach((v)->{
//			System.out.println(v);
//		});
    }
}
