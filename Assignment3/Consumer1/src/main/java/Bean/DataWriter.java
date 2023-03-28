package Bean;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataWriter {
  private AmazonDynamoDB ddb;
  private DynamoDBMapper mapper;

  public DataWriter () {
    this.ddb = DynamoDBHandler.getDDBClient();
    DynamoDBHandler.createTable(ddb, "User", "userId");
    this.mapper = new DynamoDBMapper(ddb);
  }

  public void handleSwipeDetails(SwipeDetails swipeDetails) {
    long start = System.currentTimeMillis();
    String swiper = swipeDetails.getSwiper();
    String swipee = swipeDetails.getSwipee();
    Boolean like = swipeDetails.getLike();

    User retrievedSwiper = mapper.load(User.class, swiper);
    User retrievedSwipee = mapper.load(User.class, swipee);

    if (retrievedSwiper == null) {
      retrievedSwiper = new User(swiper, null, 0, 0, null);
    }

    if (retrievedSwipee == null) {
      retrievedSwipee = new User(swipee, null, 0, 0, null);
    }

    // If it's a like we update likedUsers of the swiper
    // if the swipee liked the swiper back we update matches for both of them
    if (like) {
      Set<String> likedUsersSwiper = retrievedSwiper.getLikedUsers();
      if (likedUsersSwiper == null) {
        likedUsersSwiper = new HashSet<String>();
      }
      likedUsersSwiper.add(swipee);
      retrievedSwiper.setLikedUsers(likedUsersSwiper);

      Integer newLikes = retrievedSwiper.getLikes() + 1;
      retrievedSwiper.setLikes(newLikes);

      Set<String> likedUsersSwipee = retrievedSwipee.getLikedUsers();

      if (likedUsersSwipee != null) {

        if (likedUsersSwipee.contains(swiper)) {
          Set<String> matchesSwiper = retrievedSwiper.getMatches();
          Set<String> matchesSwipee = retrievedSwipee.getMatches();

          if (matchesSwiper == null) {
            matchesSwiper = new HashSet<>();
          }

          if (matchesSwipee == null) {
            matchesSwipee = new HashSet<>();
          }
          matchesSwiper.add(swipee);
          matchesSwipee.add(swiper);

          retrievedSwiper.setMatches(matchesSwiper);
          retrievedSwipee.setMatches(matchesSwipee);
        }
      }
    }
    else {
      Integer newDislikes = retrievedSwiper.getDislikes() + 1;
      retrievedSwiper.setDislikes(newDislikes);
    }

    mapper.save(retrievedSwiper);
    mapper.save(retrievedSwipee);
    long latency = System.currentTimeMillis() - start;
    System.out.println("latency: " + latency);
  }

  public void batchProcess(Map<String, User> userMap) {
    // Set of users that have been updated in this batch
    Set<String> updatedUsers = userMap.keySet();

    // load the data of these users from the database
    Map<String, List<Object>> returnedItems = mapper.batchLoad(new ArrayList<>(userMap.values()));
    List<Object> returnedUsers = returnedItems.get("User");
    Map<String, User> returnedUsersMap = new HashMap<>();

    for (Object obj : returnedUsers) {
      User user = (User) obj;
      returnedUsersMap.put(user.getUserId(), user);
    }

    // Here basically we merge the data returned from the database
    // with the updated data
    for (Map.Entry<String, User> entry : returnedUsersMap.entrySet()) {
      String id = entry.getKey();
      User user = entry.getValue();
      User updatedUser = userMap.get(id);
      user.setDislikes(user.getDislikes() + updatedUser.getDislikes());
      user.setLikes(user.getLikes() + updatedUser.getLikes());

      Set<String> currLikedUsers = user.getLikedUsers() == null ? new HashSet<String>() : user.getLikedUsers();
      Set<String> currMatches = user.getMatches() == null ? new HashSet<String>() : user.getMatches();
      Set<String> updatedLikedUsers = updatedUser.getLikedUsers();

      if (updatedLikedUsers != null) {
        for (String newLike : updatedUser.getLikedUsers()) {
          currLikedUsers.add(newLike);
          if (returnedUsersMap.containsKey(newLike) && returnedUsersMap.get(newLike).getLikedUsers().contains(id)) {
            currMatches.add(newLike);

            Set<String> reverseMatches = returnedUsersMap.get(newLike).getMatches();

            if (reverseMatches == null) {
              reverseMatches = new HashSet<>();
            }
            reverseMatches.add(id);
            returnedUsersMap.get(newLike).setMatches(reverseMatches);
          }
        }
      }

      user.setLikedUsers(currLikedUsers);
      user.setMatches(currMatches);

      updatedUsers.remove(id);
    }

    for (String newUser : updatedUsers) {
      returnedUsersMap.put(newUser, userMap.get(newUser));
    }

    System.out.println("Batch writing..." + returnedUsersMap.size());
    mapper.batchSave(new ArrayList<User>(returnedUsersMap.values()));
  }
}
