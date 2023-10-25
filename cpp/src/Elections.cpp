#include "Elections.h"
#include <algorithm>
#include <numeric>
#include <sstream>
#include <iomanip>

void Elections::addCandidate(string candidate)
{
    officialCandidates.push_back(candidate);
    candidates.push_back(candidate);
    votesWithoutDistricts.push_back(0);
    votesWithDistricts["District 1"].push_back(0);
    votesWithDistricts["District 2"].push_back(0);
    votesWithDistricts["District 3"].push_back(0);
}

void Elections::voteFor(string elector, string candidate, string electorDistrict)
{
    if (!withDistrict)
    {
        if (count(candidates.begin(), candidates.end(), candidate) > 0)
        {
            int index = find(candidates.begin(), candidates.end(), candidate) - candidates.begin();
            votesWithoutDistricts[index] = votesWithoutDistricts[index] + 1;
        }
        else
        {
            candidates.push_back(candidate);
            votesWithoutDistricts.push_back(1);
        }
    }
    else
    {
        if (votesWithDistricts.contains(electorDistrict))
        {
            vector<int>& districtVotes = votesWithDistricts[electorDistrict];
            if (count(candidates.begin(), candidates.end(), candidate) > 0)
            {
                int index = find(candidates.begin(), candidates.end(), candidate) - candidates.begin();
                districtVotes[index] = districtVotes[index] + 1;
            }
            else
            {
                candidates.push_back(candidate);
                for (auto &[district, votes] : votesWithDistricts)
                {
                    votes.push_back(0);
                }
                districtVotes[candidates.size() - 1] = districtVotes[candidates.size() - 1] + 1;
            }
        }
    }
}

map<string, string> Elections::results()
{
    auto format = [](const auto &param)
    {
        // This function is only needed if std::format is not implemented in your standard library
        // or if the locale support doesn't allow usage of fr-FR
        ostringstream s;
        s << std::fixed << std::setprecision(2) << param << "%";
        string result = s.str();
        // HACK: This is a hack to simulate the fr-FR locale
        // We know that only numbers need to be formatted with "," instead of ".", so just replace them
        replace(result.begin(), result.end(), '.', ',' );
        return result;
    };

    map<string, string> results;
    int nbVotes = 0;
    int nullVotes = 0;
    int blankVotes = 0;
    int nbValidVotes = 0;

    if (!withDistrict)
    {
        nbVotes = accumulate(votesWithoutDistricts.begin(), votesWithoutDistricts.end(), 0);
        for (int i = 0; i < officialCandidates.size(); i++)
        {
            int index = find(candidates.begin(), candidates.end(), officialCandidates[i]) - candidates.begin();
            nbValidVotes += votesWithoutDistricts[index];
        }

        for (int i = 0; i < votesWithoutDistricts.size(); i++)
        {
            float candidatResult = ((float)votesWithoutDistricts[i] * 100) / nbValidVotes;
            string candidate = candidates[i];
            if (count(officialCandidates.begin(), officialCandidates.end(), candidate) > 0)
            {
                results[candidate] = format(candidatResult);
            }
            else
            {
                if (candidates[i].size() == 0)
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
        for (auto entry : votesWithDistricts)
        {
            vector<int> districtVotes = entry.second;
            int nbVotesPerDistrict = accumulate(districtVotes.begin(), districtVotes.end(), 0);
            nbVotes += nbVotesPerDistrict;
        }

        for (int i = 0; i < officialCandidates.size(); i++)
        {
            int index = find(candidates.begin(), candidates.end(), officialCandidates[i]) - candidates.begin();
            for (auto entry : votesWithDistricts)
            {
                vector<int> districtVotes = entry.second;
                nbValidVotes += districtVotes[index];
            }
        }

        map<string, int> officialCandidatesResult;
        for (int i = 0; i < officialCandidates.size(); i++)
        {
            officialCandidatesResult[candidates[i]] = 0;
        }
        for (auto entry : votesWithDistricts)
        {
            vector<float> districtResult;
            vector<int> districtVotes = entry.second;
            for (int i = 0; i < districtVotes.size(); i++)
            {
                float candidateResult = 0;
                if (nbValidVotes != 0)
                    candidateResult = ((float)districtVotes[i] * 100) / nbValidVotes;
                string candidate = candidates[i];
                if (count(officialCandidates.begin(), officialCandidates.end(), candidate) > 0)
                {
                    districtResult.push_back(candidateResult);
                }
                else
                {
                    if (candidates[i].size() == 0)
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
            for (int i = 1; i < districtResult.size(); i++)
            {
                if (districtResult[districtWinnerIndex] < districtResult[i])
                    districtWinnerIndex = i;
            }
            officialCandidatesResult[candidates[districtWinnerIndex]] = officialCandidatesResult[candidates[districtWinnerIndex]] + 1;
        }
        for (int i = 0; i < officialCandidatesResult.size(); i++)
        {
            float ratioCandidate = ((float)officialCandidatesResult[candidates[i]]) / officialCandidatesResult.size() * 100;
            results[candidates[i]] = format(ratioCandidate);
        }
    }

    float blankResult = ((float)blankVotes * 100) / nbVotes;
    results["Blank"] = format( blankResult);

    float nullResult = ((float)nullVotes * 100) / nbVotes;
    results["Null"] = format(nullResult);

    vector<vector<string>> values;
    transform(list.begin(), list.end(), back_inserter(values), [](const auto &val)
              { return val.second; });

    vector<int> sizes;
    transform(values.begin(), values.end(), back_inserter(sizes), [](const auto &v)
              { return v.size(); });
    int nbElectors = accumulate(sizes.begin(), sizes.end(), 0);
    float abstentionResult = 100 - ((float)nbVotes * 100 / nbElectors);
    results["Abstention"] = format(abstentionResult);

    return results;
}