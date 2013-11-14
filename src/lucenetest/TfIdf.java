package vasil.georgiev;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author vgeorgiev
 */
public class TfIdf {

    public TfIdf() {
    }

    /**
     * Zapazwa w HashMap stoinosta na term-a i negowiq freq w documenta
     *
     * @param reader
     * @param docId
     * @return
     * @throws IOException
     */
    public Map<String, Integer> getTermFrequencies(IndexReader reader, int docId) throws IOException {
        Set<String> terms = new HashSet<>();
        Terms vector = reader.getTermVector(docId, "title");
        TermsEnum termsEnum = vector.iterator(null);
        Map<String, Integer> frequencies = new HashMap<>();
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            frequencies.put(term, freq);
            terms.add(term);
        }
        return frequencies;
    }

    public double getInverseDocumentFrequency(IndexReader reader, String field, String fieldValue) throws IOException {
        int docFreq = reader.docFreq(new Term(field, fieldValue));
        int numDocs = reader.maxDoc();
        return Math.log((double) numDocs / docFreq);
    }
}