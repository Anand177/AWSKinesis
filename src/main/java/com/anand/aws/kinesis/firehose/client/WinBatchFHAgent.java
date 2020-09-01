/**
 * 
 */
package com.anand.aws.kinesis.firehose.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResult;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.anand.aws.kinesis.twitter.TwitterReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;

/**
 * @author anand
 *
 */
public class WinBatchFHAgent {

	/**
	 * @param args
	 */
	
	private static final Logger log = LoggerFactory.getLogger(WinBatchFHAgent.class);
	
	int msgCount = 200;
	int bufferSize = 100;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private AmazonKinesisFirehose firehoseClient;
	private PutRecordBatchRequest batchPutReq;
	private PutRecordBatchResult batchPutResult;
	
	public static void main(String[] args) {

		WinBatchFHAgent fhAgent = new WinBatchFHAgent("MFKDS", Region.US_EAST_2.id());
		fhAgent.produceData();
		
	}
	
	public WinBatchFHAgent(String streamName, String region){
		
		log.info("Stream name -> " + streamName);
		log.info("Region used -> " + region);
		
		log.info("Creating AWS Objects");
		firehoseClient = AmazonKinesisFirehoseClient.builder().withRegion(region).build();
		batchPutReq = new PutRecordBatchRequest();
		batchPutReq.setDeliveryStreamName("MFKDS");
		
	}

	
	public void produceData() {

		List<Record> recordList = new ArrayList<Record>();
        List<String> msgList = getTwitterData();
        
        for(int i=0; i<msgCount; i++) {
        	
        	recordList.add(new Record().withData(ByteBuffer
        		.wrap(msgList.get(i).getBytes())));
            
            if(recordList.size() % bufferSize ==0) {
            	
            	log.info("Transmitting " + recordList.size() + " Records");
            	batchPutReq.setRecords(recordList);
	    		batchPutResult = firehoseClient.putRecordBatch(batchPutReq);
	    		
	    		log.info("Records transmitted successfully");
	    		log.info("Result -> " + batchPutResult.toString());
	    		recordList.clear();
	        }
		}
        if(recordList.size() >0) {
        	
        	log.info("Transmitting " + recordList.size() + " Records");
        	batchPutReq.setRecords(recordList);
    		batchPutResult = firehoseClient.putRecordBatch(batchPutReq);
    		
    		log.info("Records transmitted successfully");
    		log.info("Result -> " + batchPutResult.toString());
    		recordList.clear();
        }
	}
	
	
	public List<String> getRandomData() {
		
		Map<String, String> data = new HashMap<String, String>();
        List<String> msgList = new ArrayList<String>();
        
        data.put("Name", System.getProperty("user.name"));
        
        for(int i=0; i<msgCount; i++) {
        	data.put("RandomNumber", Double.toString(Math.random()));
        	data.put("Time", Long.toString(System.currentTimeMillis()));
        	try {
				msgList.add(objectMapper.writeValueAsString(data));
			} catch (JsonProcessingException e) {
				log.error("Exception Ganerating Random Data", e);
			}
        }
        return msgList;
        
	}
	
	
	public List<String> getTwitterData() {
		
        TwitterReader tr = new TwitterReader();
		List<String> msgList = tr.getTimeLineList(msgCount);
		
		while(msgList.size() < msgCount) 
			msgList.addAll(tr.getTimeLineList(msgCount - msgList.size() + 5));
		
		if(msgList.size() > msgCount)
			msgCount = msgList.size();
			
        return msgList;
        
	}


}
