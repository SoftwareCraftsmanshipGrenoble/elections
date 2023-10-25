#include <vector>
#include <string>
#include <map>
using namespace std;

class Elections
{

	private: 
		vector<string> candidates;
		vector<string> officialCandidates;
		vector<int> votesWithoutDistricts;
		map<string, vector<int>> votesWithDistricts;
		map<string, vector<string>> list;
		bool withDistrict;

	public:
		Elections(const map<string, vector<string>>& list, bool withDistrict) : list(list), withDistrict(withDistrict), votesWithDistricts()
	{
	};


		void addCandidate(string candidate);

		void voteFor(string elector, string candidate, string electorDistrict);

		map<string, string> results();

};
