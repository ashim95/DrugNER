import operator
import sys
import os

# filename = "resources/drug_names_filter.txt"


## n is for the number for suffix Ex: n = 4 for last 4 characters as suffix
def find_suffix(filename, n):
	fp = open(filename, 'rb')
	suffix_dict = {}
	for line in fp.readlines():
		line = line.strip('\n')
		suffix = line[-n:]
		if suffix not in suffix_dict:
			suffix_dict[suffix] = 1
		else:
			suffix_dict[suffix] +=1


	sorted_suffix = sorted(suffix_dict.items(), key=operator.itemgetter(1), reverse = True)
	fp.close()
	print "Total suffixes generated : " + str(len(sorted_suffix))
	
	return sorted_suffix


## N = top_how_many suffixes
def writeTopNSuffixToFile(sorted_suffix, N, n, file):

	suffix_list = []
	count = 0
	for l in sorted_suffix:
		if count < N:
			suffix_list.append(l[0])
			count +=1

	if file != "":
		file = "top_" + str(N) + "_" + str(n) + "_suffixes" + ".txt"
	fp = open(os.path.join('resources',file), 'wb')
	for item in suffix_list:
		fp.write("%s\n" % item)
	fp.close()
	print "Succesfully wrote Top " + str(N) + " " + str(n) + "-suffixes" + " to file !! "


filename = sys.argv[1] ## String File Name
top_how_many = int(sys.argv[2])
suffix_type = int(sys.argv[3])
if len(sys.argv) == 5:
	filename_to_save = sys.argv[4]
else:
	filename_to_save = ""

print "Finding Suffixes ... "

sorted_suffix = find_suffix(filename, suffix_type)

print "Writing Top " + str(top_how_many) + " Suffixes ... "

writeTopNSuffixToFile(sorted_suffix, top_how_many, suffix_type, filename_to_save)