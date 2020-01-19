@Service
class IntentService {

  private ArthaService arthaService;

  Map<String, Intent> intents;

  @Inject
  IntentService intentService(ArthaService arthaService, AppCondig appConfig) {
    this.arthaService = arthaService;
    //read from config and save intents in the Map
  }

  Intent getIntent(String message) {
    //ask artha for intent_id
    return intents.get(intent_id);
  }

}