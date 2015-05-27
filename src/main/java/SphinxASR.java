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
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import opendial.DialogueState;
import opendial.DialogueSystem;
import opendial.bn.values.Value;
import opendial.datastructs.SpeechData;
import opendial.modules.Module;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.util.TimeFrame;

/**
 * Plugin for the CMU Sphinx 4 speech recogniser. Upon receiving a new speech stream
 * from the user (represented as a SpeechData object), the plugin performs the speech
 * recognition and adds the corresponding recognition results to the dialogue state.
 *
 * <p>
 * The plugin requires the specification of a recognition grammar in JSGF format, but
 * can be easily adapted to instead employ a statistical language model. The plugin
 * uses a wideband acoustic model trained on the Wall Street Journal (dictation
 * domain, with microphone speech) and the CMU pronunciation. dictionary. These
 * models can also be straightforwardly changed, depending on the particular needs of
 * the application.
 *
 *
 * @author Pierre Lison (plison@ifi.uio.no)
 */
public class SphinxASR implements Module {

	// logger
	final static Logger log = Logger.getLogger("OpenDial");

	private static final String ACOUSTIC_MODEL =
			"src/main/resources/asr/models/en-us/en-us";
	private static final String DICTIONARY_PATH =
			"src/main/resources/asr/models/en-us/cmudict-en-us.dict";
	private static final String GRAMMAR_PATH =
			"src/main/resources/asr/";

	/**
	 * Recognition probability for the best hypothesis returned by Sphinx. This quick
	 * and dirty hack is necessary at the moment since it seems difficult to retrieve
	 * scored N-Best lists from Sphinx when the language model is grammar-based.
	 */
	public static final double RECOG_PROB = 0.7;

	/** The dialogue system to which the ASR is connected */
	DialogueSystem system;

	Configuration configuration;

	/** the speech recogniser itself */
	LiveSpeechRecognizer asr;

	/** recognition grammar (in JSGF format) */
	File grammarFile;

	/** whether the ASR is active or paused */
	boolean isPaused = true;

	/**
	 * Creates the module for the Sphinx recognition engine, connected to the
	 * dialogue system.
	 *
	 * <p>
	 * The path to the grammar file in JSGF format must be specified as parameter in
	 * the system settings (either by specifying it in the XML domain file or by
	 * adding "-Dgrammar=/path/grammar/file" to the command line).
	 *
	 * @param system the dialogue system initialised
	 */
	public SphinxASR(DialogueSystem system) {

		this.system = system;

		Configuration configuration = new Configuration();
		configuration.setAcousticModelPath(ACOUSTIC_MODEL);
		configuration.setDictionaryPath(DICTIONARY_PATH);
		configuration.setGrammarPath(GRAMMAR_PATH);
		configuration.setUseGrammar(true);

		try {
			configuration.setGrammarName("dialog");
			asr = new LiveSpeechRecognizer(configuration);

		} catch (IOException e) {
			log.warning("Failed to load config: " + e);
		}

		system.enableSpeech(true);
	}

	/**
	 * Starts the recogniser.
	 */
	@Override
	public void start() {
		isPaused = false;
	}

	/**
	 * Performs the speech recognition upon receiving a new user speech stream.
	 * Otherwise, does nothing.
	 *
	 * @param state the dialogue state
	 * @param updatedVars the set of updated variables
	 */
	@Override
	public void trigger(DialogueState state, Collection<String> updatedVars) {
		String speechVar = system.getSettings().userSpeech;

		if (updatedVars.contains(speechVar) && state.hasChanceNode(speechVar)
				&& !isPaused) {
			Value speechVal = system.getContent(speechVar).toDiscrete().getBest();
			if (speechVal instanceof SpeechData) {
				(new Thread(new RecognitionProcess((SpeechData) speechVal))).start();
			}
		}
	}

	/**
	 * Pauses or unpauses the recogniser.
	 *
	 * @param toPause true if the system should be paused, false otherwise
	 */
	@Override
	public void pause(boolean toPause) {
		isPaused = toPause;
	}

	/**
	 * Returns true if the module is currently running (= if it has been started and
	 * is not paused).
	 *
	 * @return true if the module is running, false otherwise
	 */
	@Override
	public boolean isRunning() {
		return !isPaused;
	}

	/**
	 * Creates the N-best list.
	 *
	 * @param result the speech result from Sphinx
	 * @return the corresponding N-best list
	 */
	private Map<String, Double> createNBestList(SpeechResult result) {
		Map<String, Double> table = new HashMap<String, Double>();
		table.put(result.getHypothesis(), RECOG_PROB);
		return table;
	}

	/**
	 * Thread for a speech recognition process
	 */
	class RecognitionProcess implements Runnable {

		SpeechData stream;

		public RecognitionProcess(SpeechData stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			try {

				asr.startRecognition(true);


					String utterance = asr.getResult().getHypothesis();

					system.addUserInput(utterance);

				asr.stopRecognition();
			}
			catch (Exception e) {
				log.warning("cannot do recognition: " + e.toString());
			}
		}
	}
}