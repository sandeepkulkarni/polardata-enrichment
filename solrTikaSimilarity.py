__author__ = 'AravindMac'

from urllib2 import *
import simplejson
import json

#Program to dump all the solr documents to json files

numDocs = int(sys.argv[1])  #Get number of documents to be fetched

#Create solr core named polardata and run it on localhost:8984

#Create 4 folders named Measurements,Sweet,Publications and Locations in the current directory

#1. Measurements

#Solr Query for retrieving Measurements
solrResponse = urlopen('http://localhost:8984/solr/polardata/select?q=NOT(Units%3A+%22N%2FA%22)&rows='+str(numDocs)+'&start=0&fl=Units&wt=json&indent=true')
jsonResponse = simplejson.load(solrResponse)        #Converting to json response
if(jsonResponse['response']['numFound'] < numDocs):     #Handle case when numDocs specified is more than the number of documents returned
    n = int(jsonResponse['response']['numFound'])
else:
    n = numDocs
for i in range(n):
    f = open('./Measurements/file'+str(i+1)+'.txt', 'w')        #Create files and push to Measurements folder
    json.dump(jsonResponse['response']['docs'][i]['Units'], f)  #Write Units to file
    f.close()
    print jsonResponse['response']['docs'][i]['Units']


#2. Sweet

#Solr Query for retrieving Sweet Classifications
solrResponse = urlopen('http://localhost:8984/solr/polardata/select?q=-((Phenomena%3AN%2FA)+AND+(Matter%3AN%2FA)+AND+(Process%3AN%2FA)+AND+(Realm%3AN%2FA)+AND+(Representation%3AN%2FA)+AND+(HumanActivities%3AN%2FA))&fl=Process%2CHumanActivities%2CRealm%2CPhenomena%2CMatter%2CRepresentation&rows='+str(numDocs)+'&start=0&wt=json&indent=true')
jsonResponse = simplejson.load(solrResponse)        #Converting to json response
if(jsonResponse['response']['numFound'] < numDocs):     #Handle case when numDocs specified is more than the number of documents returned
    n = jsonResponse['response']['numFound']
else:
    n = numDocs
for i in range(n):
    sweetStr = "";
    f = open('./Sweet/file'+str(i+1)+'.txt', 'w')               #Create files and push to Sweet folder
    #Build string to push Sweet tags if present
    if(jsonResponse['response']['docs'][i]['Process']!="N/A"):
        sweetStr+="Process\n"
    if(jsonResponse['response']['docs'][i]['Realm']!="N/A"):
        sweetStr+="Realm\n"
    if(jsonResponse['response']['docs'][i]['HumanActivities']!="N/A"):
        sweetStr+="HumanActivities\n"
    if(jsonResponse['response']['docs'][i]['Phenomena']!="N/A"):
        sweetStr+="Phenomena\n"
    if(jsonResponse['response']['docs'][i]['Matter']!="N/A"):
        sweetStr+="Matter\n"
    if(jsonResponse['response']['docs'][i]['Representation']!="N/A"):
        sweetStr+="Representation\n"

    f.write(sweetStr)                                           #Write Sweet tags to file
    f.close()
    print sweetStr

#3. Publications

#Solr Query for retrieving Publications
solrResponse = urlopen('http://localhost:8984/solr/polardata/select?q=Scholar_tags%3A*.*&fl=Grobid_authors%2CScholar_tags&rows='+str(numDocs)+'&start=0&wt=json&indent=true')
jsonResponse = simplejson.load(solrResponse)                    #Converting to json response
if(jsonResponse['response']['numFound'] < numDocs):             #Handle case when numDocs specified is more than the number of documents returned
    n = jsonResponse['response']['numFound']
else:
    n = numDocs
for i in range(n):
    f = open('./Publications/file'+str(i+1)+'.txt', 'w')        #Create files and push to Publications folder
    for entry in jsonResponse['response']['docs'][i]['Scholar_tags']:
        arr = entry.split(",")
        print arr[0]
        print arr[1]
        f.write(arr[0]+" "+arr[1])                              #Write cluster id and publications to file
    f.write("\n")
    f.write(jsonResponse['response']['docs'][i]['Grobid_authors'])      #Write authors to file
    f.close()
    print(jsonResponse['response']['docs'][i]['Grobid_authors'])

#4. Locations

#Solr Query for retrieving Publications
solrResponse = urlopen('http://localhost:8984/solr/polardata/select?q=NOT(Geographical_Name%3A+N%2FA)&fl=Geographical_Name&rows='+str(numDocs)+'&start=0&wt=json&indent=true')
jsonResponse = simplejson.load(solrResponse)                    #Converting to json response
if(jsonResponse['response']['numFound'] < numDocs):             #Handle case when numDocs specified is more than the number of documents returned
    n = jsonResponse['response']['numFound']
else:
    n = numDocs
for i in range(n):
    f = open('./Locations/file'+str(i+1)+'.txt', 'w')           #Create files and push to Locations folder
    #f.write(jsonResponse['response']['docs'][i]['Geographical_Name'])
    json.dump(jsonResponse['response']['docs'][i]['Geographical_Name'], f)      #Write Geographical Name to file
    f.close()
    print jsonResponse['response']['docs'][i]['Geographical_Name']
    #print json.dumps(jsonResponse['response']['docs'][i]['Geographical_Name'], sort_keys=True, indent=4, separators=(',', ': '))

