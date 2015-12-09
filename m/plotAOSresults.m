%plots the boxplots of each UF1-10 problem and the IGD, fast hypervolume
%(jmetal) and the additive epsilon values for each algorithm

% problemName = {'UF1_','UF2','UF3','UF4','UF5','UF6','UF7','UF8','UF9','UF10'};
problemName = {'UF1_','UF2','UF3','UF4','UF5','UF6','UF7','UF8','UF9','UF10'};
% problemName = {'DTLZ1_','DTLZ2_','DTLZ3_','DTLZ4_','DTLZ7_',};
selectors = {'Probability','Adaptive'};
selectorShort = {'PM','AP'};
base = 'Do';

switch base
    case {'De'}
        creditDef = {'ParentDec','Neighbor','DecompositionContribution'};
        creditShort = {'OP-De','SI-De','CS-De'};
        mode = 'MOEAD';
    case{'Do'}
        creditDef = {'ParentDom','OffspringParetoFront','OffspringEArchive','ParetoFrontContribution','EArchiveContribution'};
        creditShort = {'OP-Do','SI-Do-PF','SI-Do-A','CS-Do-PF','CS-Do-A','OP-R2','SI-R2-PF','SI-R2-A','CS-R2-PF','CS-R2-A'};
        mode = 'eMOEA';
    case{'R2'}
%         creditDef = {'OPa_BIR2PARENT','OPop_BIR2PARETOFRONT','OPop_BIR2ARCHIVE','CNI_BIR2PARETOFRONT','CNI_BIR2ARCHIVE'};
%         creditShort = {'OP-R2','SI-R2-PF','SI-R2-A','CS-R2-PF','CS-R2-A'};
        creditDef = {'OPa_BIR2PARENT','OPop_BIR2ARCHIVE','CNI_BIR2PARETOFRONT','CNI_BIR2ARCHIVE'};
        creditShort = {'OP-R2','SI-R2-A','CS-R2-PF','CS-R2-A'};
        mode = 'eMOEA';
end

% creditDef = {'ParentDec','Neighbor','DecompositionContribution',...
%     'ParentDom','OffspringParetoFront','OffspringEArchive','ParetoFrontContribution','EArchiveContribution',...
%     'OPa_BIR2PARENT','OPop_BIR2PARETOFRONT','OPop_BIR2ARCHIVE','CNI_BIR2PARETOFRONT','CNI_BIR2ARCHIVE'};
% creditShort = {'OP-De','SI-De','CS-De',...
%     'OP-Do','SI-Do-PF','SI-Do-A','CS-Do-PF','CS-Do-A','OP-R2','SI-R2-PF','SI-R2-A','CS-R2-PF','CS-R2-A',...
%     'OP-R2','SI-R2-PF','SI-R2-A','CS-R2-PF','CS-R2-A'};

% path = '/Users/nozomihitomi/Dropbox/MOHEA';
path = 'C:\Users\SEAK2\Nozomi\MOHEA\';
res_path =strcat(path,filesep,'mRes6opsInjection');
% res_path = '/Users/nozomihitomi/Desktop/untitled folder';

b = length(selectors)*length(creditDef);

h1 = figure(1); %IGD
<<<<<<< HEAD
set(h1,'Position',[150, 500, 1500,600]);
h2 = figure(2); %fHV
set(h2,'Position',[150, 100, 1500,600]);
h3 = figure(3); %AEI
=======
>>>>>>> d6d6a51d384f6e748939252132948bb74c3a9860
clf(h1)
set(h1,'Position',[150, 300, 1200,600]);
hsubplot1 = cell(length(problemName),1);
for i=1:length(problemName)
    hsubplot1{i}=subplot(2,5,i);
end
h2 = figure(2); %fHV
clf(h2)
<<<<<<< HEAD
clf(h3)
% h4 = figure(4); %# injections
=======
set(h2,'Position',[150, 100, 1200,600]);
hsubplot2 = cell(length(problemName),1);
for i=1:length(problemName)
    hsubplot2{i}=subplot(2,5,i);
end
>>>>>>> d6d6a51d384f6e748939252132948bb74c3a9860

leftPos = 0.03;
topPos = 0.7;
bottomPos = 0.25;
intervalPos = (1-leftPos)/5+0.005;
width = 0.16;
height = 0.2;

statsIGD = zeros(length(problemName),b,3);
statsfHV = zeros(length(problemName),b,3);
for i=1:length(problemName)
    probName = problemName{i};
    [benchmarkDataIGD,label_names] = getBenchmarkVals(path,probName,'IGD',mode);
    [benchmarkDatafHV,~] = getBenchmarkVals(path,probName,'fHV',mode);
    [a,c] = size(benchmarkDataIGD);
    %box plot colors for benchmarks
    boxColors = 'rkm';
    
    dataIGD = cat(2,benchmarkDataIGD,zeros(a,b));
    datafHV = cat(2,benchmarkDatafHV,zeros(a,b));
<<<<<<< HEAD
    dataAEI = cat(2,benchmarkDataAEI,zeros(a,b));
