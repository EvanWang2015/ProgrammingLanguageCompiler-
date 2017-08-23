//
//  FibonacciHeapClass.h
//

#ifndef __FibonacciHeapClass__
#define __FibonacciHeapClass__

#include <stdio.h>
#include <iostream>
#include <list>
#include <map>
#include <unordered_set>
using namespace std;

template <typename Key,typename Value>
class FibonacciHeapNode
{
public:
    Value m_val;
    Key m_key;
    FibonacciHeapNode* m_pParent;
    unordered_set<FibonacciHeapNode<Key,Value>*> m_child;
    bool m_childCut;
    FibonacciHeapNode(const Key& key,const Value& val,
                      FibonacciHeapNode* pParent=NULL,bool childCut=true):
                      m_val(val),m_key(key),m_pParent(pParent),m_childCut(childCut){}
};

template <typename Key,typename Value>
class FibonacciHeapClass
{
public:
    typedef FibonacciHeapNode<Key,Value> FibNode;
    typedef FibNode* PFibNode;
public:
    FibonacciHeapClass();
    ~FibonacciHeapClass();
    void Initialize(const int& vertexNum);
    void Release();
    FibonacciHeapNode<Key,Value>* Insert(const Key& key,const Value& val);
    void IncreaseKey(FibNode* pNode, const unsigned int& newKey);
    void DeleteMax();
    const FibNode* GetMax();
    void Delete(const Key& key);
    unsigned long GetSize();
    unsigned int TestGetSize();
    
private:
    void Destroy(FibNode* node);
    void Helper(FibNode* node,unsigned int* num);
    FibNode* Search(const Value& val);
    void CascadingCut(FibNode* decreaseNode);
    void Merge();
    void Merge(FibNode* treeTable[],FibNode* node,const unsigned long& pos);
    void FindMax();

    
    list<FibNode*> m_root;
    list<FibNode*> m_rootBak;
    typename list<FibNode*>::iterator m_max;
    //unordered_map<unsigned int,FibNode*> m_nodeDic;
    FibNode* *m_nodeDic;
    unsigned long m_maxDegree;
};

#endif /* defined(__FibonacciHeapClass__) */
