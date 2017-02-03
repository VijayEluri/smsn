package net.fortytwo.smsn.server.actions;

import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.model.Note;
import net.fortytwo.smsn.server.RequestParams;
import net.fortytwo.smsn.server.errors.RequestProcessingException;

import java.io.IOException;

/**
 * A service for finding isolated atoms (i.e. atoms with no parents or children) in an Extend-o-Brain graph
 */
public class FindIsolatedAtoms extends FilteredAction {

    @Override
    public void parseRequest(final RequestParams params) throws IOException {
        params.setFilter(getFilter());

        SemanticSynchrony.logInfo("SmSn find-isolated-atoms");
    }

    @Override
    protected void performTransaction(final RequestParams params) throws RequestProcessingException {
        Note n = params.getQueries().findIsolatedAtoms(params.getFilter());
        try {
            addView(n, params);
        } catch (IOException e) {
            throw new RequestProcessingException(e);
        }

        params.getMap().put("title", "isolated atoms");
    }

    @Override
    protected boolean doesRead() {
        return true;
    }

    @Override
    protected boolean doesWrite() {
        return false;
    }
}
