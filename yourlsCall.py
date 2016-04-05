import yourls.client
import os
import json
import fnmatch

#inputdir
rootdir="/Users/rrgirish/Downloads/polardata_json_grobid"

#creat a YOURLS client
c = yourls.client.YourlsClient('http://localhost/yourls/yourls-api.php', username="root", password="password", token = None)

#get list of files in  the directory
files = []
for root, dirnames, filenames in os.walk(rootdir):
    for filename in fnmatch.filter(filenames, '*.json'):
        files.append(os.path.join(root, filename))
for file in files:
    if file.endswith(".json")!=True:
            continue
    data={}
    with open(file) as data_file:
        data = json.load(data_file)
        strin=data["absoluteFilePath"]
        strin=strin.replace('\\',"/")
        strin=strin.replace("F:","http://polar.usc.edu")
        id = c.shorten(strin)
        id=id.replace("127.0.0.1","polar.usc.edu")

    #shortened file name
    print(id)