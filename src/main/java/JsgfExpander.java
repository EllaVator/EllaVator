import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.linguist.acoustic.UnitManager;
import edu.cmu.sphinx.linguist.dictionary.Dictionary;
import edu.cmu.sphinx.linguist.dictionary.TextDictionary;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.grammar.GrammarArc;
import edu.cmu.sphinx.linguist.language.grammar.GrammarNode;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Stream;

public class JsgfExpander {
    public static void main (String[] args) throws Exception {
        JsgfExpander expander = new JsgfExpander();
        String grammarPath = args[0];
        String grammarName = args[1];
        boolean random = Boolean.parseBoolean(args[2]);
        String fileName = args[3];
        expander.expandGrammarToFile(grammarPath, grammarName, random, fileName);
    }

    public void expandGrammarToFile(String grammarPath, String grammarName, boolean random, String fileName) throws Exception
    {
        PrintWriter writer = new PrintWriter(fileName);

        for (String utterance: expandGrammar(grammarPath, grammarName, random)) {
            writer.println(utterance);
        }
        writer.close();
    }

    public String[] expandGrammar(String grammarPath, String grammarName, boolean random) throws Exception {

        URL dictionaryUrl = getClass().getResource("/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        URL noisedictUrl = getClass().getResource("/edu/cmu/sphinx/models/en-us/en-us/noisedict");

        Dictionary dictionary = new TextDictionary(
            dictionaryUrl,
            noisedictUrl,
            null,
            null,
            new UnitManager()
        );

        dictionary.allocate();

        try {
            JSGFGrammar grammar = new JSGFGrammar(
                grammarPath, grammarName,
                true,
                true,
                true,
                true,
                dictionary
            );

            grammar.commitChanges();

            String[] utterances;
            if (random) {
                int number = 100;

                utterances = new String[number];

                for (int i = 0; i < number; i++) {
                    utterances[i] = grammar.getRandomSentence();
                }

            }
            else {
                utterances = expandGrammarRecursive(grammar.getInitialNode(), "", 0);
            }


            return utterances;
        }
        catch (JSGFGrammarParseException e) {
            System.out.println(e.details);
            throw e;
        }
    }

    private String[] expandGrammarRecursive(GrammarNode node, String startOfUtterance, int depth)
    {
        if (depth>10) {
            return new String[0];
        }

        if (node.isFinalNode()) {
            String[] results = {startOfUtterance};
            return results;
        }

        if (!node.isEmpty()) {
            Word word = node.getWord();
            if (!word.isFiller())
                startOfUtterance = startOfUtterance + word.getSpelling() + " ";
        }

        GrammarArc[] successors = node.getSuccessors();

        String[] results = new String[0];

        for (int i = 0; i< successors.length; i++) {
            String[] newUtterances = expandGrammarRecursive(successors[i].getGrammarNode(), startOfUtterance, depth+1);
            results = Stream.concat(Arrays.stream(newUtterances), Arrays.stream(results)).toArray(String[]::new);
        }

        return results;
    }
}
