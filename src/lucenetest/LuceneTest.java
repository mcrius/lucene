package vasil.georgiev;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.Map;
import org.apache.lucene.document.FieldType;

public class LuceneTest {

    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
        TfIdf t = new TfIdf();

        // 1. create the index
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_45, analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "Lucene in Action Lucene Lucene Lucene", "193398817");
        addDoc(w, "Lucene in Action", "193398817");
        addDoc(w, "Lucene for Dummies", "55320055Z");
        addDoc(w, "Managing Gigabytes with Lucene", "55063554A");
        addDoc(w, "The Art of Computer Science Lucene", "9900333X");
        addDoc(w, "The Art of Computer Science ", "9900333X");
        addDoc(w, "The Art of Computer Science ", "9900333X");
        w.close();

        // 2. query
        String querystr = args.length > 0 ? args[0] : "lucene";

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(Version.LUCENE_45, "title", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Map<String, Integer> termFrequencies = t.getTermFrequencies(reader, docId);
            //sout
            for (Map.Entry<String, Integer> entry : termFrequencies.entrySet()) {
                String string = entry.getKey();
                Integer integer = entry.getValue();
            //    System.out.println(String.format("Term: %s, Freq: %s \n", string, integer));
            }
            String term = "lucene";
            double idf = t.getInverseDocumentFrequency(reader, "title", term);
            double tf = termFrequencies.get(term);
            double tfidf = tf * idf;
            System.out.println("TF-IDF(" + docId + ", " + "lucene" + ")=" + tfidf);
        }
        
        reader.close();
    }

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        //setup
        FieldType type = new FieldType();
        type.setIndexed(true);
        type.setTokenized(true);
        type.setStored(true);
        type.setStoreTermVectors(true);
        type.setStoreTermVectorPositions(true);
        type.freeze(); //za wseki sluchai
        
        
        doc.add(new Field("isbn", isbn, type));
        doc.add(new Field("title", title, type));
        w.addDocument(doc);
    }
}
    