package BuildClient2;

public enum RequestType {
  GET("Get"),
  POST("Post"),
  PATCH("Patch"),
  DELETE("Delete");

  public final String label;

  private RequestType(String label) {
    this.label = label;
  }
}
