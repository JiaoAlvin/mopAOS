function [AEI,GD,Inj,fHV,IGD] = getMOEAIndicators(filename)
%reads the csv values starting from the 2nd column
%filename must include path and extension
%EI is the epsilon indicator
%GD is the generational distnace
%HV is the hypervolume
%IGD is the inverted generational distance

data = csvread(filename,0,1);

%sometimes there are 0.0 values added to end, so get rid of them
data = data(:,sum(data,1)>0);

%get end of run indicator values
AEI = data(1,:);
Inj = data(2,:);
GD = data(3,:);
IGD = data(4,:);
fHV = data(7,:);
