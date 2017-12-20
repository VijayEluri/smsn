package net.fortytwo.smsn.typeatron.ripple.lib;

import net.fortytwo.flow.Sink;
import net.fortytwo.ripple.RippleException;
import net.fortytwo.ripple.model.ModelConnection;
import net.fortytwo.ripple.model.RippleList;
import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.model.Filter;
import net.fortytwo.smsn.brain.model.entities.Note;
import net.fortytwo.smsn.typeatron.ripple.BrainClient;

import java.util.logging.Logger;

public class SetNoteWeightMapping extends NoteMapping {

    private static final Logger logger = Logger.getLogger(SetNoteWeightMapping.class.getName());

    public SetNoteWeightMapping(final BrainClient client,
                                final Filter filter) {
        super(client, filter);
    }

    public String[] getIdentifiers() {
        return new String[]{
                SmSnLibrary.NS_2014_12 + "set-note-weight"
        };
    }

    public Parameter[] getParameters() {
        return new Parameter[]{
                new Parameter("note", "the reference note", true),
                new Parameter("weight", "the new weight", true)};
    }

    public String getComment() {
        return "sets the @weight property of a note";
    }

    public void apply(RippleList stack,
                      final Sink<RippleList> solutions,
                      final ModelConnection mc) throws RippleException {

        Object value = stack.getFirst();
        stack = stack.getRest();
        Object no = stack.getFirst();
        stack = stack.getRest();

        Note n = toTree(no, 0, false);

        if (null == n) {
            logger.warning("can't set @weight of non-note: " + no);
        } else {
            Float f = weightFromArgument(value, mc);

            setProperty(n, SemanticSynchrony.PropertyKeys.WEIGHT, "" + f);

            // put the note back on the stack
            solutions.accept(stack.push(n));
        }
    }
}
