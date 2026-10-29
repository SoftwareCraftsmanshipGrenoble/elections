class Elections:
    def __init__(self, electors_by_district: dict[str, list[str]], with_district: bool):
        self.candidates: list[str] = []
        self.official_candidates: list[str] = []
        self.votes_without_districts: list[int] = []
        self.votes_with_districts: dict[str, list[int]] = {
            "District 1": [],
            "District 2": [],
            "District 3": [],
        }
        self._electors_by_district = electors_by_district
        self._with_district = with_district

    def add_candidate(self, candidate: str) -> None:
        self.official_candidates.append(candidate)
        self.candidates.append(candidate)
        self.votes_without_districts.append(0)
        self.votes_with_districts["District 1"].append(0)
        self.votes_with_districts["District 2"].append(0)
        self.votes_with_districts["District 3"].append(0)

    def vote_for(self, elector: str, candidate: str, elector_district: str) -> None:
        if not self._with_district:
            if candidate in self.candidates:
                index = self.candidates.index(candidate)
                self.votes_without_districts[index] += 1
            else:
                self.candidates.append(candidate)
                self.votes_without_districts.append(1)
        else:
            if elector_district in self.votes_with_districts:
                district_votes = self.votes_with_districts[elector_district]
                if candidate in self.candidates:
                    index = self.candidates.index(candidate)
                    district_votes[index] = district_votes[index] + 1
                else:
                    self.candidates.append(candidate)
                    for votes in self.votes_with_districts.values():
                        votes.append(0)
                    last = len(self.candidates) - 1
                    district_votes[last] = district_votes[last] + 1

    def results(self) -> dict[str, str]:
        results: dict[str, str] = {}
        nb_votes = 0
        null_votes = 0
        blank_votes = 0
        nb_valid_votes = 0

        if not self._with_district:
            nb_votes = sum(self.votes_without_districts)
            for official_candidate in self.official_candidates:
                index = self.candidates.index(official_candidate)
                nb_valid_votes += self.votes_without_districts[index]

            for i, votes in enumerate(self.votes_without_districts):
                candidate_result = votes * 100 / nb_valid_votes
                candidate = self.candidates[i]
                if candidate in self.official_candidates:
                    results[candidate] = _format_percentage(candidate_result)
                elif len(candidate) == 0:
                    blank_votes += votes
                else:
                    null_votes += votes
        else:
            for district_votes in self.votes_with_districts.values():
                nb_votes += sum(district_votes)

            for official_candidate in self.official_candidates:
                index = self.candidates.index(official_candidate)
                for district_votes in self.votes_with_districts.values():
                    nb_valid_votes += district_votes[index]

            official_candidates_result: dict[str, int] = {}
            for i in range(len(self.official_candidates)):
                official_candidates_result[self.candidates[i]] = 0

            for district_votes in self.votes_with_districts.values():
                district_result: list[float] = []
                for i, votes in enumerate(district_votes):
                    candidate_result = 0.0
                    if nb_valid_votes != 0:
                        candidate_result = votes * 100 / nb_valid_votes
                    candidate = self.candidates[i]
                    if candidate in self.official_candidates:
                        district_result.append(candidate_result)
                    elif len(candidate) == 0:
                        blank_votes += votes
                    else:
                        null_votes += votes

                district_winner_index = 0
                for i, candidate_result in enumerate(district_result):
                    if district_result[district_winner_index] < candidate_result:
                        district_winner_index = i
                winner = self.candidates[district_winner_index]
                official_candidates_result[winner] = official_candidates_result.get(winner, 0) + 1

            for i in range(len(official_candidates_result)):
                candidate = self.candidates[i]
                ratio_candidate = official_candidates_result.get(candidate, 0) / len(official_candidates_result) * 100
                results[candidate] = _format_percentage(ratio_candidate)

        results["Blank"] = _format_percentage(blank_votes * 100 / nb_votes)
        results["Null"] = _format_percentage(null_votes * 100 / nb_votes)

        nb_electors = sum(len(electors) for electors in self._electors_by_district.values())
        abstention_result = 100 - (nb_votes * 100 / nb_electors)
        results["Abstention"] = _format_percentage(abstention_result)
        return results


def _format_percentage(value: float) -> str:
    return f"{value:.2f}".replace(".", ",") + "%"
