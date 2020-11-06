package org.elections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ElectionsTest {

    @Test
    void first(){
        Elections elections = new Elections();
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "Bob's District");
        elections.voteFor("Anna", "Johnny", "Anna's District");

        Map<String, String> results = elections.results();

        Map<String, String> expectedResults = Map.of("Jerry" , "1", "Johnny", "1", "Michel", "0");
        Assertions.assertThat(results).isEqualTo(expectedResults);
    }
}
