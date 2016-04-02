__author__ = 'grao'
#!/usr/bin/env python2.7

import os
import json
import re
import traceback


#recursively read all files in the input directory
rootdir="/Volumes/Blunder/polardata_json/text_html"
for root, subFolders, files in os.walk(rootdir):
    for fileName in files:
        if re.match(r'^\.', fileName):
            continue
        parsed={}
        try:
            with open(os.path.join(root,fileName), 'r+') as temp:
                parsed=json.load(temp)
                #Fields for GeoTopic Parser

                geo_name="N/A"
                geo_lat="-9999"
                geo_long="-9999"

                if "geo_name" in parsed or "geo_lat" in parsed or "geo_long" in parsed:
                    print(parsed["geo_name"],parsed["geo_lat"],parsed["geo_long"])
                    continue

                if "content" in parsed:
                    flag=True
                    contentString=parsed["content"].encode('ascii','ignore')
                    contentString=contentString.decode("utf-8")

                    file = open("/Users/rrgirish/Documents/temp.geot",'w+')   # Trying to create a new file or open one
                    try:
                        file.write(contentString)
                        file.close()
                    except:
                         traceback.print_exc()

                    try:
                        #send to tika server enabled with GeoTopic Parser
                        result=os.popen("curl -T /Users/rrgirish/Documents/temp.geot -H 'Content-Disposition: attachment; filename=temp.geot' http://localhost:9998/rmeta").read()
                        parsed_json = json.loads(result)

                        #set the Geo Fields for the document
                        if "Geographic_LATITUDE" in parsed_json[0]:
                            geo_lat = parsed_json[0]["Geographic_LATITUDE"]

                        if "Geographic_LONGITUDE" in parsed_json[0]:
                            geo_long = parsed_json[0]["Geographic_LONGITUDE"]

                        if "Geographic_NAME" in parsed_json[0]:
                            geo_name = parsed_json[0]["Geographic_NAME"]
                        parsed["geo_name"]=geo_name
                        parsed["geo_lat"]=geo_lat
                        parsed["geo_long"]=geo_long

                        temp.seek(0,0)
                        json.dump(parsed,temp)
                        temp.close()
                    except:

                        traceback.print_exc()

                        continue
        except:
            traceback.print_exc()
            continue


