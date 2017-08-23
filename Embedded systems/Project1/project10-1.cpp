#include <iostream>
#include <fstream>
#include <string>
#include <sstream>

using namespace std;

// Functions declarition
void inatilize_read(string *, string *, string *);
void result_print();
void decode();
void issue(); // ISSUE1 or ISSUE2
void asu(); // ADD or SUB calculation

// Variables declarition
string INM[8];
string INB[8];
string RGF[8];
string DAM[8];
string AIB;
string ADB[8];
string REB;
string LIB;
int    RGFD[8];
int    REBD;
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
                     result_print();     
                            }
               case 2:
                    {
                     cout << "STEP 2: \n\n";
                     issue();
                     decode();
                     result_print();
                     }                   
               case 3:  // 
                    {
                     cout << "STEP 3: \n\n";
                     //asu();
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
                           value1 = atoi(mid.c_str())-1;
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
     INB[0] = INM[0];
     for (int i =0; i<7; i++)
     {
         INM[i] = INM[i+1];
         }
     }

void issue(){
     // load insturction to from buffer to LIB or AIB arrording to its operation
     // commented srpits were used for testing

     if (INB[0].find("ADD") ==1 || INB[0].find("SUB") ==1 )
     {
         // cout << "ADD or SUB is found \n";
         //cout << INB[1].find("ADD")<< "  \n" << INB[1].find("SUB");
          AIB = INB[0];
          }
     else
          {
         // cout << "LD is found \n";
          LIB = INB[0];
          }
     
    // cout <<"\nINB[0]: " << str<< "\n"<< LIB << "" << AIB << "\n\n\n"; 
     
     
     }
     
void asu() {
     // load data from register and perform ASU calcuation 
     int temp;
     string opp[3];
     int oppm[3];
     string regc[8] = {"R0", "R1","R2","R3","R4","R5","R6","R7"};
    // cout << "\nAIB: ?EMPTY()";

    // cout << AIB.size()<< AIB<<"\n";
     if (AIB.size()!=0)
     {
          size_t s1 = AIB.find(",");
          size_t s2 = AIB.find(",");
          size_t s3 = AIB.find(",");
          size_t s4 = AIB.find(">");
          opp[0] = AIB.substr(s1+1,s2-1);
          opp[1] = AIB.substr(s2+1,s3-1);
          opp[2] = AIB.substr(s3+1,s4-1);
          for (int i=0; i<3; i++)
          {
              if (opp[i].find("R") == 1)
              {      
                 for (int j =0; j<8; j++)
                 {
                     if ( opp[i].compare(regc[j])==0)
                         
                         oppm[i] = RGFD[j];// stop at here
    
                 }
                 }
                 
              else
              oppm[i] =   atoi(opp[i].c_str());                             
              }
          }
          if(AIB.find("ADD") == 1)
          temp = oppm[1] + oppm[2];
          else
          temp = oppm[1] - oppm[2];
          
          cout << "\n\n TEMP: " << temp << " " << oppm[0] << "\n";
          REB = "R"+string(itoa(oppm[0]))+","+string(itoa(temp));
          cout << "REB::" << REB<< "\n";
          
     }

         
void result_print (){
                   
     cout << "INM:  ";
     for (int i=0; i<8; i++) 
     cout << INM[i]<< " ";
                    
     cout << "\n";
     cout << "INB:  ";
     for (int i = 0; i < 8; i++)
     cout << INB[i]<< " ";
                    
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
     for (int i = 0; i < 8; i++)
     cout << ADB[i]<< " ";
                    
     cout << "\n";
     cout << "REB:  ";
     for (int i = 0; i < 8; i++)
     cout << REB[i]<< " ";
                    
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

  
