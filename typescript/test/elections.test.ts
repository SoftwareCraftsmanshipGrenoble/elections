import { Elections } from "../src";

describe("elections", () => {

  test("election without districts", () => {
    const list: Map<string, string[]> = new Map([
        ["District 1", ["Bob", "Anna", "Jess", "July"]],
        ["District 2", ["Jerry", "Simon"]],
        ["District 3", ["Johnny", "Matt", "Carole"]],
    ]);
    const elections = new Elections(list, false);
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

    const results: Map<string, string> = elections.results();

    const expectedResults = new Map([
      ["Jerry", "50,00%"],
      ["Johnny", "50,00%"],
      ["Michel", "0,00%"],
      ["Blank", "25,00%"],
      ["Null", "25,00%"],
      ["Abstention", "11,11%"],
    ]);
    expect(results).toEqual(expectedResults);
  });

  test("election with districts", () => {
    const list: Map<string, string[]> = new Map([
        ["District 1", ["Bob", "Anna", "Jess", "July"]],
        ["District 2", ["Jerry", "Simon"]],
        ["District 3", ["Johnny", "Matt", "Carole"]],
    ]);
    const elections = new Elections(list, true);
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

    const results: Map<string, string> = elections.results();

    const expectedResults = new Map([
      ["Jerry", "66,67%"],
      ["Johnny", "33,33%"],
      ["Michel", "0,00%"],
      ["Blank", "22,22%"],
      ["Null", "22,22%"],
      ["Abstention", "0,00%"],
    ]);
    expect(results).toEqual(expectedResults);
  });

});
