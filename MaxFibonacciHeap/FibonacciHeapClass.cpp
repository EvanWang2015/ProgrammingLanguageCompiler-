
//
#include <string.h>
#include "FibonacciHeapClass.h"

template <typename Key,typename Value>
FibonacciHeapClass<Key,Value>::FibonacciHeapClass()
{
    m_maxDegree=0;
}
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Initialize(const int& vertexNum)
{
    m_nodeDic=new FibNode*[vertexNum];
    memset(&m_nodeDic[0], 0, sizeof(FibNode*)*vertexNum);
}
template <typename Key,typename Value>
FibonacciHeapClass<Key,Value>::~FibonacciHeapClass()
{
    if(m_root.size()!=0)
    {
        Release();
    }
}

/************************************************************************************
 FunctionName:Inset
 Description: Insert a new pair into double link list
 Input:const Key& key---------------------->the key of new pair
       FibonacciHeapNode<Key,Value>* node-->the value of new pair
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
FibonacciHeapNode<Key,Value>* FibonacciHeapClass<Key,Value>::Insert(const Key& key,const Value& val)
{
    FibNode* node=new FibNode(key,val);
    m_root.push_back(node);                     //insert new pair into double link list
    //m_nodeDic[val.currentVertex]=node;        //hash the node
    if(m_maxDegree < node->m_child.size())      //update max degree
    {
        m_maxDegree=node->m_child.size();
    }

    if(m_root.size()==1)                        //always track the m_max node
    {
        m_max=m_root.begin();
    }
    else
    {                                           // if new node has smaller key
        if((*m_max)->m_val < node->m_val)       // let m_max point to this new node
        {
            m_max = m_root.end();
            m_max--;
        }
    }
    return node;
}

/************************************************************************************
 FunctionName:DecreaseKey
 Description: Decrease the key
 Input:const Key& key--------------------->the key of one node.
       const Value& val------------------->Because some nodes have the same key, so 
                                           you also need further information to 
                                           determine the right node you want to decrease
       const unsigned int& decreaseNum---->How many you want decrease.
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::IncreaseKey(FibNode* pNode,const unsigned int& increaseVal)
{
                                  //First, search the right node
    if(pNode!=NULL)
    {
        //unsigned int newVal = increaseVal+
        //if(newKey < pNode->m_val)
        //{
            pNode->m_val += increaseVal;
            //node->m_val.prevVertex = val.prevVertex;
            if(pNode->m_pParent != NULL)                             //if this node is not root, you need to cascading cut.
            {
                if(pNode->m_val > pNode->m_pParent->m_val)           //if the node's key is bigger than his parent, cut it.
                {
                    FibNode* parent = pNode->m_pParent;
                    m_root.push_back(pNode);
                    pNode->m_pParent = NULL;
                    if((*m_max)->m_val < pNode->m_val)               //update the min
                    {
                        m_max = m_root.end();
                        m_max--;
                    }
                    parent->m_child.erase(parent->m_child.find(pNode));
                    CascadingCut(parent);                       //cut
                }
            }
            else
            {
                FindMax();
            }
        //}
    }
    else
    {
        //Insert(key, val);
    }
}
template <typename Key,typename Value>

const FibonacciHeapNode<Key,Value>* FibonacciHeapClass<Key,Value>::GetMax()
{
    return *m_max;
}
/************************************************************************************
 FunctionName:DeleteMin
 Description: Delete the min Key pair
 Input:void
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::DeleteMax()
{
    if(m_root.size()>0)
    {
        typename unordered_set<FibNode*>::iterator it = (*m_max)->m_child.begin();  //get the children of root
        for(;it!=(*m_max)->m_child.end();it++)                                      //traverse the link list
        {
            (*it)->m_pParent=NULL;
            m_root.push_back(*it);                                                  //add all children of root in list
        }
  
        FibNode* node=*m_max;
        //---m_nodeDic.erase(m_nodeDic.find(node->m_val.currentVertex));
        //m_nodeDic[node->m_val.currentVertex]=NULL;
        m_root.remove(node);
        delete node;
        
        Merge();                                                                 //merge all the subtrees
        FindMax();                                                               //find the min key node in the new list
    }
}
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Delete(const Key& key){}

/************************************************************************************
 FunctionName:CascadingCut
 Description: Cascading cut from decreasing key node to root
 Input:const Value& decreaseNode----------->the node whose key is decreased.
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::CascadingCut(FibNode* decreaseNode)
{
    FibNode* node=decreaseNode;
    FibNode* parent=node->m_pParent;
    while((parent!=NULL)&&(node->m_childCut==true))
    {
        node->m_pParent=NULL;
        m_root.push_back(node);                                  //put the node into double link list
        if((*m_max)->m_val < node->m_val)                        //update the max
        {
            m_max=m_root.end();
            m_max--;
        }
        parent->m_child.erase(parent->m_child.find(node));       //delete the node
        node=parent;                                             //move upward to root
        parent=parent->m_pParent;
    }
    if(parent!=NULL)
    {
        parent->m_childCut=true;        //until the node's childCut field is false,stop cutting, set childCut to true
    }
}

/************************************************************************************
 FunctionName:Merge
 Description: Combine all subtrees which have the same degree using treeTable. The 
              treetable is a table which each index corresponds the degree of root of
              one subtree. Traverse the link list, put the root to corresponding position
              of the table if this position has no other tree, otherwise, combine the 
              subtree from the list with the subtree from the table. The degree of
              the combined tree will be increased by 1. Then recursivly, find next position
              to insert the combined tree until you find a position where is empty.
 Input:void
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Merge()
{
    //calculate the table size. Assume there is n subtrees in the link list and all subtrees
    //has the degree 1, then after combining, the max degree of subtrees will be log(n). So
    //the table size should be log(n)+1.
    //unsigned long size=m_maxDegree+(unsigned int)(log(m_root.size())/log(2))+5;
    
    static FibNode* treeTable[30];
    memset(&treeTable[0],0,30*sizeof(FibNode*));             //initialize the table to NULL
    
    typename list<FibNode*>::const_iterator it=m_root.begin();
    FibNode* node;
    for(;it!=m_root.end();it++)                                 //traverse the link list
    {
        node=*it;
        
        unsigned long pos=node->m_child.size();                 //get the degree of a subtree
        
        if(treeTable[pos]==NULL)                                //if the corresponding position in table is empty, just insert
        {
            treeTable[pos]=node;
        }
        else                                                    //if there has be a subtree in table, combine it with the new one
        {
            Merge(treeTable,node,pos);                          //recursivly combining.
        }
    }
    m_root=m_rootBak;                                           //you can not use clear(),becuase that will delete all the node
                                                                //So you should use another empty list to cover the old one so that the node will not
                                                                //be delete
    for(int i=0;i<30;i++)                                       //copy the combined subtrees from table to link list
    {
        if(treeTable[i]!=NULL)
        {
            m_maxDegree=i;                                      //record the new maxDegree
            m_root.push_back(treeTable[i]);
        }
    }
}

/************************************************************************************
 FunctionName:Merge
 Description: Recursivly combine the subtrees
 Input:vector<Value>& treeTable--------->treeTable
       Value node----------------------->the node coming from link list
       const unsigned long& pos--------->new position where to insert the subtree
 Output:void
 ***********************************************************************************/

