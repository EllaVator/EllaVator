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
        configuration.setGrammarName("gram");
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration)
        recognizer.startRecognition(audio);
        return recognizer.getResult().getHypothesis()
    }

    @DataProvider
    Object[][] expandedGrammar() {
        return [
            [AudioSystem.getAudioInputStream(getClass().getResourceAsStream('SpeechInput/room-one.wav')), 'room one'],
        ]
    }

    @Test(dataProvider = 'expandedGrammar')
    void canRecognizeGrammar(AudioInputStream audio, String expected) {
        def actual = recognize(audio)
        assert actual == expected
    }

}
