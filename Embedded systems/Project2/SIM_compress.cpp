/* I have neither given nor received any unauthorized aid on
 this assignment_Haitang Wang */

// Input file name: original.txt; and compressed.txt
 
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>

using namespace std;

// functions declarization 
void inatilize_oriread();
void difference(); // caluclate the number of difference
void compress_twod(); //identify two difference
void test_output();
void compress_f();
void compress_w(); //write to cout.txt
string compressd[100];
string originald[100];
string dictionary[8];
string comd[8];
string diff[100];
string coutd[100];
main(){
       
    int flag;
    int k =0;
    cout << "\n///////MENU///////// \n";
    cout << "\n1) 1 for compression \n";
    cout << "\n2) 2 for compression \n";
    cout << "\n\nPlease input your choice: \n";
    do
    {
         if (k>=1)
         {
         cout << "\nWrong input, please make your choice: ";
                  }
         cin >> flag;
         k++;
    } while (flag!=1 && flag!=2);
    
    if (flag==1)
    {
                inatilize_oriread();
                //compress_f();
                difference();
                compress_twod();
                compress_w();
                //test_output();
                }
    
    system("PAUSE");
    return 0;
}

void inatilize_oriread() {
     
     string line;
     ifstream myfile("original.txt");
     int flag =0;
     if(myfile.is_open())
     {
                         int i=1;
                         while(getline(myfile,line))
                         {
                               originald[i] = line;
                               i++;
                               //cout <<"\n" << line;
                                                    }
                         myfile.close();
                         }
     else cout << "\nUnable to open file\n";
     
     // read dictionary 
      ifstream myfile1("compressed.txt");
      if(myfile1.is_open())
      {
                           int i =0;
                           while(getline(myfile1,line))
                           {
                           if (flag ==1 && i<8)
                           {
                                    dictionary[i] = line;
                                   // dic[i] = atoi(line.c_str());
                                    i++;
                                    }
                           if (!line.find("xx")){
                           flag = 1;}                           
                           }  
                                                   
                           myfile1.close();
      }
      else cout << "\nUnable to open file\n";     
                 
}

void difference(){
     
     int did[8]={0,0,0,0,0,0,0,0};
     int con[32];
     string comd[8]={"000","001","010","011","100","101","110","111"};
     string line;
     //cout << "\n"<<"Difference Test\n";
     // find difference first
     for (int i =0; i <100; i++)
     {
         if(originald[i].size()!=0)
         {
         line = originald[i];
        // cout <<"\nLine1: "<< line<<"\n";
         for (int k =0; k<8; k++)
         {
             string line2 = dictionary[k];
            // cout << "\nLine2: "<< line2<<"\n";
             for (int j =0; j<32; j++)
             {
                 string b1= line.substr(j,1);
                 string b2= line2.substr(j,1);
                 //cout << "b1: "<< b1<< "\t";
                 //cout << "b2: " << b2 <<"\n";
                 if(b1 !=b2 )
                 {
                       did[k]++;  
                       }
             }
         }
         
         int minc=100;
         int flag = 100;
         //cout << "Diffence count: \t";
         
         for (int k =0; k<8; k++)
         {
         //    cout << did[k] << "\t";
             if(minc > did[k])
             {
                     minc = did[k];
                     flag =k;
                     }
         did[k] = 0;    
         }
        //cout << "\n Minimum difference: " << minc << "\t flag: " << flag <<"\n"; 
        if (minc >=3)
         {
         diff[i].clear();
         diff[i].append("11");
         diff[i].append(originald[i]);
         //minc=100;
         }
         if (minc ==2)
         {
                  diff[i].clear();
                  diff[i].append("x");
                  stringstream ss;
                  ss << flag;
                  string index;
                  ss >> index;
                  diff[i].append(index);
                  diff[i].append("x ");
                  diff[i].append(originald[i]);
                  //minc =100;
                  }
         if (minc ==1)
         {
                  diff[i].clear();
                  diff[i].append("11");
                  diff[i].append(originald[i]);
                  }
         if (minc ==0)
         {
                  diff[i].clear();
                  diff[i].append("00");
                  diff[i].append(comd[flag]);
                  //minc=100;
                  }
          
         }
     }  
     }
