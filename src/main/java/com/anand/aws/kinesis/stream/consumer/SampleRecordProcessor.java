/**
 * 
 */
package com.anand.aws.kinesis.stream.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

/**
 * @author anand
 *
 */
public class SampleRecordProcessor implements ShardRecordProcessor {
	
	private static final String SHARD_ID_MDC_KEY = "ShardId";
    private static final Logger log = LoggerFactory.getLogger(SampleRecordProcessor.class);
    private String shardId;

	@Override
	public void initialize(InitializationInput initializationInput) {
		
		shardId = initializationInput.shardId();
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
            log.info("Initializing @ Sequence: " + 
            	initializationInput.extendedSequenceNumber());
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
		
	}

	@Override
	public void processRecords(ProcessRecordsInput processRecordsInput) {

		MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Processing {} record(s)", processRecordsInput.records().size());
            
            for(KinesisClientRecord clientRecord : processRecordsInput.records()) {
            	
            	final byte[] bytes = new byte[clientRecord.data().remaining()];
            	clientRecord.data().duplicate().get(bytes);
            	String s = new String(bytes);
            	
            	log.info("Partition Key: " + clientRecord.partitionKey());
            	log.info("Sequence Number:  " + clientRecord.sequenceNumber());
            	log.info("Data: " + s);
            }
            
        } catch (Throwable t) {
        	t.printStackTrace();
            log.error("Caught throwable while processing records. Aborting.");
            Runtime.getRuntime().halt(1);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
		
	}

	@Override
	public void leaseLost(LeaseLostInput leaseLostInput) {

		MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Lost lease, so terminating.");
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
		
	}

	@Override
	public void shardEnded(ShardEndedInput shardEndedInput) {

		MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Reached shard end checkpointing.");
            shardEndedInput.checkpointer().checkpoint();
        } catch (InvalidStateException  e) {
            log.error("Exception while checkpointing at shard end. Giving up.", e);
        } catch(ShutdownException e){
        	log.error("Exception while checkpointing at shard end. Giving up.", e);
        }finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
		
	}

	@Override
	public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
		
		MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Scheduler is shutting down, checkpointing.");
            shutdownRequestedInput.checkpointer().checkpoint();
        } catch (ShutdownException e) {
            log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
        } catch (InvalidStateException e) {
        	log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
        }finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
		
	}

}