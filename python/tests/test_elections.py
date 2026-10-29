from elections.elections import Elections


def electors_by_district() -> dict[str, list[str]]:
    return {
        "District 1": ["Bob", "Anna", "Jess", "July"],
        "District 2": ["Jerry", "Simon"],
        "District 3": ["Johnny", "Matt", "Carole"],
    }


def test_election_without_districts():
    elections = Elections(electors_by_district(), False)
    elections.add_candidate("Michel")
    elections.add_candidate("Jerry")
    elections.add_candidate("Johnny")

    elections.vote_for("Bob", "Jerry", "District 1")
    elections.vote_for("Jerry", "Jerry", "District 2")
    elections.vote_for("Anna", "Johnny", "District 1")
    elections.vote_for("Johnny", "Johnny", "District 3")
    elections.vote_for("Matt", "Donald", "District 3")
    elections.vote_for("Jess", "Joe", "District 1")
    elections.vote_for("Simon", "", "District 2")
    elections.vote_for("Carole", "", "District 3")

    assert elections.results() == {
        "Jerry": "50,00%",
        "Johnny": "50,00%",
        "Michel": "0,00%",
        "Blank": "25,00%",
        "Null": "25,00%",
        "Abstention": "11,11%",
    }


def test_election_with_districts():
    elections = Elections(electors_by_district(), True)
    elections.add_candidate("Michel")
    elections.add_candidate("Jerry")
    elections.add_candidate("Johnny")

    elections.vote_for("Bob", "Jerry", "District 1")
    elections.vote_for("Jerry", "Jerry", "District 2")
    elections.vote_for("Anna", "Johnny", "District 1")
    elections.vote_for("Johnny", "Johnny", "District 3")
    elections.vote_for("Matt", "Donald", "District 3")
    elections.vote_for("Jess", "Joe", "District 1")
    elections.vote_for("July", "Jerry", "District 1")
    elections.vote_for("Simon", "", "District 2")
    elections.vote_for("Carole", "", "District 3")

    assert elections.results() == {
        "Jerry": "66,67%",
        "Johnny": "33,33%",
        "Michel": "0,00%",
        "Blank": "22,22%",
        "Null": "22,22%",
        "Abstention": "0,00%",
    }
