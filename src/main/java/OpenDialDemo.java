import opendial.DialogueSystem;
import opendial.domains.Domain;
import opendial.readers.XMLDomainReader;

public class OpenDialDemo {
	public static void main (String[] args) {
		// creating the dialogue system
		DialogueSystem system = new DialogueSystem();

		// Extracting the dialogue domain
		Domain domain = XMLDomainReader.extractDomain("resources/main/Domain_Opendial_Ella.xml");
		system.changeDomain(domain);

		// Adding new domain modules (optional)
		system.attachModule(new MaryTTS(system));

		// When used as part of another application, we often want to switch off the OpenDial GUI
		//system.getSettings().showGUI = false;

		// Finally, start the system
		system.startSystem();
	}
}
