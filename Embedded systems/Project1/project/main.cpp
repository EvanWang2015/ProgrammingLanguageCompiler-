#include <iostream>
#include <fstream>
#include <string>
#include <sstream>

using namespace std;

// Functions declarition
void inatilize_read(string *, string *, string *);
void result_print();
void decode();
void read();
void issue(); // ISSUE1 or ISSUE2
void asu(); // ADD or SUB calculation
void write();
void addr(); // Address buffer
void load();
// Variables declarition
string INM[8];
string INB;
string RGF[8];
string DAM[8];
string AIB;
string ADB;
string REB[2];
string LIB;
int    RGFD[8];
int    DAMD[8];

main () 
{
    
  string *INMP, *RGFP, *DAMP;
  int step = 0; // used as a flag to print choice
  
  // pointer initialization 
  INMP = &INM[0];
  RGFP = &RGF[0];
  DAMP = &DAM[0];
  
  inatilize_read(INMP, RGFP, DAMP);
  
  switch(step){
               case 0: // load data from inputs
                    {
                    cout << "STEP 0:  \n\n";
                    result_print();
                    step++;}
               case 1:  // Decode, moving the first instruction to the instruction buffer
                    {
                     cout << "STEP 1: \n\n"; 
                     decode(); 
                     read();
                     result_print();     
                            }
               case 2:
                    {
                     cout << "STEP 2: \n\n";
                     issue();
                     decode();
                     read();
                     result_print();
                     }                   
               case 3:  // 
                    {
                     cout << "STEP 3: \n\n";
                     asu();
                     issue();
                     decode();
                     read();
                     result_print();
                     }
               case 4: 
                    {
                     cout << "STEP 4: \n\n";
                     write();
                     addr();
                     issue();
                     decode();
                     read();
                     result_print();
                    }
               case 5:
                    {
                    cout << "STEP 5: \n\n";
                    asu();
                    load();
                    issue();
                    decode();
                    read();
                    result_print();                    
                                    }
               case 6:
                    {
                    cout << "STEP 6: \n\n";
                    write();
                    addr();
                    issue();
                    result_print();
                                    }
               case 7:
                    {
                    cout << "STEP 7: \n\n";
                    write();
                    asu();
                    load();
                    result_print();
                                    }
               case 8:
                    {
                    cout << "STEP 8: \n\n";
                    write();
                    
                    result_print();
                                    }
               case 9:
                    {
                    cout << "STEP 9: \n\n";
                    write();
                    result_print();
                                    }
               default:
                       break;
                       }
  
  system("PAUSE");
  return 0;
  
}
    
void inatilize_read(string *INMP, string *RGFP, string *DAMP) {
     string line;
     ifstream myfile ("instructions.txt");
     int value1;
     string mid;
     if (myfile.is_open())
     {
                          while ( getline (myfile,line) )
                          {
                                *INMP = line;
                                INMP++;
                                }
                          myfile.close();
     }
     else cout << "Unable to open file"; 
     
     ifstream myfile1("registers.txt");
     if (myfile1.is_open())
     { 
                           while (getline(myfile1, *RGFP))
                           RGFP++;
                           myfile1.close();
                           }
     else cout << "Uable to open file";
     
     for (int i =0; i<8; i++)
     {
         if (!RGF[i].empty())
         {
                           size_t s1 = RGF[i].find("R");
                           size_t s2 = RGF[i].find(",");
                           size_t s3 = RGF[i].find(">");
                           mid = RGF[i].substr(s1+1,s2-1);
                           //cout << "REF::: " << RGF[i]<< "MID:::"<< mid<< "\n";
                           value1 = atoi(mid.c_str());
                           RGFD[value1] = atoi(RGF[i].substr(s2+1,s3-1).c_str());
                           //cout << "RGFD:::" << RGFD[value1] << "\n"; 
                           }
     }
     
     ifstream myfile2("datamemory.txt");
     if (myfile2.is_open())
     { 
                           while (getline(myfile2, *DAMP))
                           DAMP++;
                           }
     else cout << "Uable to open file";
     
     myfile2.close();
     // save data to DAM in int format
     for (int i =0; i<8; i++)
     {
         if (!DAM[i].empty())
         {
                           size_t s1 = DAM[i].find("<");
                           size_t s2 = DAM[i].find(",");
                           size_t s3 = DAM[i].find(">");
                           mid = DAM[i].substr(s1+1,s2-1);
                           //cout << "DAM::: " << DAM[i]<< "MID:::"<< mid<< "\n";
                           value1 = atoi(mid.c_str())-1;
                           DAMD[value1] = atoi(DAM[i].substr(s2+1,s3-1).c_str()); 
                           //cout << "RGFD:::" << DAMD[value1] << "\n"; 
                           }
     }
     
}
 
void decode(){
     // load data from instruction memory to Instruction buffer (INB)
     INB = INM[0];
     for (int i =0; i<7; i++)
     {
         INM[i] = INM[i+1];
         }
     }
