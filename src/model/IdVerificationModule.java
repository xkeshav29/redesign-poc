class IdVerificationModule implements Module {

  String id;
  List<String> instructions;
	List<String> instructionIds;
	
  @Override
  public String getFirstInstructionId() {
    //get first instructin id from above list
  }


	@Override
	public String getNextInstruction(String currentInstructionId) {
		//get from list of instructions
	}
	
	@Override
	public void onComplete(User user) {
		//send id for verification to 3rd party
	}


}