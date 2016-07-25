package net.fortytwo.smsn.rdf.vocab;

import net.fortytwo.rdfagents.RDFAgents;
import org.openrdf.model.IRI;

/**
 * The Bibliographic Ontology as a collection of terms
 *
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class Bibo {
    private static final String NAMESPACE = "http://purl.org/ontology/bibo/";

    public static final IRI
            ACADEMIC_ARTICLE = RDFAgents.createIRI(NAMESPACE + "AcademicArticle"),
            ARTICLE = RDFAgents.createIRI(NAMESPACE + "Article"),
            AUDIO_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "AudioDocument"),
            AUDIO_VISUAL_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "AudioVisualDocument"),
            BILL = RDFAgents.createIRI(NAMESPACE + "Bill"),
            BOOK = RDFAgents.createIRI(NAMESPACE + "Book"),
            BOOK_SECTION = RDFAgents.createIRI(NAMESPACE + "BookSection"),
            BRIEF = RDFAgents.createIRI(NAMESPACE + "Brief"),
            CHAPTER_CLASS = RDFAgents.createIRI(NAMESPACE + "Chapter"),
            CODE = RDFAgents.createIRI(NAMESPACE + "Code"),
            COLLECTED_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "CollectedDocument"),
            COLLECTION = RDFAgents.createIRI(NAMESPACE + "Collection"),
            CONFERENCE = RDFAgents.createIRI(NAMESPACE + "Conference"),
            COURT_REPORTER = RDFAgents.createIRI(NAMESPACE + "CourtReporter"),
            DOCUMENT = RDFAgents.createIRI(NAMESPACE + "Document"),
            DOCUMENT_PART = RDFAgents.createIRI(NAMESPACE + "DocumentPart"),
            DOCUMENT_STATUS = RDFAgents.createIRI(NAMESPACE + "DocumentStatus"),
            EDITED_BOOK = RDFAgents.createIRI(NAMESPACE + "EditedBook"),
            EMAIL = RDFAgents.createIRI(NAMESPACE + "Email"),
            EVENT = RDFAgents.createIRI(NAMESPACE + "Event"),
            EXCERPT = RDFAgents.createIRI(NAMESPACE + "Excerpt"),
            FILM = RDFAgents.createIRI(NAMESPACE + "Film"),
            HEARING = RDFAgents.createIRI(NAMESPACE + "Hearing"),
            IMAGE = RDFAgents.createIRI(NAMESPACE + "Image"),
            INTERVIEW = RDFAgents.createIRI(NAMESPACE + "Interview"),
            ISSUE_CLASS = RDFAgents.createIRI(NAMESPACE + "Issue"),
            JOURNAL = RDFAgents.createIRI(NAMESPACE + "Journal"),
            LEGAL_CASE_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "LegalCaseDocument"),
            LEGAL_DECISION = RDFAgents.createIRI(NAMESPACE + "LegalDecision"),
            LEGAL_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "LegalDocument"),
            LEGISLATION = RDFAgents.createIRI(NAMESPACE + "Legislation"),
            LETTER = RDFAgents.createIRI(NAMESPACE + "Letter"),
            MAGAZINE = RDFAgents.createIRI(NAMESPACE + "Magazine"),
            MANUAL = RDFAgents.createIRI(NAMESPACE + "Manual"),
            MANUSCRIPT = RDFAgents.createIRI(NAMESPACE + "Manuscript"),
            MAP = RDFAgents.createIRI(NAMESPACE + "Map"),
            MULTI_VOLUME_BOOK = RDFAgents.createIRI(NAMESPACE + "MultiVolumeBook"),
            NEWSPAPER = RDFAgents.createIRI(NAMESPACE + "Newspaper"),
            NOTE = RDFAgents.createIRI(NAMESPACE + "Note"),
            PATENT = RDFAgents.createIRI(NAMESPACE + "Patent"),
            PERFORMANCE = RDFAgents.createIRI(NAMESPACE + "Performance"),
            PERIODICAL = RDFAgents.createIRI(NAMESPACE + "Periodical"),
            PERSONAL_COMMUNICATION = RDFAgents.createIRI(NAMESPACE + "PersonalCommunication"),
            PERSONAL_COMMUNICATION_DOCUMENT = RDFAgents.createIRI(NAMESPACE + "PersonalCommunicationDocument"),
            PROCEEDINGS = RDFAgents.createIRI(NAMESPACE + "Proceedings"),
            QUOTE = RDFAgents.createIRI(NAMESPACE + "Quote"),
            REFERENCE_SOURCE = RDFAgents.createIRI(NAMESPACE + "ReferenceSource"),
            REPORT = RDFAgents.createIRI(NAMESPACE + "Report"),
            SERIES = RDFAgents.createIRI(NAMESPACE + "Series"),
            SLIDE = RDFAgents.createIRI(NAMESPACE + "Slide"),
            SLIDESHOW = RDFAgents.createIRI(NAMESPACE + "Slideshow"),
            STANDARD = RDFAgents.createIRI(NAMESPACE + "Standard"),
            STATUTE = RDFAgents.createIRI(NAMESPACE + "Statute"),
            THESIS = RDFAgents.createIRI(NAMESPACE + "Thesis"),
            THESIS_DEGREE = RDFAgents.createIRI(NAMESPACE + "ThesisDegree"),
            WEBPAGE = RDFAgents.createIRI(NAMESPACE + "Webpage"),
            WEBSITE = RDFAgents.createIRI(NAMESPACE + "Website"),
            WORKSHOP = RDFAgents.createIRI(NAMESPACE + "Workshop"),
            ABSTRACT = RDFAgents.createIRI(NAMESPACE + "abstract"),
            ACCEPTED = RDFAgents.createIRI(NAMESPACE + "accepted"),
            AFFIRMED_BY = RDFAgents.createIRI(NAMESPACE + "affirmedBy"),
            ANNOTATES = RDFAgents.createIRI(NAMESPACE + "annotates"),
            ARGUED = RDFAgents.createIRI(NAMESPACE + "argued"),
            ASIN = RDFAgents.createIRI(NAMESPACE + "asin"),
            AUTHOR_LIST = RDFAgents.createIRI(NAMESPACE + "authorList"),
            CHAPTER_PROPERTY = RDFAgents.createIRI(NAMESPACE + "chapter"),
            CITED_BY = RDFAgents.createIRI(NAMESPACE + "citedBy"),
            CITES = RDFAgents.createIRI(NAMESPACE + "cites"),
            CODEN = RDFAgents.createIRI(NAMESPACE + "coden"),
            CONTENT = RDFAgents.createIRI(NAMESPACE + "content"),
            CONTRIBUTOR_LIST = RDFAgents.createIRI(NAMESPACE + "contributorList"),
            COURT = RDFAgents.createIRI(NAMESPACE + "court"),
            DEGREE = RDFAgents.createIRI(NAMESPACE + "degree"),
            DIRECTOR = RDFAgents.createIRI(NAMESPACE + "director"),
            DISTRIBUTOR = RDFAgents.createIRI(NAMESPACE + "distributor"),
            DOI = RDFAgents.createIRI(NAMESPACE + "doi"),
            DRAFT = RDFAgents.createIRI(NAMESPACE + "draft"),
            EANUCC_13 = RDFAgents.createIRI(NAMESPACE + "eanucc13"),
            EDITION = RDFAgents.createIRI(NAMESPACE + "edition"),
            EDITOR = RDFAgents.createIRI(NAMESPACE + "editor"),
            EDITOR_LIST = RDFAgents.createIRI(NAMESPACE + "editorList"),
            EISSN = RDFAgents.createIRI(NAMESPACE + "eissn"),
            FORTHCOMING = RDFAgents.createIRI(NAMESPACE + "forthcoming"),
            GTIN_14 = RDFAgents.createIRI(NAMESPACE + "gtin14"),
            HANDLE = RDFAgents.createIRI(NAMESPACE + "handle"),
            IDENTIFIER = RDFAgents.createIRI(NAMESPACE + "identifier"),
            INTERVIEWEE = RDFAgents.createIRI(NAMESPACE + "interviewee"),
            INTERVIEWER = RDFAgents.createIRI(NAMESPACE + "interviewer"),
            ISBN = RDFAgents.createIRI(NAMESPACE + "isbn"),
            ISBN_10 = RDFAgents.createIRI(NAMESPACE + "isbn10"),
            ISBN_13 = RDFAgents.createIRI(NAMESPACE + "isbn13"),
            ISSN = RDFAgents.createIRI(NAMESPACE + "issn"),
            ISSUE_PROPERTY = RDFAgents.createIRI(NAMESPACE + "issue"),
            ISSUER = RDFAgents.createIRI(NAMESPACE + "issuer"),
            LCCN = RDFAgents.createIRI(NAMESPACE + "lccn"),
            LEGAL = RDFAgents.createIRI(NAMESPACE + "legal"),
            LOCATOR = RDFAgents.createIRI(NAMESPACE + "locator"),
            MA = RDFAgents.createIRI(NAMESPACE + "ma"),
            MS = RDFAgents.createIRI(NAMESPACE + "ms"),
            NON_PEER_REVIEWED = RDFAgents.createIRI(NAMESPACE + "nonPeerReviewed"),
            NUM_PAGES = RDFAgents.createIRI(NAMESPACE + "numPages"),
            NUM_VOLUMES = RDFAgents.createIRI(NAMESPACE + "numVolumes"),
            NUMBER = RDFAgents.createIRI(NAMESPACE + "number"),
            OCLCNUM = RDFAgents.createIRI(NAMESPACE + "oclcnum"),
            ORGANIZER = RDFAgents.createIRI(NAMESPACE + "organizer"),
            OWNER = RDFAgents.createIRI(NAMESPACE + "owner"),
            PAGE_END = RDFAgents.createIRI(NAMESPACE + "pageEnd"),
            PAGE_START = RDFAgents.createIRI(NAMESPACE + "pageStart"),
            PAGES = RDFAgents.createIRI(NAMESPACE + "pages"),
            PEER_REVIEWED = RDFAgents.createIRI(NAMESPACE + "peerReviewed"),
            PERFORMER = RDFAgents.createIRI(NAMESPACE + "performer"),
            PHD = RDFAgents.createIRI(NAMESPACE + "phd"),
            PMID = RDFAgents.createIRI(NAMESPACE + "pmid"),
            PREFIX_NAME = RDFAgents.createIRI(NAMESPACE + "prefixName"),
            PRESENTED_AT = RDFAgents.createIRI(NAMESPACE + "presentedAt"),
            PRESENTS = RDFAgents.createIRI(NAMESPACE + "presents"),
            PRODUCER = RDFAgents.createIRI(NAMESPACE + "producer"),
            PUBLISHED = RDFAgents.createIRI(NAMESPACE + "published"),
            RECIPIENT = RDFAgents.createIRI(NAMESPACE + "recipient"),
            REJECTED = RDFAgents.createIRI(NAMESPACE + "rejected"),
            REPRODUCED_IN = RDFAgents.createIRI(NAMESPACE + "reproducedIn"),
            REVERSED_BY = RDFAgents.createIRI(NAMESPACE + "reversedBy"),
            REVIEW_OF = RDFAgents.createIRI(NAMESPACE + "reviewOf"),
            SECTION = RDFAgents.createIRI(NAMESPACE + "section"),
            SHORT_DESCRIPTION = RDFAgents.createIRI(NAMESPACE + "shortDescription"),
            SHORT_TITLE = RDFAgents.createIRI(NAMESPACE + "shortTitle"),
            SICI = RDFAgents.createIRI(NAMESPACE + "sici"),
            STATUS = RDFAgents.createIRI(NAMESPACE + "status"),
            SUBSEQUENT_LEGAL_DECISION = RDFAgents.createIRI(NAMESPACE + "subsequentLegalDecision"),
            SUFFIX_NAME = RDFAgents.createIRI(NAMESPACE + "suffixName"),
            TRANSCRIPT_OF = RDFAgents.createIRI(NAMESPACE + "transcriptOf"),
            TRANSLATION_OF = RDFAgents.createIRI(NAMESPACE + "translationOf"),
            TRANSLATOR = RDFAgents.createIRI(NAMESPACE + "translator"),
            UNPUBLISHED = RDFAgents.createIRI(NAMESPACE + "unpublished"),
            UPC = RDFAgents.createIRI(NAMESPACE + "upc"),
            URI = RDFAgents.createIRI(NAMESPACE + "uri"),
            VOLUME = RDFAgents.createIRI(NAMESPACE + "volume");
}
