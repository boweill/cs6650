package Bean;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.BillingMode;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import java.util.ArrayList;
import java.util.List;
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

  public static void createTable(AmazonDynamoDB ddb, String tableName, String primaryKey) {
    List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
    attributeDefinitions.add(new AttributeDefinition().withAttributeName(primaryKey).withAttributeType("S"));

    List<KeySchemaElement> keySchema = new ArrayList<>();
    keySchema.add(new KeySchemaElement().withAttributeName(primaryKey).withKeyType(KeyType.HASH));
    CreateTableRequest createTableRequest = new CreateTableRequest()
        .withAttributeDefinitions(attributeDefinitions)
        .withKeySchema(keySchema)
        //.withBillingMode(BillingMode.PAY_PER_REQUEST)
        .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(2000L).withWriteCapacityUnits(2000L))
        .withTableName(tableName);

    try {
      CreateTableResult createTableResult = ddb.createTable(createTableRequest);
      // wait til table is there
      Awaitility.await()
          .atMost(120, TimeUnit.SECONDS)
          .pollInterval(1000, TimeUnit.MILLISECONDS)
          .until(() -> tableExists(ddb, tableName));
      System.out.println(createTableResult.getTableDescription());
    } catch (ResourceInUseException e) {
      System.out.println("WARNING: Table exists");
    } catch (AmazonServiceException e) {
      System.err.println("AmazonServiceException:" + e.getErrorMessage());
      System.exit(1);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Other exceptions: " + e.getMessage());
    }
  }

  private static boolean tableExists(AmazonDynamoDB ddb, String tableName) {
    try {
      ddb.describeTable(tableName);
    } catch (ResourceInUseException e) {
      return true;
    }
    return false;
  }

}
