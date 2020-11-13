package org.elections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElectionData {
	final List<String> candidates;
	final List<Integer> votesWithoutDistricts;
	final Map<String, ArrayList<Integer>> votesWithDistricts;

	public ElectionData(List<String> candidates, List<Integer> votesWithoutDistricts, Map<String, ArrayList<Integer>> votesWithDistricts) {
		this.candidates = candidates;
		this.votesWithoutDistricts = votesWithoutDistricts;
		this.votesWithDistricts = votesWithDistricts;
	}

	public ElectionData voteFor(String candidate, String electorDistrict, boolean withDistrict) {
		List<String> newCandidates = new ArrayList<>(this.candidates);
		List<Integer> newVotesWithoutDistrict = new ArrayList<>(this.votesWithoutDistricts);
		Map<String, ArrayList<Integer>> newVotesWithDistricts = new HashMap<>(this.votesWithDistricts);
		if (!withDistrict) {
			if (newCandidates.contains(candidate)) {
				int index = newCandidates.indexOf(candidate);
				newVotesWithoutDistrict.set(index, newVotesWithoutDistrict.get(index) + 1);
			} else {
				newCandidates.add(candidate);
				newVotesWithoutDistrict.add(1);
			}
		} else {
			if (newVotesWithDistricts.containsKey(electorDistrict)) {
				ArrayList<Integer> districtVotes = newVotesWithDistricts.get(electorDistrict);
				if (newCandidates.contains(candidate)) {
					int index = newCandidates.indexOf(candidate);
					districtVotes.set(index, districtVotes.get(index) + 1);
				} else {
					newCandidates.add(candidate);
					newVotesWithDistricts.forEach((district, votes) -> {
						votes.add(0);
					});
					districtVotes.set(newCandidates.size() - 1, districtVotes.get(newCandidates.size() - 1) + 1);
				}
			}
		}
		return new ElectionData(newCandidates, newVotesWithoutDistrict, newVotesWithDistricts);
	}
}
