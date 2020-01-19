class LanguageChangeIntent implements Intent {
  
  String name;

  @Override
  public void fulfil(User user) {
    //change user language in db
  }

  @Override
  public boolean isMatch(String message) {
    //do regex match if message is for lang change 
    //or ask artha
  }

}