package net.fortytwo.myotherbrain.flashcards.decks.vocab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: josh
 * Date: 3/19/11
 * Time: 8:20 PM
 */
public class HSK4Compounds extends VocabularyDeck {

    public HSK4Compounds() throws IOException {
        super("hsk4_compounds");
    }

    @Override
    public Map<String, Term> createVocabulary() throws IOException {
        Map<String, Term> terms = new HashMap<String, Term>();

        // HSK level 4 vocabulary list retrieved on 2011-3-19 from:
        //     http://www.chinese-forums.com/index.php?/topic/14829-hsk-character-lists/
        // (provided by user renzhe)
        InputStream is = HSK4Characters.class.getResourceAsStream("hsk4-multitab.txt");
        try {
            InputStreamReader r = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(r);
            String l;
            while ((l = br.readLine()) != null) {
                Term t = new Term();
                int tab = l.indexOf('\t');
                int star = l.indexOf('*');
                t.normativeForm = l.substring(0, tab).trim();
                t.pronunciation = l.substring(tab + 1, star).trim();
                t.meaning = l.substring(star + 1).trim();
                //System.out.println(c.normativeForm + ", " + c.pronunciation + ", " + c.meaning);

                terms.put(findCardName(t), t);
            }
        } finally {
            is.close();
        }

        return terms;
    }
}