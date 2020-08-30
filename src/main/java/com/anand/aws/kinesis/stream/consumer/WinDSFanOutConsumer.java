package com.anand.aws.kinesis.stream.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.InitialPositionInStream;
import software.amazon.kinesis.common.InitialPositionInStreamExtended;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.fanout.FanOutConfig;
import software.amazon.kinesis.retrieval.polling.PollingConfig;


//arn:aws:kinesis:us-east-2:347540785859:stream/MFKDS
public class WinDSFanOutConsumer {

	private static final Logger log = LoggerFactory.getLogger(WinDSFanOutConsumer.class);
	
	
	public static void main(String[] args) {
		
		String streamName = "MFKDS";
		String id = UUID.randomUUID().toString();
		String appName = "AnandsApp1598791961096";//"AnandsApp" + System.currentTimeMillis();
		int maxListShardsRetryAttempts = 3;
		Region region = Region.US_EAST_2;
	    
		KinesisAsyncClient kinesisClient = KinesisClientUtil
	    	.createKinesisAsyncClient(KinesisAsyncClient.builder().region(region));
		DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();
		CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder()
			.region(region).build();
		ConfigsBuilder configsBuilder = new ConfigsBuilder(streamName, streamName, kinesisClient,
			dynamoClient, cloudWatchClient, id, new SampleRecordProcessorFactory());
		FanOutConfig fanOutConfig = new FanOutConfig(kinesisClient).streamName(streamName)
	    			.applicationName(appName);
		
	    System.out.println("Id -> " + id );
	    System.out.println("App Name -> " + appName);
	    
		Scheduler scheduler = new Scheduler(configsBuilder.checkpointConfig(),
            configsBuilder.coordinatorConfig(), configsBuilder.leaseManagementConfig(),
            configsBuilder.lifecycleConfig(), configsBuilder.metricsConfig(),
            configsBuilder.processorConfig(), configsBuilder.retrievalConfig()
            	.retrievalSpecificConfig(fanOutConfig)
                .maxListShardsRetryAttempts(maxListShardsRetryAttempts)
                .initialPositionInStreamExtended(InitialPositionInStreamExtended
                	.newInitialPosition(InitialPositionInStream.LATEST)));
        
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true);
        schedulerThread.start();

        System.out.println("Press enter to shutdown");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            reader.readLine();
        } catch (IOException e) {
            log.error("Caught exception while waiting for confirm. Shutting down.", e);
        }
        
        
        
        Future<Boolean> gracefulShutdownFuture = scheduler.startGracefulShutdown();
        log.info("Waiting up to 20 seconds for shutdown to complete.");
        try {
            gracefulShutdownFuture.get(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("Interrupted while waiting for graceful shutdown. Continuing.");
        } catch (ExecutionException e) {
            log.error("Exception while executing graceful shutdown.", e);
        } catch (TimeoutException e) {
            log.error("Timeout while waiting for shutdown.  Scheduler may not have exited.");
        }
        log.info("Completed, shutting down now.");
    
	}

}
