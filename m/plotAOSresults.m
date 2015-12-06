%plots the boxplots of each UF1-10 problem and the IGD, fast hypervolume
%(jmetal) and the additive epsilon values for each algorithm

% problemName = {'UF1_','UF2','UF3','UF4','UF5','UF6','UF7','UF8','UF9','UF10'};
problemName = {'UF1_','UF2','UF3','UF4','UF5','UF6','UF7'};
% problemName = {'DTLZ1_','DTLZ2_','DTLZ3_','DTLZ4_','DTLZ7_',};
selectors = {'Probability','Adaptive'};
selectorShort = {'PM','AP'};
creditDef = {'ParentDec','Neighbor','DecompositionContribution','Parent','OffspringParetoFront','OffspringEArchive','ParetoFrontContribution','EArchiveContribution','OPa_BIR2PARENT','OPop_BIR2PARETOFRONT','OPop_BIR2ARCHIVE','CNI_BIR2PARETOFRONT','CNI_BIR2ARCHIVE'};
creditShort = {'OP-De','SI-R2','CS-R2','OP-Do','SI-Do-PF','SI-Do-A','CS-Do-PF','CS-Do-A','OP-R2','SI-R2-PF','SI-R2-A','CS-R2-PF','CS-R2-A'};
% creditDef = { 'OPa_BIR2PARENT','OPop_BIR2PARETOFRONT','OPop_BIR2ARCHIVE','CNI_BIR2PARETOFRONT','CNI_BIR2ARCHIVE'};
% creditShort = {'OPaR2','OPopPFR2','OPopEAR2','CPFR2','CEAR2'};
% creditDef = {'_'};
% creditShort= {'_'};

path = '/Users/nozomihitomi/Dropbox/MOHEA/';
% path = 'C:\Users\SEAK2\Nozomi\MOHEA\';
res_path =strcat(path,'mRes6opsInjection');
% res_path = '/Users/nozomihitomi/Desktop/untitled folder';

b = length(selectors)*length(creditDef);

h1 = figure(1); %IGD
h2 = figure(2); %fHV
h3 = figure(3); %AEI
% h4 = figure(4); %# injections

%box plot colors for benchmarks
boxColors = 'rkm';

for i=1:length(problemName)
    probName = problemName{i};
    [benchmarkDataIGD,label_names] = getBenchmarkVals(path,probName,'IGD');
    [benchmarkDatafHV,~] = getBenchmarkVals(path,probName,'fHV');
    [benchmarkDataAEI,~] = getBenchmarkVals(path,probName,'AEI');
    [a,c] = size(benchmarkDataIGD);

    dataIGD = cat(2,benchmarkDataIGD,zeros(a,b));
    datafHV = cat(2,benchmarkDatafHV,zeros(a,b));
    dataAEI = cat(2,benchmarkDataAEI,zeros(a,b));
%     dataInj = zeros(a,b);
    
    label_names_IGD=label_names;
    label_names_fHV=label_names;
    label_names_AEI=label_names;
    
    for j=1:length(selectors)
        for k=1:length(creditDef)
            c = c+1;
            file = strcat(res_path,filesep,probName,'_',selectors{j},'_',creditDef{k},'.mat');
            load(file,'res'); %assume that the reults stored in vairable named res
            dataIGD(:,c) = res.IGD;
            datafHV(:,c) = res.fHV;
            dataAEI(:,c) = res.AEI;
%             dataInj(:,c-size(benchmarkDataIGD,2)) = res.Inj;
            [p,sig] = runMWUsignificance(path,selectors{j},creditDef{k},'Random',probName);
            extra = '';
            if sig.IGD==1
                extra = '+';
            elseif sig.IGD==-1
                extra = '-'; 
            end
            label_names_IGD = [label_names_IGD,strcat(selectorShort{j},'_',creditShort{k},extra)]; %concats the labels
            extra = '';
            if sig.fHV==1
                extra = '+';
            elseif sig.fHV==-1
                extra = '-'; 
            end
            label_names_fHV = {label_names_fHV{:},strcat(selectorShort{j},'_',creditShort{k},extra)}; %concats the labels
            extra = '';
            if sig.AEI==1
                extra = '+';
            elseif sig.AEI==-1
                extra = '-'; 
            end
            label_names_AEI = {label_names_AEI{:},strcat(selectorShort{j},'_',creditShort{k},extra)}; %concats the labels
            boxColors = [boxColors,'b'];
        end
    end
    
    figure(h1)
    subplot(2,5,i);
    boxplot(dataIGD,label_names_IGD,'labelorientation','inline','colors',boxColors,'plotstyle','compact')
    title(probName)
    
    figure(h2)
    subplot(2,5,i);
    boxplot(datafHV,label_names_fHV,'labelorientation','inline','colors',boxColors,'plotstyle','compact')    
    title(probName)
    
    figure(h3)
    subplot(2,5,i);
    boxplot(dataAEI,label_names_AEI,'labelorientation','inline','colors',boxColors,'plotstyle','compact')
    title(probName)
    
%     figure(h4)
%     subplot(2,5,i);
%     boxplot(dataInj,{label_names{size(benchmarkDataIGD,2)+1:end}},'labelorientation','inline','colors',boxColors,'plotstyle','compact')
%     title(probName)
    hold off
end