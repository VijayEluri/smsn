package net.fortytwo.smsn.brain;

import net.fortytwo.smsn.brain.model.entities.Note;
import net.fortytwo.smsn.brain.model.TopicGraph;
import net.fortytwo.smsn.brain.model.Filter;
import net.fortytwo.smsn.brain.model.entities.Topic;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

public class History {
    private static final int CAPACITY = 1000;

    private final String[] visited;
    private int totalVisits;

    public History() {
        this.visited = new String[CAPACITY];
        totalVisits = 0;
    }

    public void visit(final String noteId) {
        // repeated actions upon the same note count as a single visit
        if (totalVisits == 0 || !noteId.equals(getLastVisit())) {
            appendVisit(noteId);
        }
    }

    public Iterable<Note> getHistory(final int maxlen,
                                     final TopicGraph graph,
                                     final Filter filter) {
        Collection<Note> notes = new LinkedList<>();

        int low = Math.max(totalVisits - CAPACITY, 0);

        for (int i = totalVisits - 1; i >= low; i--) {
            if (notes.size() >= maxlen) {
                break;
            }

            String id = visited[i % CAPACITY];

            Optional<Topic> a = graph.getTopicById(id);
            if (a.isPresent()) {
                Iterator<Note> iter = a.get().getNotes();
                while (iter.hasNext()) {
                    Note note = iter.next();
                    if (filter.test(note)) {
                        notes.add(note);
                    }
                }
            }
        }

        return notes;
    }

    private String getLastVisit() {
        return visited[(totalVisits - 1) % CAPACITY];
    }

    private void appendVisit(final String noteId) {
        visited[totalVisits % CAPACITY] = noteId;
        totalVisits++;
    }
}
