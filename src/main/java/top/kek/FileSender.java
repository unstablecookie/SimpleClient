package top.kek;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.File;

import org.apache.http.client.ClientProtocolException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.http.message.BasicHeader;

public class FileSender implements Runnable {
	String fileString;
	URI connection;
	
	public FileSender(String fileString,String connection) throws URISyntaxException {
		this.fileString = fileString;
		this.connection = new URI(connection);
	}
	
	@Override
	public void run()  {//throws ClientProtocolException,IOException
		File file = new File(fileString);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(connection);
		//custom header
		httppost.addHeader(new BasicHeader("Real-length",String.valueOf(file.length())));//my value to process binary data later
		httppost.addHeader(new BasicHeader("File-name",file.getName()));
		HttpEntity httpentity = MultipartEntityBuilder.create()
					 .setMode(HttpMultipartMode.RFC6532)
					 .addBinaryBody("my file", file)
					 .build();
		httppost.setEntity(httpentity);
		int responceCode = 0;
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			responceCode = response.getStatusLine().getStatusCode();
			System.out.println("responceCode:"+responceCode);
			HttpEntity responceEntity = response.getEntity();
			response.close();
			if (responceEntity != null) {
				System.out.println("Response content length: " + responceEntity.getContentLength());
	         }
				 EntityUtils.consume(responceEntity);
			 }catch(ClientProtocolException e) {
				 e.printStackTrace();
			 }catch(IOException e) {
				 e.printStackTrace();
			 }
			 try {
				 httpclient.close();
			 }catch(IOException e) {
				 e.printStackTrace();
			 }
			 if(responceCode!=200) {
				 try {
					 failedJobs(fileString);
				 }catch(IOException e) {
					 e.printStackTrace();
				 }
			 }
	}
	
	public void failedJobs(String fileString) throws IOException {
		File failedFile = new File("failedFile");
		
		Files.writeString(Paths.get(failedFile.toURI()),
				fileString+ "\n",
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND);
		synchronized(Entry.set) {
			if(Entry.set.contains(fileString)) Entry.set.remove(fileString);
		}
		
	}
}
