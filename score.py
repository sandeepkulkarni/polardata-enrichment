from os import listdir
from os.path import isfile, join
import json
from pprint import pprint

inputPath = "D://599test//samplejson"
files = [f for f in listdir(inputPath) if isfile(join(inputPath, f))]
#print files
for file in files:
    with open(inputPath + '/' +file) as data_file:
        data = json.load(data_file)

    finalScore = 0.0
    score = 0.0
    totalWeight = 0
    #pprint(data)
    print "**** File: "+file

    if "avgTagRatio" in data:
        avgTagRatio = data["avgTagRatio"]
        #print 'avgTagRatio : ' + str(avgTagRatio)
        if avgTagRatio >= 10 and avgTagRatio < 100:
            score = 0.25
        elif avgTagRatio >= 100 and avgTagRatio < 500:
            score = 0.5
        elif avgTagRatio >= 500 and avgTagRatio < 1000:
            score = 0.75
        else:
            score = 1
        finalScore = finalScore + 10 * score
    totalWeight = totalWeight + 10

    if "STANFORD_NER" in data:
        if "MEASUREMENT" in data["STANFORD_NER"][0]:
            score = 0.5
            #print "### Measurement"
        if "UNIT" in data["STANFORD_NER"][0]:
            score = 1.0
            #print "### Unit"
        finalScore = finalScore + 10 * score
    totalWeight = totalWeight + 10

    if "grobid_tags" in data:
        score = 0.25
        #print "### Grobid"
        if "grobid:header_Authors" in data["grobid_tags"]:
            score = 0.5
            #print '#######Author Present######'
            if "scholar_tags" in data:
                score = 1.0
        finalScore = finalScore + 10 * score
    totalWeight = totalWeight + 10

    if "geo_name" in data:
        #print data["geo_name"]
        if data["geo_name"] == "N/A":
            score = 0
        else:
            score = 1
        finalScore = finalScore + 10 * score
    totalWeight = totalWeight + 10

    if "SWEET" in data:
        #print "### SWEET present"
        score = 1
        finalScore = finalScore + 10 * score
    totalWeight = totalWeight + 10

    # Constant score for url
    finalScore = finalScore + 10 * 1
    totalWeight = totalWeight + 10

    # Constant score for Solr
    finalScore = finalScore + 10 * 1
    totalWeight = totalWeight + 10

    # Weighted avg: Final score
    finalScore = finalScore/totalWeight

    print 'finalScore : '+str(finalScore)
