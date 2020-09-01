/**
 * 
 */
package com.anand.aws.kinesis.stream.producer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.anand.aws.kinesis.twitter.TwitterReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;

/**
 * @author anand
 *
 */
public class WinDSProducer {

	/**
	 * @param args
	 */
	
	private static final Logger log = LoggerFactory.getLogger(WinDSProducer.class);
	
	int msgCount = 20;
	String streamName;
	String region;
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private AmazonKinesis kinesisClient;
	private PutRecordsRequest putRecordsRequest;
	private List <PutRecordsRequestEntry> putRecordsRequestEntryList;
	private PutRecordsRequestEntry putRecordsRequestEntry;
	private PutRecordsResult putRecordsResult;
	
	public static void main(String[] args) {
		
		String streamName = "MFKDS";
		Region region = Region.US_EAST_2;
		
		WinDSProducer kinesisProducer = new WinDSProducer(streamName, region.id());
		kinesisProducer.produceData();

	}
	
	
	public WinDSProducer(String streamName, String region){
		
		log.info("Stream name -> " + streamName);
		log.info("Region used -> " + region);
		
		this.streamName = streamName;
		this.region = region;
		
		log.info("Initializing AWS Objects");
		kinesisClient = AmazonKinesisClientBuilder.standard().withRegion(region).build();
		putRecordsRequest  = new PutRecordsRequest();
		putRecordsRequestEntryList  = new ArrayList<PutRecordsRequestEntry>();
		
		putRecordsRequest.setStreamName(streamName);
		
	}
	
	
	public void produceData() {
		
		List<String> msgList = getRandomData();
		
		for(int i =0; i<msgCount; i++) {
			
			putRecordsRequestEntry = new PutRecordsRequestEntry();
			putRecordsRequestEntry.setPartitionKey("PK-" +  i%2);
			try {
				putRecordsRequestEntry.setData(ByteBuffer.wrap(objectMapper
					.writeValueAsString(msgList.get(i)).getBytes()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			putRecordsRequestEntryList.add(putRecordsRequestEntry);
			
		}
		
		log.info(msgCount + " messages generated for transmission. Pushing to Kinesis...");
		
		putRecordsRequest.setRecords(putRecordsRequestEntryList);
		putRecordsResult  = kinesisClient.putRecords(putRecordsRequest);
		
		log.info("Messages pushed to Kinesis successfully");
		log.info("Result -> " + putRecordsResult);

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
