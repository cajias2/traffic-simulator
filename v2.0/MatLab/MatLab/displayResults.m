function [] = displayResults (rootFolder, numGraphs, pngName)

color = ['r', 'b', 'g', 'k' ];
%rootFolder = '2Str_1X_Smart';
%rootFolder = 'Smart_test';
list=dir(rootFolder);


h = zeros(1, numGraphs);
s = cell(1, numGraphs);
C = [ 1 1 0; 0 1 0; 0 1 1; 0 0 1; 0 0 0; 1 0 1];
%C = [0 0 0 0; 0 0 0 1; 0 0 1 0; 0 0 1 1; 0 1 0 0; 0 1 0 1; 0 1 1 0; 0 1 1 1; 1 0 0 0; 1 0 0 1; 1 0 1 0; 1 0 1 1; 1 1 0 0];

figure();
hold on;
title( '2 Streets');
k = 0;
for i = 1 : size(list)

    if(list(i).isdir ~= 1)
        list(i).name;

        batch = strcat(rootFolder,'/');
        batch = strcat(batch,list(i).name);


        try
         outBatch = load(batch);                       
         h(k) = plot(outBatch(:,1),outBatch(:,2), 'Color', C(mod(k,6), :));
         s{k} = sprintf(list(i).name);        
        catch me
            list(i).name
        end;
    k = k+1;

    end;
end;
% legend('1(1 TO aX1)', 'a(a TO aX1)');
xlabel('Iteraciones');
ylabel('Velocidad Media');
%
ind = 1:1:numGraphs;
% Create legend for the selected plots
% legend('x', 'y', 'z');
 legend(h(ind), s{ind});
hold off;


if(pngName)
hgsave('test1');
h = hgload('test1');
saveas(h, strcat(pngName,'.png'));
end;

end



