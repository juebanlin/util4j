package net.jueb.util4j.beta.tools.file;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class FileMonitorJdkImpl {
	public static void main(String[] args) throws IOException, InterruptedException {
		String dir="C:/Users/juebanlin/git/juebCore/juebCore/target";
		WatchService watchService=FileSystems.getDefault().newWatchService();  
	    Paths.get(dir).register(watchService,   
	            StandardWatchEventKinds.ENTRY_CREATE,  
	            StandardWatchEventKinds.ENTRY_DELETE,  
	            StandardWatchEventKinds.ENTRY_MODIFY);  
	    while(true)  
	    {  
	        WatchKey watchKey=watchService.take();  
            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();  
            for(WatchEvent<?> event : watchEvents){  
                //TODO 根据事件类型采取不同的操作。。。。。。。  
                System.out.println("["+dir+"/"+event.context()+"]文件发生了["+event.kind()+"]事件");    
            }  
            watchKey.reset(); 
	    }  
	}
}