/**
 * 
 */
package com.anand.aws.kinesis.stream.producer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.anand.aws.kinesis.twitter.TwitterReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author anand
 *
 */
public class WinDSProducer {

	/**
	 * @param args
	 */
	
	static int msgCount = 20;
	
	public static void main(String[] args) {

		AmazonKinesis kinesisClient = AmazonKinesisClientBuilder.standard()
			.withRegion(Regions.US_EAST_2).build();
		
		PutRecordsRequest putRecordsRequest  = new PutRecordsRequest();
		putRecordsRequest.setStreamName("MFKDS");
		
		ObjectMapper objectMapper = new ObjectMapper();
		List <PutRecordsRequestEntry> putRecordsRequestEntryList  = 
			new ArrayList<PutRecordsRequestEntry>(); 
		PutRecordsRequestEntry putRecordsRequestEntry;
		List<String> msgList = getTwitterData();
		
		for(int i =0; i<msgCount; i++) {
			
			putRecordsRequestEntry = new PutRecordsRequestEntry();
			putRecordsRequestEntry.setPartitionKey("PK-" +  i);
			try {
				putRecordsRequestEntry.setData(ByteBuffer.wrap(objectMapper
					.writeValueAsString(msgList.get(i)).getBytes()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			putRecordsRequestEntryList.add(putRecordsRequestEntry);
			
		}
		putRecordsRequest.setRecords(putRecordsRequestEntryList);
		PutRecordsResult putRecordsResult  = kinesisClient.putRecords(putRecordsRequest);
		
		System.out.println(putRecordsResult);

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
