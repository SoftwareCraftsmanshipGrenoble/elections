using System.Globalization;

namespace Elections;


public class Elections
{
    List<String> candidates = new List<String>();
    List<String> officialCandidates = new List<String>();
    List<int> votesWithoutDistricts = new List<int>();
    Dictionary<String, List<int>> votesWithDistricts;
    private Dictionary<String, List<String>> list;
    private bool withDistrict;

    public Elections(Dictionary<String, List<String>> list, bool withDistrict)
    {
        this.list = list;
        this.withDistrict = withDistrict;

        votesWithDistricts = new Dictionary<string, List<int>>();
        votesWithDistricts["District 1"] = new List<int>();
        votesWithDistricts["District 2"] = new List<int>();
        votesWithDistricts["District 3"] = new List<int>();
    }

    public void addCandidate(String candidate)
    {
        officialCandidates.Add(candidate);
        candidates.Add(candidate);
        votesWithoutDistricts.Add(0);
        votesWithDistricts["District 1"].Add(0);
        votesWithDistricts["District 2"].Add(0);
        votesWithDistricts["District 3"].Add(0);
    }

    public void voteFor(String elector, String candidate, String electorDistrict)
    {
        if (!withDistrict)
        {
            if (candidates.Contains(candidate))
            {
                int index = candidates.IndexOf(candidate);
                votesWithoutDistricts[index] = votesWithoutDistricts[index] + 1;
            }
            else
            {
                candidates.Add(candidate);
                votesWithoutDistricts.Add(1);
            }
        }
        else
        {
            if (votesWithDistricts.ContainsKey(electorDistrict))
            {
                List<int> districtVotes = votesWithDistricts[electorDistrict];
                if (candidates.Contains(candidate))
                {
                    int index = candidates.IndexOf(candidate);
                    districtVotes[index] = districtVotes[index] + 1;
                }
                else
                {
                    candidates.Add(candidate);
                    foreach(var (district, votes) in votesWithDistricts){
                        votes.Add(0);
                    }
                    districtVotes[candidates.Count - 1] = districtVotes[candidates.Count - 1] + 1;
                }
            }
        }
    }

    public Dictionary<String, String> results()
    {
        Dictionary<String, String> results = new Dictionary<String, String>();
        int nbVotes = 0;
        int nullVotes = 0;
        int blankVotes = 0;
        int nbValidVotes = 0;

        if (!withDistrict)
        {
            nbVotes = votesWithoutDistricts.Sum();
            for (int i = 0; i < officialCandidates.Count; i++)
            {
                int index = candidates.IndexOf(officialCandidates[i]);
                nbValidVotes += votesWithoutDistricts[index];
            }

            for (int i = 0; i < votesWithoutDistricts.Count; i++)
            {
                float candidatResult = ((float)votesWithoutDistricts[i] * 100) / nbValidVotes;
                String candidate = candidates[i];
                if (officialCandidates.Contains(candidate))
                {
                    results[candidate] = String.Format(CultureInfo.GetCultureInfo("fr-FR"), "{0:F2}%", candidatResult);
                }
                else
                {
                    if (String.IsNullOrEmpty(candidates[i]))
                    {
                        blankVotes += votesWithoutDistricts[i];
                    }
                    else
                    {
                        nullVotes += votesWithoutDistricts[i];
                    }
                }
            }
        }
        else
        {
            foreach (var entry in votesWithDistricts)
            {
                List<int> districtVotes = entry.Value;
                nbVotes += districtVotes.Sum();
            }

            for (int i = 0; i < officialCandidates.Count; i++)
            {
                int index = candidates.IndexOf(officialCandidates[i]);
                foreach (var entry in votesWithDistricts)
                {
                    List<int> districtVotes = entry.Value;
                    nbValidVotes += districtVotes[index];
                }
            }

            Dictionary<String, int> officialCandidatesResult = new Dictionary<String, int>();
            for (int i = 0; i < officialCandidates.Count; i++)
            {
                officialCandidatesResult[candidates[i]] = 0;
            }
            foreach (var entry in votesWithDistricts)
            {
                List<float> districtResult = new List<float>();
                List<int> districtVotes = entry.Value;
                for (int i = 0; i < districtVotes.Count; i++)
                {
                    float candidateResult = 0;
                    if (nbValidVotes != 0)
                        candidateResult = ((float)districtVotes[i] * 100) / nbValidVotes;
                    String candidate = candidates[i];
                    if (officialCandidates.Contains(candidate))
                    {
                        districtResult.Add(candidateResult);
                    }
                    else
                    {
                        if (String.IsNullOrEmpty(candidates[i]))
                        {
                            blankVotes += districtVotes[i];
                        }
                        else
                        {
                            nullVotes += districtVotes[i];
                        }
                    }
                }
                int districtWinnerIndex = 0;
                for (int i = 1; i < districtResult.Count; i++)
                {
                    if (districtResult[districtWinnerIndex] < districtResult[i])
                        districtWinnerIndex = i;
                }
                officialCandidatesResult[candidates[districtWinnerIndex]] = officialCandidatesResult[candidates[districtWinnerIndex]] + 1;
            }
            for (int i = 0; i < officialCandidatesResult.Count; i++)
            {
                float ratioCandidate = ((float)officialCandidatesResult[candidates[i]]) / officialCandidatesResult.Count * 100;
                results[candidates[i]] = String.Format(CultureInfo.GetCultureInfo("fr-FR"), "{0:F2}%", ratioCandidate);
            }
        }

        float blankResult = ((float)blankVotes * 100) / nbVotes;
        results["Blank"] = String.Format(CultureInfo.GetCultureInfo("fr-FR"), "{0:F2}%", blankResult);

        float nullResult = ((float)nullVotes * 100) / nbVotes;
        results["Null"] = String.Format(CultureInfo.GetCultureInfo("fr-FR"), "{0:F2}%", nullResult);

        int nbElectors = list.Values.Select(v => v.Count).Sum();
        float abstentionResult = 100 - ((float)nbVotes * 100 / nbElectors);
        results["Abstention"] = String.Format(CultureInfo.GetCultureInfo("fr-FR"), "{0:F2}%", abstentionResult);

        return results;
    }
}
