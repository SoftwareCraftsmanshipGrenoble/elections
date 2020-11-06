package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elections {
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    ArrayList<Integer> votes = new ArrayList<>();

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votes.add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
        if (candidates.contains(candidate)) {
            int index = candidates.indexOf(candidate);
            votes.set(index, votes.get(index) + 1);
        } else {
            candidates.add(candidate);
            votes.add(1);
        }
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        Integer nbVotes = votes.stream().reduce(0, Integer::sum);
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = 0;
        for (int i = 0; i < officialCandidates.size(); i++) {
            int index = candidates.indexOf(officialCandidates.get(i));
            nbValidVotes += votes.get(index);
        }

        for (int i = 0; i < votes.size(); i++) {
            Integer candidatResult = (votes.get(i) * 100) / nbValidVotes;
            String candidate = candidates.get(i);
            if (officialCandidates.contains(candidate)) {
                results.put(candidate, candidatResult.toString() + "%");
            } else {
                if (candidates.get(i).isEmpty()) {
                    blankVotes += votes.get(i);
                } else {
                    nullVotes += votes.get(i);
                }
            }
        }

        results.put("Blank", ((Integer) ((blankVotes * 100) / nbVotes)).toString() + "%");

        Integer nullResult = (nullVotes * 100) / nbVotes;
        results.put("Null", nullResult.toString() + "%");

        return results;
    }
}
