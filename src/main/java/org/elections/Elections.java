package org.elections;

import java.text.DecimalFormat;
import java.util.*;

public class Elections {

    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    ArrayList<Integer> votesWithoutDistricts = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private final Map<String, List<String>> list;
    private final boolean withDistrict;

    public Elections(Map<String, List<String>> list, boolean withDistrict, List<String> districts) {
        this.list = list;
        this.withDistrict = withDistrict;

        votesWithDistricts = new HashMap<>();
        for (String district: districts){
            votesWithDistricts.put(district, new ArrayList<>());
        }
    }

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votesWithoutDistricts.add(0);
        for (String district: votesWithDistricts.keySet()){
            votesWithDistricts.get(district).add(0);
        }
    }

    public void voteFor(String candidate, String electorDistrict) {
        if (!withDistrict) {
            votForWithoutDistricts(candidate);
        } else {
            voteForWithDistrict(candidate, electorDistrict);
        }
    }

    private void voteForWithDistrict(String candidate, String electorDistrict) {
        if (votesWithDistricts.containsKey(electorDistrict)) {
            ArrayList<Integer> districtVotes = votesWithDistricts.get(electorDistrict);
            if (candidates.contains(candidate)) {
                int index = candidates.indexOf(candidate);
                districtVotes.set(index, districtVotes.get(index) + 1);
            } else {
                candidates.add(candidate);
                votesWithDistricts.forEach((district, votes) -> votes.add(0));
                districtVotes.set(candidates.size() - 1, districtVotes.get(candidates.size() - 1) + 1);
            }
        }
    }

    private void votForWithoutDistricts(String candidate) {
        if (candidates.contains(candidate)) {
            int index = candidates.indexOf(candidate);
            votesWithoutDistricts.set(index, votesWithoutDistricts.get(index) + 1);
        } else {
            candidates.add(candidate);
            votesWithoutDistricts.add(1);
        }
    }

    public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        int nbElectors = list.values().stream().map(List::size).reduce(0, Integer::sum);
        Result result = new Result(nbElectors);

        if (!withDistrict) {
            result.addNbVotes(votesWithoutDistricts.stream().reduce(0, Integer::sum));
            for (String officialCandidate : officialCandidates) {
                int index = candidates.indexOf(officialCandidate);
                result.addValidVotes(votesWithoutDistricts.get(index));
            }

            for (int i = 0; i < votesWithoutDistricts.size(); i++) {
                int nbVoteTotal = votesWithoutDistricts.get(i);
                int candidateResult = result.nbVoteCandidate(nbVoteTotal);
                String candidate = candidates.get(i);
                if (officialCandidates.contains(candidate)) {
                    results.put(candidate, candidateResult + "%");
                } else {
                    if (candidates.get(i).isEmpty()) {
                        result.addBlankVotes(votesWithoutDistricts.get(i));
                    } else {
                        result.addNullVotes(votesWithoutDistricts.get(i));
                    }
                }
            }
        } else {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                Integer nbVotes = districtVotes.stream().reduce(0, Integer::sum);
                result.addNbVotes(nbVotes);
            }

            for (String officialCandidate : officialCandidates) {
                int index = candidates.indexOf(officialCandidate);
                for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                    ArrayList<Integer> districtVotes = entry.getValue();
                    result.addValidVotes(districtVotes.get(index));
                }
            }

            Map<String, Integer> officialCandidatesResult = new HashMap<>();
            for (int i = 0; i < officialCandidates.size(); i++) {
                officialCandidatesResult.put(candidates.get(i), 0);
            }
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtResult = new ArrayList<>();
                ArrayList<Integer> districtVotes = entry.getValue();
                for (int i = 0; i < districtVotes.size(); i++) {
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        districtResult.add(result.nbVoteCandidate(districtVotes.get(i)));
                    } else {
                        if (candidates.get(i).isEmpty()) {
                            result.addBlankVotes(districtVotes.get(i));
                        } else {
                            result.addNullVotes(districtVotes.get(i));
                        }
                    }
                }
                int districtWinnerIndex = 0;
                for (int i = 1; i < districtResult.size(); i++) {
                    if (districtResult.get(districtWinnerIndex) < districtResult.get(i))
                        districtWinnerIndex = i;
                }
                officialCandidatesResult.put(candidates.get(districtWinnerIndex), officialCandidatesResult.get(candidates.get(districtWinnerIndex)) + 1);
            }
            for (int i = 0; i < officialCandidatesResult.size(); i++) {
                Float ratioCandidate = ((float) officialCandidatesResult.get(candidates.get(i))) / officialCandidatesResult.size() * 100;
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                results.put(candidates.get(i), df.format(ratioCandidate) + "%");
            }
        }

        Integer blankRatio = result.blankRatio();
        results.put("Blank", blankRatio.toString() + "%");

        int nullResult = result.nullRatio();
        results.put("Null", nullResult + "%");

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        results.put("Abstention", df.format(result.abstentionRatio()) + "%");

        return results;
    }
}
