/**
 * 
 */
package com.anand.aws.kinesis.firehose.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResult;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.anand.aws.kinesis.twitter.TwitterReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anand
 *
 */
public class WinBatchFHAgent {

	/**
	 * @param args
	 */
	
	static int msgCount = 100;
	static int bufferSize = 50;
	
	public static void main(String[] args) {

		//Create Firehose Client
		AmazonKinesisFirehose firehoseClient = AmazonKinesisFirehoseClient.builder()
		        .withRegion(Regions.US_EAST_2).build();

		//Configure Put Record
		PutRecordBatchRequest batchPutReq = new PutRecordBatchRequest();
		batchPutReq.setDeliveryStreamName("MFKDS");
		
		PutRecordBatchResult batchPutResult;
        List<Record> recordList = new ArrayList<Record>();
        List<String> msgList = getTwitterData();
        
        for(int i=0; i<msgCount; i++) {
        	
        	recordList.add(new Record().withData(ByteBuffer
        		.wrap(msgList.get(i).getBytes())));
            
            if(recordList.size() % bufferSize ==0) {
            	batchPutReq.setRecords(recordList);
	    		batchPutResult = firehoseClient.putRecordBatch(batchPutReq);
	    		
	    		System.out.println(batchPutResult.toString());
	    		recordList.clear();
	        }
		}
        if(recordList.size() >0) {
        	batchPutReq.setRecords(recordList);
    		batchPutResult = firehoseClient.putRecordBatch(batchPutReq);
    		
    		System.out.println(batchPutResult.toString());
    		recordList.clear();
        }

	}
	
	
	public static List<String> getRandomData() {
		
		ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = new HashMap<String, String>();
        List<String> msgList = new ArrayList<String>();
        
        data.put("Name", System.getProperty("user.name"));
        
        for(int i=0; i<msgCount; i++) {
        	data.put("RandomNumber", Double.toString(Math.random()));
        	data.put("Time", Long.toString(System.currentTimeMillis()));
        	try {
				msgList.add(objectMapper.writeValueAsString(data));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
        }
        return msgList;
        
	}
	
	
	public static List<String> getTwitterData() {
		
        TwitterReader tr = new TwitterReader();
		List<String> msgList = tr.getTimeLineList(msgCount);
		
		while(msgList.size() < msgCount) 
			msgList.addAll(tr.getTimeLineList(msgCount - msgList.size() + 5));
		
		if(msgList.size() > msgCount)
			msgCount = msgList.size();
			
        return msgList;
        
	}

}
