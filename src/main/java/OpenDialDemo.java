import opendial.DialogueSystem;
import opendial.domains.Domain;
import opendial.readers.XMLDomainReader;

public class OpenDialDemo {
	public static void main (String[] args) {
		// creating the dialogue system
		DialogueSystem system = new DialogueSystem();

		// Extracting the dialogue domain
		Domain domain = XMLDomainReader.extractDomain("./src/main/resources/dialog/example-flightbooking.xml");
		system.changeDomain(domain);

		system.getSettings().params.setProperty("grammar", "./src/main/resources/demo.gram");

		// Adding new domain modules (optional)
		system.attachModule(new MaryTTS(system));
		system.attachModule(new SphinxASR(system));


		// When used as part of another application, we often want to switch off the OpenDial GUI
		//system.getSettings().showGUI = false;

		// Finally, start the system
		system.startSystem();


	}
}