template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Merge(FibNode* treeTable[],FibNode* node,const unsigned long& pos)
{
    unsigned long index=pos;
    FibNode* combineNode;
    while(treeTable[index]!=NULL)
    {
        combineNode = treeTable[index];
        if(combineNode->m_val < node->m_val)                //merge two tree, the min Key will be the root
        {
            node->m_child.insert(combineNode);              //node is the root, so add combineNode into children of node
            combineNode->m_pParent = node;                  //let parent of combineNode be the node
            combineNode->m_childCut = false;                //childCut sets to false
        }
        else                                                //otherwise, let combineNode be the root.
        {
            combineNode->m_child.insert(node);
            node->m_pParent=combineNode;
            node->m_childCut=false;
            node=combineNode;
        }
        treeTable[index]=NULL;
        index++;
    }
    treeTable[index]=node;
}

/************************************************************************************
 FunctionName:FindMin
 Description: Find the node who has the minimun key
 Input:void
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::FindMax()
{
    typename list<FibNode*>::iterator it=m_root.begin();
    m_max=it;
    while(it!=m_root.end())                         //traverse the link list
    {
        if((*it)->m_val > (*m_max)->m_val)
        {
            m_max=it;
        }
        it++;
    }
}
/************************************************************************************
 FunctionName:Search the node by value
 Description: Find the node who has the minimun key
 Input:const Value& val-------------------->value
 Output:FibonacciHeapNode<Key, Value>*----->return the node
 ***********************************************************************************/
template <typename Key,typename Value>
FibonacciHeapNode<Key, Value>* FibonacciHeapClass<Key,Value>::Search(const Value& val)
{
    //if(m_nodeDic.find(val.currentVertex)!=m_nodeDic.end())
    //{
       // return m_nodeDic[val.currentVertex];
    //}
    //return NULL;
}

/************************************************************************************
 FunctionName:Destroy
 Description: recursivly destroy the heap
 Input:FibNode* node-------------------->node
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>:: Destroy(FibNode* node)
{
    if(node->m_child.size()==0)                 //if the node has no child, then delete the node
    {
        delete node;
        return;
    }
    typename unordered_set<FibNode*>::iterator it=node->m_child.begin();    //traverse the all children of the node, delete them
    for(;it!=node->m_child.end();it++)
    {
        Destroy(*it);
    }
}
/************************************************************************************
 FunctionName:GetSize
 Description: Get the total number of nodes in the tree
 Input:FibNode* node-------------------->node
 Output:void
 ***********************************************************************************/
template <typename Key,typename Value>
unsigned long FibonacciHeapClass<Key,Value>::GetSize()
{
    return -1;
    //return m_nodeDic.size();
}

/* ------------------The code below is just for testing----------------------------*/
template <typename Key,typename Value>
unsigned int FibonacciHeapClass<Key,Value>::TestGetSize()
{
    unsigned int number=0;
    typename list<FibNode*>::iterator it=m_root.begin();
    for(;it!=m_root.end();it++)
    {
        number=number+1;
        Helper(*it,&number);
    }
    return number;
}
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Helper(FibNode* node,unsigned int* number)
{
    
    typename unordered_set<FibNode*>::iterator it=node->m_child.begin();
    for(;it!=node->m_child.end();it++)
    {
        *number=*number+1;
        Helper((*it),number);
    }
}
template <typename Key,typename Value>
void FibonacciHeapClass<Key,Value>::Release()
{
    typename list<FibNode*>::iterator it=m_root.begin();
    for(;it!=m_root.end();it++)
    {
        Destroy(*it);
    }
    m_root.clear();
    //m_nodeDic.clear();
    delete m_nodeDic;
}
//
////explicitly declare the template class, to avoid link error when you want to use the separating compiling template.
template class FibonacciHeapClass<string,unsigned int>;
//template class FibonacciHeapClass<unsigned int,unsigned int>;









