%Haitang Wang

%  generating binary data; encryption; decryption; alarm system

function MedicalSystem()

clc; clear all; close all;

% initialize the system
% count = 200;
key = key_generation(); % saved in insulin pump

data = data_generation(); % data test

disp('Original data input data package:');
data.da

% data encription
trans = encription(data);
disp('Transmitted data after encryption:');
trans.da
% data decription
decr = data_decryption(trans,data);
alarm(decr,data)
    function key = key_generation()
        key.l1 = '101011111111010101000111111001001100'; % 000
        key.l2 = '101111111001001000111011010111000001'; % 001
        key.l3 = '101111001100101100011101101011100011'; % 010
        key.l4 = '011101001010101000000110100000101111'; % 011
        key.l5 = '010101100110111001111000101100000000'; % 100
        key.l6 = '100100001011010000001001010100100110'; % 101
        key.l7 = '010101111100100100001101001001001100'; % 110
        key.l8 = '011011010110111111100101001110000001'; % 111
    end

    function device = data_generation()
        device.type = '0000';% which is a fixed value;
        
        device.pin = '110110001001000110010100001110110100';
        %         device.information = dec2bin(rand(1)*4096); % denote the button information;
        device.information = '110011111000';

%         count = dec2hex(count);
%         if length(count) ==1
%             count1 = hex2bin(count);
%             x1 = size(count1);
%             for i = 0 : (6-x1)
%                 count1 = strcat('0',count1);
%             end
%             count2 = '000000';
%         else
% %             count1 = hex2bin(count(1));
% %             count2 = hex2bin(count(2));     
%            count
%            size(count)
%         end
        device.count = '001000001111'; %initial value
        device.CRC = '000000000000';
        device.C = '0101';
        device.da = strcat(device.type,device.pin,device.information,device.count,device.CRC,device.C);
    end
    
    function trans = encription(device)
        trans.type = device.da(1:4);
        PIN = device.da(5:40);
        count1 = device.da(41:52);
        cou1 = device.da(53:58);
        cou1 = bin2dec(cou1);
        cou1 = dec2hex(cou1);
        cou2 = device.da(59:64);
        cou2 = bin2dec(cou2);
        cou2 = dec2hex(cou2);
        count2 = strcat(cou1,cou2);
        count2 = hex2dec(count2);
        countv = mod(count2,8);
        I1 = dec2bin(countv,3);
        I0 = '11111';
        I2 = '11111';
        I3 = mod(countv,6)+1;
        I5 = 6 - I3;
        I3 = dec2bin(I3,3);
        I4 = '11111';
        I5 = dec2bin(I5,3);
        I6 = dec2bin(rand(1)*4096,12);
        trans.pin = strcat(I0,I1,I2,I3,I4,I5,I6);
        trans.information = device.information;
        count3 = device.count;
        cou31 = count3(1:6);
        cou = bin2dec(I3);
        cou33 = cou31(1:cou-1);
        if (cou31(cou) == '0')
            cou33 = strcat(cou31(1:cou-1),'1');
        else
            cou33 = strcat(cou33,'0');
        end
        cou33 = strcat(cou33,cou31(cou+1:6));
        cou31 = cou33;
  
        cou32 = count3(7:12);
        cou = bin2dec(I5);
        cou34 = cou32;
        cou32 = cou34(1:cou-1);
        
        if (cou34(cou) == '1')
            cou32 = strcat(cou34(1:cou-1),'0');
        else
            cou32 = strcat(cou34(1:cou-1),'1');
        end
        cou32 = strcat(cou32,cou34(cou+1:6));
        trans.count = strcat(cou31,cou32);
        trans.CRC = device.CRC;
        trans.C = device.C;
        trans.da = strcat(trans.type,trans.pin,trans.information,trans.count,trans.CRC,trans.C);
    end

    function decr = data_decryption(trans,data)
        decr.type = trans.type;
        trans.pin;
        I0 = trans.pin(1:5);
        if I0 == ('11111')
            I1 = trans.pin(6:8);
        end
        I2 = trans.pin(9:13);
        if I2 == '11111'
            I3 = trans.pin(14:16);
        end
        I4 = trans.pin(17:21);
        if I4 == '11111'
            I5 = trans.pin(22: 24);
        end
        decr.count = data.count;
        
    end

    function [] = alarm(decr,data)
        if decr.count == data.count
            disp('Normal');
        else
            disp('Abnormal');
        end
    end
end