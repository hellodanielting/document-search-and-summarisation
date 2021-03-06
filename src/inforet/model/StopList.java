package inforet.model;

import inforet.util.IndexArgs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Daniel on 12/08/2014.
 */
public class StopList {

    // Load StopList on First Call
    Set<String> stopList = null;
    int hashSetSize = 630; // This is calculated from the number of lines in the stoplist / 0.75

    // Model ======================================================
    public StopList() {
    }

    private  Boolean enabled = false;
    public  void setEnabled() {
        this.enabled = true;
    }
    public Boolean isEnabled() {
        return enabled;
    }


    // Initialise Stop List ======================================
    public void initStopList(String stopFile){

        //Read File
        try {
            BufferedReader br = new BufferedReader((new FileReader(stopFile)));
            String word;

            while (( word = br.readLine()) != null ){
                // Processing the Stop Word
                insertStopWord(word);
            }
            setEnabled();

        } catch (FileNotFoundException e) {
            System.err.println(stopFile+" not found.");
        } catch (IOException e) {
            System.err.println("IO error reading "+stopFile);
        }

    }

    private void insertStopWord(String word){
        if (this.stopList == null){
            this.stopList= new HashSet<String>(hashSetSize);
        }
        // Check hashMap for existing
        if ( !stopList.contains( word )){
            // Insert if not existing
            this.stopList.add(word);
        }
    }

    public Boolean contains(String word)
    {
        return stopList.contains(word);
    }


    // CRUD Interface for StopList
    // TODO Create CRUD methods.


}
