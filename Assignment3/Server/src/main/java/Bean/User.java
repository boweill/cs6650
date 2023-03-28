package Bean;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.util.Set;

@DynamoDBTable(tableName = "User")
public class User {

  private String userId;
  private Set<String> likedUsers;
  private Integer likes;
  private Integer dislikes;
  private Set<String> matches;

  public User() {

  }

  public User(String userId, Set<String> likedUsers, Integer likes, Integer dislikes,
      Set<String> matches) {
    this.userId = userId;
    this.likedUsers = likedUsers;
    this.likes = likes;
    this.dislikes = dislikes;
    this.matches = matches;
  }

  @DynamoDBHashKey(attributeName = "userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @DynamoDBAttribute(attributeName = "likedUsers")
  public Set<String> getLikedUsers() {
    return likedUsers;
  }

  public void setLikedUsers(Set<String> likedUsers) {
    this.likedUsers = likedUsers;
  }

  @DynamoDBAttribute(attributeName = "likes")
  public Integer getLikes() {
    return likes;
  }

  public void setLikes(Integer likes) {
    this.likes = likes;
  }

  @DynamoDBAttribute(attributeName = "dislikes")
  public Integer getDislikes() {
    return dislikes;
  }

  public void setDislikes(Integer dislikes) {
    this.dislikes = dislikes;
  }

  @DynamoDBAttribute(attributeName = "matches")
  public Set<String> getMatches() {
    return matches;
  }

  public void setMatches(Set<String> matches) {
    this.matches = matches;
  }
}
