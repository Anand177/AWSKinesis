/**
 * 
 */
package com.anand.aws.kinesis.client;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordResult;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anand
 *
 */
public class WinKinesisSimpleClient {

	/**
	 * @param args
	 */
	
	static int msgCount=100;
	
	public static void main(String[] args) {
		
		//Create Firehose Client
		AmazonKinesisFirehose firehoseClient = AmazonKinesisFirehoseClient.builder()
		        .withRegion(Regions.US_EAST_2).build();

		//Configure Put Record
		PutRecordRequest putRecordRequest = new PutRecordRequest();
		putRecordRequest.setDeliveryStreamName("MFKDS");
		
		Map<String, String> data = new HashMap<String, String>();
        data.put("Name", System.getProperty("user.name"));
        
        
        ObjectMapper objectMapper = new ObjectMapper();
        Record record;
        PutRecordResult putRecordResult;
        
        for(int i=0; i<msgCount; i++) {
        	
        	data.put("RandomNumber", Double.toString(Math.random()));
            data.put("Time", Long.toString(System.currentTimeMillis()));
            
            try {
				record = new Record().withData(ByteBuffer
					.wrap(objectMapper.writeValueAsString(data).getBytes()));
				
				putRecordRequest.setRecord(record);
	    		putRecordResult = firehoseClient.putRecord(putRecordRequest);
	    		
	    		System.out.println("RecordId -> " + putRecordResult.getRecordId() + 
	    			" successfully added to Kinesis Firehose");
	    		
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}
            
            

        }
		
		 
		
		
	}

}