void read(){
     string opp[3];
     string medi[4];
     size_t s0 = INB.find("<");
     size_t s1 = INB.find(",");
     medi[3] = INB.substr(s0,s1);
     size_t s2 = INB.find(">");
     opp[0] = INB.substr(s1+1,s2);
     s1 = opp[0].find(",");
     s2 = opp[0].find(">");
     medi[0] = opp[0].substr(0,s1);
     opp[1] = opp[0].substr(s1+1,s2);
     s1 = opp[1].find(",");
     s2 = opp[1].find(">");
     medi[1] = opp[1].substr(0,s1);
     opp[2] = opp[1].substr(s1+1,s2);
     s1 = opp[2].find(",");
     s2 = opp[2].find(">");
     medi[2] = opp[2].substr(s1+1,s2);
     //cout << "medi0: " << medi[0] << "\n";
     //cout << "medi1: " << medi[1] << "\n";
     //cout << "medi2: " << medi[2] << "\n"; 
     for (int i =0; i<2; i++)
     {
         if (medi[i+1].find("R") ==0)
         {
                                 //medi[i+1] = medi[i+1].erase(medi[i+1].begin()+1);
                                 medi[i+1] = medi[i+1].append(",");
                                 size_t s3 = medi[i+1].find("R");
                                 size_t s4 = medi[i+1].find(",");
                                 string mstr = medi[i+1].substr(s3+1,s4-1);
                                 medi[i+1].clear();
                                 //medi[i+1] = mstr;
                                 //cout << "medi::" << i+1 << " " << medi[i+1] << "\n";
                                 int value1 = RGFD[atoi(mstr.c_str())];
                                 stringstream ss;
                                 ss << value1;
                                 ss >> medi[i+1];
                                // cout << "MEDI:::i+1 " << i+1 <<" "<< medi[i+1] << " ";
                                 //medi[i+1] = ss.str() ;
                                 }
                                 }
         
     INB.clear();
     INB.append(medi[3]);
     INB.append(",");
     INB.append(medi[0]);
     INB.append(",");
     INB.append(medi[1]);
     INB.append(",");
     INB.append(medi[2]);
     INB.append(">");    
     }

void issue(){
     // load insturction to from buffer to LIB or AIB arrording to its operation
     // commented srpits were used for testing

     if (INB.find("ADD") ==1 || INB.find("SUB") ==1 )
     {
         // cout << "ADD or SUB is found \n";
         //cout << INB[1].find("ADD")<< "  \n" << INB[1].find("SUB");
          AIB = INB;
          }
     else
          {
         // cout << "LD is found \n";
          LIB = INB;
          }
     INB.clear();
    // cout <<"\nINB[0]: " << str<< "\n"<< LIB << "" << AIB << "\n\n\n"; 
     
     
     }
     
     
void addr(){
     string opps[4];
     size_t s0 = LIB.find("<");
     size_t s1 = LIB.find(",");
     size_t s2 = LIB.find(">");
     opps[0]=LIB.substr(s0+1,s1);
     string mids = LIB.substr(s1+1,s2);
     s1 = mids.find(",");
     s2 = mids.find(">");
     opps[1] = mids.substr(0,s1);
     string mid2s = mids.substr(s1+1,s2);
     s1 = mid2s.find(",");
     s2 = mid2s.find(">");
     opps[2] = mid2s.substr(0,s1);
     mids.clear();
     mids = mid2s.substr(s1+1,s2);
     s1 = mids.find(",");
     s2 = mids.find(">");
     opps[3] = mids.substr(s1+1,s2);
     //cout << "OPPS:::\n"  << " " << opps[3] << "\n";
     int opp[3];
     for (int i =0; i<2; i++)
     {
         if (opps[i+2].find("R")==0)
         {
                                    opps[i+2].append(",");
                                    size_t s3 = opps[i+2].find("R");
                                    size_t s4 = opps[i+2].find(",");
                                    string mstr = opps[i+1].substr(s1+1,s2-1);
                                    opp[i+1] = RGFD[atoi(mstr.c_str())-1];
                                  }
         else
         opp[i+1] = atoi(opps[i+2].c_str());
     }
     opp[0] = opp[1] + opp[2];
     LIB.clear();
     ADB.append("<");
     ADB.append(opps[1]);
     ADB.append(",");
     stringstream ssa;
     ssa << opp[0];
     string opps1;        
     ssa >> opps1;
     ADB.append(opps1);
     ADB.append(">");
     
     } 

