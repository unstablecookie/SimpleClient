package top.kek;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.File;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import java.io.ByteArrayInputStream;


public class Sender {
	 public static void main(String[] args) throws URISyntaxException,ClientProtocolException,IOException {
		 
		 //TEST GET
		 //URI address = new URI("http", null, "localhost", 8000, "/test", null, null);
		 //HttpGet httpget = new HttpGet("http://localhost");
		 URI addressGet = new URI("http://localhost:8000/testget");
		 try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 HttpGet httpget = new HttpGet(addressGet);
			 CloseableHttpResponse  response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine().getStatusCode());
			 response.close();
		 }catch(Exception e) {
			 e.printStackTrace();
		 }finally {
			 
		 }
		 
		 //TEST POST string
		 URI addressPost = new URI("http://localhost:8000/testpost");
		 String testString = "my test string";
		 HttpPost httppost = new HttpPost(addressPost);
		 HttpEntity entity = new InputStreamEntity(new ByteArrayInputStream("my input string".getBytes(StandardCharsets.UTF_8)));
		 httppost.setEntity(entity);
		 try {
			 CloseableHttpClient httpclient = HttpClients.createDefault();
			 CloseableHttpResponse response = (CloseableHttpResponse) httpclient.execute(httppost);
			 System.out.println(response.getStatusLine().getStatusCode());
			 response.close();
		 }catch(Exception e) {
			 e.printStackTrace();
		 }finally {
			 
		 }
		 
		 //TEST POST file
		 URI addressFile = new URI("http://localhost:8000/testfile");
		 File file = new File("/168.jpg");
		 
		 
	 }	
}
