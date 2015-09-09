import edu.cmu.sphinx.api.StreamSpeechRecognizer
import edu.cmu.sphinx.api.Configuration;

import javax.sound.sampled.AudioInputStream

import org.testng.annotations.*

import javax.sound.sampled.AudioSystem

class SpeechIOTest {

    String recognize(AudioInputStream audio) {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/grammar_en/ella_en.dict");
        configuration.setGrammarPath("resource:/grammar_en");
        configuration.setUseGrammar(true);
        configuration.setGrammarName("compact_gram");
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration)
        recognizer.startRecognition(audio);
        return recognizer.getResult().getHypothesis()
    }

    @DataProvider
    Object[][] prompts() {
        def items = []
        new File(System.properties.audioDir).eachFileMatch(~/.+_en_.+\.txt/) { txtFile ->
            def wavFile = new File(txtFile.path - '.txt' + '_close_16.wav')
            def audio = AudioSystem.getAudioInputStream(wavFile)
            items << [audio, txtFile.text]
        }
        return items
    }

    @Test(dataProvider = 'prompts')
    void canRecognizeGrammar(AudioInputStream audio, String expected) {
        def actual = recognize(audio)
        assert actual == expected
    }

}
