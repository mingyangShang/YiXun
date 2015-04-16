package com.yixun.manager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yixun.constants.Constants;
import com.yixun.myview.SocketService;

public class HttpTool {
	 /** 
     * 传送文本,例如Json,xml等 
     */ 
    	 private static DataOutputStream dos= null;
    public static String sendTxt(String urlPath, String txt, String encoding)  
            throws Exception {  
        byte[] sendData = txt.getBytes(Constants.UTF_8);  
        URL url = new URL(urlPath);  
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setRequestMethod("POST");  
        conn.setConnectTimeout(Constants.TIME_OUT);  
        // 如果通过post提交数据，必须设置允许对外输出数据  
        conn.setDoOutput(true);  
        conn.setDoInput(true);  
        conn.setRequestProperty("Content-Type", "text/xml");  
        conn.setRequestProperty("Charset", encoding);  
        conn.setRequestProperty("Content-Length", String  
                .valueOf(sendData.length));  
        OutputStream outStream = conn.getOutputStream();  
        outStream.write(sendData);  
        outStream.flush();  
        outStream.close(); 
        Log.i("debug", conn.getResponseCode()+"");
        if (conn.getResponseCode() == 200) {  
            // 获得服务器响应的数据  
            BufferedReader in = new BufferedReader(new InputStreamReader(conn  
                    .getInputStream(), encoding));  
            // 数据  
            String retData = null;  
            StringBuffer responseData = new StringBuffer();  
            while ((retData = in.readLine()) != null) {  
                responseData.append(retData);  
            }  
            in.close();  
            return responseData.toString();  
        }  
        return "sendText error!";  
    }  
    public static String sendJson(String url,String json,String encoding){
    	HttpClient client = new DefaultHttpClient();
    	System.out.println("json:"+json);
    	HttpPost request = new HttpPost(url);
    	List<NameValuePair> post = new ArrayList<NameValuePair>();
    	try {
			post.add(new BasicNameValuePair(Constants.JSON, json));//new String(json.getBytes(Constants.UTF_8),Constants.UTF_8)));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return "sendText error!";
		}
    	UrlEncodedFormEntity form=null;
    	HttpResponse response = null;
    	System.out.println("post完毕");
		try {
			form = new UrlEncodedFormEntity(post,Constants.UTF_8);
			request.setEntity(form);
	    	response = client.execute(request);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "sendText error!";
		} catch(Exception e ){
			e.printStackTrace();
			return "sendText error!";
		}
		 try {
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), encoding));
			   String retData = null;  
	            StringBuffer responseData = new StringBuffer();  
	            while ((retData = in.readLine()) != null) {  
	                responseData.append(retData);  
	            }  
	            in.close(); 
	            return responseData.toString();  
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "sendText error!";
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "sendText error!";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "sendText error!";
		}  	
    }
    //获得一个图片的输入流,path是图片的路径
    public static Bitmap getImageStream(String path) throws Exception{
        URL url = new URL(path);
        Bitmap bm=null;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
        	bm=BitmapFactory.decodeStream(conn.getInputStream());
        	return bm;
        }
        return null;
    }
    //利用socket发送消息
    public static Boolean SendMsg(Socket socket,String json){
//    	 DataOutputStream dos;// = new DataOutputStream(socket.getOutputStream());
    	if(dos==null){
    		  try {
				dos=new DataOutputStream(SocketService.socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();//用来包装socket的输出流
			}
    	}
    	System.out.println("发送消息");
    	 synchronized (dos) {
			 try {
			dos.write(json.getBytes(Constants.UTF_8));
			dos.flush();
			System.out.println("发送了消息");
			 return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		}
    	
    }
    /**
     * 上传文件到服务器
     * 
     * @param file
     *            需要上传的文件
     * @param RequestURL
     *            请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file,String RequestURL) {   
    	    final int TIME_OUT = 10*10000000; //超时时间   
    	    final String SUCCESS="1";
    	    final String FAILURE="0"; 
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 
        String PREFIX = "--" , LINE_END = "\r\n";   
        String CONTENT_TYPE = "multipart/form-data"; //内容类型   
        try {  
            URL url = new URL(RequestURL);   
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
            conn.setReadTimeout(TIME_OUT); conn.setConnectTimeout(TIME_OUT); 
            conn.setDoInput(true); //允许输入流  
            conn.setDoOutput(true); //允许输出流  
            conn.setUseCaches(false); //不允许使用缓存   
            conn.setRequestMethod("POST"); //请求方式   
            conn.setRequestProperty("Charset", Constants.UTF_8);   
            //设置编码   
            conn.setRequestProperty("connection", "keep-alive");   
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);  
            if(file!=null) {   
                /** * 当文件不为空，把文件包装并且上传 */  
                OutputStream outputSteam=conn.getOutputStream();   
                DataOutputStream dos = new DataOutputStream(outputSteam);   
                StringBuffer sb = new StringBuffer();   
                sb.append(PREFIX);   
                sb.append(BOUNDARY); sb.append(LINE_END);   
                /**  
                * 这里重点注意：  
                * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件  
                * filename是文件的名字，包含后缀名的 比如:abc.png  
                */   
                sb.append("Content-Disposition:form-data; name=\"icon\"; filename=\""+file.getName()+"\""+LINE_END);  
              sb.append("Content-Type:image/png; charset="+ Constants.UTF_8+LINE_END);   

                sb.append(LINE_END);   
                dos.write(sb.toString().getBytes());   
                InputStream is = new FileInputStream(file);  
                byte[] bytes = new byte[1024];   
                int len = 0;   
                while((len=is.read(bytes))!=-1)   
                {   
                   dos.write(bytes, 0, len);   
                }   
                is.close();   
                dos.write(LINE_END.getBytes());   
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();   
                dos.write(end_data);   
                dos.flush();  
                /**  
                * 获取响应码 200=成功  
                * 当响应成功，获取响应的流  
                */   
                int res = conn.getResponseCode();   
                if (res == 200) {
                        InputStream input = conn.getInputStream();
                        StringBuffer sb1 = new StringBuffer();
                        int ss;
                        while ((ss = input.read()) != -1) {
                                sb1.append((char) ss);
                        }
                       String  result = sb1.toString();
                        System.out.println("result:"+result);
                        return SUCCESS;   
                } else {
                }                
            }   
        } catch (MalformedURLException e)   
        { e.printStackTrace(); }   
        catch (IOException e)   
        { e.printStackTrace(); }   
        return FAILURE;   
    }   

    
}
