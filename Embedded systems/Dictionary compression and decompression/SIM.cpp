/* I have neither given nor received any unauthorized aid on
 this assignment_Haitang Wang */

// Input file name: original.txt; and compressed.txt
 
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <bitset>
using namespace std;

// functions declarization 
void inatilize_oriread();
void inatilize_decomread();
void difference(); // caluclate the number of difference
void compress_twod(); //identify two difference
void test_output();
void compress_f();
void compress_w(); //write to cout.txt
void decompress_w();// write to dout.txt
void test_dout();
void decompress();
void reverse_dict();// matching dictionary 
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
    cout << "\n2) 2 for decompression \n";
    cout << "\n\nPlease input your choice: \n";
    //cout << "\nPlese only input 1 or 2.\n";
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
    if (flag ==2)
    {
                inatilize_decomread();
                decompress();
                reverse_dict();
                decompress_w();
                //test_dout();
             }
   // system("PAUSE");
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
                             int stc = 5-strlen(sss);
                             while (stc > 0)
                             {
                                        diff[i].append("0");
                                        stc = stc-1;
                                        }
                             diff[i].append(sss);
                             //diff[i].append(" ");
                             //cout << "\nsss test: " << i << "\t" << strlen(sss);
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
                      
                      int stc2 = 5 - strlen(s11);
                      while (stc2>0)
                      {diff[i].append("0");
                      stc2 = stc2 -1;
                      
                      }
                      //diff[i].append("0");
                      //cout << "Test: "<< strlen(s11)<< "\t" << strlen(s22)<<"\t";
                      //cout << "char: " << s33 << "\t" << s22;
                      diff[i].append(s11);
                      //diff[i].append(" ");
                      int stc3 = 5 - strlen(s22);
                      while (stc3>0)
                      {
                      diff[i].append("0");
                      stc3 = stc3-1;
                      }
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
         cout << "\t" << i << "\t"<<diff[i]<<"\n";
     }    
}

