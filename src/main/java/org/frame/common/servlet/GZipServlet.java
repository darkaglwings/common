package org.frame.common.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GZipServlet extends HttpServlet {  
	  
	private static final long serialVersionUID = -6333961274811512112L;
  
	private byte[] compress(byte[] data) throws Exception {
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
  
        GZIPOutputStream gos = new GZIPOutputStream(baos);
  
        gos.write(data, 0, data.length);
  
        gos.finish();
  
        byte[] output = baos.toByteArray();
  
        baos.flush();
        baos.close();
  
        return output;
    }
	
    @SuppressWarnings("unused")
	private byte[] compress(String string) throws Exception {
    	byte[] bytes = string.getBytes();
    	return this.compress(bytes);
    }
    
    @SuppressWarnings("unused")
	private byte[] compress(String string, String encoding) throws Exception {
    	byte[] bytes = string.getBytes(encoding);
    	return this.compress(bytes);
    }
  
    private void excute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
  
    	String ENCODING = "UTF-8";
        byte[] data = "我是一个中国人！".getBytes(ENCODING);
  
        try {
            byte[] output = compress(data);
  
            // 设置Content-Encoding，这是关键点！  
            response.setHeader("Content-Encoding", "gzip");
            // 设置字符集
            response.setCharacterEncoding(ENCODING);
            // 设定输出流中内容长度
            response.setContentLength(output.length);
  
            OutputStream out = response.getOutputStream();
            out.write(output);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }  
  
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        excute(request, response);
    }
  
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        excute(request, response);
    }
  
}
