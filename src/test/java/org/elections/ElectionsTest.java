package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ElectionsTest {

    @Test
    void electionWithoutDistricts() {
        Map<String, List<String>> list = Map.of(
                "District 1", Arrays.asList("Bob", "Anna", "Jess", "July"),
                "District 2", Arrays.asList("Jerry", "Simon"),
                "District 3", Arrays.asList("Johnny", "Matt", "Carole")
        );
        Elections elections = new Elections(list, false, Arrays.asList("District 1", "District 2", "District 3"));
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Jerry", "District 1");
        elections.voteFor("Jerry", "District 2");
        elections.voteFor("Johnny", "District 1");
        elections.voteFor("Johnny", "District 3");
        elections.voteFor("Donald", "District 3");
        elections.voteFor("Joe", "District 1");
        elections.voteFor("", "District 2");
        elections.voteFor("", "District 3");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "50%",
                "Johnny", "50%",
                "Michel", "0%",
                "Blank", "25%",
                "Null", "25%",
                "Abstention", "11,11%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }

    @Test
    void electionWithDistricts() {
        Map<String, List<String>> list = Map.of(
                "District 1", Arrays.asList("Bob", "Anna", "Jess", "July"),
                "District 2", Arrays.asList("Jerry", "Simon"),
                "District 3", Arrays.asList("Johnny", "Matt", "Carole")
        );
        Elections elections = new Elections(list, true, Arrays.asList("District 1", "District 2", "District 3"));
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Jerry", "District 1");
        elections.voteFor("Jerry", "District 2");
        elections.voteFor("Johnny", "District 1");
        elections.voteFor("Johnny", "District 3");
        elections.voteFor("Donald", "District 3");
        elections.voteFor("Joe", "District 1");
        elections.voteFor("Jerry", "District 1");
        elections.voteFor("", "District 2");
        elections.voteFor("", "District 3");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "66,67%",
                "Johnny", "33,33%",
                "Michel", "0%",
                "Blank", "22%",
                "Null", "22%",
                "Abstention", "0%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