void compress_w(){
     string combine;
     int j=0;
     for (int i=0; i<100; i++)
     if (diff[i].size()!=0)
     {
                           combine.append(diff[i]);
                           //cout << "\ntest\t" << diff[i];
                         //  cout << "\n size: " << i << "\t" << combine.size();
          }
          
     int padd,col;
     //string coutd[100];
     col = combine.size()%32;
    // cout << "\t outsize: \t" << combine.size();
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

void inatilize_decomread(){
     
     string line;
     int flag =0;
     
     // read dictionary 
      ifstream myfile1("compressed.txt");
      if(myfile1.is_open())
      {
                           int i =0;
                           while(getline(myfile1,line))
                           {
                                                       
                           if (flag ==0 && line.find("xx"))
                           {
                                    compressd[i] = line;
                                    i++;
                                    }
                           if (flag ==1 && i<8)
                           {
                                    dictionary[i] = line;
                                   // dic[i] = atoi(line.c_str());
                                    i++;
                                    }
                           if (!line.find("xx")){
                           flag = 1; 
                           i=0;
                           }                           
                           }  
                                                   
                           myfile1.close();
      }
      else cout << "\nUnable to open file\n";        
     }

void test_dout(){
     
     cout << "\nCompressed:\n"; 
     for (int i =0; i<100; i++)
     {
         if(compressd[i].size()!=0)
         cout << compressd[i]<< "\n";
     } 
     cout << "\nDictionary:\n";
     for (int i= 0; i<8; i++)     
     {
         cout << dictionary[i] << "\n";
     } 
     
     /*cout << "\nOriginal:\n";
     for (int i =0; i<100;i++)
     {
         if(originald[i].size()!=0)
         cout << i << "\t"<< originald[i]<<"\n"; */
         
     cout << "\nOriginal:\n";
     for (int i =0; i<100;i++)
     {
         if(diff[i].size()!=0)
         cout << i << "\t"<< diff[i]<<"\n";
     }    
     }

void decompress(){
     
     string ss; // used to read every first two elements
     string compp;
     int endc0, endc1,endc2,endc3;
     //int end1;
     int ss2;
     for (int i =0; i<100; i++)
     {
         if(compressd[i].size()!=0)
         compp.append(compressd[i]);
     }
     
     
     string scompp;
     scompp = compp;
     
     int j =0;
     ss = scompp.substr(0,2);
     
     //cout << "\ntest: \t" << scompp.size();
     //cout << "\nSS: \t" << ss;
     ss2 = atoi(ss.c_str());
     //if (ss2 == 0)
    // cout << "\nWe belive";
    // cout << "\ntrying to locate error: " << scompp.size();
     while (ss.size()!=0)
     {
           ss = scompp.substr(0,2);
           endc0 = scompp.size();
           if(scompp.size()>0){
           scompp = scompp.substr(2,endc0);
           ss2 = atoi(ss.c_str());
          // cout << "\ntest: \t" << j << "\t" << ss;
           if (ss2 == 0)
           {
                   diff[j].append("00");
                   diff[j].append(" ");
                   endc0 = scompp.size();
                   if (endc0 <3){
                             diff[j].clear();
                             break;
                             }
                   diff[j].append(scompp.substr(0,3));
                   scompp = scompp.substr(3, endc0);
                   j++;
                   
                   }
           if (ss2 ==1)
           {
                   diff[j].append("01");
                   diff[j].append(" ");
                   endc0 = scompp.size();
                   if (endc0<8){
                                diff[j].clear();
                                break;}
                   diff[j].append(scompp.substr(0,8));
                   scompp = scompp.substr(8, endc0);
                   j++;
                   }
           if (ss2 ==10)
           {
                   diff[j].append("10");
                   diff[j].append(" ");
                   endc0 = scompp.size();
                   if (endc0 <13){
                             diff[j].clear();
                             break;
                             }
                   diff[j].append(scompp.substr(0,13));
                   scompp = scompp.substr(13, endc0);  // mistake was wrong number
                   j++;
                   }
           if (ss2 ==11)
           {
                   diff[j].append("11");
                   diff[j].append(" ");
                   endc0 = scompp.size();
                   if (endc0 <32){
                             diff[j].clear();
                             break;}
                   diff[j].append(scompp.substr(0,32));
                   scompp = scompp.substr(32,endc0);
                   j++;
                   } 
                   }
            else
            break;       
           }

     }
     
void reverse_dict(){
     
     //matching compressed data to the dictionary
     string ss;
     int length;
     int i =0;
     
     //cout << "Test: \t"<< diff[0].size();
     string comd[8]= {"000","001","010","011","100","101","110","111"};
     
     while (diff[i].size()!=0 )
     {
         if (diff[i].size()<7)
         {
         ss = diff[i].substr(0,2); /// at here
         if (ss.compare("00")==0)
         {
         length = diff[i].size();
         diff[i] = diff[i].substr(3,length);
         //cout << "\nDIFF: \t" << diff[i];
         //originald[i] = diff[i];
         for (int j =0; j<8; j++)
         {
             //string str1= ss;
             if (diff[i].compare(comd[j])==0)
             {
                           originald[i].clear();
                           originald[i] = dictionary[j];
                           }
         }
     
         }
         }
         if (diff[i].size()> 7 && diff[i].size()<12)
         {
                           ss=diff[i].substr(0,2);
                           if(ss.compare("01")==0)
                           {
                                              length = diff[i].size();
                                              diff[i]= diff[i].substr(3,length);
                                              string location;
                                              int opp[5];
                                              
                                              length = diff[i].size();
                                              location = diff[i].substr(0,5);
                                              char str[100];
                                              
                                             // for (int k =0; k < location.size(); k++)
                                             char cstr[32];
                                             strcpy(cstr, location.c_str());
                                             cstr[6]=0;
                                             //delete [] cstr;
                                             //itoa(opp,cstr,10);
                                             //for (int k =0; k< strlen(cstr); k++)
                                             //{
                                             //    opp[k] = cstr[k]-'0';
                                             //}
                                             char *end;
                                            long int value = strtol(cstr,& end,2);
                                           //cout << "Location: \t" <<value;
                                                       
                                             //cout << "\nOPP: \t"<< value;
                                              
                                              string index;
                                              index = diff[i].substr(5,length);
                                              for (int j =0; j<8; j++)
                                              {
                                                  if(index.compare(comd[j])==0)
                                                  {
                                                       originald[i].clear();
                                                       string fff = dictionary[j];
                                                       //cout << "\nDIF: \t" << dictionary[j];
                                                       originald[i].append(dictionary[j].substr(0,value));
                                                       for (int kk =0; kk<2; kk++)
                                                       {
                                                           //cout << "\nOut:\t"<< fff[value+kk+1];
                                                           //string *addr;
                                                          // addr=fff[value+kk].c_str();
                                                           //cha = *addr;
                                                           //string cha;
                                                           int tt;
                                                           tt = fff[value+kk]-'0';
                                                           //cha.append(fff[value+kk]);
                                                           //int cch = atoi(cha.c_str());
                                                           if (tt==0)
                                                           {originald[i].append("1");
                                                            
                                                           }
                                                           else 
                                                           originald[i].append("0");
                                                           
                                                          // cout <<"\n fff: \t"<< fff[value+kk]<<"\t"; 
                                                       }
                                                       //originald[i].append(fff);
                                                       originald[i].append(dictionary[j].substr(value+2,32));
                                                      // originald[i].append("*");
                                                                               }
                                              }
                                              
                                              }
                                              
                           
                           
                           }
         
         if (diff[i].size()>12 && diff[i].size()< 20)
         {
                            ss=diff[i].substr(0,2);
                            if(ss.compare("10")==0)
                            {
                                          length = diff[i].size();
                                          diff[i] = diff[i].substr(3,length);
                                          string location1;
                                          string location2;
                                          location1 = diff[i].substr(0,5);
                                          location2 = diff[i].substr(5,5);
                                          
                                          //int value4; 
                                          //int value5;
                                         // char str[100];
                                          char cstr1[32];
                                          char cstr2[32];
                                          strcpy(cstr1, location1.c_str());
                                          strcpy(cstr2, location2.c_str());
                                          
                                          char *end;
                                          long int value4 = strtol(cstr1,& end,2);
                                          long int value5 = strtol(cstr2,& end,2);
                                          long valuem;
                                          valuem = value4;
                                          if(value4>value5)
                                          {
                                                      value4 = value5;
                                                      value5 = valuem;
                                                           }
                                          
                                          string index;
                                          index = diff[i].substr(10,3);
                                          diff[i] = diff[i].substr(3,length);
                                          for(int j=0; j<8; j++)
                                          {
                                                  if(index.compare(comd[j])==0)
                                                  {
                                                     originald[i].clear();
                                                     string fff3 = dictionary[j]; 
                                                    // cout << "\nDIC: \t" << dictionary[j];
                                                     originald[i].append(dictionary[j].substr(0,value4)); 
                                                     int fv4 = fff3[value4]-'0';;
                                                     int fv5 = fff3[value5]-'0';
                                                     //cout << "\nLocation\t: " << value4 << "\t" << value5;
                                                     if (fv4 ==0)
                                                     originald[i].append("1");
                                                     else
                                                     originald[i].append("0");
                                                     
                                                     originald[i].append(dictionary[j].substr(value4+1,value5-value4-1));
                                                     
                                                     if(fv5==0)
                                                     originald[i].append("1");
                                                     else
                                                     originald[i].append("0");
                                                     
                                                     originald[i].append(dictionary[j].substr(value5+1,32-value5));
                                                     //originald[i] = dictionary[j];
                                                     //originald[i].append("**");
                                                                               }
                                                  }     
                                               }
                            }
                            
         if (diff[i].size()> 20)
         {
                           ss = diff[i].substr(0,2);
                           originald[i] = diff[i].substr(3,32);
                           diff[i] = diff[i].substr(3,32);
                           }
         //cout << "\n" << i;
         i++;
     
     }
}

void decompress_w(){
     
     ofstream outFile("dout.txt");
     for (int i=0; i<100; i++)
     if (originald[i].size()!=0)
     {
                           outFile<<originald[i];
                           outFile << "\n";
          }
     outFile.close();
}
