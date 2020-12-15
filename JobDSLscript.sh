#!/bin/bash
count=$(wc -l list.txt | awk '{print $1}')
for ((i=1;i<=$count;i++))
do
	repoName=$(awk 'NR=='$i'' list.txt)
	echo ${repoName}
	cp QA_EU_gehc_mobile_m1_projectc_api.groovy QA_EU_${repoName}.groovy
done
