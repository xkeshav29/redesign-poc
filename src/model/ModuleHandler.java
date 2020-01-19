interface ModuleHandler {
	String getFirstInstructionId();

	String getNextInstructionId(String currentInstructionId);

	void onComplete(User user);
}