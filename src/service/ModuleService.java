@Service
class ModuleService {

	private Map<String, ModuleHandler> moduleHandlers;

  List<String> modules;

  @Inject
  ModuleService(AppConfig appConfig) {
    //read modules from config and store in modules Map
  }

  public Module getModule(String id) {
    return modules.get(id);
  }

	public String nextModuleId(String currentModuleId) {
    //get next module form this.modules
	}
}