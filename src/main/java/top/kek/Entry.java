package top.kek;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileSystems;

import java.io.File;
import java.net.URISyntaxException;

public class Entry {
	public static void main(String[] args) {
		//Path path = Paths.get("D:\\Java\\jars\\files"); TO DEL
		File dir = new File("D:\\Java\\jars\\files");
		if(dir.isFile()) {//if dir is a directory
			System.out.println("it's a file, not a directory!");
			System.exit(0);
		}
		
		File[] files = dir.listFiles();
		for(File f: files) {
			//System.out.println(f);
			try {
			Thread thread = new Thread(new FileSender(f,"http://localhost:8000/testfile"));
			thread.start();
			thread.join();
			}catch(URISyntaxException e) {
				e.printStackTrace();
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
	}
}
