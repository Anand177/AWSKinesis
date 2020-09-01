create database anand_athena;

CREATE EXTERNAL TABLE tweets(
userName VARCHAR(100),
userId BIGINT,
userDescription VARCHAR(1000),
userEmail VARCHAR(100),
userverified BOOLEAN,
tweetId BIGINT,
tweet VARCHAR(500),
language VARCHAR(100),
source VARCHAR(200),
tweetedAt VARCHAR(100),
retweetCount INT,
retweeted BOOLEAN,
country VARCHAR(200)
) PARTITIONED BY (year INT, month INT, day INT, hour INT)
ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
STORED AS TEXTFILE
LOCATION 's3://anandsbkt/Twitter';

msck repair table tweets;