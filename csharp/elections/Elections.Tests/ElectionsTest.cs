namespace Elections.Tests;

public class ElectionsTest
{

        [Fact]
        void ElectionWithoutDistricts()
        {
                Dictionary<String, List<String>> list = new Dictionary<string, List<string>>{
                { "District 1", new List<string>{"Bob", "Anna", "Jess", "July"} },
                {"District 2", new List<string>{"Jerry", "Simon"} },
                {"District 3", new List<string>{"Johnny", "Matt", "Carole"} }
                };

                Elections elections = new Elections(list, false);
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

                Dictionary<String, String> results = elections.results();

                Dictionary<String, String> expectedResults = new Dictionary<string, string>{
                {"Jerry", "50,00%" },
                {"Johnny", "50,00%" },
                {"Michel", "0,00%" },
                {"Blank", "25,00%" },
                {"Null", "25,00%" },
                {"Abstention", "11,11%"}
                };

                Assert.Equal(expectedResults, results);
        }

        [Fact]
        void ElectionWithDistricts()
        {
                Dictionary<String, List<String>> list = new Dictionary<string, List<string>>{
                        {"District 1", new List<string>{"Bob", "Anna", "Jess", "July"} },
                        {"District 2", new List<string>{"Jerry", "Simon"} },
                        {"District 3", new List<string>{"Johnny", "Matt", "Carole"}}
                };
                Elections elections = new Elections(list, true);
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

                Dictionary<String, String> results = elections.results();

                Dictionary<String, String> expectedResults = new Dictionary<string, string>{
                        {"Jerry", "66,67%" },
                        {"Johnny", "33,33%" },
                        {"Michel", "0,00%" },
                        {"Blank", "22,22%" },
                        {"Null", "22,22%" },
                        {"Abstention", "0,00%"}
                };

                Assert.Equal(expectedResults, results);
        }
}
