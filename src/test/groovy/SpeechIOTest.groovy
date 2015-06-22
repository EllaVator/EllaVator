import javax.sound.sampled.AudioInputStream

import marytts.LocalMaryInterface

import org.testng.annotations.*

class SpeechIOTest {

    def mary;

    @BeforeSuite
    void startUpMaryTTS() {
        mary = new LocalMaryInterface()
    }

    AudioInputStream synthesize(String text) {
        mary.generateAudio(text)
    }

    String recognize(AudioInputStream audio) {
        'NOT YET IMPLEMENTED!'
    }

    @DataProvider
    Object[][] expandedGrammar() {
        getClass().getResourceAsStream('grammar_en_sample.txt').readLines().collect { [it] }
    }

    @Test(dataProvider = 'expandedGrammar')
    void canRecognizeGrammar(String expected) {
        def audio = synthesize(expected)
        def actual = recognize(audio)
        assert actual == expected
    }

}
