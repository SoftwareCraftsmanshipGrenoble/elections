package org.elections;

public class Result {
    private Integer nbVotes;
    private Integer nullVotes;
    private Integer blankVotes;
    private int nbValidVotes;
    private int nbElectors;

    public Result(int nbElectors) {
        this.nbElectors = nbElectors;
        nbVotes = 0;
        nullVotes = 0;
        blankVotes = 0;
        nbValidVotes = 0;
    }

    int blankRatio() {
        return (blankVotes * 100) / nbVotes;
    }

    int nullRatio() {
        return (nullVotes * 100) / nbVotes;
    }

    float abstentionRatio() {
        return 100 - ((float) nbVotes * 100 / nbElectors);
    }

    void addValidVotes(Integer nbVotesForCandidate) {
        nbValidVotes = nbValidVotes + nbVotesForCandidate;
    }

    void addBlankVotes(Integer nbBlankVotes) {
        blankVotes = blankVotes + nbBlankVotes;
    }

    void addNullVotes(Integer nbNullVotes) {
        nullVotes = nullVotes + nbNullVotes;
    }

    void addNbVotes(Integer nbVotes) {
        this.nbVotes = this.nbVotes + nbVotes;
    }

    int nbVoteCandidate(int nbVoteTotal) {
        if (nbValidVotes == 0)
            return 0;
        return nbVoteTotal * 100 / nbValidVotes;
    }
}
