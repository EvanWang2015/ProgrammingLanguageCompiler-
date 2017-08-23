

#include "FibonacciHeapClass.h"
#include <unordered_map>
#include <fstream>
#include <stack>
#include <string.h>

using namespace std;
typedef FibonacciHeapNode<string,unsigned int>  FibNode;
typedef FibonacciHeapNode<string,unsigned int>* PtrFibNode;
FibonacciHeapClass<string,unsigned int> fib;
unordered_map<string, PtrFibNode> tagMap2Node;


void Output(const unsigned int& outputNum, FILE* fp)
{
    stack<FibNode> removeStack;
    for(int i=0;i<outputNum;i++)
    {
        const FibNode* pNode = fib.GetMax();
        if(pNode == NULL)
            cout<<"Heap is Empty"<<endl;
        else
            cout<< "key = " <<pNode->m_key<<", value = " <<pNode->m_val<<endl;
        if(i != outputNum-1)
            fprintf(fp, "%s%d,",pNode->m_key.c_str(),pNode->m_val);
        else
            fprintf(fp, "%s%d",pNode->m_key.c_str(),pNode->m_val);

        removeStack.push(*pNode);
        tagMap2Node.erase(pNode->m_key);
        fib.DeleteMax();
    }
    fprintf(fp, "\n");
    cout<< "**********************************"<<endl;
    while(removeStack.size())
    {
        FibNode node = removeStack.top();
        removeStack.pop();
        
        FibNode* pNode = fib.Insert(node.m_key, node.m_val);
        tagMap2Node[node.m_key] = pNode; 
    }
}

void InsertTags(string line)
{
    size_t found = line.find('#');
    size_t space = line.find(" ");
    string word = line.substr(found + 1, space - found-1); // get tag name

    unsigned int value = stoi(line.substr(space + 1));                // get frequency number 

    PtrFibNode pNode;
    unordered_map<std::string, PtrFibNode>::const_iterator got = tagMap2Node.find(word);

    // if the tag is new, then insert it into fib heap
    if (got == tagMap2Node.end())
    {
        pNode = fib.Insert(word, value);
        tagMap2Node[word] = pNode;      //hash the node into hashtable
    }
    // otherwise, just perform increaseKey
    else
    {

        PtrFibNode temp;
        temp = (*got).second;
        if(temp->m_key == "chlorococcales")
        {
            pNode = tagMap2Node["chlorococcales"];
            cout << pNode->m_val <<endl;
        }
        fib.IncreaseKey(temp, value);
        //cout<<temp->m_key<<endl;
    }
}

void ParseInput(string fileName)
{   

    string str;
    ifstream infile(fileName.c_str());
    string line;
    string startFlag = "#";
    string target2 = " ";
    string stopFlag = "STOP";
    int NumOutput = 0;
    ofstream output_file;

    cout << "Output Test:\n" << endl;
    FILE* fp = fopen("sample_output1.txt","wt");
    while (getline(infile, line) || strstr(line.c_str(), stopFlag.c_str()))
    {
        if (strstr(line.c_str(), startFlag.c_str()))    //find "#"
        {
            InsertTags(line);
        }
        else
        {
            if (strstr(line.c_str(), stopFlag.c_str()))
            {
                cout << "End of the input\n." << endl;
                return;
            }
            int outputNum = stoi(line);
            Output(outputNum,fp);
        }
    }
    fclose(fp);   
}

int main(int argc, char* argv[])
{
   
    if(argc <= 1)
    {
        cout<<"please input file name"<<endl;
        return -1;
    }
    string fileName;
    fileName = argv[1];
  
    ParseInput(fileName);
    return 1;
}