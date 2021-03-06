package inforet.controller;

import inforet.model.QueryResult;
import inforet.view.ResultsView;
import inforet.model.Model;
import inforet.module.*;
import inforet.util.QueryArgs;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daniel on 16/08/2014.
 */
public class QueryController {

    // Initialize all the necessary modules

    // This is yo big daddy controller ! I do everything !
    // So, what are my arguments.

    /***
     * Perform the query tasks upon instantiation of the controller.
     * This parses the query into a usable bag of words upon which a query of each query term is done
     * against the inverted index.
     *
     * @param args
     */
    public QueryController(String[] args){
        // Parse Args
        QueryArgs queryArgs = new QueryArgs();
        try {
            queryArgs.parseArgs(args);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.err.println("Invalid Arguments received. Please check your arguments passed to ./search");
            return;
        }

        // Run the Term Normaliser
        TermNormalizer normalizer = new TermNormalizer();
        String[] queryTerms = normalizer.transformedStringToTerms(queryArgs.queryString);

        //create the model
        Model model = new Model();
        model.loadCollectionFromMap(queryArgs.mapPath);
        model.loadLexicon(queryArgs.lexiconPath);
        model.loadInvertedList(queryArgs.invlistPath);
        if(queryArgs.stopListEnabled)
        {
            model.loadStopList(queryArgs.stopListPath);
        }
        if(queryArgs.printSummary && queryArgs.collectionPath != "")
        {
            model.setPathToCollection(queryArgs.collectionPath);
        }


        //fetch the top results from the query module
        QueryModule queryModule = new QueryModule();
        queryModule.doQuery(queryTerms, model, queryArgs.bm25Enabled);
        List<QueryResult> topResults = queryModule.getTopResult(queryArgs.maxResults);



        ResultsView resultsView = new ResultsView();
        if(queryArgs.printSummary)
        {

            DocSummary docSummary = new DocSummary();
            for(QueryResult result:topResults)
            {
                // retrieve the actualy text from the document collection
                result.setDoc(model.getDocumentCollection().getDocumentByIndex(result.getDoc().getIndex(), true));

                //set the summary on the result object by fetching it from the docSummary object
                result.setSummaryNQB(docSummary.getNonQueryBiasedSummary(result.getDoc(), model.getStopListModule()));
                result.setSummaryQB(docSummary.getQueryBiasedSummary(result.getDoc(), model.getStopListModule(), Arrays.asList(queryTerms)));
            }
            resultsView.printResultsWithSummary(topResults,queryArgs.queryLabel);
        }else
        {
            resultsView.printResults(topResults,queryArgs.queryLabel);
        }

    }
}
