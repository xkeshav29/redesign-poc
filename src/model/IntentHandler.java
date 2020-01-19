interface IntentHandler {
  void fulfil(User user);

  boolean isMatch(String message);
}