void load() {
     
     //Load data from memory and save to REB (Result Buffer)
     string opps[2];
     
     if (ADB.size()!=0)
     {
         size_t s1 = ADB.find("<");
         size_t s2 = ADB.find(",");
         size_t s3 = ADB.find(">");
         opps[0] = ADB.substr(s1+1,s2-1);
         opps[1] = ADB.substr(s2+1,s3-1);
         int value;
         value =  DAMD[atoi(opps[1].c_str())-1];
         stringstream ss1;
         ss1 << value;
         string opps1;        
         ss1 >> opps1;
         string REB1;
         REB1.append("<");
         REB1.append(opps[0]);
         REB1.append(",");
         REB1.append(opps1);
         REB1.append(">");
         ADB.clear();
         if (REB[0].size()!=0)
         {
                           //string REB2 = REB[0];
                           REB[1] = REB[0];
                           REB[0].clear();
                           REB[0].append(REB1);
                           //REB.append(" ");
                           //REB.append(REB2);
                     
                       }
         else
         REB[0] = REB1;
         }
     else
     cout << "\n ADB is empty at the moment";
     
     
     
     }
     
void asu() {
     // load data from register and perform ASU calcuation 
     //string regc[8] = {"R0", "R1","R2","R3","R4","R5","R6","R7"};
    // cout << AIB.size()<< AIB<<"\n";
     if (AIB.size()!=0)
     {
          int opp[3];
          string opps[4];
          string temp;
          size_t s0 = AIB.find("<");
          size_t s1 = AIB.find(",");
          size_t s2 = AIB.find(">");
          opps[0] = AIB.substr(s0+1,s1-1); // ADD or SUB
          temp = AIB.substr(s1+1,s2);
          s1 = temp.find(","); 
          s2 = temp.find(">");
          opps[1] = temp.substr(0,s1);  //Register
          string temp2 = temp.substr(s1+1,s2);
          s1 = temp2.find(",");
          s2 = temp2.find(">");
          opps[2] = temp2.substr(0,s1);
          opp[1] = atoi(opps[2].c_str());
          temp.clear();
          temp = temp2.substr(s1+1,s2);
          s2 = temp.find(">");
          opps[3] = temp.substr(0,s2);
          opp[2] = atoi(opps[3].c_str());
          if (opps[0] == "ADD")
          opp[0] = opp[1] + opp[2];
          else
          opp[0] = opp[1] - opp[2];
          int value = opp[0];
          stringstream ss;
          ss << value;        
          ss >> opps[2];
          //opps[2] = std::to_string(6);
          REB[0].clear();
          REB[0].append("<");
          REB[0].append(opps[1]);
          REB[0].append(",");
          //REB.insert(5,opps[1]);
          REB[0].append(opps[2]);
          REB[0].append(">");
          
          AIB.clear();
          }
    else 
          cout << "Wait for instruction \n";      
     }

void write(){
     // write value from REB to RGF
     string opps[2];
     size_t s1 = REB[0].find("R");
     size_t s2 = REB[0].find(",");
     size_t s3 = REB[0].find(">");
     opps[0] = REB[0].substr(s1+1,s2-1);
     opps[1] = REB[0].substr(s2+1,s3-1);
     int value = atoi(opps[0].c_str());
     RGFD[value] = atoi(opps[1].c_str());
     //cout << " RGF " << RGFD[value]<< "\n";
     //update RGF strings
    // cout << "RGFD \n";
     for (int i =0; i<8; i++)
     {
         RGF[i].clear();
        // cout << "" << RGFD[i] << " ";
         }
     
     int j=0;
     for (int i =0; i<8; i++)
     {
         if (RGFD[i]!=NULL)
         {
                           RGF[j].append("<R");
                           stringstream ss;
                           ss << i;
                           string s1;
                           ss >> s1;
                           string s2;
                           stringstream ss1;
                           RGF[j].append(s1);
                           RGF[j].append(",");
                           ss1 << RGFD[i];
                           ss1 >> s2;
                           RGF[j].append(s2);
                           RGF[j].append(">");
                           j++;
                           }
     }
     REB[0].clear();
     
     if (REB[1].size()!=0)
     {
                          //size_t s4 = REB[1].find("<");
                          //size_t s5 = REB[1].find(">");
                          REB[0] = REB[1];
                          //REB[0] = REB[1].substr(s4,s5);
                          REB[1].clear();
                          }
     
     }

   
              
void result_print (){
                   
     cout << "INM:  ";
     for (int i=0; i<8; i++) {
     cout << INM[i]<< " ";
     }
                    
     cout << "\n";
     cout << "INB:  ";
     cout << INB<< " ";
                    
     cout << "\n";
     cout << "AIB:  ";
     //for (int i = 0; i < 8; i++)
     cout << AIB<< " ";
                    
     cout << "\n";
     cout << "LIB:  ";
    // for (int i = 0; i < 8; i++)
     cout << LIB<< " ";
                    
     cout << "\n";
     cout << "ADB:  ";
     //for (int i = 0; i < 8; i++)
     cout << ADB<< " ";
                    
     cout << "\n";
     cout << "REB:  ";
     for (int i = 0; i < 2; i++)
     cout <<REB[i]<< " ";
                    
     cout << "\n";
     cout << "RGF:  ";
     for (int i = 0; i < 8; i++)
     cout << RGF[i]<< " ";
                    
     cout << "\n";
     cout << "DAM:  ";
     for (int i =0; i<8; i++)
     cout << DAM[i]<< " ";
                    
     cout << "\n\n";
     }

  
