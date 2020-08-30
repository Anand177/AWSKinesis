/**
 * 
 */
package com.anand.aws.kinesis.twitter;

/**
 * @author anand
 *
 */
public class TweetBO {
	
	String userName;
	Long userId;
	String userDescription;
	String userEmail;
	Boolean userverified;
	Long tweetId;
	String tweet;
	String language;
	String source;
	String tweetedAt;
	Long retweetCount;
	Boolean retweeted;
	String country;
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getUserDescription() {
		return userDescription;
	}
	
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public Boolean isUserverified() {
		return userverified;
	}
	
	public void setUserverified(Boolean userverified) {
		this.userverified = userverified;
	}
	
	public Long getTweetId() {
		return tweetId;
	}
	
	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}
	
	public String getTweet() {
		return tweet;
	}
	
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getTweetedAt() {
		return tweetedAt;
	}
	
	public void setTweetedAt(String tweetedAt) {
		this.tweetedAt = tweetedAt;
	}
	
	public Long getRetweetCount() {
		return retweetCount;
	}
	
	public void setRetweetCount(Long retweetCount) {
		this.retweetCount = retweetCount;
	}
	
	public Boolean isRetweeted() {
		return retweeted;
	}
	
	public void setRetweeted(Boolean retweeted) {
		this.retweeted = retweeted;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	

}