void compress_twod(){
     string comd[8]={"000","001","010","011","100","101","110","111"};
     string line;
     for (int i =0; i <100; i++)
     {
         if (diff[i].size()!=0 && (!diff[i].find("x")))
         {
                  line = diff[i];
                  int flag;
                  int num =0;
                  size_t s1 = line.find("x");
                  size_t s2 = line.find("x ");
                  string ss = line.substr(s1+1,s2-1);
                  line = originald[i];
                  //cout<< "\nWhat is Ss: \t" << ss<< "\t"<< s1<<"\t"<<s2;
                  flag = atoi(ss.c_str());
                 // cout << "\nflag: \t"<< flag<<"";
                  string line2 = dictionary[flag];
                  int dif[2];// identify its location
                  
                  //2 bit consecutive mismatches;
                  //cout << "\nCan you see me";
                  for (int j=0; j<32; j++)
                  {
                      string b1=line.substr(j,1);
                      string b2 = line2.substr(j,1);
                      if(b1 != b2)
                      {
                            num++;
                            if (num==1)
                            dif[0]=j;
                            if (num==2)
                            dif[1]=j;
                            }
                  }
                 // cout << "\nDIf: \t" << dif[0] << "\t" << dif[1];
                  if (dif[1]-dif[0]==1)
                  {
                             diff[i].clear();
                             diff[i].append("01");
                             dif[0]=dif[0];
                             char sss[5];
                             itoa(dif[0],sss,2);
                             diff[i].append(sss);
                             //diff[i].append(" ");
                             diff[i].append(comd[flag]);
                  }
                  else
     
                 {

                      //cout << "\ndif: \t" << dif[0] << "\t"<< dif[1];
                      char s11[5];
                      char s22[5];
                      itoa(dif[0],s11,2);
                      dif[1] = dif[1];
                      itoa(dif[1],s22,2);
                      diff[i].clear();
                      diff[i].append("10");
                      char s33[5];
                      //s33.append(s11);
                      if(strlen(s11)==4)
                      diff[i].append("0");
                      //cout << "Test: "<< strlen(s11)<< "\t" << strlen(s22)<<"\t";
                      //cout << "char: " << s33 << "\t" << s22;
                      diff[i].append(s11);
                      //diff[i].append(" ");
                      if(strlen(s22)==4)
                      diff[i].append("0");
                      diff[i].append(s22);
                      //diff[i].append(" ");
                      diff[i].append(comd[flag]);
                      
                  }
         }
     
     }
}     
     

void test_output() {
     
     cout << "\nOriginal:\n"; 
     for (int i =0; i<100; i++)
     {
         if(originald[i].size()!=0)
         cout << originald[i]<< "\n";
     } 
     cout << "\nDictionary:\n";
     for (int i= 0; i<8; i++)     
     {
         cout << dictionary[i] << "\n";
     } 
     
     cout << "\nDifference:\n";
     for (int i =0; i<100;i++)
     {
         if(diff[i].size()!=0)
         cout << diff[i]<<"\n";
     }    
}

void compress_w(){
     string combine;
     int j=0;
     for (int i=0; i<100; i++)
     if (diff[i].size()!=0)
     {
                           //if(coutd[j].size()<=32)
                           //{
                                                  combine.append(diff[i]);
                                                 // }
          
                          
     
                           }
     int padd,col;
     //string coutd[100];
     col = combine.size()%32;
     padd = 32-col;
     for (int i=0; i<padd;i++)
     combine.append("1"); 
     //cout << "length of string:\t" << combine.size();
     col = combine.size()/32;
     ofstream outFile("cout.txt");
     for (int i=1; i<=col; i++)
     {
     int s1 = 32*(i-1);
     int s2 = 32*(i);
     //cout << s1 << "\t" << s2;
     string line = combine.substr(s1,32);
     //cout << "\t" << line.size()<< "\n";
     outFile << line;
     line.clear();
     outFile << "\n";
     }
     outFile << "xxxx\n";
     
     for (int i= 0; i<8; i++)     
     {
         outFile << dictionary[i] << "\n";
     } 
     outFile.close();
      
     }


