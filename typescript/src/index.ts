export class Elections {
  candidates: string[] = [];
  officialCandidates: string[] = [];
  votesWithoutDistricts: number[] = [];
  votesWithDistricts: Map<string, number[]>;
  private list: Map<string, string[]>;
  private withDistrict: boolean;

  constructor(list: Map<string, string[]>, withDistrict: boolean) {
    this.list = list;
    this.withDistrict = withDistrict;

    this.votesWithDistricts = new Map();
    this.votesWithDistricts.set("District 1", []);
    this.votesWithDistricts.set("District 2", []);
    this.votesWithDistricts.set("District 3", []);
  }

  public addCandidate(candidate: string): void {
    this.officialCandidates.push(candidate);
    this.candidates.push(candidate);
    this.votesWithoutDistricts.push(0);
    this.votesWithDistricts.get("District 1")?.push(0);
    this.votesWithDistricts.get("District 2")?.push(0);
    this.votesWithDistricts.get("District 3")?.push(0);
  }

  // @ts-ignore
  public voteFor(elector: string, candidate: string, electorDistrict: string) {
    if (!this.withDistrict) {
      if (this.candidates.includes(candidate)) {
        const index: number = this.candidates.indexOf(candidate);
        this.votesWithoutDistricts[index]++;
      } else {
        this.candidates.push(candidate);
        this.votesWithoutDistricts.push(1);
      }
    } else {
      if (this.votesWithDistricts.has(electorDistrict)) {
        const districtVotes: number[] = this.votesWithDistricts.get(electorDistrict)!;
        if (this.candidates.includes(candidate)) {
          const index: number = this.candidates.indexOf(candidate);
          districtVotes[index] = districtVotes[index] + 1;
        } else {
          this.candidates.push(candidate);
          // @ts-ignore
          this.votesWithDistricts.forEach((votes: number[], district: string) => {
            votes.push(0);
          });
          districtVotes[this.candidates.length - 1] = districtVotes[this.candidates.length - 1] + 1;
        }
      }
    }
  }

  public results(): Map<string, string> {
    const results: Map<string, string> = new Map();
    let nbVotes: number = 0;
    let nullVotes: number = 0;
    let blankVotes: number = 0;
    let nbValidVotes: number = 0;

    if (!this.withDistrict) {
      nbVotes = this.votesWithoutDistricts.reduce(((previousValue, currentValue) => previousValue + currentValue), 0);
      for (let i: number = 0; i < this.officialCandidates.length; i++) {
        const index: number = this.candidates.indexOf(this.officialCandidates[i]);
        nbValidVotes += this.votesWithoutDistricts[index];
      }

      for (let i: number = 0; i < this.votesWithoutDistricts.length; i++) {
        const candidatResult = this.votesWithoutDistricts[i] * 100 / nbValidVotes;
        const candidate = this.candidates[i];
        if (this.officialCandidates.includes(candidate)) {
          results.set(candidate, candidatResult.toLocaleString("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2}) + "%");
        } else {
          if (this.candidates[i].length === 0) {
            blankVotes += this.votesWithoutDistricts[i];
          } else {
            nullVotes += this.votesWithoutDistricts[i];
          }
        }
      }
    } else {
      for (let entry of Array.from(this.votesWithDistricts.entries())) {
        const districtVotes: number[] = entry[1];
        nbVotes += districtVotes.reduce(((previousValue, currentValue) => previousValue + currentValue), 0);
      }

      for (let i = 0; i < this.officialCandidates.length; i++) {
        const index: number = this.candidates.indexOf(this.officialCandidates[i]);
        for (let entry of Array.from(this.votesWithDistricts.entries())) {
          const districtVotes: number[] = entry[1];
          nbValidVotes += districtVotes[index];
        }
      }

      const officialCandidatesResult: Map<string, number> = new Map();
      for (let i = 0; i < this.officialCandidates.length; i++) {
        officialCandidatesResult.set(this.candidates[i], 0);
      }
      for (let entry of Array.from(this.votesWithDistricts.entries())) {
        const districtResult: number[] = [];
        const districtVotes: number[] = entry[1];
        for (let i = 0; i < districtVotes.length; i++) {
          let candidateResult: number = 0;
          if (nbValidVotes != 0)
            candidateResult = districtVotes[i] * 100 / nbValidVotes;
          const candidate: string = this.candidates[i];
          if (this.officialCandidates.includes(candidate)) {
            districtResult.push(candidateResult);
          } else {
            if (this.candidates[i].length === 0) {
              blankVotes += districtVotes[i];
            } else {
              nullVotes += districtVotes[i];
            }
          }
        }
        let districtWinnerIndex: number = 0;
        for (let i = 0; i < districtResult.length; i++) {
          if (districtResult[districtWinnerIndex] < districtResult[i])
            districtWinnerIndex = i;
        }
        officialCandidatesResult.set(this.candidates[districtWinnerIndex], (officialCandidatesResult.get(this.candidates[districtWinnerIndex]) || 0) + 1);
      }
      for (let i = 0; i < officialCandidatesResult.size; i++) {
        const ratioCandidate: number = (officialCandidatesResult.get(this.candidates[i]) || 0) / officialCandidatesResult.size * 100;
        results.set(this.candidates[i], ratioCandidate.toLocaleString("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2}) + "%");
      }
    }

    const blankResult = blankVotes * 100 / nbVotes;
    results.set("Blank", blankResult.toLocaleString("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2}) + "%");

    const nullResult = nullVotes * 100 / nbVotes;
    results.set("Null", nullResult.toLocaleString("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2}) + "%");

    const nbElectors = Array.from(this.list.values()).map(value => value.length).reduce((previousValue, currentValue) => previousValue + currentValue, 0);
    // @ts-ignore
    const nf: Intl.NumberFormat = new Intl.NumberFormat("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2});
    const abstentionResult = 100 - (nbVotes * 100 / nbElectors);
    results.set("Abstention", abstentionResult.toLocaleString("fr", {"minimumFractionDigits": 2, "maximumFractionDigits": 2}) + "%")
    return results;
  }

}
