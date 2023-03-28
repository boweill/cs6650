package Bean;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;

public class DynamoDBHandler {

  public static AmazonDynamoDB getDDBClient() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    String accessKeyId = processBuilder.environment().get("aws_access_key_id");
    String secretAccessKey = processBuilder.environment().get("aws_secret_access_key");
    String sessionToken = processBuilder.environment().get("aws_session_token");

    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(accessKeyId, secretAccessKey, sessionToken);

    AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.US_WEST_2)
        .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
        .build();

    return ddb;
  }

}
