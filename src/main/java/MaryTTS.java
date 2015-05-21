// =================================================================                                                                   
// Copyright (C) 2011-2015 Pierre Lison (plison@ifi.uio.no)

// Permission is hereby granted, free of charge, to any person 
// obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, 
// including without limitation the rights to use, copy, modify, merge, 
// publish, distribute, sublicense, and/or sell copies of the Software, 
// and to permit persons to whom the Software is furnished to do so, 
// subject to the following conditions:

// The above copyright notice and this permission notice shall be 
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// =================================================================                                                                   

import java.util.logging.*;
import java.util.Collection;
import java.util.Set;

import javax.sound.sampled.*;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import opendial.DialogueState;
import opendial.DialogueSystem;
import opendial.bn.values.StringVal;
import opendial.bn.values.Value;
import opendial.datastructs.Assignment;
import opendial.datastructs.SpeechData;
import opendial.modules.Module;
import marytts.util.data.audio.AudioPlayer;

/**
 * Plugin for the Mary Text-to-Speech engine (cf. http://mary.dfki.de for details).
 *
 * @author Pierre Lison (plison@ifi.uio.no)
 */
public class MaryTTS implements Module {

    // logger
    final static Logger log = Logger.getLogger("OpenDial");

    // dialogue system
    DialogueSystem system;

    // Mary interface
    MaryInterface tts;

    // whether the engine is paused or not
    boolean isPaused = true;

    /**
     * Creates a new module connecting the dialogue system to the Mary text-to-speech
     * engine.
     *
     * @param system the dialogue system
     */
    public MaryTTS(DialogueSystem system) {
        try {
            tts = new LocalMaryInterface();
        }
        catch (MaryConfigurationException e) {
            throw new RuntimeException("Cannot start Mary TTS: " + e);
        }
        this.system = system;
    }

    /**
     * Starts the TTS engine.
     */
    @Override
    public void start() {
        isPaused = false;
    }

    /**
     * If the updated variables contains the system output (and the system is not
     * paused), synthesises the utterance.
     *
     * @param state the current dialogue state
     * @param updatedVars the set of updated variables
     */
    @Override
    public void trigger(DialogueState state, Collection<String> updatedVars) {
        String systemOutput = system.getSettings().systemOutput;
        if (updatedVars.contains(systemOutput) && state.hasChanceNode(systemOutput)
                && !isPaused) {
            Value utteranceVal =
                    state.queryProb(systemOutput).toDiscrete().getBest();
            if (utteranceVal instanceof StringVal) {
                synthesise(utteranceVal.toString());
            }
        }
    }

    /**
     * Pauses or unpauses the engine.
     *
     * @param toPause true if the engine should be paused, false if it should be
     *            unpaused.
     */
    @Override
    public void pause(boolean toPause) {
        isPaused = toPause;
    }

    /**
     * Returns true if the engine is currently active (not paused), and false
     * otherwise.
     *
     * @return true if active, false if inactive.
     */
    @Override
    public boolean isRunning() {
        return !isPaused;
    }

    /**
     * Synthesises the utterance on the current audio mixer.
     *
     * @param utterance the utterance to synthesise.
     */
    public void synthesise(String utterance) {
        try {
            //TODO[ab]: use system.addContent(new Assignment(...));

            MaryInterface marytts = new LocalMaryInterface();

            Set<String> voices = marytts.getAvailableVoices();
            marytts.setVoice(voices.iterator().next());

            try {
                AudioInputStream audio2 = marytts.generateAudio(utterance);
                AudioPlayer player = new AudioPlayer(audio2);
                player.start();
                try {
                    player.join();
                } catch (InterruptedException e){
                    log.warning("Speech interrupted: " + e);
                }
            } catch (SynthesisException e) {
                log.warning("Cannot synthesis utterance: " + e);
            }

        }
        catch (MaryConfigurationException e) {
            log.warning("Cannot configure MaryTTS: " + e);
        }

    }

}