interface Intent {
  void fulfil(User user);

  boolean isMatch(String message);
}