%         dataInj = zeros(a,b);
=======
    %     dataInj = zeros(a,b);
>>>>>>> d6d6a51d384f6e748939252132948bb74c3a9860
    
    label_names_IGD=label_names;
    label_names_fHV=label_names;
    
    for j=1:length(selectors)
        for k=1:length(creditDef)
            c = c+1;
            file = strcat(res_path,filesep,probName,'_',selectors{j},'_',creditDef{k},'.mat');
            load(file,'res'); %assume that the reults stored in vairable named res
            dataIGD(:,c) = res.IGD;
            datafHV(:,c) = res.fHV;
            
            %             dataInj(:,c-size(benchmarkDataIGD,2)) = res.Inj;
            if strcmp(creditDef{k},'ParentDec')||strcmp(creditDef{k},'Neighbor')||strcmp(creditDef{k},'DecompositionContribution')
                [p,sig] = runMWUsignificance(path,selectors{j},creditDef{k},'best1opMOEAD',probName);
            else
%                 [p,sig] = runMWUsignificance(path,selectors{j},creditDef{k},'best1opeMOEA',probName);
%                 [p,sig] = runMWUsignificance(path,selectors{j},creditDef{k},'eMOEA',probName);
                [p,sig] = runMWUsignificance(path,selectors{j},creditDef{k},'RandomeMOEA',probName);
            end
            extra = '';
            if sig.IGD==1
                extra = '(-)';
                statsIGD(i,c,3) = 1;
            elseif sig.IGD==-1
                extra = '(+)';
                statsIGD(i,c,1) = 1;
            else
                statsIGD(i,c,2) = 1;
            end
            label_names_IGD = [label_names_IGD,strcat(selectorShort{j},'-',creditShort{k},extra)]; %concats the labels
            extra = '';
            if sig.fHV==1
                extra = '(+)';
                statsfHV(i,c,1) = 1;
            elseif sig.fHV==-1
                extra = '(-)';
                statsfHV(i,c,3) = 1;
            else
                statsfHV(i,c,2) = 1;
            end
            label_names_fHV = {label_names_fHV{:},strcat(selectorShort{j},'-',creditShort{k},extra)}; %concats the labels
            boxColors = strcat(boxColors,'b');
        end
    end
    
    if strcmp(probName,'UF1_')
        probName = 'UF1';
    end
    
    figure(h1) 
    [~,ind]=min(mean(dataIGD,1));
    label_names_IGD{ind} = strcat('\bf{',label_names_IGD{ind},'}');
    boxplot(hsubplot1{i},dataIGD,label_names_IGD,'colors',boxColors,'boxstyle','filled','medianstyle','target','symbol','+')
    set(hsubplot1{i},'TickLabelInterpreter','tex');
    set(hsubplot1{i},'XTickLabelRotation',90);
    set(hsubplot1{i},'FontSize',13)
    title(hsubplot1{i},probName)
    %ensures all boxplot axes are the same size and aligned.
    if i<6
        set(hsubplot1{i},'Position',[leftPos+intervalPos*(i-1),topPos,width,height]);
    else
        set(hsubplot1{i},'Position',[leftPos+intervalPos*(i-6),bottomPos,width,height]);
    end
    
    figure(h2)
    [~,ind]=max(mean(datafHV,1));
    label_names_fHV{ind} = strcat('\bf{',label_names_fHV{ind},'}');
    boxplot(hsubplot2{i},datafHV,label_names_fHV,'colors',boxColors,'boxstyle','filled','medianstyle','target','symbol','+')
    set(hsubplot2{i},'TickLabelInterpreter','tex');
    set(hsubplot2{i},'XTickLabelRotation',90);
    set(hsubplot2{i},'FontSize',13);
    title(hsubplot2{i},probName)
    %ensures all boxplot axes are the same size and aligned.
    if i<6
        set(hsubplot2{i},'Position',[leftPos+intervalPos*(i-1),topPos,width,height]);
    else
        set(hsubplot2{i},'Position',[leftPos+intervalPos*(i-6),bottomPos,width,height]);
    end
<<<<<<< HEAD
    
    pause(0.2)
    figure(h3)
    subplot(2,5,i);
    boxplot(datafHV,label_names_AEI,'colors',boxColors,'boxstyle','filled','medianstyle','target','symbol','o')
    set(gca,'TickLabelInterpreter','tex');
    set(gca,'XTickLabelRotation',90);
    set(gca,'FontSize',12)
    title(probName)
    
    %     figure(h4)
    %     subplot(2,5,i);
    %     boxplot(dataInj,{label_names{size(benchmarkDataIGD,2)+1:end}},'labelorientation','inline','colors',boxColors,'plotstyle','compact')
    %     title(probName)
    hold off
end
=======
end

statsIGD = squeeze(sum(statsIGD,1));
statsfHV = squeeze(sum(statsfHV,1));



% saveas(h1,strcat(base,'_IGD'),'fig');
% saveas(h2,strcat(base,'_HV'),'fig');
>>>>>>> d6d6a51d384f6e748939252132948bb74c3a9860
