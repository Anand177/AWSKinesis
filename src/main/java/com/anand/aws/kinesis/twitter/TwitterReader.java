/**
 * 
 */
package com.anand.aws.kinesis.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author anand
 *
 */
public class TwitterReader {

	/**
	 * @param args
	 */
	
	private ConfigurationBuilder confBuild;
	private Twitter twitter;
	
	public static void main(String[] args) throws TwitterException {

		TwitterReader tr = new TwitterReader();
		
		List<String> tweetBoList = tr.getTimeLineList(50);
		System.out.println(tweetBoList.size());
		
		System.out.println(tweetBoList.get(0) + tweetBoList.get(10));
		
	}
	
	public TwitterReader() {
		
		Properties twitConnProp = new Properties();
		Map<String, String> twitterCredentials = new HashMap<String, String>();
		
		try {
			twitConnProp.load(
				new FileInputStream("C:\\Users\\anand\\Documents\\AWS\\AnandTwitter.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		twitterCredentials.put("consumerKey", 
			twitConnProp.getProperty("twitter4j.oauth.consumerKey"));
		twitterCredentials.put("consumerSecret", 
			twitConnProp.getProperty("twitter4j.oauth.consumerSecret"));
		twitterCredentials.put("accessToken", 
			twitConnProp.getProperty("twitter4j.oauth.accessToken"));
		twitterCredentials.put("accessTokenSecret", 
			twitConnProp.getProperty("twitter4j.oauth.accessTokenSecret"));
		
		confBuild = new ConfigurationBuilder();
		confBuild.setDebugEnabled(true)
			.setOAuthConsumerKey(twitterCredentials.get("consumerKey"))
			.setOAuthConsumerSecret(twitterCredentials.get("consumerSecret"))
			.setOAuthAccessToken(twitterCredentials.get("accessToken"))
			.setOAuthAccessTokenSecret(twitterCredentials.get("accessTokenSecret"));

		twitter = (new TwitterFactory(confBuild.build())).getInstance();
		
	}
	
	public String postTweet(String tweet) {
		
		try {
			Status status = twitter.updateStatus(tweet);
			return status.getText();
		} catch (TwitterException e) {
			e.printStackTrace();
			
			return "Exception";
		}
		
	}
	
	public ResponseList<Status> getTimeLine() throws TwitterException {
		return twitter.getHomeTimeline(new Paging(0, 100));
	}
	
	public List<String> getTimeLineList(int msgCount) {
		
		ObjectMapper mapper = new ObjectMapper();
		TweetBO tweetBo;
		User twitterUser;
		
		List<String> tweetList = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzzz");
		
		ResponseList<Status> statusList;
		try {
			statusList = twitter.getHomeTimeline(new Paging(1, msgCount));
		} catch (TwitterException e) {
			e.printStackTrace();
			return null;
		}
		
		for(Status status : statusList) {
			
			tweetBo = new TweetBO();
			twitterUser = status.getUser();
			
			tweetBo.setUserName(twitterUser.getName());
			tweetBo.setUserId(new Long(twitterUser.getId()));
			tweetBo.setUserDescription(twitterUser.getDescription());
			tweetBo.setUserEmail(twitterUser.getEmail());
			tweetBo.setUserverified(new Boolean(twitterUser.isVerified()));
			tweetBo.setTweetId(new Long(status.getId()));
			tweetBo.setTweet(status.getText());
			tweetBo.setLanguage(status.getLang());
			tweetBo.setSource(status.getSource());
			tweetBo.setTweetedAt(sdf.format(status.getCreatedAt()));
			tweetBo.setRetweetCount(new Long(status.getRetweetCount()));
			tweetBo.setRetweeted(new Boolean(status.isRetweet()));
			tweetBo.setCountry(
				( null == status.getPlace()) ? "": status.getPlace().getCountry());

			try {
				tweetList.add(mapper.writeValueAsString(tweetBo) + "\n");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			
		}
		return tweetList;
	}
	

}
