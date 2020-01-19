class IdVerificationModuleHandler implements ModuleHandler {

	List<Instruction> instructions;

	@Override
	public String getFirstInstructionId() {
		// get first instructin id from above list
	}

	@Override
	public String getNextInstruction(String currentInstructionId) {
		// get from list of instructions
	}

	@Override
	public void onComplete(User user) {
		// send id for verification to 3rd party
	}

}