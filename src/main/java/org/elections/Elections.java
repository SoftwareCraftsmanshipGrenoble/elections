package org.elections;

import java.text.DecimalFormat;
import java.util.*;

import static java.util.Collections.unmodifiableList;

public class Elections {
    List<String> candidates = new ArrayList<>();
    List<String> officialCandidates = new ArrayList<>();
    List<Integer> votesWithoutDistricts = new ArrayList<>();
    Map<String, ArrayList<Integer>> votesWithDistricts;
    private Map<String, List<String>> list;
    private boolean withDistrict;

    public Elections(Map<String, List<String>> list, boolean withDistrict) {
        this.list = list;
        this.withDistrict = withDistrict;

        votesWithDistricts = new HashMap<>();
        votesWithDistricts.put("District 1", new ArrayList<>());
        votesWithDistricts.put("District 2", new ArrayList<>());
        votesWithDistricts.put("District 3", new ArrayList<>());
    }

    public void addCandidate(String candidate) {
        officialCandidates.add(candidate);
        candidates.add(candidate);
        votesWithoutDistricts.add(0);
        votesWithDistricts.get("District 1").add(0);
        votesWithDistricts.get("District 2").add(0);
        votesWithDistricts.get("District 3").add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict) {
	    Output output = voteFor(
	    		candidate, electorDistrict, withDistrict,
			    unmodifiableList(candidates),
			    unmodifiableList(votesWithoutDistricts),
			    votesWithDistricts
	    );
	    candidates = output.newCandidates;
	    votesWithoutDistricts = output.newVotesWithoutDistrict;
    }

    private static class Output {
	    List<String> newCandidates;
	    List<Integer> newVotesWithoutDistrict;

	    public Output(List<String> newCandidates, List<Integer> newVotesWithoutDistrict) {
		    this.newCandidates = newCandidates;
		    this.newVotesWithoutDistrict = newVotesWithoutDistrict;
	    }
    }

	private static Output voteFor(String candidate, String electorDistrict, boolean withDistrict, List<String> candidates,
	                              List<Integer> votesWithoutDistricts, Map<String, ArrayList<Integer>> votesWithDistricts) {
    	List<String> newCandidates = new ArrayList<>(candidates);
    	List<Integer> newVotesWithoutDistrict = new ArrayList<>(votesWithoutDistricts);
		if (!withDistrict) {
			if (newCandidates.contains(candidate)) {
		        int index = newCandidates.indexOf(candidate);
				newVotesWithoutDistrict.set(index, newVotesWithoutDistrict.get(index) + 1);
		    } else {
		        newCandidates.add(candidate);
				newVotesWithoutDistrict.add(1);
		    }
		} else {
		    if (votesWithDistricts.containsKey(electorDistrict)) {
		        ArrayList<Integer> districtVotes = votesWithDistricts.get(electorDistrict);
		        if (newCandidates.contains(candidate)) {
		            int index = newCandidates.indexOf(candidate);
		            districtVotes.set(index, districtVotes.get(index) + 1);
		        } else {
		            newCandidates.add(candidate);
		            votesWithDistricts.forEach((district, votes) -> {
		                votes.add(0);
		            });
		            districtVotes.set(newCandidates.size() - 1, districtVotes.get(newCandidates.size() - 1) + 1);
		        }
		    }
		}
		return new Output(newCandidates, newVotesWithoutDistrict);
	}

	public Map<String, String> results() {
        Map<String, String> results = new HashMap<>();
        Integer nbVotes = 0;
        Integer nullVotes = 0;
        Integer blankVotes = 0;
        int nbValidVotes = 0;

        if (!withDistrict) {
            nbVotes = votesWithoutDistricts.stream().reduce(0, Integer::sum);
            for (int i = 0; i < officialCandidates.size(); i++) {
                int index = candidates.indexOf(officialCandidates.get(i));
                nbValidVotes += votesWithoutDistricts.get(index);
            }

            for (int i = 0; i < votesWithoutDistricts.size(); i++) {
                Integer candidatResult = (votesWithoutDistricts.get(i) * 100) / nbValidVotes;
                String candidate = candidates.get(i);
                if (officialCandidates.contains(candidate)) {
                    results.put(candidate, candidatResult.toString() + "%");
                } else {
                    if (candidates.get(i).isEmpty()) {
                        blankVotes += votesWithoutDistricts.get(i);
                    } else {
                        nullVotes += votesWithoutDistricts.get(i);
                    }
                }
            }
        } else {
            for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                ArrayList<Integer> districtVotes = entry.getValue();
                nbVotes += districtVotes.stream().reduce(0, Integer::sum);
            }

            for (int i = 0; i < officialCandidates.size(); i++) {
                int index = candidates.indexOf(officialCandidates.get(i));
                for (Map.Entry<String, ArrayList<Integer>> entry : votesWithDistricts.entrySet()) {
                    ArrayList<Integer> districtVotes = entry.getValue();
                    nbValidVotes += districtVotes.get(index);
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
                    Integer candidateResult = 0;
                    if (nbValidVotes != 0)
                        candidateResult = (districtVotes.get(i) * 100) / nbValidVotes;
                    String candidate = candidates.get(i);
                    if (officialCandidates.contains(candidate)) {
                        districtResult.add(candidateResult);
                    } else {
                        if (candidates.get(i).isEmpty()) {
                            blankVotes += districtVotes.get(i);
                        } else {
                            nullVotes += districtVotes.get(i);
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

        results.put("Blank", ((Integer) ((blankVotes * 100) / nbVotes)).toString() + "%");

        Integer nullResult = (nullVotes * 100) / nbVotes;
        results.put("Null", nullResult.toString() + "%");

        int nbElectors = list.values().stream().map(List::size).reduce(0, Integer::sum);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        results.put("Abstention", df.format(100 - ((float) nbVotes * 100 / nbElectors)) + "%");

        return results;
    }
}
