package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ElectionsTest {

    @Test
    void first() {
        Elections elections = new Elections();
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "Bob's District");
        elections.voteFor("Jerry", "Jerry", "Jerry's District");
        elections.voteFor("Anna", "Johnny", "Anna's District");
        elections.voteFor("Johnny", "Johnny", "Johnny's District");
        elections.voteFor("Matt", "Donald", "Matt's District");
        elections.voteFor("Jess", "Joe", "Jess's District");
        elections.voteFor("Simon", "", "Simon's District");
        elections.voteFor("Carole", "", "Carole's District");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of(
                "Jerry", "50%",
                "Johnny", "50%",
                "Michel", "0%",
                "Blank", "25%",
                "Null", "25%");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
