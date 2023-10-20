#define DOCTEST_CONFIG_IMPLEMENT_WITH_MAIN
#include "doctest.h"
#include "Elections.h"
using namespace std;

TEST_CASE("ElectionWithoutDistricts")
{
        map<string, vector<string>> list = {
            {"District 1", vector<string>{"Bob", "Anna", "Jess", "July"}},
            {"District 2", vector<string>{"Jerry", "Simon"}},
            {"District 3", vector<string>{"Johnny", "Matt", "Carole"}}};

        Elections elections(list, false);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "District 1");
        elections.voteFor("Jerry", "Jerry", "District 2");
        elections.voteFor("Anna", "Johnny", "District 1");
        elections.voteFor("Johnny", "Johnny", "District 3");
        elections.voteFor("Matt", "Donald", "District 3");
        elections.voteFor("Jess", "Joe", "District 1");
        elections.voteFor("Simon", "", "District 2");
        elections.voteFor("Carole", "", "District 3");

        map<string, string> results = elections.results();

        map<string, string> expectedResults = {
            {"Jerry", "50,00%"},
            {"Johnny", "50,00%"},
            {"Michel", "0,00%"},
            {"Blank", "25,00%"},
            {"Null", "25,00%"},
            {"Abstention", "11,11%"}};

        CHECK_EQ(expectedResults, results);
}

TEST_CASE("ElectionWithDistricts")
{
        map<string, vector<string>> list = {
            {"District 1", vector<string>{"Bob", "Anna", "Jess", "July"}},
            {"District 2", vector<string>{"Jerry", "Simon"}},
            {"District 3", vector<string>{"Johnny", "Matt", "Carole"}}};
        Elections elections(list, true);
        elections.addCandidate("Michel");
        elections.addCandidate("Jerry");
        elections.addCandidate("Johnny");

        elections.voteFor("Bob", "Jerry", "District 1");
        elections.voteFor("Jerry", "Jerry", "District 2");
        elections.voteFor("Anna", "Johnny", "District 1");
        elections.voteFor("Johnny", "Johnny", "District 3");
        elections.voteFor("Matt", "Donald", "District 3");
        elections.voteFor("Jess", "Joe", "District 1");
        elections.voteFor("July", "Jerry", "District 1");
        elections.voteFor("Simon", "", "District 2");
        elections.voteFor("Carole", "", "District 3");

        map<string, string> results = elections.results();

        map<string, string> expectedResults = {
            {"Jerry", "66,67%"},
            {"Johnny", "33,33%"},
            {"Michel", "0,00%"},
            {"Blank", "22,22%"},
            {"Null", "22,22%"},
            {"Abstention", "0,00%"}};

        CHECK_EQ(expectedResults, results);
}