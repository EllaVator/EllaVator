import opendial.DialogueSystem;
import opendial.domains.Domain;
import opendial.readers.XMLDomainReader;
import opendial.Settings;

public class OpenDialDemo {
    public static void main (String[] args) {
        // creating the dialogue system
        DialogueSystem system = new DialogueSystem();

        // Extracting the dialogue domain
        Domain domain = XMLDomainReader.extractDomain("resources/main/Domain_Opendial_Ella.xml");
        system.changeDomain(domain);

        Settings setting = system.getSettings();
        setting.params.put("acousticmodel", "resources/main/edu/cmu/sphinx/models/en-us/en-us");
        setting.params.put("dictionary", "resources/main/grammar_en/ella_en.dict");
        setting.params.put("grammar", "resources/main/grammar_en/compact_gram.gram");
        system.changeSettings(setting);

        // Adding new domain modules (optional)
        system.attachModule(new MaryTTS(system));
        system.attachModule(new SphinxASR(system));

        // When used as part of another application, we often want to switch off the OpenDial GUI
        //system.getSettings().showGUI = false;

        // Finally, start the system
        system.startSystem();
    }
}
