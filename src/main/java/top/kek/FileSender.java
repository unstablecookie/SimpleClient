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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class FileSender implements Runnable {
	File file;
	URI connection;
	
	public FileSender(File file,String connection) throws URISyntaxException {
		this.file = file;
		this.connection = new URI(connection);
	}
	
	@Override
	public void run()  {//throws ClientProtocolException,IOException
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(connection);
		System.out.println("file.length():"+file.length());
		//custom header
		httppost.addHeader(new BasicHeader("Real-length",String.valueOf(file.length())));
		httppost.addHeader(new BasicHeader("File-name",file.getName()));
		HttpEntity httpentity = MultipartEntityBuilder.create()
					 .setMode(HttpMultipartMode.RFC6532)
					 .addBinaryBody("my file", file)
					 .build();
		httppost.setEntity(httpentity);
			 
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			System.out.println(response.getStatusLine().getStatusCode());
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
	}
}
