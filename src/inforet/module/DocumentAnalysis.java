package inforet.module;

import inforet.model.Document;
import inforet.model.Sentence;
import javafx.collections.transformation.SortedList;

import java.lang.StringBuilder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * Created by Daniel on 24/09/2014.
 */
public class DocumentAnalysis {

    private int SENTENCE_MIN_LENGTH = 3;


    //Ascending Ranked List of most used words
    private List<String> wordRank       = null;
    //MetaData Annotated Sentences
    private List<Sentence> sentences    = null;


    public DocumentAnalysis(Document doc) {
        this.wordRank = new ArrayList<String>();
        this.sentences = new ArrayList<Sentence>();

        //Lets begin by ranking all the words used in descending order of use count.
        this.wordRank = this.doWordRank(doc);
        //Extract the sentences and perform sentence level analysis
        this.sentences = this.doSentenceAnalysis(doc.getBodyText());
    }

    public int getSENTENCE_MIN_LENGTH() {
        return SENTENCE_MIN_LENGTH;
    }

    public List<String> getWordRank() {
        return wordRank;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }


///////////// Document Word Ranking /////////////////////////
    private List<String> doWordRank ( Document doc ){
        List<String> wordRank = new ArrayList<String>();
        for ( String key : doc.getTermFrequency().keySet() ){
            insertSortWordRank(key, doc); // Ascending List
        }
        Collections.reverse(this.wordRank); // Descending List
        return wordRank;
    }
    // TODO SORTING IS BROKEN ! FIX ME !
    private void insertSortWordRank (String key, Document doc){
        if( doc.getTermFrequency().get(key) >= doc.getTermFrequency().get(wordRank.get(wordRank.size() - 1 ) ) ){
            this.wordRank.add(key);
        }
        else {
            String temp = this.wordRank.get(wordRank.size() - 1);
            this.wordRank.set((wordRank.size() - 1), key);
            this.wordRank.add(temp);
        }
    }
/////////// Sentence Analysis ///////////////


    private List<Sentence> doSentenceAnalysis ( String text ){
        List<Sentence> sentenceList = new ArrayList<Sentence>();
        // Read one character at a time until the end of text,
        // spliting the sentences and adding metadata as we go.

        StringCharacterIterator sci = new StringCharacterIterator(text);
        StringBuilder strBld = null;
        int paragraph           = 0;
        int sinceLastDotCounter = 0;
        int newlineCounter      = 0;

        //Iterate through the text until the end is reached
        for (char ch = sci.first(); ch != CharacterIterator.DONE; ch = sci.next()){
            ch = Character.toLowerCase(ch);
            //Determine if is new paragraph
            //Check if we get a newline
            if ( ch == '\n' ){
                newlineCounter++;
                if ( newlineCounter > 1 ){
                    newlineCounter = 0;
                    paragraph++;        // Conditions for a new paragraph is met.
                }
                continue; // Do not add new line to the collection of sentences.
            }

            // Extract the sentences
            if(strBld == null) strBld = new StringBuilder();

            strBld.append(ch);
            sinceLastDotCounter++;

            if ( strBld.length() < SENTENCE_MIN_LENGTH ){ // Is shorter than min length, lets continue.
                continue;
            }
            else if ( isSentenceTerminator(ch) ){   //A dot character has been found.
                //Filter out acronyms such as A.B.C. or just .
                if ( sinceLastDotCounter > 2 ){  // Previous to last character was a not  "."
                    // We've got a legitimate sentence terminator
                    Sentence sentence = new Sentence(strBld.toString(), paragraph);
                    sentenceList.add(sentence);
                    strBld = null;
                }
                sinceLastDotCounter = 0; // Reset the counter.
            }
        }

        return sentenceList;
    }

    private static boolean isSentenceTerminator(char c){

        // Note : This isn't a proper way to determine sentence boundaries, but will do good enough.
        // See the OpenNLP (natural language processing) project for the proper way.
        // http://opennlp.apache.org/
        switch(c){
            case '.' : return true;
            case '!' : return true;
            case '?' : return true;
            default  : return false;
        }

    }
}
