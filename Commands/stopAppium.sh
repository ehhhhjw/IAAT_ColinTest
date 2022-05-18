result=$(echo `netstat -nlp 2>/dev/null | grep node | grep $1`)
arr=(${result//,/ })  
for i in ${arr[@]}  
do  
	if [[ $i == *node ]]
	then
		tmp=$i
	fi
done
process_id=${tmp%/*}
if [ -n $process_id ]
then
	kill $process_id 2>/dev/null	#不显示标准错误输出
fi
