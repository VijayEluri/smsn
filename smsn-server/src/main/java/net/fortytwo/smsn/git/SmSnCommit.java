package net.fortytwo.smsn.git;

import com.google.common.base.Preconditions;
import net.fortytwo.smsn.SemanticSynchrony;
import net.fortytwo.smsn.brain.model.dto.NoteDTO;
import net.fortytwo.smsn.brain.model.dto.ListNodeDTO;
import net.fortytwo.smsn.brain.model.entities.Note;
import net.fortytwo.smsn.brain.model.entities.ListNode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SmSnCommit extends NoteDTO {
    private final SmSnGitRepository repository;
    private final RevCommit commit;

    public SmSnCommit(final SmSnGitRepository repository, final RevCommit commit) {
        super();

        this.repository = repository;
        this.commit = commit;

        this.setCreated(commit.getCommitTime() * 1000L);
        this.setLabel(createTitle());
        this.setSource(repository.getSource());
    }


    private String createTitle() {
        String message = commit.getFullMessage().trim();

        String dateLabel = SmSnGitRepository.formatDate(this.getCreated());
        String authorLabel = getPersonLabel(getAuthorOrCommitter(commit));

        return dateLabel + " " + authorLabel + ": " + message;
    }

    @Override
    public ListNode<Note> getChildren() {
        Optional<RevCommit> parent = getParent(commit);
        if (!parent.isPresent()) return null;

        try {
            return getDiffs(parent.get(), commit);
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private String getPersonLabel(final PersonIdent person) {
        Preconditions.checkNotNull(person);
        String name = person.getName();
        String email = person.getEmailAddress();
        Preconditions.checkNotNull(name);
        return null == email ? name : name + " (" + email + ")";
    }

    private PersonIdent getAuthorOrCommitter(final RevCommit revCommit) {
        PersonIdent author = revCommit.getAuthorIdent();
        return null == author ? revCommit.getCommitterIdent() : author;
    }

    private Optional<RevCommit> getParent(final RevCommit commit) {
        RevCommit[] parents = commit.getParents();
        return 0 == parents.length ? Optional.empty() : Optional.of(parents[0]);
    }

    private ListNode<Note> getDiffs(final RevCommit oldCommit, final RevCommit newCommit)
            throws IOException, GitAPIException {
        Git git = repository.getGit();
        ListNode<Note> list = null, cur = null;

        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldCommit.getTree().getId());
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, newCommit.getTree().getId());
        List<DiffEntry> diffs = git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call();

        for (DiffEntry diffEntry : diffs) {
            // note: ignored
            DiffEntry.ChangeType changeType = diffEntry.getChangeType();
            String oldPath = diffEntry.getOldPath();
            int score = diffEntry.getScore();

            //System.out.println("diff: " + diffEntry.toString());

            String newPath = diffEntry.getNewPath();

            String id = SmSnGitRepository.toId(changeType == DiffEntry.ChangeType.DELETE ? oldPath : newPath);
            Note changed = changedNote(id, SmSnGitRepository.getTimeStamp(newCommit), changeType);

            list = creatList(changed, list);
        }

        return list;
    }

    private ListNode<Note> creatList(final Note first, final ListNode<Note> rest) {
        return new ListNodeDTO<>(first, rest);
    }

    private Note changedNote(final String id, final long timestamp, final DiffEntry.ChangeType changeType) {
        Optional<Note> opt = repository.getBrain().getTopicGraph().getTopicById(id);
        Note note;

        if (opt.isPresent()) {
            note = opt.get();
        } else {
            note = new NoteDTO();
            Note.setId(note, id);
            note.setCreated(timestamp);
            note.setLabel(SmSnGitRepository.titleForMissingNote(changeType));
            note.setWeight(SemanticSynchrony.DEFAULT_WEIGHT);
            note.setSource(this.getSource());
        }

        return note;
    }
}
