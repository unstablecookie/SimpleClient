package top.kek;

import top.kek.FileSender;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.nio.file.FileSystems;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.ArrayDeque;
import java.io.BufferedWriter;
import java.io.BufferedReader;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

public class Entry {
	
	static Set<String> set = new HashSet<>();
	
	static AtomicReference<ArrayDeque> queue = new AtomicReference<>(new ArrayDeque());
	
	static String hostName = "localhost";
	
	static File dir = new File("D:\\Java\\jars\\files");
	
	static int timer = 120000;
	
	static LocalTime startTime;
	
	public static void main(String[] args) {
		startTime= LocalTime.now();
		//replacing hostName and dir with arguments
		if(args.length>0) {
			hostName = args[0];
			dir = new File(args[1]);
		}

		if(dir.isFile()) {//if dir is a directory
			System.out.println("it's a file, not a directory!");
			System.exit(0);
		}
		
		File[] files = dir.listFiles();//creating working set with all files
		for(File f: files) {
			set.add(f.toString());
		}
		
		List<String> setList = new ArrayList<>(set);
		List<String> fileTableList = new ArrayList<>();
		
		try {
			fileTableList  = loadFileTable();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		setList.removeAll(fileTableList);
		updateWorkingQueue(setList);//updating working queue
		
		PostWorker.pushFileUpload();//push data with 4 threads
	}
	static void updateWorkingQueue(List<String> list) {
		synchronized(queue) {
			ArrayDeque<String> ardq = queue.get();
			for(String s: list) {
				ardq.addFirst(s);
			}
		}
	}
	
	static List<String> loadFileTable() throws IOException {
		File fileTable = new File("FileTable");
		if(!fileTable.exists()) {
			fileTable.createNewFile();
		}
		BufferedReader in = new BufferedReader(new FileReader(fileTable));
		
		List<String> list = in.lines().collect(Collectors.toList());
		return list;
	}
	
	static void updateFileTable() throws IOException{
		File fileTable = new File("FileTable");
		if(!fileTable.exists()) {
			fileTable.createNewFile();
		}
			
		synchronized(set) {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileTable));
			Iterator it = set.iterator();
			while(it.hasNext()) {
				out.write(String.valueOf(it.next()));
				out.newLine();
			}
			out.close();
		}
	}
	
	static class PostWorker{
		static ExecutorService executor = Executors.newFixedThreadPool(4);
		static AtomicInteger integer = new AtomicInteger(8001);
		
		static void pushFileUpload() {
			synchronized(queue) {
				ArrayDeque<String> workingQueue = queue.get();
				while(!workingQueue.isEmpty()) {
					String  file = workingQueue.pollFirst();
					int portNumber;
					synchronized(integer) {
						portNumber = integer.getAndIncrement();
						if(integer.get()>=8005) integer.set(8001);
					}
					StringBuilder builder = new StringBuilder("http://")
							.append(hostName)
							.append(":")
							.append(portNumber)
							.append("/testfile");
					try {
						executor.execute(new top.kek.FileSender(file,builder.toString()));
					}catch(URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
			executor.shutdown();
			
			try {
				if(!executor.awaitTermination(timer, TimeUnit.MILLISECONDS)) {
					executor.shutdownNow();
					try {//write copied files to file
						updateFileTable();
					}catch(IOException e) {
						e.printStackTrace();
					}
					LocalTime endTime = LocalTime.now();
					System.out.println(((double)(endTime.toNanoOfDay() - Entry.startTime.toNanoOfDay()))/(double)1000000000);//
					System.exit(0);
				}
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
}
