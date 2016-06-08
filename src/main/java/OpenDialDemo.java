import opendial.DialogueSystem;
import opendial.domains.Domain;
import opendial.plugins.MaryTTS;
import opendial.plugins.SphinxASR;
import opendial.readers.XMLDomainReader;
import opendial.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class OpenDialDemo {
    public static void main (String[] args) throws IOException {
        // creating the dialogue system
        DialogueSystem system = new DialogueSystem();

        // Extracting the dialogue domain
        InputStream domainStream = OpenDialDemo.class.getClassLoader().getResourceAsStream("Domain_Opendial_Ella.xml");
        List<String> domainLines = IOUtils.readLines(domainStream, "UTF-8");
        File domainFile = File.createTempFile("domain", ".xml");
        FileUtils.writeLines(domainFile, domainLines);
        Domain domain = XMLDomainReader.extractDomain(domainFile.getCanonicalPath());
        system.changeDomain(domain);

        Settings setting = system.getSettings();
        setting.params.put("acousticmodel", "resource:/edu/cmu/sphinx/models/en-us/en-us");
        setting.params.put("dictionary", "resource:/grammar_en/ella_en.dict");
//        setting.params.put("dictionary", "resources/main/grammar_en/test_dict.dic");
//        setting.params.put("dictionary", "resources/main/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        setting.params.put("grammar", "resource:/grammar_en/compact_gram.gram");
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
