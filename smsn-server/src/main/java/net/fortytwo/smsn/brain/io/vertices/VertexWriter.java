package net.fortytwo.smsn.brain.io.vertices;

import com.google.common.base.Preconditions;
import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.io.NoteWriter;
import net.fortytwo.smsn.brain.io.Format;
import net.fortytwo.smsn.brain.model.entities.Note;
import net.fortytwo.smsn.brain.model.TopicGraph;
import net.fortytwo.smsn.brain.model.Filter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class VertexWriter extends NoteWriter {
    private static final Logger logger = Logger.getLogger(VertexWriter.class.getName());

    @Override
    public List<Format> getFormats() {
        return Collections.singletonList(VertexTSVFormat.getInstance());
    }

    @Override
    public void doWrite(Context context) throws IOException {

        TopicGraph sourceGraph = context.getTopicGraph();
        Filter filter = context.getFilter();
        Preconditions.checkNotNull(filter);
        PrintStream p = new PrintStream(context.getDestStream());

        p.println("created\tid\tweight\tsource\ttitle\talias");

        for (Note a : sourceGraph.getAllNotes()) {
            if (isTrueNote(a) && filter.test(a)) {
                p.print(a.getCreated());
                p.print('\t');
                p.print(a.getTopic().getId());
                p.print('\t');
                p.print(a.getWeight());
                p.print('\t');
                p.print(a.getSource());
                p.print('\t');

                String value = a.getLabel();
                if (null == value) {
                    logger.warning("note has null @title: " + a.getTopic().getId());
                } else {
                    p.print(escapeValue(a.getLabel()));
                }
                p.print('\t');

                String alias = a.getAlias();
                if (null != alias) {
                    p.print(escapeValue(alias));
                }

                p.print('\n');
            }
        }
    }

    private boolean isTrueNote(final Note a) {
        return null != a.getCreated();
    }

    // Note: quote characters (") need to be replaced, e.g. with underscores (_), if this data is imported into R.
    // Otherwise, R becomes confused and skips rows.
    private String escapeValue(final String value) {
        return SemanticSynchrony.unicodeEscape(value);
    }
}
