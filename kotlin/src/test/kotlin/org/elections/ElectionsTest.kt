package org.elections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ElectionsTest {

    @Test
    fun `Elections without districts`() {
        val list = mapOf(
            "District 1" to listOf("Bob", "Anna", "Jess", "July"),
            "District 2" to listOf("Jerry", "Simon"),
            "District 3" to listOf("Johnny", "Matt", "Carole")
        )
        val elections = Elections(list, false)
        elections.addCandidate("Michel")
        elections.addCandidate("Jerry")
        elections.addCandidate("Johnny")
        elections.voteFor("Bob", "Jerry", "District 1")
        elections.voteFor("Jerry", "Jerry", "District 2")
        elections.voteFor("Anna", "Johnny", "District 1")
        elections.voteFor("Johnny", "Johnny", "District 3")
        elections.voteFor("Matt", "Donald", "District 3")
        elections.voteFor("Jess", "Joe", "District 1")
        elections.voteFor("Simon", "", "District 2")
        elections.voteFor("Carole", "", "District 3")
        val results = elections.results()
        val expectedResults = mapOf(
            "Jerry" to "50,00%",
            "Johnny" to "50,00%",
            "Michel" to "0,00%",
            "Blank" to "25,00%",
            "Null" to "25,00%",
            "Abstention" to "11,11%"
        )
        assertThat(results).isEqualTo(expectedResults)
    }

    @Test
    fun `Elections with districts`() {
        val list = mapOf(
            "District 1" to listOf("Bob", "Anna", "Jess", "July"),
            "District 2" to listOf("Jerry", "Simon"),
            "District 3" to listOf("Johnny", "Matt", "Carole")
        )
        val elections = Elections(list, true)
        elections.addCandidate("Michel")
        elections.addCandidate("Jerry")
        elections.addCandidate("Johnny")
        elections.voteFor("Bob", "Jerry", "District 1")
        elections.voteFor("Jerry", "Jerry", "District 2")
        elections.voteFor("Anna", "Johnny", "District 1")
        elections.voteFor("Johnny", "Johnny", "District 3")
        elections.voteFor("Matt", "Donald", "District 3")
        elections.voteFor("Jess", "Joe", "District 1")
        elections.voteFor("July", "Jerry", "District 1")
        elections.voteFor("Simon", "", "District 2")
        elections.voteFor("Carole", "", "District 3")
        val results = elections.results()
        val expectedResults = mapOf(
            "Jerry" to "66,67%",
            "Johnny" to "33,33%",
            "Michel" to "0,00%",
            "Blank" to "22,22%",
            "Null" to "22,22%",
            "Abstention" to "0,00%"
        )
        assertThat(results).isEqualTo(expectedResults)
    }
}
