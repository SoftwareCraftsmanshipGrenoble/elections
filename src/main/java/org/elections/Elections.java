package org.elections;

import java.util.*;

public class Elections {
    List<String> candidates = new ArrayList<>();
    ArrayList<Integer> votes = new ArrayList<>();

    public void addCandidate(String candidate) {
        candidates.add(candidate);
        votes.add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
        if (candidates.contains(candidate)) {
            int index = candidates.indexOf(candidate);
            votes.set(index, votes.get(index) + 1);
        }
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        Integer nbVotes = votes.stream().reduce(0, Integer::sum);

        for (int i = 0; i < votes.size(); i++) {
            Integer candidatResult = (votes.get(i)*100)/nbVotes;
            results.put(candidates.get(i), candidatResult.toString() + "%");
        }
        return results;
    }
}
