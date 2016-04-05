#program to parse the data from the json files and index into solr
import json
import solr
import yourls.client
import traceback
import re
import os
import fnmatch


#Directory where the json files are stored
rootdir="/Users/rrgirish/Downloads/polardata_json"
#rootdir="/Users/rrgirish/Downloads/polardata_json_grobid"

# files = [f for f in listdir(rootdir) if isfile(join(rootdir, f))]
files = []
for root, dirnames, filenames in os.walk(rootdir):
    for filename in fnmatch.filter(filenames, '*.json'):
        files.append(os.path.join(root, filename))

#create a connection to solr
s = solr.SolrConnection('http://localhost:8983/solr/polardata')

#Create a YOURLS client
c = yourls.client.YourlsClient('http://localhost/yourls/yourls-api.php', username="root", password="password", token = None)
for file in files:
    try:
    #parse each json file in the directory
        print(file)

        if file.endswith(".json")!=True:
            continue
        data={}
        with open(file) as data_file:
            data = json.load(data_file)

            Grobid_authors="N/A"
            Scholar_tags=[]
            Measurements="N/A"
            Units="N/A"
            Geographical_Name="N/A"
            Geographical_Longitude="-9999"
            Geographical_Latitude="-9999"
            Representation="N/A"
            Realm="N/A"
            MeasurementsUnits="N/A"
            Matter="N/A"
            HumanActivities="N/A"
            Phenomena="N/A"
            ContentType="N/A"
            Author="N/A"
            title="N/A"
            absoluteFilePath="N/A"
            Process="N/A"
            Created_date="N/A"

            if(data):
                #Generate a unique name for the file using YOURLS client and use
                #that as an id for indexing in SOLR
                strin=data["absoluteFilePath"]
                strin=strin.replace('\\',"/")
                strin=strin.replace("F:","http://polar.usc.edu")
                id = c.shorten(strin)
                id=id.replace("127.0.0.1","polar.usc.edu")


                finalScore = 0.0
                score = 0.0
                totalWeight = 0

                #compute score while indexing
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

                #check if NER data is present
                if "STANFORD_NER" in data:
                    if "MEASUREMENT" in data["STANFORD_NER"][0]:
                        score = 0.5
                        Measurements=",".join(data["STANFORD_NER"][0]["MEASUREMENT"])
                        MeasurementsUnits = ''.join([i for i in Measurements if not i.isdigit()])
                        MeasurementUnitsArray=list(set(MeasurementsUnits.split(",")))
                        MeasurementUnitsArray = [x for x in MeasurementUnitsArray if x != '']
                        MeasurementsUnits=",".join(MeasurementUnitsArray)

                    if "UNIT" in data["STANFORD_NER"][0]:
                        score = 1.0
                        Units=",".join(data["STANFORD_NER"][0]["UNIT"])
                        if MeasurementsUnits != "N/A":
                            Units=Units+","+MeasurementsUnits
                            Units=",".join(list(set(Units.split(","))))
                    else:
                        Units=MeasurementsUnits
                    finalScore = finalScore + 10 * score

                totalWeight = totalWeight + 10

                #check if grobid and scholar data is present
                if "grobid_tags" in data:
                    score = 0.25
                    if "grobid:header_Authors" in data["grobid_tags"]:
                        score = 0.5
                        Grobid_authors=data["grobid_tags"]["grobid:header_Authors"]
                        Grobid_authors=Grobid_authors
                        Grobid_authors=Grobid_authors.encode('ascii', 'ignore')
                        Grobid_authors = re.sub("^\d+\s|\s\d+\s|\s\d+$", ",", Grobid_authors)
                        if "scholar_tags" in data:
                            for i in range(0,len(data["scholar_tags"])):
                                if("Cluster ID" in data["scholar_tags"][i] and "URL" in data["scholar_tags"][i] and "Title" in data["scholar_tags"][i] and "Citations list" in data["scholar_tags"][i]):
                                    Scholar_tags.append(data["scholar_tags"][i]["Cluster ID"]+","+data["scholar_tags"][i]["URL"]+","+data["scholar_tags"][i]["Title"]+","+data["scholar_tags"][i]["Citations list"])
                            print(Scholar_tags)
                            score = 1.0
                        else:
                            Scholar_tags.append("N/A")
                    finalScore = finalScore + 10 * score
                totalWeight = totalWeight + 10

                #check if geolocation information is present
                if "geo_name" in data:
                    if data["geo_name"] == "N/A":
                        score = 0
                    else:
                        Geographical_Name=data["geo_name"]
                        score = 1
                    finalScore = finalScore + 10 * score
                totalWeight = totalWeight + 10

                if "geo_long" in data:
                    Geographical_Longitude=data["geo_long"]

                if "geo_lat" in data:
                    Geographical_Latitude=data["geo_lat"]


                #check if SWEET data is present
                if "SWEET" in data:
                    score = 1
                    if("REPRESENTATION" in data["SWEET"][0]):
                        Representation=",".join(data["SWEET"][0]["REPRESENTATION"])

                    if("REALM" in data["SWEET"][0]):
                        Realm=",".join(data["SWEET"][0]["REALM"])

                    if("PROCESS" in data["SWEET"][0]):
                        Process=",".join(data["SWEET"][0]["PROCESS"])

                    if("PHENOMENA" in data["SWEET"][0]):
                        Phenomena=",".join(data["SWEET"][0]["PHENOMENA"])

                    if("HUMAN ACTIVITIES" in data["SWEET"][0]):
                        HumanActivities=",".join(data["SWEET"][0]["HUMAN ACTIVITIES"])

                    if("MATTER" in data["SWEET"][0]):
                        Matter=",".join(data["SWEET"][0]["MATTER"])



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

                if "content" in data:
                    content=data["content"]


                #add the meta data fields from Tika
                if "meta-tags" in data:
                    if "Content-Type" in data["meta-tags"][0]:
                        ContentType=data["meta-tags"][0]["Content-Type"]
                    if "Author" in data["meta-tags"][0]:
                        Author=data["meta-tags"][0]["Author"]
                    if "date" in data["meta-tags"][0]:
                        Created_date=data["meta-tags"][0]["date"]
                    if "dc:title" in data["meta-tags"][0]:
                        title=data["meta-tags"][0]["dc:title"]

                if "absoluteFilePath" in data:
                    absoluteFilePath=data["absoluteFilePath"]

                # #send to solr to index document
                s.add(content=content,id=id,Measurements=Measurements, Units=Units,Geographical_Name=Geographical_Name,
                          Geographical_Latitude=Geographical_Latitude,Geographical_Longitude=Geographical_Longitude,
                          title=title, Representation=Representation,Realm=Realm,Matter=Matter,HumanActivities=HumanActivities,
                          Phenomena=Phenomena,ContentType=ContentType,Author=Author,absoluteFilePath=absoluteFilePath,
                          Created_date=Created_date,score=finalScore,Process=Process,Grobid_authors=Grobid_authors,Scholar_tags=Scholar_tags
                )

                s.commit()
    except:
        traceback.print_exc()
        continue
