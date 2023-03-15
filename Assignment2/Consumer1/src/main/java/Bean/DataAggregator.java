package Bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataAggregator {
  // We only return 100 max of users that one specific user liked
  public static final int POTENTIAL_MATCH_CAP = 100;
  // Stores User and the Set of Users they liked
  private Map<String, Set<String>> likeData;
  // Stores User and the number users they disliked
  private Map<String, Integer> disLikeData;

  public DataAggregator () {
    // Used synchronizedMap since it's updated by multiple threads.
    likeData = Collections.synchronizedMap(new HashMap<String, Set<String>>());
    disLikeData = Collections.synchronizedMap(new HashMap<String, Integer>());

  }

  public void handleSwipeDetails(SwipeDetails swipeDetails) {
    String swiper = swipeDetails.getSwiper();
    String swipee = swipeDetails.getSwipee();
    Boolean like = swipeDetails.getLike();

    // Store into likeData or dislikeData depending on user action
    if (like) {
        likeData.putIfAbsent(swiper, Collections.synchronizedSet(new HashSet<String>()));
        likeData.get(swiper).add(swipee);
    } else {
        disLikeData.put(swiper, disLikeData.getOrDefault(swiper, 0) + 1);
    }
  }

  public int getLikesForSwiper(String swiper) {
    Set<String> likes = likeData.get(swiper);
    if (likes == null) {
      return 0;
    }
    return likes.size();
  }

  public int getDislikesForSwiper(String swiper) {
    Integer dislikes = disLikeData.get(swiper);
    if (dislikes == null) {
      return 0;
    }
    return dislikes;
  }

  public int getNumOfMatchesForSwiper(String swiper) {
    Set<String> likedUsers = likeData.get(swiper);
    int res = 0;
    if (likedUsers == null) {
      return res;
    }

    // for each user the current user liked, we check if they also liked
    // the current user.
    for (String user : likedUsers) {
      Set<String> likesForUser = likeData.get(user);
      if (likesForUser != null && likesForUser.contains(swiper)) {
        res++;
      }
    }
    return res;
  }

  public Set<String> getPotentialMatchesForSwiper(String swiper) {
    Set<String> likedUsers = likeData.get(swiper);
    Set<String> res = new HashSet<>();

    if (likedUsers == null) {
      return res;
    }

    int i = 0;
    // Return first 100 users the user liked as potential matches.
    for (String user : likedUsers) {
      if (i >= likedUsers.size() || i >= POTENTIAL_MATCH_CAP) {
        break;
      }
      res.add(user);
    }
    return res;
  }

  public synchronized int getDataSize() {
    return likeData.size();
  }
}
