package net.fortytwo.extendo.brain.rdf;

import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import junit.framework.TestCase;
import net.fortytwo.extendo.Extendo;
import net.fortytwo.extendo.brain.Atom;
import net.fortytwo.extendo.brain.BrainGraph;
import net.fortytwo.extendo.brain.ExtendoBrain;
import net.fortytwo.extendo.brain.Filter;
import net.fortytwo.extendo.brain.Note;
import net.fortytwo.extendo.brain.NoteQueries;
import net.fortytwo.extendo.brain.rdf.classes.AKAReference;
import net.fortytwo.extendo.brain.rdf.classes.BibtexReference;
import net.fortytwo.extendo.brain.rdf.classes.Date;
import net.fortytwo.extendo.brain.rdf.classes.Document;
import net.fortytwo.extendo.brain.rdf.classes.ISBNReference;
import net.fortytwo.extendo.brain.rdf.classes.LinkedConcept;
import net.fortytwo.extendo.brain.rdf.classes.Person;
import net.fortytwo.extendo.brain.rdf.classes.QuotedValue;
import net.fortytwo.extendo.brain.rdf.classes.RFIDReference;
import net.fortytwo.extendo.brain.rdf.classes.TODOTask;
import net.fortytwo.extendo.brain.rdf.classes.Tool;
import net.fortytwo.extendo.brain.rdf.classes.URLReference;
import net.fortytwo.extendo.brain.rdf.classes.Usage;
import net.fortytwo.extendo.brain.rdf.classes.WebPage;
import net.fortytwo.extendo.brain.rdf.classes.collections.DocumentCollection;
import net.fortytwo.extendo.brain.rdf.classes.collections.GenericCollection;
import net.fortytwo.extendo.brain.wiki.NoteParser;
import net.fortytwo.extendo.rdf.vocab.FOAF;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.algebra.evaluation.util.ValueComparator;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class KnowledgeBaseTest extends TestCase {
    @Test
    public void testAKASyntax() throws Exception {
        AtomClass t = AKAReference.class.newInstance();

        assertTrue(t.getValueRegex().matcher("aka \"Ix\"").matches());
        assertTrue(t.getValueRegex().matcher("brand name \"Ix\"").matches());
        assertTrue(t.getValueRegex().matcher("trade name \"Ix\"").matches());
        assertTrue(t.getValueRegex().matcher("formerly \"Ix\"").matches());
        assertFalse(t.getValueRegex().matcher("aka \"\"Ix\"").matches());
        assertFalse(t.getValueRegex().matcher("aka Ix").matches());
        assertFalse(t.getValueRegex().matcher("AKA \"Ix\"").matches());
        assertFalse(t.getValueRegex().matcher("AKA\"Ix\"").matches());

        assertTrue(t.getValueRegex().matcher("aka \"Foo\", \"Bar\"").matches());
        assertFalse(t.getValueRegex().matcher("aka \"Foo\",\"Bar\"").matches());
    }

    @Test
    public void testBibtexReferenceSyntax() throws Exception {
        AtomClass t = BibtexReference.class.newInstance();

        assertTrue(t.getValueRegex().matcher("bibtex: @article{}").matches());
        assertTrue(t.getValueRegex().matcher("bibtex: {{{@article{}}}}").matches());
        assertTrue(t.getValueRegex().matcher("bibtex: {{{\n" +
                "@article{}\n" +
                "}}}").matches());
        assertFalse(t.getValueRegex().matcher("bibtex @article{}").matches());
    }

    @Test
    public void testDateSyntax() throws Exception {
        AtomClass t = Date.class.newInstance();

        assertTrue(t.getValueRegex().matcher("2013-09-17").matches());
        assertFalse(t.getValueRegex().matcher("2013 09 17").matches());
        assertFalse(t.getValueRegex().matcher("2013").matches());
        assertFalse(t.getValueRegex().matcher("9/17/2013").matches());
    }

    @Test
    public void testDocumentCollectionSyntax() throws Exception {
        AtomClass t = DocumentCollection.class.newInstance();

        assertTrue(t.getValueRegex().matcher("some of Arthur Dent's papers").matches());
        assertFalse(t.getValueRegex().matcher("papers by Arthur Dent").matches());
        assertFalse(t.getValueRegex().matcher("Arthur Dent's publications").matches());
    }

    @Test
    public void testDocumentSyntax() throws Exception {
        AtomClass t = Document.class.newInstance();

        assertTrue(t.getValueRegex().matcher("War and Peace").matches());
        assertTrue(t.getValueRegex().matcher("War & Peace").matches());
        assertFalse(t.getValueRegex().matcher("war & peace").matches());
        assertFalse(t.getValueRegex().matcher("& War Peace").matches());
    }

    @Test
    public void testGenericCollectionSyntax() throws Exception {
        AtomClass t = GenericCollection.class.newInstance();

        assertTrue(t.getValueRegex().matcher("some things I like about fish").matches());
        assertFalse(t.getValueRegex().matcher("things I like about fish").matches());
        assertFalse(t.getValueRegex().matcher("something I like about fish").matches());
    }

    @Test
    public void testISBNSyntax() throws Exception {
        AtomClass t = ISBNReference.class.newInstance();

        // valid ISBN-10
        assertTrue(t.getValueRegex().matcher("ISBN: 0-674-01846-X").matches());
        //assertTrue(t.additionalConstraintsSatisfied("ISBN: 0-674-01846-X"));

        // valid ISBN-13
        assertTrue(t.getValueRegex().matcher("ISBN: 978-7-5619-1129-7").matches());
        //assertTrue(t.additionalConstraintsSatisfied("ISBN: 978-7-5619-1129-7"));

        // spaces instead of dashes are OK
        assertTrue(t.getValueRegex().matcher("ISBN: 978 0 335 21344 3").matches());
        //assertTrue(t.additionalConstraintsSatisfied("ISBN: 978 0 335 21344 3"));

        // omission of spaces and dashes is OK
        assertTrue(t.getValueRegex().matcher("ISBN: 089744969X").matches());
        //assertTrue(t.additionalConstraintsSatisfied("ISBN: 089744969X"));

        // invalid ISBN-10 (bad checksum)
        assertTrue(t.getValueRegex().matcher("ISBN: 0-674-01846-0").matches());
        //assertFalse(t.additionalConstraintsSatisfied("ISBN: 0-674-01846-0"));

        // invalid ISBN-13 (bad checksum)
        assertTrue(t.getValueRegex().matcher("ISBN: 978-7-5619-1129-9").matches());
        //assertFalse(t.additionalConstraintsSatisfied("ISBN: 978-7-5619-1129-9"));

        // invalid top-level syntax
        assertFalse(t.getValueRegex().matcher("isbn: 0-674-01846-X").matches());
        assertFalse(t.getValueRegex().matcher("0-674-01846-X").matches());
    }

    @Test
    public void testLinkedConceptSyntax() throws Exception {
        AtomClass t = LinkedConcept.class.newInstance();

        assertTrue(t.getAliasRegex().matcher("http://dbpedia.org/resource/Fish").matches());
        assertFalse(t.getAliasRegex().matcher("http://example.org/resource/Fish").matches());

        assertTrue(t.getValueRegex().matcher("This is a Concept").matches());
        assertFalse(t.getValueRegex().matcher("...Not a Concept").matches());
    }

    @Test
    public void testPersonSyntax() throws Exception {
        AtomClass t = Person.class.newInstance();

        assertTrue(t.getValueRegex().matcher("Arthur Dent").matches());
        assertFalse(t.getValueRegex().matcher("arthur dent").matches());
        assertFalse(t.getValueRegex().matcher("42 Dent").matches());
    }

    @Test
    public void testQuotedValueSyntax() throws Exception {
        AtomClass t = QuotedValue.class.newInstance();

        assertTrue(t.getValueRegex().matcher("\"Belgium\"").matches());
        assertFalse(t.getValueRegex().matcher(" \"Belgium\"").matches());
        assertFalse(t.getValueRegex().matcher("Belgium").matches());
        assertFalse(t.getValueRegex().matcher(" \"Belgium").matches());
        assertFalse(t.getValueRegex().matcher("\"\"").matches());
    }

    @Test
    public void testRFIDSyntax() throws Exception {
        AtomClass t = RFIDReference.class.newInstance();

        assertTrue(t.getValueRegex().matcher("RFID: E200 1021 3707 0148 2440 1C7C").matches());
        assertFalse(t.getValueRegex().matcher("RFID: 12345").matches());
        assertFalse(t.getValueRegex().matcher("RFID E200 1021 3707 0148 2440 1C7C").matches());
    }

    @Test
    public void testTODOSyntax() throws Exception {
        AtomClass t = TODOTask.class.newInstance();

        assertTrue(t.getValueRegex().matcher("TODO: get it done").matches());
        assertFalse(t.getValueRegex().matcher("todo: get it done").matches());
        assertFalse(t.getValueRegex().matcher("TODO tomorrow").matches());
    }

    @Test
    public void testToolSyntax() throws Exception {
        AtomClass t = Tool.class.newInstance();

        assertTrue(t.getValueRegex().matcher("Extend-o-Brain").matches());
        assertFalse(t.getValueRegex().matcher("...not a tool").matches());
        assertFalse(t.getValueRegex().matcher("This is the Really Long Name of a Really Important Tool" +
                " Which Unfortunately Will Not Be Recognized As Such By Extend-o-Brain").matches());
    }

    @Test
    public void testURLSyntax() throws Exception {
        AtomClass t = URLReference.class.newInstance();

        assertTrue(t.getValueRegex().matcher("http://example.org/foobar").matches());
        assertTrue(t.getValueRegex().matcher("https://example.org/foobar").matches());
        assertFalse(t.getValueRegex().matcher("git://github.com/joshsh/extendo.git").matches());
    }

    @Test
    public void testUsageSyntax() throws Exception {
        AtomClass t = Usage.class.newInstance();

        assertTrue(t.getValueRegex().matcher("Extend-o-Brain usage").matches());
        assertFalse(t.getValueRegex().matcher("how to use Extend-o-Brain").matches());
    }

    @Test
    public void testWebPageSyntax() throws Exception {
        AtomClass t = WebPage.class.newInstance();

        assertTrue(t.getValueRegex().matcher("The Least Known Page on the Web (web page)").matches());
        assertFalse(t.getValueRegex().matcher("The Least Known Page on the Web").matches());
    }

    @Test
    public void testInference() throws Exception {
        KeyIndexableGraph g = new TinkerGraph();
        BrainGraph bg = new BrainGraph(g);
        ExtendoBrain brain = new ExtendoBrain(bg);
        KnowledgeBase kb = new KnowledgeBase(bg);
        NoteParser parser = new NoteParser();
        NoteQueries queries = new NoteQueries(brain);
        Filter filter = new Filter();
        Atom root = bg.createAtom(filter, Extendo.createRandomKey());
        String rootId = (String) root.asVertex().getId();

        InputStream in = KnowledgeBase.class.getResourceAsStream("inference-example-1.txt");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while (null != (line = br.readLine())) {
                int height = Integer.valueOf(line.trim());
                StringBuilder sb = new StringBuilder();

                while (null != (line = br.readLine()) && 0 < line.trim().length()) {
                    sb.append(line).append("\n");
                }
                String text = sb.toString().trim();
                Note rootNote = parser.fromWikiText(text);
                System.out.println("children: " + rootNote.getChildren().size() + ", height: " + height);
                for (Note c : rootNote.getChildren()) {
                    System.out.println("\t" + c.getValue());
                    for (Note c2 : c.getChildren()) {
                        System.out.println("\t\t" + c2.getValue());
                    }
                }
                rootNote.setId(rootId);
                queries.update(root, rootNote, height, filter, NoteQueries.FORWARD_ADJACENCY);
            }
        } finally {
            in.close();
        }

        kb.addDefaultClasses();

        Atom einstein = bg.getAtom("yOXFhhN");
        Atom einsteinPapers = bg.getAtom("Z5UUQn6");
        Atom specialRelPaper = bg.getAtom("mRwSsu2");
        Atom bibtex = bg.getAtom("xKWD1wC");
        Atom einsteinQuotes = bg.getAtom("5OfUlUN");
        Atom einsteinQuotes2 = bg.getAtom("vtdNdMF");
        Atom einsteinFamily = bg.getAtom("yWBqSc2");
        Atom speedOfLight = bg.getAtom("dDn4jt0");
        Atom simultaneity = bg.getAtom("x6rw4et");
        Atom relativity = bg.getAtom("-kKLYO8");
        Atom paperPdf = bg.getAtom("gsaYMBs");
        Atom topics = bg.getAtom("GORFdGO");
        Atom ellipsis = bg.getAtom("0MQ4h4a");

        // The following are nested directly under Einstein's family
        Atom hermann = bg.getAtom("mPx8zEW");
        Atom pauline = bg.getAtom("PR8p9B5");
        Atom maria = bg.getAtom("b6jFIkg");
        // The following are under Einstein's family, but also have Wikipedia links
        Atom mileva = bg.getAtom("U2RAPqU");
        Atom elsa = bg.getAtom("Wks2hZM");
        // The following are two degrees removed, under Einstein's family > Einstein's children
        Atom lieserl = bg.getAtom("Y3X-skF");
        Atom hansAlbert = bg.getAtom("6_sSpVa");
        Atom eduard = bg.getAtom("dleUIwo");

        for (int i = 0; i < 4; i++) {
            System.out.println("#### ITERATION #" + (i + 1) + " ######");
            kb.inferClasses(null, null);

            kb.viewInferred(einstein);
            System.out.println("");
            kb.viewInferred(einsteinQuotes);
            System.out.println("");
            kb.viewInferred(einsteinQuotes2);
            System.out.println("");
            kb.viewInferred(einsteinFamily);
            System.out.println("");
            kb.viewInferred(einsteinPapers);
            System.out.println("");
            kb.viewInferred(specialRelPaper);
            System.out.println("");
            kb.viewInferred(bibtex);
            System.out.println("");
            kb.viewInferred(topics);
        }

        assertClassEquals("person", einstein, kb);
        assertClassEquals("document-collection", einsteinPapers, kb);
        assertClassEquals("document", specialRelPaper, kb);
        assertClassEquals("bibtex-reference", bibtex, kb);
        assertClassEquals("quoted-value-collection", einsteinQuotes, kb);
        assertClassEquals("quoted-value-collection", einsteinQuotes2, kb);
        assertClassEquals("person-collection", einsteinFamily, kb);
        assertClassEquals("linked-concept", speedOfLight, kb);
        assertClassEquals("webpage", paperPdf, kb);

        kb.exportRDF(new FileOutputStream("/tmp/test.nt"), RDFFormat.NTRIPLES, null);

        Sail inferred = new MemoryStore();
        Repository repo = new SailRepository(inferred);
        repo.initialize();
        RepositoryConnection rc = repo.getConnection();
        RDFHandler handler;
        RDFizationContext context;
        try {
            handler = new RDFInserter(rc);
            context = new RDFizationContext(handler, inferred.getValueFactory());
            kb.inferClasses(handler, null);
            rc.commit();

            // ellipsis has no class, as the value "..." is matched by no regex
            assertObjects(rc, context.uriOf(ellipsis), RDF.TYPE);

            Resource[] known = new Resource[]{
                    context.uriOf(hermann),
                    context.uriOf(pauline),
                    context.uriOf(maria),
                    context.uriOf(mileva),
                    context.uriOf(elsa),
                    context.uriOf(lieserl),
                    context.uriOf(hansAlbert),
                    context.uriOf(eduard)
            };

            for (Resource r : known) {
                assertObjects(rc, r, RDF.TYPE, FOAF.PERSON);
            }

            assertObjects(rc, context.uriOf(einstein), RDF.TYPE, FOAF.PERSON);
            assertObjects(rc, context.uriOf(einstein), FOAF.NAME, new LiteralImpl("Albert Einstein"));
            assertObjects(rc, context.uriOf(einstein), FOAF.KNOWS, known);

            assertObjects(rc, context.uriOf(einstein), OWL.SAMEAS, new URIImpl("http://dbpedia.org/resource/Albert_Einstein"));
            assertObjects(rc, context.uriOf(elsa), OWL.SAMEAS, new URIImpl("http://dbpedia.org/resource/Elsa_Einstein"));
            assertObjects(rc, context.uriOf(eduard), OWL.SAMEAS, new URIImpl("http://dbpedia.org/resource/Eduard_Einstein"));

            assertObjects(rc, context.uriOf(specialRelPaper), RDF.TYPE, FOAF.DOCUMENT);
            assertObjects(rc, context.uriOf(specialRelPaper), FOAF.MAKER, context.uriOf(einstein));
            assertObjects(rc, context.uriOf(specialRelPaper), FOAF.PAGE, context.uriOf(paperPdf));
            assertObjects(rc, context.uriOf(specialRelPaper), FOAF.TOPIC,
                    context.uriOf(speedOfLight),
                    context.uriOf(simultaneity),
                    context.uriOf(relativity));
            assertObjects(rc, context.uriOf(speedOfLight), RDF.TYPE, OWL.THING);
            assertObjects(rc, context.uriOf(speedOfLight), OWL.SAMEAS, new URIImpl("http://dbpedia.org/resource/Speed_of_light"));
            assertObjects(rc, context.uriOf(specialRelPaper), DCTERMS.BIBLIOGRAPHIC_CITATION, new LiteralImpl("bibtex reference here"));
        } finally {
            rc.close();
        }
    }

    private final Comparator<KnowledgeBase.AtomClassEntry> atomClassComp
            = new KnowledgeBase.AtomClassificationComparator();

    private void assertClassEquals(final String className, final Atom atom, final KnowledgeBase kb) {
        assertTrue(kb.getClassInfo(atom).size() > 0);
        List<KnowledgeBase.AtomClassEntry> helper = new LinkedList<KnowledgeBase.AtomClassEntry>();
        helper.addAll(kb.getClassInfo(atom));
        Collections.sort(helper, atomClassComp);
        assertEquals(className, helper.get(0).getInferredClassName());
    }

    private final Comparator<Value> valueComparator = new ValueComparator();

    private void assertObjects(final RepositoryConnection rc,
                               final Resource subject,
                               final URI predicate,
                               final Value... objects) throws RepositoryException {
        RepositoryResult<Statement> rr = rc.getStatements(subject, predicate, null, false);
        assertSubjectsOrObjects(rr, true, objects);
    }

    private void assertSubjectsOrObjects(final RepositoryResult<Statement> actual,
                                         final boolean selectObjects,
                                         final Value... expected) throws RepositoryException {
        //actual.enableDuplicateFilter();

        List<Value> actualList = new LinkedList<Value>();
        while (actual.hasNext()) {
            actualList.add(selectObjects ? actual.next().getObject() : actual.next().getSubject());
        }
        actual.close();
        List<Value> expectedList = new LinkedList<Value>();
        Collections.addAll(expectedList, expected);

        Collections.sort(expectedList, valueComparator);
        Collections.sort(actualList, valueComparator);

        for (int j = 0; j < actualList.size(); j++) {
            if (j == expectedList.size()) {
                fail("" + (actualList.size() - expectedList.size()) + " unexpected results, including " + actualList.get(expectedList.size()));
            }

            Value a = actualList.get(j);
            Value e = expectedList.get(j);

            if (0 != valueComparator.compare(a, e)) {
                fail("unexpected result(s), including " + a);
            }
        }
        if (expectedList.size() > actualList.size()) {
            fail("" + (expectedList.size() - actualList.size()) + " expected results not found, including " + expectedList.get(actualList.size()));
        }
    }
}