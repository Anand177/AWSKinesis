/**
 * 
 */
package com.anand.aws.kinesis.firehose.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anand
 *
 */
public class UnixFHAgent {

	/**
	 * @param args
	 */
	
	static int msgCount = 102;
	static int batchSize=40;
	
	public static void main(String[] args) {

		Map<String, String> data = new HashMap<String, String>();
        data.put("Name", System.getProperty("user.name"));
        
        ObjectMapper objectMapper = new ObjectMapper();
        BufferedWriter bw;
        
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        
        for(int i=0; i<msgCount; i++) {
        	
        	data.put("RandomNumber", Double.toString(Math.random()));
            data.put("Time", Long.toString(System.currentTimeMillis()));
            
            try {
            	sb.append(objectMapper.writeValueAsString(data) + "\n");
            } catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}
            
            if(i%batchSize ==0) {
            	String fileName = args[0] + File.separator + 
            		Integer.toString(Math.abs(random.nextInt())) + ".log";
            	
            	try{
            		bw = new BufferedWriter(new FileWriter(new File(fileName)));
            		bw.write(sb.toString());
            		bw.flush();
            		sb = new StringBuffer();
            		
            		
            	} catch(IOException e) {
            		e.printStackTrace();
            	}
            }
        }

	}

}
