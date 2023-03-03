package Bean;

import java.util.Objects;

public class SwipeDetails {
  private String swiper;
  private String swipee;
  private String message;
  private Boolean like;

  public String getSwiper() {
    return swiper;
  }

  public void setSwiper(String swiper) {
    this.swiper = swiper;
  }

  public String getSwipee() {
    return swipee;
  }

  public void setSwipee(String swipee) {
    this.swipee = swipee;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Boolean getLike() {
    return like;
  }

  public void setLike(Boolean like) {
    this.like = like;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SwipeDetails that = (SwipeDetails) o;
    return swiper.equals(that.swiper) && swipee.equals(that.swipee) && Objects.equals(
        message, that.message) && like.equals(that.like);
  }

  @Override
  public int hashCode() {
    return Objects.hash(swiper, swipee, message, like);
  }
}
