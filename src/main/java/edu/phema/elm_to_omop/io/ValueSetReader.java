package edu.phema.elm_to_omop.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.hl7.elm.r1.Library;
import org.json.simple.parser.ParseException;

import edu.phema.elm_to_omop.helper.Config;
import edu.phema.elm_to_omop.model_omop.Concept;
import edu.phema.elm_to_omop.model_omop.ConceptSets;
import edu.phema.elm_to_omop.model_omop.Expression;
import edu.phema.elm_to_omop.model_omop.Items;
import edu.phema.elm_to_omop.model_phema.PhemaCode;
import edu.phema.elm_to_omop.model_phema.PhemaValueSet;

/** 
 * Reads Value sets from a spreadsheet formatted in PhEMA authoring tool standard.
 */
public class ValueSetReader {

    private static String vocabSouce;
    private static String serverUrl;
    
    public static ConceptSets getConcepts(Library elmContents, String directory, Logger logger, String domain, String source) throws MalformedURLException, ProtocolException, IOException, ParseException, InvalidFormatException {
        vocabSouce = source;
        serverUrl = domain;
        String vsDirectory = directory + Config.getVsFileName();
        
        SpreadsheetReader vsReader = new SpreadsheetReader();
        ArrayList<PhemaValueSet>  codes = new ArrayList<PhemaValueSet> ();
        codes = vsReader.getSpreadsheetData(vsDirectory, Config.getTab());
        ConceptSets conceptSets = getConceptSets(elmContents, codes);
        
        return conceptSets;
    }
    
    private static ConceptSets getConceptSets(Library elmContents, ArrayList<PhemaValueSet>  pvsList) throws MalformedURLException, ProtocolException, IOException, ParseException {
        
        ConceptSets conceptSets = new ConceptSets();
        Expression expression = new Expression();
        Items items = new Items();
        
        int count = 0;
        for (PhemaValueSet pvs : pvsList) {

            conceptSets.setId(count);
            conceptSets.setName(pvs.getName());
            
            expression = new Expression();
            items = new Items();
            
            ArrayList<PhemaCode> codes = pvs.getCodes();            
            
            for (PhemaCode code : codes) {
                Concept concept = new Concept();
                concept = OmopRepository.getConceptMetada(serverUrl, vocabSouce, code.getCode());

                items = new Items();
                items.setConcept(concept);
                expression.addItem(items);
            }
            count++;
        }
        conceptSets.setExpression(expression);
        
        return conceptSets;
    }

}
